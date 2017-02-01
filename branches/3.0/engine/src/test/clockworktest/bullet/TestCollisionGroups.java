
package clockworktest.bullet;

import com.clockwork.app.SimpleApplication;
import com.clockwork.bullet.BulletAppState;
import com.clockwork.bullet.PhysicsSpace;
import com.clockwork.bullet.collision.PhysicsCollisionObject;
import com.clockwork.bullet.collision.shapes.MeshCollisionShape;
import com.clockwork.bullet.collision.shapes.SphereCollisionShape;
import com.clockwork.bullet.control.RigidBodyControl;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.RenderManager;
import com.clockwork.scene.Node;
import com.clockwork.scene.shape.Box;
import com.clockwork.scene.shape.Sphere;

/**
 *
 */
public class TestCollisionGroups extends SimpleApplication {

    private BulletAppState bulletAppState;

    public static void main(String[] args) {
        TestCollisionGroups app = new TestCollisionGroups();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        
        // Add a physics sphere to the world
        Node physicsSphere = PhysicsTestHelper.createPhysicsTestNode(assetManager, new SphereCollisionShape(1), 1);
        physicsSphere.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(3, 6, 0));
        rootNode.attachChild(physicsSphere);
        getPhysicsSpace().add(physicsSphere);

        // Add a physics sphere to the world
        Node physicsSphere2 = PhysicsTestHelper.createPhysicsTestNode(assetManager, new SphereCollisionShape(1), 1);
        physicsSphere2.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(4, 8, 0));
        physicsSphere2.getControl(RigidBodyControl.class).addCollideWithGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        rootNode.attachChild(physicsSphere2);
        getPhysicsSpace().add(physicsSphere2);

        // an obstacle mesh, does not move (mass=0)
        Node node2 = PhysicsTestHelper.createPhysicsTestNode(assetManager, new MeshCollisionShape(new Sphere(16, 16, 1.2f)), 0);
        node2.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(2.5f, -4, 0f));
        node2.getControl(RigidBodyControl.class).setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        node2.getControl(RigidBodyControl.class).setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_02);
        rootNode.attachChild(node2);
        getPhysicsSpace().add(node2);

        // the floor, does not move (mass=0)
        Node node3 = PhysicsTestHelper.createPhysicsTestNode(assetManager, new MeshCollisionShape(new Box(Vector3f.ZERO, 100f, 0.2f, 100f)), 0);
        node3.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(0f, -6, 0f));
        rootNode.attachChild(node3);
        getPhysicsSpace().add(node3);
    }

    private PhysicsSpace getPhysicsSpace() {
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
