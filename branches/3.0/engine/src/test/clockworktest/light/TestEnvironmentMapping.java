package clockworktest.light;

import com.clockwork.app.SimpleApplication;
import com.clockwork.asset.TextureKey;
import com.clockwork.input.ChaseCamera;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.Material;
import com.clockwork.math.Vector3f;
import com.clockwork.post.FilterPostProcessor;
import com.clockwork.post.filters.BloomFilter;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.texture.Texture;
import com.clockwork.util.SkyFactory;

/**
 * test
 */
public class TestEnvironmentMapping extends SimpleApplication {

    public static void main(String[] args) {
        TestEnvironmentMapping app = new TestEnvironmentMapping();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        final Node buggy = (Node) assetManager.loadModel("Models/Buggy/Buggy.j3o");

        TextureKey key = new TextureKey("Textures/Sky/Bright/BrightSky.dds", true);
        key.setGenerateMips(true);
        key.setAsCube(true);
        final Texture tex = assetManager.loadTexture(key);

        for (Spatial geom : buggy.getChildren()) {
            if (geom instanceof Geometry) {
                Material m = ((Geometry) geom).getMaterial();
                m.setTexture("EnvMap", tex);
                m.setVector3("FresnelParams", new Vector3f(0.05f, 0.18f, 0.11f));
            }
        }

        flyCam.setEnabled(false);

        ChaseCamera chaseCam = new ChaseCamera(cam, inputManager);
        chaseCam.setLookAtOffset(new Vector3f(0,0.5f,-1.0f));
        buggy.addControl(chaseCam);
        rootNode.attachChild(buggy);
        rootNode.attachChild(SkyFactory.createSky(assetManager, tex, false));

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        BloomFilter bf = new BloomFilter(BloomFilter.GlowMode.Objects);
        bf.setBloomIntensity(2.3f);
        bf.setExposurePower(0.6f);
        
        fpp.addFilter(bf);
        
        DirectionalLight l = new DirectionalLight();
        l.setDirection(new Vector3f(0, -1, -1));
        rootNode.addLight(l);
        
        viewPort.addProcessor(fpp);
    }
}
