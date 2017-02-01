
package clockworktest.light;

import com.clockwork.app.SimpleApplication;
import com.clockwork.light.PointLight;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.post.FilterPostProcessor;
import com.clockwork.renderer.queue.RenderQueue;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.shape.Box;
import com.clockwork.scene.shape.Sphere;
import com.clockwork.shadow.EdgeFilteringMode;
import com.clockwork.shadow.PointLightShadowFilter;
import com.clockwork.shadow.PointLightShadowRenderer;

public class TestPointLightShadows extends SimpleApplication {
    public static final int SHADOWMAP_SIZE = 512;

    public static void main(String[] args) {
        TestPointLightShadows app = new TestPointLightShadows();
        app.start();
    }
    Node lightNode;
    PointLightShadowRenderer plsr;
    PointLightShadowFilter plsf;

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10);
        cam.setLocation(new Vector3f(0.040581334f, 1.7745866f, 6.155161f));
        cam.setRotation(new Quaternion(4.3868728E-5f, 0.9999293f, -0.011230096f, 0.0039059948f));


        Node scene = (Node) assetManager.loadModel("Models/Test/CornellBox.j3o");
        scene.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.attachChild(scene);
        rootNode.getChild("Cube").setShadowMode(RenderQueue.ShadowMode.Receive);
        lightNode = (Node) rootNode.getChild("Lamp");
        Geometry lightMdl = new Geometry("Light", new Sphere(10, 10, 0.1f));
        //Geometry  lightMdl = new Geometry("Light", new Box(.1f,.1f,.1f));
        lightMdl.setMaterial(assetManager.loadMaterial("Common/Materials/RedColor.j3m"));
        lightMdl.setShadowMode(RenderQueue.ShadowMode.Off);
        lightNode.attachChild(lightMdl);
        //lightMdl.setLocalTranslation(lightNode.getLocalTranslation());


        Geometry box = new Geometry("box", new Box(0.2f, 0.2f, 0.2f));
        //Geometry  lightMdl = new Geometry("Light", new Box(.1f,.1f,.1f));
        box.setMaterial(assetManager.loadMaterial("Common/Materials/RedColor.j3m"));
        box.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.attachChild(box);
        box.setLocalTranslation(-1f, 0.5f, -2);


        plsr = new PointLightShadowRenderer(assetManager, SHADOWMAP_SIZE);
        plsr.setLight((PointLight) scene.getLocalLightList().get(0));
        plsr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
       // plsr.setFlushQueues(false);
        //plsr.displayFrustum();
        plsr.displayDebug();
        viewPort.addProcessor(plsr);


//        PointLight pl = new PointLight();
//        pl.setPosition(new Vector3f(0, 0.5f, 0));
//        pl.setRadius(5);
//        rootNode.addLight(pl);
//
//        Geometry lightMdl2 = new Geometry("Light2", new Sphere(10, 10, 0.1f));
//        //Geometry  lightMdl = new Geometry("Light", new Box(.1f,.1f,.1f));
//        lightMdl2.setMaterial(assetManager.loadMaterial("Common/Materials/RedColor.j3m"));
//        lightMdl2.setShadowMode(RenderQueue.ShadowMode.Off);
//        rootNode.attachChild(lightMdl2);
//        lightMdl2.setLocalTranslation(pl.getPosition());
//        PointLightShadowRenderer plsr2 = new PointLightShadowRenderer(assetManager, 512);
//        plsr2.setShadowIntensity(0.3f);
//        plsr2.setLight(pl);
//        plsr2.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
//        //   plsr.displayDebug();
//        viewPort.addProcessor(plsr2);


        plsf = new PointLightShadowFilter(assetManager, SHADOWMAP_SIZE);
        plsf.setLight((PointLight) scene.getLocalLightList().get(0));     
        plsf.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
        plsf.setEnabled(false);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(plsf);
        viewPort.addProcessor(fpp);
              
        ShadowTestUIManager uiMan = new ShadowTestUIManager(assetManager, plsr, plsf, guiNode, inputManager, viewPort);
    }

    @Override
    public void simpleUpdate(float tpf) {
 //      lightNode.move(FastMath.cos(tpf) * 0.4f, 0, FastMath.sin(tpf) * 0.4f);
    }
}