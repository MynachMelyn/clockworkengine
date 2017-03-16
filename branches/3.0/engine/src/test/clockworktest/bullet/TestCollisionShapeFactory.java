
package clockworktest.bullet;

import com.clockwork.app.SimpleApplication;
import com.clockwork.bullet.BulletAppState;
import com.clockwork.bullet.PhysicsSpace;
import com.clockwork.bullet.control.RigidBodyControl;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.FastMath;
import com.clockwork.math.Quaternion;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.shape.Box;
import com.clockwork.scene.shape.Cylinder;
import com.clockwork.scene.shape.Torus;

/**
 * This is a basic Test of jbullet-CW functions
 *
 */
public class TestCollisionShapeFactory extends SimpleApplication {

    private BulletAppState bulletAppState;
    private Material mat1;
    private Material mat2;
    private Material mat3;

    public static void main(String[] args) {
        TestCollisionShapeFactory app = new TestCollisionShapeFactory();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        createMaterial();

        Node node = new Node("node1");
        attachRandomGeometry(node, mat1);
        randomizeTransform(node);

        Node node2 = new Node("node2");
        attachRandomGeometry(node2, mat2);
        randomizeTransform(node2);

        node.attachChild(node2);
        rootNode.attachChild(node);

        RigidBodyControl control = new RigidBodyControl(0);
        node.addControl(control);
        getPhysicsSpace().add(control);

        //test single geometry too
        Geometry myGeom = new Geometry("cylinder", new Cylinder(16, 16, 0.5f, 1));
        myGeom.setMaterial(mat3);
        randomizeTransform(myGeom);
        rootNode.attachChild(myGeom);
        RigidBodyControl control3 = new RigidBodyControl(0);
        myGeom.addControl(control3);
        getPhysicsSpace().add(control3);
    }

    private void attachRandomGeometry(Node node, Material mat) {
        Box box = new Box(0.25f, 0.25f, 0.25f);
        Torus torus = new Torus(16, 16, 0.2f, 0.8f);
        Geometry[] boxes = new Geometry[]{
            new Geometry("box1", box),
            new Geometry("box2", box),
            new Geometry("box3", box),
            new Geometry("torus1", torus),
            new Geometry("torus2", torus),
            new Geometry("torus3", torus)
        };
        for (int i = 0; i < boxes.length; i++) {
            Geometry geometry = boxes[i];
            geometry.setLocalTranslation((float) Math.random() * 10 -10, (float) Math.random() * 10 -10, (float) Math.random() * 10 -10);
            geometry.setLocalRotation(new Quaternion().fromAngles((float) Math.random() * FastMath.PI, (float) Math.random() * FastMath.PI, (float) Math.random() * FastMath.PI));
            geometry.setLocalScale((float) Math.random() * 10 -10, (float) Math.random() * 10 -10, (float) Math.random() * 10 -10);
            geometry.setMaterial(mat);
            node.attachChild(geometry);
        }
    }

    private void randomizeTransform(Spatial spat){
        spat.setLocalTranslation((float) Math.random() * 10, (float) Math.random() * 10, (float) Math.random() * 10);
        spat.setLocalTranslation((float) Math.random() * 10, (float) Math.random() * 10, (float) Math.random() * 10);
        spat.setLocalScale((float) Math.random() * 2, (float) Math.random() * 2, (float) Math.random() * 2);
    }

    private void createMaterial() {
        mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Green);
        mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);
        mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat3.setColor("Color", ColorRGBA.Yellow);
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
}
