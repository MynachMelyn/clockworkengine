

package clockworktest.bullet;

import com.clockwork.app.SimpleApplication;
import com.clockwork.bullet.BulletAppState;
import com.clockwork.bullet.PhysicsSpace;
import com.clockwork.bullet.collision.PhysicsCollisionEvent;
import com.clockwork.bullet.collision.PhysicsCollisionListener;
import com.clockwork.bullet.collision.shapes.SphereCollisionShape;
import com.clockwork.renderer.RenderManager;
import com.clockwork.scene.shape.Sphere;
import com.clockwork.scene.shape.Sphere.TextureMode;

/**
 *
 * 
 */
public class TestCollisionListener extends SimpleApplication implements PhysicsCollisionListener {

    private BulletAppState bulletAppState;
    private Sphere bullet;
    private SphereCollisionShape bulletCollisionShape;

    public static void main(String[] args) {
        TestCollisionListener app = new TestCollisionListener();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        bullet = new Sphere(32, 32, 0.4f, true, false);
        bullet.setTextureMode(TextureMode.Projected);
        bulletCollisionShape = new SphereCollisionShape(0.4f);

        PhysicsTestHelper.createPhysicsTestWorld(rootNode, assetManager, bulletAppState.getPhysicsSpace());
        PhysicsTestHelper.createBallShooter(this, rootNode, bulletAppState.getPhysicsSpace());

        // add ourselves as collision listener
        getPhysicsSpace().addCollisionListener(this);
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

    public void collision(PhysicsCollisionEvent event) {
        if ("Box".equals(event.getNodeA().getName()) || "Box".equals(event.getNodeB().getName())) {
            if ("bullet".equals(event.getNodeA().getName()) || "bullet".equals(event.getNodeB().getName())) {
                fpsText.setText("You hit the box!");
            }
        }
    }

}
