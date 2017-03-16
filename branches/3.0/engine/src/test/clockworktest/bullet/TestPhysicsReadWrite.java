

package clockworktest.bullet;


import com.clockwork.app.SimpleApplication;
import com.clockwork.bullet.BulletAppState;
import com.clockwork.bullet.PhysicsSpace;
import com.clockwork.bullet.collision.shapes.*;
import com.clockwork.bullet.control.RigidBodyControl;
import com.clockwork.bullet.joints.HingeJoint;
import com.clockwork.export.binary.BinaryExporter;
import com.clockwork.export.binary.BinaryImporter;
import com.clockwork.math.Plane;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.RenderManager;
import com.clockwork.scene.Node;
import com.clockwork.scene.shape.Sphere;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a basic Test of jbullet-CW functions
 *
 */
public class TestPhysicsReadWrite extends SimpleApplication{
    private BulletAppState bulletAppState;
    private Node physicsRootNode;
    public static void main(String[] args){
        TestPhysicsReadWrite app = new TestPhysicsReadWrite();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        physicsRootNode=new Node("PhysicsRootNode");
        rootNode.attachChild(physicsRootNode);

        // Add a physics sphere to the world
        Node physicsSphere = PhysicsTestHelper.createPhysicsTestNode(assetManager, new SphereCollisionShape(1), 1);
        physicsSphere.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(3, 6, 0));
        rootNode.attachChild(physicsSphere);
        getPhysicsSpace().add(physicsSphere);

        // Add a physics sphere to the world using the collision shape from sphere one
        Node physicsSphere2 = PhysicsTestHelper.createPhysicsTestNode(assetManager, physicsSphere.getControl(RigidBodyControl.class).getCollisionShape(), 1);
        physicsSphere2.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(4, 8, 0));
        rootNode.attachChild(physicsSphere2);
        getPhysicsSpace().add(physicsSphere2);

        // Add a physics box to the world
        Node physicsBox = PhysicsTestHelper.createPhysicsTestNode(assetManager, new BoxCollisionShape(new Vector3f(1, 1, 1)), 1);
        physicsBox.getControl(RigidBodyControl.class).setFriction(0.1f);
        physicsBox.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(.6f, 4, .5f));
        rootNode.attachChild(physicsBox);
        getPhysicsSpace().add(physicsBox);

        // Add a physics cylinder to the world
        Node physicsCylinder = PhysicsTestHelper.createPhysicsTestNode(assetManager, new CylinderCollisionShape(new Vector3f(1f, 1f, 1.5f)), 1);
        physicsCylinder.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(2, 2, 0));
        rootNode.attachChild(physicsCylinder);
        getPhysicsSpace().add(physicsCylinder);

        // an obstacle mesh, does not move (mass=0)
        Node node2 = PhysicsTestHelper.createPhysicsTestNode(assetManager, new MeshCollisionShape(new Sphere(16, 16, 1.2f)), 0);
        node2.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(2.5f, -4, 0f));
        rootNode.attachChild(node2);
        getPhysicsSpace().add(node2);

        // the floor mesh, does not move (mass=0)
        Node node3 = PhysicsTestHelper.createPhysicsTestNode(assetManager, new PlaneCollisionShape(new Plane(new Vector3f(0, 1, 0), 0)), 0);
        node3.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(0f, -6, 0f));
        rootNode.attachChild(node3);
        getPhysicsSpace().add(node3);

        // Join the physics objects with a Point2Point joint
        HingeJoint joint=new HingeJoint(physicsSphere.getControl(RigidBodyControl.class), physicsBox.getControl(RigidBodyControl.class), new Vector3f(-2,0,0), new Vector3f(2,0,0), Vector3f.UNIT_Z,Vector3f.UNIT_Z);
        getPhysicsSpace().add(joint);

        //save and load the physicsRootNode
        try {
            //remove all physics objects from physics space
            getPhysicsSpace().removeAll(physicsRootNode);
            physicsRootNode.removeFromParent();
            //export to byte array
            ByteArrayOutputStream bout=new ByteArrayOutputStream();
            BinaryExporter.getInstance().save(physicsRootNode, bout);
            //import from byte array
            ByteArrayInputStream bin=new ByteArrayInputStream(bout.toByteArray());
            BinaryImporter imp=BinaryImporter.getInstance();
            imp.setAssetManager(assetManager);
            Node newPhysicsRootNode=(Node)imp.load(bin);
            //add all physics objects to physics space
            getPhysicsSpace().addAll(newPhysicsRootNode);
            rootNode.attachChild(newPhysicsRootNode);
        } catch (IOException ex) {
            Logger.getLogger(TestPhysicsReadWrite.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private PhysicsSpace getPhysicsSpace(){
        return bulletAppState.getPhysicsSpace();
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

}
