
package clockworktest.model;

import com.clockwork.app.SimpleApplication;
import com.clockwork.input.ChaseCamera;
import com.clockwork.light.DirectionalLight;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.FastMath;
import com.clockwork.math.Vector3f;
import com.clockwork.post.FilterPostProcessor;
import com.clockwork.post.filters.BloomFilter;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.control.LodControl;
import clockworktest.post.BloomUI;

/**
 *
 */
public class TestHoverTank extends SimpleApplication {

    public static void main(String[] args) {
        TestHoverTank app = new TestHoverTank();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Node tank = (Node) assetManager.loadModel("Models/HoverTank/Tank2.mesh.xml");

        flyCam.setEnabled(false);
        ChaseCamera chaseCam = new ChaseCamera(cam, tank, inputManager);
        chaseCam.setSmoothMotion(true);
        chaseCam.setMaxDistance(100000);
        chaseCam.setMinVerticalRotation(-FastMath.PI / 2);
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);

        Geometry tankGeom = (Geometry) tank.getChild(0);
        LodControl control = new LodControl();
        tankGeom.addControl(control);
        rootNode.attachChild(tank);

        Vector3f lightDir = new Vector3f(-0.8719428f, -0.46824604f, 0.14304268f);
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(new ColorRGBA(1.0f, 0.92f, 0.75f, 1f));
        dl.setDirection(lightDir);

        Vector3f lightDir2 = new Vector3f(0.70518064f, 0.5902297f, -0.39287305f);
        DirectionalLight dl2 = new DirectionalLight();
        dl2.setColor(new ColorRGBA(0.7f, 0.85f, 1.0f, 1f));
        dl2.setDirection(lightDir2);

        rootNode.addLight(dl);
        rootNode.addLight(dl2);
        rootNode.attachChild(tank);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        BloomFilter bf = new BloomFilter(BloomFilter.GlowMode.Objects);
        bf.setBloomIntensity(2.0f);
        bf.setExposurePower(1.3f);
        fpp.addFilter(bf);
        BloomUI bui = new BloomUI(inputManager, bf);
        viewPort.addProcessor(fpp);
    }
}
