

package clockworktest.post;

import com.clockwork.app.SimpleApplication;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.post.FilterPostProcessor;
import com.clockwork.post.filters.BloomFilter;
import com.clockwork.renderer.queue.RenderQueue.ShadowMode;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.debug.WireFrustum;
import com.clockwork.scene.shape.Box;
import com.clockwork.util.SkyFactory;

public class TestBloom extends SimpleApplication {

    float angle;
    Spatial lightMdl;
    Spatial teapot;
    Geometry frustumMdl;
    WireFrustum frustum;
    boolean active=true;
    FilterPostProcessor fpp;
    
    public static void main(String[] args){
        TestBloom app = new TestBloom();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // put the camera in a bad position
        cam.setLocation(new Vector3f(-2.336393f, 11.91392f, -7.139601f));
        cam.setRotation(new Quaternion(0.23602544f, 0.11321983f, -0.027698677f, 0.96473104f));
        //cam.setFrustumFar(1000);


        Material mat = new Material(assetManager,"Common/MatDefs/Light/Lighting.j3md");
        mat.setFloat("Shininess", 15f);
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Ambient", ColorRGBA.Yellow.mult(0.2f));
        mat.setColor("Diffuse", ColorRGBA.Yellow.mult(0.2f));
        mat.setColor("Specular", ColorRGBA.Yellow.mult(0.8f));

    


        Material matSoil = new Material(assetManager,"Common/MatDefs/Light/Lighting.j3md");
        matSoil.setFloat("Shininess", 15f);
        matSoil.setBoolean("UseMaterialColors", true);
        matSoil.setColor("Ambient", ColorRGBA.Gray);
        matSoil.setColor("Diffuse", ColorRGBA.Black);
        matSoil.setColor("Specular", ColorRGBA.Gray);
       


        teapot = assetManager.loadModel("Models/Teapot/Teapot.obj");
        teapot.setLocalTranslation(0,0,10);

        teapot.setMaterial(mat);
        teapot.setShadowMode(ShadowMode.CastAndReceive);
        teapot.setLocalScale(10.0f);
        rootNode.attachChild(teapot);

  

        Geometry soil=new Geometry("soil", new Box(new Vector3f(0, -13, 550), 800, 10, 700));
        soil.setMaterial(matSoil);
        soil.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(soil);

        DirectionalLight light=new DirectionalLight();
        light.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
        light.setColor(ColorRGBA.White.mult(1.5f));
        rootNode.addLight(light);

        // load sky
        Spatial sky = SkyFactory.createSky(assetManager, "Textures/Sky/Bright/FullskiesBlueClear03.dds", false);
        sky.setCullHint(Spatial.CullHint.Never);
        rootNode.attachChild(sky);

        fpp=new FilterPostProcessor(assetManager);
       // fpp.setNumSamples(4);
        BloomFilter bloom=new BloomFilter();
        bloom.setDownSamplingFactor(2);
        bloom.setBlurScale(1.37f);
        bloom.setExposurePower(3.30f);
        bloom.setExposureCutOff(0.2f);
        bloom.setBloomIntensity(2.45f);
        BloomUI ui=new BloomUI(inputManager, bloom);
   

        viewPort.addProcessor(fpp);
        fpp.addFilter(bloom);
        initInputs();

    }
    
         private void initInputs() {
        inputManager.addMapping("toggle", new KeyTrigger(KeyInput.KEY_SPACE));
     
        ActionListener acl = new ActionListener() {

            public void onAction(String name, boolean keyPressed, float tpf) {
                if (name.equals("toggle") && keyPressed) {
                    if(active){
                        active=false;
                        viewPort.removeProcessor(fpp);
                    }else{
                        active=true;
                        viewPort.addProcessor(fpp);
                    }
                }
            }
        };
             
        inputManager.addListener(acl, "toggle");

    }

 

}
