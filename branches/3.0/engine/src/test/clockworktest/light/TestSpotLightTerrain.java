
package clockworktest.light;

import com.clockwork.app.SimpleApplication;
import com.clockwork.bounding.BoundingBox;
import com.clockwork.font.BitmapText;
import com.clockwork.light.AmbientLight;
import com.clockwork.light.PointLight;
import com.clockwork.light.SpotLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.FastMath;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Spatial;
import com.clockwork.terrain.geomipmap.TerrainLodControl;
import com.clockwork.terrain.geomipmap.TerrainQuad;
import com.clockwork.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.clockwork.terrain.heightmap.AbstractHeightMap;
import com.clockwork.terrain.heightmap.ImageBasedHeightMap;
import com.clockwork.texture.Texture;
import com.clockwork.texture.Texture.WrapMode;
import com.clockwork.util.SkyFactory;

/**
 * Uses the terrain's lighting texture with normal maps and lights.
 *
 */
public class TestSpotLightTerrain extends SimpleApplication {

    private TerrainQuad terrain;
    Material matTerrain;
    Material matWire;
    boolean wireframe = false;
    boolean triPlanar = false;
    boolean wardiso = false;
    boolean minnaert = false;
    protected BitmapText hintText;
    PointLight pl;
    Geometry lightMdl;
    private float grassScale = 64;
    private float dirtScale = 16;
    private float rockScale = 128;
    SpotLight sl;

    public static void main(String[] args) {
        TestSpotLightTerrain app = new TestSpotLightTerrain();
        app.start();
    }

  
    @Override
    public void simpleInitApp() {  
        makeTerrain();
        flyCam.setMoveSpeed(50);

        sl = new SpotLight();
        sl.setSpotRange(100);
        sl.setSpotOuterAngle(20 * FastMath.DEG_TO_RAD);
        sl.setSpotInnerAngle(15 * FastMath.DEG_TO_RAD);
        sl.setDirection(new Vector3f(-0.39820394f, -0.73094344f, 0.55421597f));
        sl.setPosition(new Vector3f(-64.61567f, -87.615425f, -202.41328f));
        rootNode.addLight(sl);

        AmbientLight ambLight = new AmbientLight();
        ambLight.setColor(new ColorRGBA(0.8f, 0.8f, 0.8f, 0.2f));
        rootNode.addLight(ambLight);

        cam.setLocation(new Vector3f(-41.219646f, -84.8363f, -171.67267f));
        cam.setRotation(new Quaternion(-0.04562731f, 0.89917684f, -0.09668826f, -0.4243236f));
        sl.setDirection(cam.getDirection());
        sl.setPosition(cam.getLocation());

    }
    
      @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        sl.setDirection(cam.getDirection());
        sl.setPosition(cam.getLocation());

    }

    private void makeTerrain() {
        // TERRAIN TEXTURE material
        matTerrain = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
        matTerrain.setBoolean("useTriPlanarMapping", false);
        matTerrain.setBoolean("WardIso", true);

        // ALPHA map (for splat textures)
        matTerrain.setTexture("AlphaMap", assetManager.loadTexture("Textures/Terrain/splat/alpha1.png"));
        matTerrain.setTexture("AlphaMap_1", assetManager.loadTexture("Textures/Terrain/splat/alpha2.png"));

        // HEIGHTMAP image (for the terrain heightmap)
        Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");


        // GRASS texture
        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        grass.setWrap(WrapMode.Repeat);
        matTerrain.setTexture("DiffuseMap", grass);
        matTerrain.setFloat("DiffuseMap_0_scale", grassScale);

        // DIRT texture
        Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        matTerrain.setTexture("DiffuseMap_1", dirt);
        matTerrain.setFloat("DiffuseMap_1_scale", dirtScale);

        // ROCK texture
        Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
        rock.setWrap(WrapMode.Repeat);
        matTerrain.setTexture("DiffuseMap_2", rock);
        matTerrain.setFloat("DiffuseMap_2_scale", rockScale);

        // BRICK texture
        Texture brick = assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
        brick.setWrap(WrapMode.Repeat);
        matTerrain.setTexture("DiffuseMap_3", brick);
        matTerrain.setFloat("DiffuseMap_3_scale", rockScale);

        // RIVER ROCK texture
        Texture riverRock = assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg");
        riverRock.setWrap(WrapMode.Repeat);
        matTerrain.setTexture("DiffuseMap_4", riverRock);
        matTerrain.setFloat("DiffuseMap_4_scale", rockScale);


        Texture normalMap0 = assetManager.loadTexture("Textures/Terrain/splat/grass_normal.jpg");
        normalMap0.setWrap(WrapMode.Repeat);
        Texture normalMap1 = assetManager.loadTexture("Textures/Terrain/splat/dirt_normal.png");
        normalMap1.setWrap(WrapMode.Repeat);
        Texture normalMap2 = assetManager.loadTexture("Textures/Terrain/splat/road_normal.png");
        normalMap2.setWrap(WrapMode.Repeat);
        matTerrain.setTexture("NormalMap", normalMap0);
        matTerrain.setTexture("NormalMap_1", normalMap2);
        matTerrain.setTexture("NormalMap_2", normalMap2);
        matTerrain.setTexture("NormalMap_4", normalMap2);

        // WIREFRAME material
        matWire = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matWire.getAdditionalRenderState().setWireframe(true);
        matWire.setColor("Color", ColorRGBA.Green);

        createSky();

        // CREATE HEIGHTMAP
        AbstractHeightMap heightmap = null;
        try {
            //heightmap = new HillHeightMap(1025, 1000, 50, 100, (byte) 3);

            heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 1f);
            heightmap.load();

        } catch (Exception e) {
            e.printStackTrace();
        }

        terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());//, new LodPerspectiveCalculatorFactory(getCamera(), 4)); // add this in to see it use entropy for LOD calculations
        TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
        control.setLodCalculator( new DistanceLodCalculator(65, 2.7f) );
        terrain.addControl(control);
        terrain.setMaterial(matTerrain);
        terrain.setModelBound(new BoundingBox());
        terrain.updateModelBound();
        terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(1f, 1f, 1f);
        rootNode.attachChild(terrain);
    }

    private void createSky() {
        Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
        Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
        Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
        Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
        Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
        Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");

        Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        rootNode.attachChild(sky);
    }

  
}
