

package clockworktest.water;

import com.clockwork.app.SimpleApplication;
import com.clockwork.asset.plugins.HttpZipLocator;
import com.clockwork.asset.plugins.ZipLocator;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.Material;
import com.clockwork.math.*;
import com.clockwork.renderer.queue.RenderQueue.ShadowMode;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.shape.Quad;
import com.clockwork.scene.shape.Sphere;
import com.clockwork.util.SkyFactory;
import com.clockwork.water.SimpleWaterProcessor;
import java.io.File;

public class TestSceneWater extends SimpleApplication {

    // set default for applets
    private static boolean useHttp = true;

    public static void main(String[] args) {
      
        TestSceneWater app = new TestSceneWater();
        app.start();
    }

    public void simpleInitApp() {
        this.flyCam.setMoveSpeed(10);
        Node mainScene=new Node();
        cam.setLocation(new Vector3f(-27.0f, 1.0f, 75.0f));
        cam.setRotation(new Quaternion(0.03f, 0.9f, 0f, 0.4f));

        // load sky
        mainScene.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));

        
        File file = new File("wildhouse.zip");
        if (file.exists()) {
            useHttp = false;
        }
        // create the geometry and attach it
        // load the level from zip or http zip
        if (useHttp) {
            assetManager.registerLocator("http://jmonkeyengine.googlecode.com/files/wildhouse.zip", HttpZipLocator.class);
        } else {
            assetManager.registerLocator("wildhouse.zip", ZipLocator.class);
        }
        Spatial scene = assetManager.loadModel("main.scene");

        DirectionalLight sun = new DirectionalLight();
        Vector3f lightDir=new Vector3f(-0.37352666f, -0.50444174f, -0.7784704f);
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
        scene.addLight(sun);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));
           //add lightPos Geometry
        Sphere lite=new Sphere(8, 8, 3.0f);
        Geometry lightSphere=new Geometry("lightsphere", lite);
        lightSphere.setMaterial(mat);
        Vector3f lightPos=lightDir.multLocal(-400);
        lightSphere.setLocalTranslation(lightPos);
        rootNode.attachChild(lightSphere);


        SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(assetManager);
        waterProcessor.setReflectionScene(mainScene);
        waterProcessor.setDebug(false);
        waterProcessor.setLightPosition(lightPos);
        waterProcessor.setRefractionClippingOffset(1.0f);


        //setting the water plane
        Vector3f waterLocation=new Vector3f(0,-20,0);
        waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));
        WaterUI waterUi=new WaterUI(inputManager, waterProcessor);
        waterProcessor.setWaterColor(ColorRGBA.Brown);
        waterProcessor.setDebug(true);
        //lower render size for higher performance
//        waterProcessor.setRenderSize(128,128);
        //raise depth to see through water
//        waterProcessor.setWaterDepth(20);
        //lower the distortion scale if the waves appear too strong
//        waterProcessor.setDistortionScale(0.1f);
        //lower the speed of the waves if they are too fast
//        waterProcessor.setWaveSpeed(0.01f);

        Quad quad = new Quad(400,400);

        //the texture coordinates define the general size of the waves
        quad.scaleTextureCoordinates(new Vector2f(6f,6f));

        Geometry water=new Geometry("water", quad);
        water.setShadowMode(ShadowMode.Receive);
        water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        water.setMaterial(waterProcessor.getMaterial());
        water.setLocalTranslation(-200, -20, 250);

        rootNode.attachChild(water);

        viewPort.addProcessor(waterProcessor);

        mainScene.attachChild(scene);
        rootNode.attachChild(mainScene);
    }
}
