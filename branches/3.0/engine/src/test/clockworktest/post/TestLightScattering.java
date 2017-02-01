

package clockworktest.post;

import com.clockwork.app.SimpleApplication;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.post.FilterPostProcessor;
import com.clockwork.post.filters.LightScatteringFilter;
import com.clockwork.renderer.queue.RenderQueue.ShadowMode;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.shadow.PssmShadowRenderer;
import com.clockwork.util.SkyFactory;
import com.clockwork.util.TangentBinormalGenerator;

public class TestLightScattering extends SimpleApplication {

    public static void main(String[] args) {
        TestLightScattering app = new TestLightScattering();
        
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // put the camera in a bad position
        cam.setLocation(new Vector3f(55.35316f, -0.27061665f, 27.092093f));
        cam.setRotation(new Quaternion(0.010414706f, 0.9874893f, 0.13880467f, -0.07409228f));
//        cam.setDirection(new Vector3f(0,-0.5f,1.0f));
//        cam.setLocation(new Vector3f(0, 300, -500));
        //cam.setFrustumFar(1000);
        flyCam.setMoveSpeed(10);
        Material mat = assetManager.loadMaterial("Textures/Terrain/Rocky/Rocky.j3m");
        Spatial scene = assetManager.loadModel("Models/Terrain/Terrain.mesh.xml");
        TangentBinormalGenerator.generate(((Geometry)((Node)scene).getChild(0)).getMesh());
        scene.setMaterial(mat);
        scene.setShadowMode(ShadowMode.CastAndReceive);
        scene.setLocalScale(400);
        scene.setLocalTranslation(0, -10, -120);

        rootNode.attachChild(scene);

        // load sky
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/FullskiesBlueClear03.dds", false));

        DirectionalLight sun = new DirectionalLight();
        Vector3f lightDir = new Vector3f(-0.12f, -0.3729129f, 0.74847335f);
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
        scene.addLight(sun);

        PssmShadowRenderer pssmRenderer = new PssmShadowRenderer(assetManager,1024,4);
        pssmRenderer.setDirection(lightDir);
        pssmRenderer.setShadowIntensity(0.55f);
     //   viewPort.addProcessor(pssmRenderer);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
//        SSAOFilter ssaoFilter= new SSAOFilter(viewPort, new SSAOConfig(0.36f,1.8f,0.84f,0.16f,false,true));
//        fpp.addFilter(ssaoFilter);


//           Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat2.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));
//
//        Sphere lite=new Sphere(8, 8, 10.0f);
//        Geometry lightSphere=new Geometry("lightsphere", lite);
//        lightSphere.setMaterial(mat2);
        Vector3f lightPos = lightDir.multLocal(-3000);
//        lightSphere.setLocalTranslation(lightPos);
        // rootNode.attachChild(lightSphere);
        LightScatteringFilter filter = new LightScatteringFilter(lightPos);
        LightScatteringUI ui = new LightScatteringUI(inputManager, filter);
        fpp.addFilter(filter);
//fpp.setNumSamples(4);
        //fpp.addFilter(new RadialBlurFilter(0.3f,15.0f));
        //    SSAOUI ui=new SSAOUI(inputManager, ssaoFilter.getConfig());

        viewPort.addProcessor(fpp);
    }

    @Override
    public void simpleUpdate(float tpf) {
    }
}
