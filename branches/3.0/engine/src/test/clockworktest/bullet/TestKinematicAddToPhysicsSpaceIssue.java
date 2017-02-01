
package clockworktest.bullet;

import com.clockwork.app.SimpleApplication;
import com.clockwork.bullet.BulletAppState;
import com.clockwork.bullet.PhysicsSpace;
import com.clockwork.bullet.collision.shapes.MeshCollisionShape;
import com.clockwork.bullet.collision.shapes.PlaneCollisionShape;
import com.clockwork.bullet.collision.shapes.SphereCollisionShape;
import com.clockwork.bullet.control.RigidBodyControl;
import com.clockwork.math.Plane;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Node;
import com.clockwork.scene.shape.Sphere;

/**
 *
 */
public class TestKinematicAddToPhysicsSpaceIssue extends SimpleApplication {

    public static void main(String[] args) {
        TestKinematicAddToPhysicsSpaceIssue app = new TestKinematicAddToPhysicsSpaceIssue();
        app.start();
    }
    BulletAppState bulletAppState;

    @Override
    public void simpleInitApp() {

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        // Add a physics sphere to the world
        Node physicsSphere = PhysicsTestHelper.createPhysicsTestNode(assetManager, new SphereCollisionShape(1), 1);
        physicsSphere.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(3, 6, 0));
        rootNode.attachChild(physicsSphere);

        //Setting the rigidBody to kinematic before adding it to the physic space
        physicsSphere.getControl(RigidBodyControl.class).setKinematic(true);
        //adding it to the physic space
        getPhysicsSpace().add(physicsSphere);         
        //Making it not kinematic again, it should fall under gravity, it doesn't
        physicsSphere.getControl(RigidBodyControl.class).setKinematic(false);

        // Add a physics sphere to the world using the collision shape from sphere one
        Node physicsSphere2 = PhysicsTestHelper.createPhysicsTestNode(assetManager, new SphereCollisionShape(1), 1);
        physicsSphere2.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(5, 6, 0));
        rootNode.attachChild(physicsSphere2);
        
        //Adding the rigid body to physic space
        getPhysicsSpace().add(physicsSphere2);
        //making it kinematic
        physicsSphere2.getControl(RigidBodyControl.class).setKinematic(false);
        //Making it not kinematic again, it works properly, the rigidbody is affected by grvity.
        physicsSphere2.getControl(RigidBodyControl.class).setKinematic(false);

      

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

    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
}
