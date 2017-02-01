

package clockworktest.scene;

import com.clockwork.app.SimpleApplication;
import com.clockwork.asset.plugins.HttpZipLocator;
import com.clockwork.asset.plugins.ZipLocator;
import com.clockwork.light.AmbientLight;
import com.clockwork.light.DirectionalLight;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.shape.Sphere;
import com.clockwork.util.SkyFactory;
import java.io.File;

public class TestSceneLoading extends SimpleApplication {

    private Sphere sphereMesh = new Sphere(32, 32, 10, false, true);
    private Geometry sphere = new Geometry("Sky", sphereMesh);
    private static boolean useHttp = false;

    public static void main(String[] args) {
     
        TestSceneLoading app = new TestSceneLoading();
        app.start();
    }

    @Override
    public void simpleUpdate(float tpf){
        sphere.setLocalTranslation(cam.getLocation());
    }

    public void simpleInitApp() {
        this.flyCam.setMoveSpeed(10);

        // load sky
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));

        File file = new File("wildhouse.zip");
        if (!file.exists()) {
            useHttp = true;
        }
        // create the geometry and attach it
        // load the level from zip or http zip
        if (useHttp) {
            assetManager.registerLocator("http://jmonkeyengine.googlecode.com/files/wildhouse.zip", HttpZipLocator.class);
        } else {
            assetManager.registerLocator("wildhouse.zip", ZipLocator.class);
        }
        Spatial scene = assetManager.loadModel("main.scene");

        AmbientLight al = new AmbientLight();
        scene.addLight(al);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0.69077975f, -0.6277887f, -0.35875428f).normalizeLocal());
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
        scene.addLight(sun);

        rootNode.attachChild(scene);
    }
}
