

package clockworktest.post;

import com.clockwork.app.SimpleApplication;
import com.clockwork.light.PointLight;
import com.clockwork.material.Material;
import com.clockwork.math.*;
import com.clockwork.post.SceneProcessor;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.renderer.queue.RenderQueue;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.texture.FrameBuffer;
import com.clockwork.texture.Image.Format;
import com.clockwork.texture.Texture2D;
import com.clockwork.ui.Picture;

public class TestMultiRenderTarget extends SimpleApplication implements SceneProcessor {

    private FrameBuffer fb;
    private Texture2D diffuseData, normalData, specularData, depthData;
    private Geometry sphere;
    private Picture display1, display2, display3, display4;
    
    private Picture display;
    private Material mat;

    public static void main(String[] args){
        TestMultiRenderTarget app = new TestMultiRenderTarget();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        viewPort.addProcessor(this);
        renderManager.setForcedTechnique("GBuf");

//        flyCam.setEnabled(false);
        cam.setLocation(new Vector3f(4.8037705f, 4.851632f, 10.789033f));
        cam.setRotation(new Quaternion(-0.05143692f, 0.9483723f, -0.21131563f, -0.230846f));

        Node tank = (Node) assetManager.loadModel("Models/HoverTank/Tank2.mesh.xml");
        
        //tankMesh.getMaterial().setColor("Specular", ColorRGBA.Black);
        rootNode.attachChild(tank);

        display1 = new Picture("Picture");
        display1.move(0, 0, -1); // make it appear behind stats view
        display2 = (Picture) display1.clone();
        display3 = (Picture) display1.clone();
        display4 = (Picture) display1.clone();
        display  = (Picture) display1.clone();

        ColorRGBA[] colors = new ColorRGBA[]{
            ColorRGBA.White,
            ColorRGBA.Blue,
            ColorRGBA.Cyan,
            ColorRGBA.DarkGray,
            ColorRGBA.Green,
            ColorRGBA.Magenta,
            ColorRGBA.Orange,
            ColorRGBA.Pink,
            ColorRGBA.Red,
            ColorRGBA.Yellow
        };

        for (int i = 0; i < 3; i++){
            PointLight pl = new PointLight();
            float angle = 0.314159265f * i;
            pl.setPosition( new Vector3f(FastMath.cos(angle)*2f, 0,
                                         FastMath.sin(angle)*2f));
            pl.setColor(colors[i]);
            pl.setRadius(5);
            rootNode.addLight(pl);
            display.addLight(pl);
        }
    }

    public void initialize(RenderManager rm, ViewPort vp) {
        reshape(vp, vp.getCamera().getWidth(), vp.getCamera().getHeight());
        viewPort.setOutputFrameBuffer(fb);
        guiViewPort.setClearFlags(true, true, true);
        guiNode.attachChild(display);
//        guiNode.attachChild(display1);
//        guiNode.attachChild(display2);
//        guiNode.attachChild(display3);
//        guiNode.attachChild(display4);
        guiNode.updateGeometricState();
    }

    public void reshape(ViewPort vp, int w, int h) {
        diffuseData  = new Texture2D(w, h, Format.RGBA8);
        normalData   = new Texture2D(w, h, Format.RGBA8);
        specularData = new Texture2D(w, h, Format.RGBA8);
        depthData    = new Texture2D(w, h, Format.Depth);

        mat = new Material(assetManager, "Common/MatDefs/Light/Deferred.j3md");
        mat.setTexture("DiffuseData",  diffuseData);
        mat.setTexture("SpecularData", specularData);
        mat.setTexture("NormalData",   normalData);
        mat.setTexture("DepthData",    depthData);

        display.setMaterial(mat);
        display.setPosition(0, 0);
        display.setWidth(w);
        display.setHeight(h);

        display1.setTexture(assetManager, diffuseData, false);
        display2.setTexture(assetManager, normalData, false);
        display3.setTexture(assetManager, specularData, false);
        display4.setTexture(assetManager, depthData, false);

        display1.setPosition(0, 0);
        display2.setPosition(w/2, 0);
        display3.setPosition(0, h/2);
        display4.setPosition(w/2, h/2);

        display1.setWidth(w/2);
        display1.setHeight(h/2);

        display2.setWidth(w/2);
        display2.setHeight(h/2);

        display3.setWidth(w/2);
        display3.setHeight(h/2);

        display4.setWidth(w/2);
        display4.setHeight(h/2);

        guiNode.updateGeometricState();
        
        fb = new FrameBuffer(w, h, 1);
        fb.setDepthTexture(depthData);
        fb.addColorTexture(diffuseData);
        fb.addColorTexture(normalData);
        fb.addColorTexture(specularData);
        fb.setMultiTarget(true);

        /*
         * Marks pixels in front of the far light boundary
            Render back-faces of light volume
            Depth test GREATER-EQUAL
            Write to stencil on depth pass
            Skipped for very small distant lights
         */
        
        /*
         * Find amount of lit pixels inside the volume
             Start pixel query
             Render front faces of light volume
             Depth test LESS-EQUAL
             Don’t write anything – only EQUAL stencil test
         */

        /*
         * Enable conditional rendering
            Based on query results from previous stage
            GPU skips rendering for invisible lights
         */

        /*
         * Render front-faces of light volume
            Depth test - LESS-EQUAL
            Stencil test - EQUAL
            Runs only on marked pixels inside light
         */
    }

    public boolean isInitialized() {
        return diffuseData != null;
    }

    public void preFrame(float tpf) {
        Matrix4f inverseViewProj = cam.getViewProjectionMatrix().invert();
        mat.setMatrix4("ViewProjectionMatrixInverse", inverseViewProj);
    }

    public void postQueue(RenderQueue rq) {
    }

    public void postFrame(FrameBuffer out) {
    }

    public void cleanup() {
    }

}
