
package clockworktest.bullet;

import com.clockwork.app.SimpleApplication;
import com.clockwork.bullet.BulletAppState;
import com.clockwork.bullet.PhysicsSpace;
import com.clockwork.bullet.collision.shapes.MeshCollisionShape;
import com.clockwork.bullet.control.BetterCharacterControl;
import com.clockwork.bullet.control.RigidBodyControl;
import com.clockwork.bullet.debug.DebugTools;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.material.Material;
import com.clockwork.math.FastMath;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.RenderManager;
import com.clockwork.scene.CameraNode;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.control.CameraControl.ControlDirection;
import com.clockwork.scene.shape.Sphere;
import com.clockwork.system.AppSettings;

/**
 * A walking physical character followed by a 3rd person camera. (No animation.)
 *
 * 
 */
public class TestBetterCharacter extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private BetterCharacterControl physicsCharacter;
    private Node characterNode;
    private CameraNode camNode;
    boolean rotate = false;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private Vector3f viewDirection = new Vector3f(0, 0, 1);
    boolean leftStrafe = false, rightStrafe = false, forward = false, backward = false,
            leftRotate = false, rightRotate = false;
    private Vector3f normalGravity = new Vector3f(0, -9.81f, 0);
    private Geometry planet;

    public static void main(String[] args) {
        TestBetterCharacter app = new TestBetterCharacter();
        AppSettings settings = new AppSettings(true);
        settings.setRenderer(AppSettings.LWJGL_OPENGL2);
        settings.setAudioRenderer(AppSettings.LWJGL_OPENAL);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //setup keyboard mapping
        setupKeys();

        // activate physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(true);

        // init a physics test scene
        PhysicsTestHelper.createPhysicsTestWorldSoccer(rootNode, assetManager, bulletAppState.getPhysicsSpace());
        PhysicsTestHelper.createBallShooter(this, rootNode, bulletAppState.getPhysicsSpace());
        setupPlanet();

        // Create a node for the character model
        characterNode = new Node("character node");
        characterNode.setLocalTranslation(new Vector3f(4, 5, 2));

        // Add a character control to the node so we can add other things and
        // control the model rotation
        physicsCharacter = new BetterCharacterControl(0.3f, 2.5f, 8f);
        characterNode.addControl(physicsCharacter);
        getPhysicsSpace().add(physicsCharacter);

        // Load model, attach to character node
        Node model = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");
        model.setLocalScale(1.50f);
        characterNode.attachChild(model);

        // Add character node to the rootNode
        rootNode.attachChild(characterNode);

        // Set forward camera node that follows the character, only used when
        // view is "locked"
        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(0, 2, -6));
        Quaternion quat = new Quaternion();
        // These coordinates are local, the camNode is attached to the character node!
        quat.lookAt(Vector3f.UNIT_Z, Vector3f.UNIT_Y);
        camNode.setLocalRotation(quat);
        characterNode.attachChild(camNode);
        // Disable by default, can be enabled via keyboard shortcut
        camNode.setEnabled(false);
    }

    @Override
    public void simpleUpdate(float tpf) {
        // Apply planet gravity to character if close enough (see below)
        checkPlanetGravity();

        // Get current forward and left vectors of model by using its rotation
        // to rotate the unit vectors
        Vector3f modelForwardDir = characterNode.getWorldRotation().mult(Vector3f.UNIT_Z);
        Vector3f modelLeftDir = characterNode.getWorldRotation().mult(Vector3f.UNIT_X);

        // WalkDirection is global!
        // You *can* make your character fly with this.
        walkDirection.set(0, 0, 0);
        if (leftStrafe) {
            walkDirection.addLocal(modelLeftDir.mult(3));
        } else if (rightStrafe) {
            walkDirection.addLocal(modelLeftDir.negate().multLocal(3));
        }
        if (forward) {
            walkDirection.addLocal(modelForwardDir.mult(3));
        } else if (backward) {
            walkDirection.addLocal(modelForwardDir.negate().multLocal(3));
        }
        physicsCharacter.setWalkDirection(walkDirection);

        // ViewDirection is local to characters physics system!
        // The final world rotation depends on the gravity and on the state of
        // setApplyPhysicsLocal()
        if (leftRotate) {
            Quaternion rotateL = new Quaternion().fromAngleAxis(FastMath.PI * tpf, Vector3f.UNIT_Y);
            rotateL.multLocal(viewDirection);
        } else if (rightRotate) {
            Quaternion rotateR = new Quaternion().fromAngleAxis(-FastMath.PI * tpf, Vector3f.UNIT_Y);
            rotateR.multLocal(viewDirection);
        }
        physicsCharacter.setViewDirection(viewDirection);
        fpsText.setText("Touch da ground = " + physicsCharacter.isOnGround());
        if (!lockView) {
            cam.lookAt(characterNode.getWorldTranslation().add(new Vector3f(0, 2, 0)), Vector3f.UNIT_Y);
        }
    }

    private void setupPlanet() {
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));
        //immovable sphere with mesh collision shape
        Sphere sphere = new Sphere(64, 64, 20);
        planet = new Geometry("Sphere", sphere);
        planet.setMaterial(material);
        planet.setLocalTranslation(30, -15, 30);
        planet.addControl(new RigidBodyControl(new MeshCollisionShape(sphere), 0));
        rootNode.attachChild(planet);
        getPhysicsSpace().add(planet);
    }

    private void checkPlanetGravity() {
        Vector3f planetDist = planet.getWorldTranslation().subtract(characterNode.getWorldTranslation());
        if (planetDist.length() < 24) {
            physicsCharacter.setGravity(planetDist.normalizeLocal().multLocal(9.81f));
        } else {
            physicsCharacter.setGravity(normalGravity);
        }
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Strafe Left")) {
            if (value) {
                leftStrafe = true;
            } else {
                leftStrafe = false;
            }
        } else if (binding.equals("Strafe Right")) {
            if (value) {
                rightStrafe = true;
            } else {
                rightStrafe = false;
            }
        } else if (binding.equals("Rotate Left")) {
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
        } else if (binding.equals("Jump")) {
            physicsCharacter.jump();
        } else if (binding.equals("Duck")) {
            if (value) {
                physicsCharacter.setDucked(true);
            } else {
                physicsCharacter.setDucked(false);
            }
        } else if (binding.equals("Lock View")) {
            if (value && lockView) {
                lockView = false;
            } else if (value && !lockView) {
                lockView = true;
            }
            flyCam.setEnabled(!lockView);
            camNode.setEnabled(lockView);
        }
    }
    private boolean lockView = false;

    private void setupKeys() {
        inputManager.addMapping("Strafe Left",
                new KeyTrigger(KeyInput.KEY_U),
                new KeyTrigger(KeyInput.KEY_Z));
        inputManager.addMapping("Strafe Right",
                new KeyTrigger(KeyInput.KEY_O),
                new KeyTrigger(KeyInput.KEY_X));
        inputManager.addMapping("Rotate Left",
                new KeyTrigger(KeyInput.KEY_J),
                new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Rotate Right",
                new KeyTrigger(KeyInput.KEY_L),
                new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Walk Forward",
                new KeyTrigger(KeyInput.KEY_I),
                new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Walk Backward",
                new KeyTrigger(KeyInput.KEY_K),
                new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("Jump",
                new KeyTrigger(KeyInput.KEY_F),
                new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Duck",
                new KeyTrigger(KeyInput.KEY_G),
                new KeyTrigger(KeyInput.KEY_LSHIFT),
                new KeyTrigger(KeyInput.KEY_RSHIFT));
        inputManager.addMapping("Lock View",
                new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "Strafe Left", "Strafe Right");
        inputManager.addListener(this, "Rotate Left", "Rotate Right");
        inputManager.addListener(this, "Walk Forward", "Walk Backward");
        inputManager.addListener(this, "Jump", "Duck", "Lock View");
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }
}
