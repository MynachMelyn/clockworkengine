
package clockworktest.post;

import com.clockwork.app.SimpleApplication;
import com.clockwork.asset.plugins.HttpZipLocator;
import com.clockwork.asset.plugins.ZipLocator;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.DirectionalLight;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.post.FilterPostProcessor;
import com.clockwork.post.filters.BloomFilter;
import com.clockwork.post.filters.ColorOverlayFilter;
import com.clockwork.post.ssao.SSAOFilter;
import com.clockwork.scene.Spatial;
import com.clockwork.util.SkyFactory;
import com.clockwork.water.WaterFilter;
import java.io.File;

public class TestMultiplesFilters extends SimpleApplication {

    private static boolean useHttp = false;

    public static void main(String[] args) {
        File file = new File("wildhouse.zip");
        if (!file.exists()) {
            useHttp = true;
        }
        TestMultiplesFilters app = new TestMultiplesFilters();
        app.start();
    }
    SSAOFilter ssaoFilter;
    FilterPostProcessor fpp;
    boolean en = true;

    public void simpleInitApp() {
        this.flyCam.setMoveSpeed(10);
        cam.setLocation(new Vector3f(6.0344796f, 1.5054002f, 55.572033f));
        cam.setRotation(new Quaternion(0.0016069f, 0.9810479f, -0.008143323f, 0.19358753f));

        // load sky
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));

        // create the geometry and attach it
        assetManager.registerLocator("wildhouse.zip", ZipLocator.class);
        Spatial scene = assetManager.loadModel("main.scene");


        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.4790551f, -0.39247334f, -0.7851566f));
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
        scene.addLight(sun);

        fpp = new FilterPostProcessor(assetManager);
      //  fpp.setNumSamples(4);
        ssaoFilter = new SSAOFilter(0.92f, 2.2f, 0.46f, 0.2f);
        final WaterFilter water=new WaterFilter(rootNode,new Vector3f(-0.4790551f, -0.39247334f, -0.7851566f));
        water.setWaterHeight(-20);
        SSAOUI ui=new SSAOUI(inputManager,ssaoFilter);
        final BloomFilter bloom = new BloomFilter();
        final ColorOverlayFilter overlay = new ColorOverlayFilter(ColorRGBA.LightGray);
        

        fpp.addFilter(ssaoFilter);
        
        fpp.addFilter(water);

        fpp.addFilter(bloom);

        fpp.addFilter(overlay);

        viewPort.addProcessor(fpp);

        rootNode.attachChild(scene);
        
        inputManager.addListener(new ActionListener() {

            public void onAction(String name, boolean isPressed, float tpf) {
                if ("toggleSSAO".equals(name) && isPressed) {
                    if (ssaoFilter.isEnabled()) {
                        ssaoFilter.setEnabled(false);
                    } else {
                        ssaoFilter.setEnabled(true);
                    }
                }
                if ("toggleWater".equals(name) && isPressed) {
                    if (water.isEnabled()) {
                        water.setEnabled(false);
                    } else {
                        water.setEnabled(true);
                    }
                }
                if ("toggleBloom".equals(name) && isPressed) {
                    if (bloom.isEnabled()) {
                        bloom.setEnabled(false);
                    } else {
                        bloom.setEnabled(true);
                    }
                }
                if ("toggleOverlay".equals(name) && isPressed) {
                    if (overlay.isEnabled()) {
                        overlay.setEnabled(false);
                    } else {
                        overlay.setEnabled(true);
                    }
                }
            }
        }, "toggleSSAO", "toggleBloom", "toggleWater","toggleOverlay");
        inputManager.addMapping("toggleSSAO", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("toggleWater", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("toggleBloom", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("toggleOverlay", new KeyTrigger(KeyInput.KEY_4));
        
    }
}
