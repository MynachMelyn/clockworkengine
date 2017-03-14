

package clockworktest.post;

import com.clockwork.app.SimpleApplication;
import com.clockwork.asset.plugins.HttpZipLocator;
import com.clockwork.asset.plugins.ZipLocator;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.AnalogListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.DirectionalLight;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.post.FilterPostProcessor;
import com.clockwork.post.filters.FogFilter;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.util.SkyFactory;
import java.io.File;

public class TestFog extends SimpleApplication {

    private FilterPostProcessor fpp;
    private boolean enabled=true;
    private FogFilter fog;

    // set default for applets
    private static boolean useHttp = true;

    public static void main(String[] args) {
        File file = new File("wildhouse.zip");
        if (file.exists()) {
            useHttp = false;
        }
        TestFog app = new TestFog();
        app.start();
    }

    public void simpleInitApp() {
        this.flyCam.setMoveSpeed(10);
        Node mainScene=new Node();
        cam.setLocation(new Vector3f(-27.0f, 1.0f, 75.0f));
        cam.setRotation(new Quaternion(0.03f, 0.9f, 0f, 0.4f));

        // load sky
        mainScene.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));

        // create the geometry and attach it
        // load the level from zip or http zip
        assetManager.registerLocator("wildhouse.zip", ZipLocator.class);
        Spatial scene = assetManager.loadModel("main.scene");

        DirectionalLight sun = new DirectionalLight();
        Vector3f lightDir=new Vector3f(-0.37352666f, -0.50444174f, -0.7784704f);
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
        scene.addLight(sun);


        mainScene.attachChild(scene);
        rootNode.attachChild(mainScene);

        fpp=new FilterPostProcessor(assetManager);
        //fpp.setNumSamples(4);
        fog=new FogFilter();
        fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
        fog.setFogDistance(155);
        fog.setFogDensity(2.0f);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);
        initInputs();
    }

     private void initInputs() {
        inputManager.addMapping("toggle", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("DensityUp", new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addMapping("DensityDown", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("DistanceUp", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("DistanceDown", new KeyTrigger(KeyInput.KEY_J));


        ActionListener acl = new ActionListener() {

            public void onAction(String name, boolean keyPressed, float tpf) {
                if (name.equals("toggle") && keyPressed) {
                    if(enabled){
                        enabled=false;
                        viewPort.removeProcessor(fpp);
                    }else{
                        enabled=true;
                        viewPort.addProcessor(fpp);
                    }
                }

            }
        };

        AnalogListener anl=new AnalogListener() {

            public void onAnalog(String name, float isPressed, float tpf) {
                if(name.equals("DensityUp")){
                    fog.setFogDensity(fog.getFogDensity()+0.001f);
                    System.out.println("Fog density : "+fog.getFogDensity());
                }
                if(name.equals("DensityDown")){
                    fog.setFogDensity(fog.getFogDensity()-0.010f);
                    System.out.println("Fog density : "+fog.getFogDensity());
                }
                if(name.equals("DistanceUp")){
                    fog.setFogDistance(fog.getFogDistance()+0.5f);
                    System.out.println("Fog Distance : "+fog.getFogDistance());
                }
                if(name.equals("DistanceDown")){
                    fog.setFogDistance(fog.getFogDistance()-0.5f);
                    System.out.println("Fog Distance : "+fog.getFogDistance());
                }

            }
        };

        inputManager.addListener(acl, "toggle");
        inputManager.addListener(anl, "DensityUp","DensityDown","DistanceUp","DistanceDown");

    }
}

