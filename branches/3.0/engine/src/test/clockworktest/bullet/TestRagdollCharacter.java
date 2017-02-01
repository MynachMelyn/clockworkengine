
package clockworktest.bullet;

import com.clockwork.animation.AnimChannel;
import com.clockwork.animation.AnimControl;
import com.clockwork.animation.AnimEventListener;
import com.clockwork.animation.LoopMode;
import com.clockwork.app.SimpleApplication;
import com.clockwork.asset.TextureKey;
import com.clockwork.bullet.BulletAppState;
import com.clockwork.bullet.PhysicsSpace;
import com.clockwork.bullet.control.KinematicRagdollControl;
import com.clockwork.bullet.control.RigidBodyControl;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector2f;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.queue.RenderQueue.ShadowMode;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.shape.Box;
import com.clockwork.texture.Texture;

/**
 */
public class TestRagdollCharacter extends SimpleApplication implements AnimEventListener, ActionListener {

    BulletAppState bulletAppState;
    Node model;
    KinematicRagdollControl ragdoll;
    boolean leftStrafe = false, rightStrafe = false, forward = false, backward = false,
            leftRotate = false, rightRotate = false;
    AnimControl animControl;
    AnimChannel animChannel;

    public static void main(String[] args) {
        TestRagdollCharacter app = new TestRagdollCharacter();
        app.start();
    }

    public void simpleInitApp() {
        setupKeys();

        bulletAppState = new BulletAppState();
        bulletAppState.setEnabled(true);
        stateManager.attach(bulletAppState);


//        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        PhysicsTestHelper.createPhysicsTestWorld(rootNode, assetManager, bulletAppState.getPhysicsSpace());
        initWall(2,1,1);
        setupLight();

        cam.setLocation(new Vector3f(-8,0,-4));
        cam.lookAt(new Vector3f(4,0,-7), Vector3f.UNIT_Y);

        model = (Node) assetManager.loadModel("Models/Sinbad/Sinbad.mesh.xml");
        model.lookAt(new Vector3f(0,0,-1), Vector3f.UNIT_Y);
        model.setLocalTranslation(4, 0, -7f);

        ragdoll = new KinematicRagdollControl(0.5f);
        model.addControl(ragdoll);

        getPhysicsSpace().add(ragdoll);
        speed = 1.3f;

        rootNode.attachChild(model);


        AnimControl control = model.getControl(AnimControl.class);
        animChannel = control.createChannel();
        animChannel.setAnim("IdleTop");
        control.addListener(this);

    }

    private void setupLight() {
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f, -0.7f, -1).normalizeLocal());
        dl.setColor(new ColorRGBA(1f, 1f, 1f, 1.0f));
        rootNode.addLight(dl);
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    private void setupKeys() {
        inputManager.addMapping("Rotate Left",
                new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("Rotate Right",
                new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("Walk Forward",
                new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("Walk Backward",
                new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Slice",
                new KeyTrigger(KeyInput.KEY_SPACE),
                new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "Strafe Left", "Strafe Right");
        inputManager.addListener(this, "Rotate Left", "Rotate Right");
        inputManager.addListener(this, "Walk Forward", "Walk Backward");
        inputManager.addListener(this, "Slice");
    }

    public void initWall(float bLength, float bWidth, float bHeight) {
        Box brick = new Box(Vector3f.ZERO, bLength, bHeight, bWidth);
        brick.scaleTextureCoordinates(new Vector2f(1f, .5f));
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key = new TextureKey("Textures/Terrain/BrickWall/BrickWall.jpg");
        key.setGenerateMips(true);
        Texture tex = assetManager.loadTexture(key);
        mat2.setTexture("ColorMap", tex);
        
        float startpt = bLength / 4;
        float height = -5;
        for (int j = 0; j < 15; j++) {
            for (int i = 0; i < 4; i++) {
                Vector3f ori = new Vector3f(i * bLength * 2 + startpt, bHeight + height, -10);
                Geometry reBoxg = new Geometry("brick", brick);
                reBoxg.setMaterial(mat2);
                reBoxg.setLocalTranslation(ori);
                //for geometry with sphere mesh the physics system automatically uses a sphere collision shape
                reBoxg.addControl(new RigidBodyControl(1.5f));
                reBoxg.setShadowMode(ShadowMode.CastAndReceive);
                reBoxg.getControl(RigidBodyControl.class).setFriction(0.6f);
                this.rootNode.attachChild(reBoxg);
                this.getPhysicsSpace().add(reBoxg);
            }
            startpt = -startpt;
            height += 2 * bHeight;
        }
    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {

        if (channel.getAnimationName().equals("SliceHorizontal")) {
            channel.setLoopMode(LoopMode.DontLoop);
            channel.setAnim("IdleTop", 5);
            channel.setLoopMode(LoopMode.Loop);
        }

    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }
    
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Rotate Left")) {
            if (value) {
                leftRotate = true;
            } else {
                leftRotate = false;
            }
        } else if (binding.equals("Rotate Right")) {
            if (value) {
                rightRotate = true;
            } else {
                rightRotate = false;
            }
        } else if (binding.equals("Walk Forward")) {
            if (value) {
                forward = true;
            } else {
                forward = false;
            }
        } else if (binding.equals("Walk Backward")) {
            if (value) {
                backward = true;
            } else {
                backward = false;
            }
        } else if (binding.equals("Slice")) {
            if (value) {
                animChannel.setAnim("SliceHorizontal");
                animChannel.setSpeed(0.3f);
            }
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        if(forward){
            model.move(model.getLocalRotation().multLocal(new Vector3f(0,0,1)).multLocal(tpf));
        }else if(backward){
            model.move(model.getLocalRotation().multLocal(new Vector3f(0,0,1)).multLocal(-tpf));
        }else if(leftRotate){
            model.rotate(0, tpf, 0);
        }else if(rightRotate){
            model.rotate(0, -tpf, 0);
        }
        fpsText.setText(cam.getLocation() + "/" + cam.getRotation());
    }

}
