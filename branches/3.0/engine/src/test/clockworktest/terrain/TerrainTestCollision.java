
package clockworktest.terrain;

import com.clockwork.app.SimpleApplication;
import com.clockwork.bounding.BoundingBox;
import com.clockwork.bullet.BulletAppState;
import com.clockwork.bullet.collision.shapes.SphereCollisionShape;
import com.clockwork.bullet.control.RigidBodyControl;
import com.clockwork.collision.CollisionResult;
import com.clockwork.collision.CollisionResults;
import com.clockwork.font.BitmapText;
import com.clockwork.input.KeyInput;
import com.clockwork.input.MouseInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.input.controls.MouseButtonTrigger;
import com.clockwork.light.DirectionalLight;
import com.clockwork.light.PointLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Ray;
import com.clockwork.math.Vector2f;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.shape.Box;
import com.clockwork.scene.shape.Sphere;
import com.clockwork.terrain.geomipmap.TerrainLodControl;
import com.clockwork.terrain.geomipmap.TerrainQuad;
import com.clockwork.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.clockwork.terrain.heightmap.AbstractHeightMap;
import com.clockwork.terrain.heightmap.ImageBasedHeightMap;
import com.clockwork.texture.Texture;
import com.clockwork.texture.Texture.WrapMode;

/**
 * Creates a terrain object and a collision node to go with it. Then
 * drops several balls from the sky that collide with the terrain
 * and roll around.
 * Left click to place a sphere on the ground where the crosshairs intersect the terrain.
 * Hit keys 1 or 2 to raise/lower the terrain at that spot.
 *
 */
public class TerrainTestCollision extends SimpleApplication {

    TerrainQuad terrain;
    Node terrainPhysicsNode;
    Material matRock;
    Material matWire;
    boolean wireframe = false;
    protected BitmapText hintText;
    PointLight pl;
    Geometry lightMdl;
    Geometry collisionMarker;
    private BulletAppState bulletAppState;
    Geometry collisionSphere;
    Geometry collisionBox;
    Geometry selectedCollisionObject;

    public static void main(String[] args) {
        TerrainTestCollision app = new TerrainTestCollision();
        app.start();
    }

    @Override
    public void initialize() {
        super.initialize();
        loadHintText();
        initCrossHairs();
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        setupKeys();
        matRock = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
        matRock.setTexture("Alpha", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));
        Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");
        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        grass.setWrap(WrapMode.Repeat);
        matRock.setTexture("Tex1", grass);
        matRock.setFloat("Tex1Scale", 64f);
        Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        matRock.setTexture("Tex2", dirt);
        matRock.setFloat("Tex2Scale", 32f);
        Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
        rock.setWrap(WrapMode.Repeat);
        matRock.setTexture("Tex3", rock);
        matRock.setFloat("Tex3Scale", 128f);
        matWire = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matWire.getAdditionalRenderState().setWireframe(true);
        matWire.setColor("Color", ColorRGBA.Green);
        AbstractHeightMap heightmap = null;
        try {
            heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 0.25f);
            heightmap.load();

        } catch (Exception e) {
        }

        terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());
        TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
        control.setLodCalculator( new DistanceLodCalculator(65, 2.7f) ); // patch size, and a multiplier
        terrain.addControl(control);
        terrain.setMaterial(matRock);
        terrain.setLocalScale(new Vector3f(2, 2, 2));
        terrain.setLocked(false); // unlock it so we can edit the height
        rootNode.attachChild(terrain);


        /**
         * Create PhysicsRigidBodyControl for collision
         */
        terrain.addControl(new RigidBodyControl(0));
        bulletAppState.getPhysicsSpace().addAll(terrain);


        // Add 5 physics spheres to the world, with random sizes and positions
        // let them drop from the sky
        for (int i = 0; i < 5; i++) {
            float r = (float) (8 * Math.random());
            Geometry sphere = new Geometry("cannonball", new Sphere(10, 10, r));
            sphere.setMaterial(matWire);
            float x = (float) (20 * Math.random()) - 40; // random position
            float y = (float) (20 * Math.random()) - 40; // random position
            float z = (float) (20 * Math.random()) - 40; // random position
            sphere.setLocalTranslation(new Vector3f(x, 100 + y, z));
            sphere.addControl(new RigidBodyControl(new SphereCollisionShape(r), 2));
            rootNode.attachChild(sphere);
            bulletAppState.getPhysicsSpace().add(sphere);
        }

        collisionBox = new Geometry("collisionBox", new Box(2, 2, 2));
        collisionBox.setModelBound(new BoundingBox());
        collisionBox.setLocalTranslation(new Vector3f(20, 95, 30));
        collisionBox.setMaterial(matWire);
        rootNode.attachChild(collisionBox);
        selectedCollisionObject = collisionBox;

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(1, -0.5f, -0.1f).normalizeLocal());
        dl.setColor(new ColorRGBA(0.50f, 0.40f, 0.50f, 1.0f));
        rootNode.addLight(dl);

        cam.setLocation(new Vector3f(0, 25, -10));
        cam.lookAtDirection(new Vector3f(0, -1, 0).normalizeLocal(), Vector3f.UNIT_Y);
    }

    public void loadHintText() {
        hintText = new BitmapText(guiFont, false);
        hintText.setSize(guiFont.getCharSet().getRenderedSize());
        hintText.setLocalTranslation(0, getCamera().getHeight(), 0);
        //hintText.setText("Hit T to switch to wireframe");
        hintText.setText("");
        guiNode.attachChild(hintText);
    }

    protected void initCrossHairs() {
        //guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
                settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
                settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }

    private void setupKeys() {
        flyCam.setMoveSpeed(50);
        inputManager.addMapping("wireframe", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(actionListener, "wireframe");
        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Forwards", new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addMapping("Backs", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addListener(actionListener, "Lefts");
        inputManager.addListener(actionListener, "Rights");
        inputManager.addListener(actionListener, "Ups");
        inputManager.addListener(actionListener, "Downs");
        inputManager.addListener(actionListener, "Forwards");
        inputManager.addListener(actionListener, "Backs");
        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "shoot");
        inputManager.addMapping("cameraDown", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(actionListener, "cameraDown");
    }

    @Override
    public void update() {
        super.update();
    }

    private void createCollisionMarker() {
        Sphere s = new Sphere(6, 6, 1);
        collisionMarker = new Geometry("collisionMarker");
        collisionMarker.setMesh(s);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Orange);
        collisionMarker.setMaterial(mat);
        rootNode.attachChild(collisionMarker);
    }
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String binding, boolean keyPressed, float tpf) {
            if (binding.equals("wireframe") && !keyPressed) {
                wireframe = !wireframe;
                if (!wireframe) {
                    terrain.setMaterial(matWire);
                } else {
                    terrain.setMaterial(matRock);
                }
            } else if (binding.equals("shoot") && !keyPressed) {

                Vector3f origin = cam.getWorldCoordinates(new Vector2f(settings.getWidth() / 2, settings.getHeight() / 2), 0.0f);
                Vector3f direction = cam.getWorldCoordinates(new Vector2f(settings.getWidth() / 2, settings.getHeight() / 2), 0.3f);
                direction.subtractLocal(origin).normalizeLocal();


                Ray ray = new Ray(origin, direction);
                CollisionResults results = new CollisionResults();
                int numCollisions = terrain.collideWith(ray, results);
                if (numCollisions > 0) {
                    CollisionResult hit = results.getClosestCollision();
                    if (collisionMarker == null) {
                        createCollisionMarker();
                    }
                    Vector2f loc = new Vector2f(hit.getContactPoint().x, hit.getContactPoint().z);
                    float height = terrain.getHeight(loc);
                    System.out.println("collide " + hit.getContactPoint() + ", height: " + height + ", distance: " + hit.getDistance());
                    collisionMarker.setLocalTranslation(new Vector3f(hit.getContactPoint().x, height, hit.getContactPoint().z));
                }
            } else if (binding.equals("cameraDown") && !keyPressed) {
                getCamera().lookAtDirection(new Vector3f(0, -1, 0), Vector3f.UNIT_Y);
            } else if (binding.equals("Lefts") && !keyPressed) {
                Vector3f oldLoc = selectedCollisionObject.getLocalTranslation().clone();
                selectedCollisionObject.move(-0.5f, 0, 0);
                testCollision(oldLoc);
            } else if (binding.equals("Rights") && !keyPressed) {
                Vector3f oldLoc = selectedCollisionObject.getLocalTranslation().clone();
                selectedCollisionObject.move(0.5f, 0, 0);
                testCollision(oldLoc);
            } else if (binding.equals("Forwards") && !keyPressed) {
                Vector3f oldLoc = selectedCollisionObject.getLocalTranslation().clone();
                selectedCollisionObject.move(0, 0, 0.5f);
                testCollision(oldLoc);
            } else if (binding.equals("Backs") && !keyPressed) {
                Vector3f oldLoc = selectedCollisionObject.getLocalTranslation().clone();
                selectedCollisionObject.move(0, 0, -0.5f);
                testCollision(oldLoc);
            } else if (binding.equals("Ups") && !keyPressed) {
                Vector3f oldLoc = selectedCollisionObject.getLocalTranslation().clone();
                selectedCollisionObject.move(0, 0.5f, 0);
                testCollision(oldLoc);
            } else if (binding.equals("Downs") && !keyPressed) {
                Vector3f oldLoc = selectedCollisionObject.getLocalTranslation().clone();
                selectedCollisionObject.move(0, -0.5f, 0);
                testCollision(oldLoc);
            }

        }
    };

    private void testCollision(Vector3f oldLoc) {
        if (terrain.collideWith(selectedCollisionObject.getWorldBound(), new CollisionResults()) > 0) {
            selectedCollisionObject.setLocalTranslation(oldLoc);
        }
    }
}
