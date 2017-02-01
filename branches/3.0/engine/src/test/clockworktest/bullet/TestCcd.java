
package clockworktest.bullet;

import com.clockwork.app.SimpleApplication;
import com.clockwork.bullet.BulletAppState;
import com.clockwork.bullet.PhysicsSpace;
import com.clockwork.bullet.collision.shapes.BoxCollisionShape;
import com.clockwork.bullet.collision.shapes.MeshCollisionShape;
import com.clockwork.bullet.collision.shapes.SphereCollisionShape;
import com.clockwork.bullet.control.RigidBodyControl;
import com.clockwork.input.MouseInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.MouseButtonTrigger;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.queue.RenderQueue.ShadowMode;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.shape.Box;
import com.clockwork.scene.shape.Sphere;
import com.clockwork.scene.shape.Sphere.TextureMode;

/**
 *
 * @author normenhansen
 */
public class TestCcd extends SimpleApplication implements ActionListener {

    private Material mat;
    private Material mat2;
    private Sphere bullet;
    private SphereCollisionShape bulletCollisionShape;
    private BulletAppState bulletAppState;

    public static void main(String[] args) {
        TestCcd app = new TestCcd();
        app.start();
    }

    private void setupKeys() {
        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("shoot2", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(this, "shoot");
        inputManager.addListener(this, "shoot2");
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        bullet = new Sphere(32, 32, 0.4f, true, false);
        bullet.setTextureMode(TextureMode.Projected);
        bulletCollisionShape = new SphereCollisionShape(0.1f);
        setupKeys();

        mat = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Green);

        mat2 = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.getAdditionalRenderState().setWireframe(true);
        mat2.setColor("Color", ColorRGBA.Red);

        // An obstacle mesh, does not move (mass=0)
        Node node2 = new Node();
        node2.setName("mesh");
        node2.setLocalTranslation(new Vector3f(2.5f, 0, 0f));
        node2.addControl(new RigidBodyControl(new MeshCollisionShape(new Box(Vector3f.ZERO, 4, 4, 0.1f)), 0));
        rootNode.attachChild(node2);
        getPhysicsSpace().add(node2);

        // The floor, does not move (mass=0)
        Node node3 = new Node();
        node3.setLocalTranslation(new Vector3f(0f, -6, 0f));
        node3.addControl(new RigidBodyControl(new BoxCollisionShape(new Vector3f(100, 1, 100)), 0));
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

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("shoot") && !value) {
            Geometry bulletg = new Geometry("bullet", bullet);
            bulletg.setMaterial(mat);
            bulletg.setName("bullet");
            bulletg.setLocalTranslation(cam.getLocation());
            bulletg.setShadowMode(ShadowMode.CastAndReceive);
            bulletg.addControl(new RigidBodyControl(bulletCollisionShape, 1));
            bulletg.getControl(RigidBodyControl.class).setCcdMotionThreshold(0.1f);
            bulletg.getControl(RigidBodyControl.class).setLinearVelocity(cam.getDirection().mult(40));
            rootNode.attachChild(bulletg);
            getPhysicsSpace().add(bulletg);
        } else if (binding.equals("shoot2") && !value) {
            Geometry bulletg = new Geometry("bullet", bullet);
            bulletg.setMaterial(mat2);
            bulletg.setName("bullet");
            bulletg.setLocalTranslation(cam.getLocation());
            bulletg.setShadowMode(ShadowMode.CastAndReceive);
            bulletg.addControl(new RigidBodyControl(bulletCollisionShape, 1));
            bulletg.getControl(RigidBodyControl.class).setLinearVelocity(cam.getDirection().mult(40));
            rootNode.attachChild(bulletg);
            getPhysicsSpace().add(bulletg);
        }
    }
}
