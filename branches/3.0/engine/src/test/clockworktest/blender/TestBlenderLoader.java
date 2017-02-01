

package clockworktest.blender;

import com.clockwork.app.SimpleApplication;
import com.clockwork.light.DirectionalLight;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Spatial;

public class TestBlenderLoader extends SimpleApplication {

    public static void main(String[] args){
        TestBlenderLoader app = new TestBlenderLoader();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);

        //load model with packed images
        Spatial ogre = assetManager.loadModel("Blender/2.4x/Sinbad.blend");
        rootNode.attachChild(ogre);

        //load model with referenced images
        Spatial track = assetManager.loadModel("Blender/2.4x/MountainValley_Track.blend");
        rootNode.attachChild(track);
        
        // sunset light
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f,-0.7f,1).normalizeLocal());
        dl.setColor(new ColorRGBA(0.44f, 0.30f, 0.20f, 1.0f));
        rootNode.addLight(dl);

        // skylight
        dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.6f,-1,-0.6f).normalizeLocal());
        dl.setColor(new ColorRGBA(0.10f, 0.22f, 0.44f, 1.0f));
        rootNode.addLight(dl);

        // white ambient light
        dl = new DirectionalLight();
        dl.setDirection(new Vector3f(1, -0.5f,-0.1f).normalizeLocal());
        dl.setColor(new ColorRGBA(0.80f, 0.70f, 0.80f, 1.0f));
        rootNode.addLight(dl);
    }

    @Override
    public void simpleUpdate(float tpf){
    }
    
}
