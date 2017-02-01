

package clockworktest.bullet;

import com.clockwork.app.SimpleApplication;
import com.clockwork.asset.plugins.HttpZipLocator;
import com.clockwork.asset.plugins.ZipLocator;
import com.clockwork.bullet.BulletAppState;
import com.clockwork.bullet.PhysicsSpace;
import com.clockwork.bullet.collision.shapes.SphereCollisionShape;
import com.clockwork.bullet.control.RigidBodyControl;
import com.clockwork.bullet.objects.PhysicsCharacter;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.AmbientLight;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.MaterialList;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Node;
import com.clockwork.scene.plugins.ogre.OgreMeshKey;
import java.io.File;

public class TestQ3 extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private Node gameLevel;
    private PhysicsCharacter player;
    private Vector3f walkDirection = new Vector3f();
    private static boolean useHttp = false;
    private boolean left=false,right=false,up=false,down=false;

    public static void main(String[] args) {
        File file = new File("quake3level.zip");
        if (!file.exists()) {
            useHttp = true;
        }
        TestQ3 app = new TestQ3();
        app.start();
    }

    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        flyCam.setMoveSpeed(100);
        setupKeys();

        this.cam.setFrustumFar(2000);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White.clone().multLocal(2));
        dl.setDirection(new Vector3f(-1, -1, -1).normalize());
        rootNode.addLight(dl);

        AmbientLight am = new AmbientLight();
        am.setColor(ColorRGBA.White.mult(2));
        rootNode.addLight(am);

        // load the level from zip or http zip
        if (useHttp) {
            assetManager.registerLocator("http://jmonkeyengine.googlecode.com/files/quake3level.zip", HttpZipLocator.class);
        } else {
            assetManager.registerLocator("quake3level.zip", ZipLocator.class);
        }

        // create the geometry and attach it
        MaterialList matList = (MaterialList) assetManager.loadAsset("Scene.material");
        OgreMeshKey key = new OgreMeshKey("main.meshxml", matList);
        gameLevel = (Node) assetManager.loadAsset(key);
        gameLevel.setLocalScale(0.1f);

        // add a physics control, it will generate a MeshCollisionShape based on the gameLevel
        gameLevel.addControl(new RigidBodyControl(0));

        player = new PhysicsCharacter(new SphereCollisionShape(5), .01f);
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        player.setGravity(30);

        player.setPhysicsLocation(new Vector3f(60, 10, -60));

        rootNode.attachChild(gameLevel);

        getPhysicsSpace().addAll(gameLevel);
        getPhysicsSpace().add(player);
    }

    private PhysicsSpace getPhysicsSpace(){
        return bulletAppState.getPhysicsSpace();
    }

    @Override
    public void simpleUpdate(float tpf) {
        Vector3f camDir = cam.getDirection().clone().multLocal(0.6f);
        Vector3f camLeft = cam.getLeft().clone().multLocal(0.4f);
        walkDirection.set(0,0,0);
        if(left)
            walkDirection.addLocal(camLeft);
        if(right)
            walkDirection.addLocal(camLeft.negate());
        if(up)
            walkDirection.addLocal(camDir);
        if(down)
            walkDirection.addLocal(camDir.negate());
        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
    }

    private void setupKeys() {
        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this,"Lefts");
        inputManager.addListener(this,"Rights");
        inputManager.addListener(this,"Ups");
        inputManager.addListener(this,"Downs");
        inputManager.addListener(this,"Space");
    }

    public void onAction(String binding, boolean value, float tpf) {

        if (binding.equals("Lefts")) {
            if(value)
                left=true;
            else
                left=false;
        } else if (binding.equals("Rights")) {
            if(value)
                right=true;
            else
                right=false;
        } else if (binding.equals("Ups")) {
            if(value)
                up=true;
            else
                up=false;
        } else if (binding.equals("Downs")) {
            if(value)
                down=true;
            else
                down=false;
        } else if (binding.equals("Space")) {
            player.jump();
        }
    }
}
