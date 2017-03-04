package clockworktest.terrain;

import com.clockwork.app.SimpleApplication;
import com.clockwork.app.state.ScreenshotAppState;
import com.clockwork.asset.plugins.HttpZipLocator;
import com.clockwork.asset.plugins.ZipLocator;
import com.clockwork.bullet.BulletAppState;
import com.clockwork.bullet.collision.shapes.CapsuleCollisionShape;
import com.clockwork.bullet.collision.shapes.HeightfieldCollisionShape;
import com.clockwork.bullet.control.CharacterControl;
import com.clockwork.bullet.control.RigidBodyControl;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector3f;
import com.clockwork.terrain.geomipmap.TerrainGrid;
import com.clockwork.terrain.geomipmap.TerrainGridListener;
import com.clockwork.terrain.geomipmap.TerrainGridLodControl;
import com.clockwork.terrain.geomipmap.TerrainLodControl;
import com.clockwork.terrain.geomipmap.TerrainQuad;
import com.clockwork.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import java.io.File;

public class TerrainGridSerialisationTest extends SimpleApplication {

    private TerrainGrid terrain;
    private boolean usePhysics = true;

    public static void main(final String[] args) {
        TerrainGridSerialisationTest app = new TerrainGridSerialisationTest();
        app.start();
    }
    private CharacterControl player3;

    @Override
    public void simpleInitApp() {
        File file = new File("TerrainGridTestData.zip");
        if (!file.exists()) {
            assetManager.registerLocator("http://jmonkeyengine.googlecode.com/files/TerrainGridTestData.zip", HttpZipLocator.class);
        } else {
            assetManager.registerLocator("TerrainGridTestData.zip", ZipLocator.class);
        }

        this.flyCam.setMoveSpeed(100f);
        ScreenshotAppState state = new ScreenshotAppState();
        this.stateManager.attach(state);

        this.terrain= (TerrainGrid) assetManager.loadModel("TerrainGrid/TerrainGrid.j3o");
        
        this.rootNode.attachChild(this.terrain);
        
        TerrainLodControl control = new TerrainGridLodControl(this.terrain, getCamera());
        control.setLodCalculator( new DistanceLodCalculator(65, 2.7f) ); // patch size, and a multiplier
        this.terrain.addControl(control);
        
        final BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        this.getCamera().setLocation(new Vector3f(0, 256, 0));

        this.viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));

        if (usePhysics) {
            CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.5f, 1.8f, 1);
            player3 = new CharacterControl(capsuleShape, 0.5f);
            player3.setJumpSpeed(20);
            player3.setFallSpeed(10);
            player3.setGravity(10);

            player3.setPhysicsLocation(new Vector3f(cam.getLocation().x, 256, cam.getLocation().z));

            bulletAppState.getPhysicsSpace().add(player3);

            terrain.addListener(new TerrainGridListener() {

                public void gridMoved(Vector3f newCenter) {
                }

                public void tileAttached(Vector3f cell, TerrainQuad quad) {
                    //workaround for bugged test j3o's
                    while(quad.getControl(RigidBodyControl.class)!=null){
                        quad.removeControl(RigidBodyControl.class);
                    }
                    quad.addControl(new RigidBodyControl(new HeightfieldCollisionShape(quad.getHeightMap(), terrain.getLocalScale()), 0));
                    bulletAppState.getPhysicsSpace().add(quad);
                }

                public void tileDetached(Vector3f cell, TerrainQuad quad) {
                    if (quad.getControl(RigidBodyControl.class) != null) {
                        bulletAppState.getPhysicsSpace().remove(quad);
                        quad.removeControl(RigidBodyControl.class);
                    }
                }

            });
        }
        
        this.initKeys();
    }

    private void initKeys() {
        // You can map one or several inputs to one named action
        this.inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_A));
        this.inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_D));
        this.inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_W));
        this.inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_S));
        this.inputManager.addMapping("Jumps", new KeyTrigger(KeyInput.KEY_SPACE));
        this.inputManager.addListener(this.actionListener, "Lefts");
        this.inputManager.addListener(this.actionListener, "Rights");
        this.inputManager.addListener(this.actionListener, "Ups");
        this.inputManager.addListener(this.actionListener, "Downs");
        this.inputManager.addListener(this.actionListener, "Jumps");
    }
    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;
    private final ActionListener actionListener = new ActionListener() {

        @Override
        public void onAction(final String name, final boolean keyPressed, final float tpf) {
            if (name.equals("Lefts")) {
                if (keyPressed) {
                    TerrainGridSerialisationTest.this.left = true;
                } else {
                    TerrainGridSerialisationTest.this.left = false;
                }
            } else if (name.equals("Rights")) {
                if (keyPressed) {
                    TerrainGridSerialisationTest.this.right = true;
                } else {
                    TerrainGridSerialisationTest.this.right = false;
                }
            } else if (name.equals("Ups")) {
                if (keyPressed) {
                    TerrainGridSerialisationTest.this.up = true;
                } else {
                    TerrainGridSerialisationTest.this.up = false;
                }
            } else if (name.equals("Downs")) {
                if (keyPressed) {
                    TerrainGridSerialisationTest.this.down = true;
                } else {
                    TerrainGridSerialisationTest.this.down = false;
                }
            } else if (name.equals("Jumps")) {
                TerrainGridSerialisationTest.this.player3.jump();
            }
        }
    };
    private final Vector3f walkDirection = new Vector3f();

    @Override
    public void simpleUpdate(final float tpf) {
        Vector3f camDir = this.cam.getDirection().clone().multLocal(0.6f);
        Vector3f camLeft = this.cam.getLeft().clone().multLocal(0.4f);
        this.walkDirection.set(0, 0, 0);
        if (this.left) {
            this.walkDirection.addLocal(camLeft);
        }
        if (this.right) {
            this.walkDirection.addLocal(camLeft.negate());
        }
        if (this.up) {
            this.walkDirection.addLocal(camDir);
        }
        if (this.down) {
            this.walkDirection.addLocal(camDir.negate());
        }

        if (usePhysics) {
            this.player3.setWalkDirection(this.walkDirection);
            this.cam.setLocation(this.player3.getPhysicsLocation());
        }
    }
}
