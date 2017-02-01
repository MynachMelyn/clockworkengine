
package clockworktest.renderer;

import com.clockwork.app.SimpleApplication;
import com.clockwork.light.DirectionalLight;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.Camera;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Geometry;

public class TestMultiViews extends SimpleApplication {

    public static void main(String[] args) {
        TestMultiViews app = new TestMultiViews();
        app.start();
    }

    public void simpleInitApp() {
        // create the geometry and attach it
        Geometry teaGeom = (Geometry) assetManager.loadModel("Models/Teapot/Teapot.obj");
        teaGeom.scale(3);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(Vector3f.UNIT_XYZ.negate());

        rootNode.addLight(dl);
        rootNode.attachChild(teaGeom);

        // Setup first view
        viewPort.setBackgroundColor(ColorRGBA.Blue);
        cam.setViewPort(.5f, 1f, 0f, 0.5f);
        cam.setLocation(new Vector3f(3.3212643f, 4.484704f, 4.2812433f));
        cam.setRotation(new Quaternion(-0.07680723f, 0.92299235f, -0.2564353f, -0.27645364f));

        // Setup second view
        Camera cam2 = cam.clone();
        cam2.setViewPort(0f, 0.5f, 0f, 0.5f);
        cam2.setLocation(new Vector3f(-0.10947256f, 1.5760219f, 4.81758f));
        cam2.setRotation(new Quaternion(0.0010108891f, 0.99857414f, -0.04928594f, 0.020481428f));

        ViewPort view2 = renderManager.createMainView("Bottom Left", cam2);
        view2.setClearFlags(true, true, true);
        view2.attachScene(rootNode);

        // Setup third view
        Camera cam3 = cam.clone();
        cam3.setViewPort(0f, .5f, .5f, 1f);
        cam3.setLocation(new Vector3f(0.2846221f, 6.4271426f, 0.23380789f));
        cam3.setRotation(new Quaternion(0.004381671f, 0.72363687f, -0.69015175f, 0.0045953835f));

        ViewPort view3 = renderManager.createMainView("Top Left", cam3);
        view3.setClearFlags(true, true, true);
        view3.attachScene(rootNode);

        // Setup fourth view
        Camera cam4 = cam.clone();
        cam4.setViewPort(.5f, 1f, .5f, 1f);
        cam4.setLocation(new Vector3f(4.775564f, 1.4548365f, 0.11491505f));
        cam4.setRotation(new Quaternion(0.02356979f, -0.74957186f, 0.026729556f, 0.66096294f));

        ViewPort view4 = renderManager.createMainView("Top Right", cam4);
        view4.setClearFlags(true, true, true);
        view4.attachScene(rootNode);

        //test multiview for gui 
        guiViewPort.getCamera().setViewPort(.5f, 1f, .5f, 1f);

        // Setup second gui view
        Camera guiCam2 = guiViewPort.getCamera().clone();
        guiCam2.setViewPort(0f, 0.5f, 0f, 0.5f);
        ViewPort guiViewPort2 = renderManager.createPostView("Gui 2", guiCam2);
        guiViewPort2.setClearFlags(false, false, false);
        guiViewPort2.attachScene(guiViewPort.getScenes().get(0));

    }
}
