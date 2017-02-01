

package clockworktest.post;

import com.clockwork.app.SimpleApplication;
import com.clockwork.material.Material;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.Renderer;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.shape.Sphere;
import com.clockwork.texture.FrameBuffer;
import com.clockwork.texture.Image.Format;
import com.clockwork.texture.Texture2D;
import com.clockwork.ui.Picture;

/**
 * Demonstrates FrameBuffer usage.
 * The scene is first rendered to an FB with a texture attached,
 * the texture is then rendered onto the screen in ortho mode.
 *
 */
public class TestFBOPassthrough extends SimpleApplication {

    private Node fbNode = new Node("Framebuffer Node");
    private FrameBuffer fb;

    public static void main(String[] args){
        TestFBOPassthrough app = new TestFBOPassthrough();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        int w = settings.getWidth();
        int h = settings.getHeight();

        //setup framebuffer
        fb = new FrameBuffer(w, h, 1);

        Texture2D fbTex = new Texture2D(w, h, Format.RGBA8);
        fb.setDepthBuffer(Format.Depth);
        fb.setColorTexture(fbTex);

        // setup framebuffer's scene
        Sphere sphMesh = new Sphere(20, 20, 1);
        Material solidColor = assetManager.loadMaterial("Common/Materials/RedColor.j3m");

        Geometry sphere = new Geometry("sphere", sphMesh);
        sphere.setMaterial(solidColor);
        fbNode.attachChild(sphere);

        //setup main scene
        Picture p = new Picture("Picture");
        p.setPosition(0, 0);
        p.setWidth(w);
        p.setHeight(h);
        p.setTexture(assetManager, fbTex, false);

        rootNode.attachChild(p);
    }

    @Override
    public void simpleUpdate(float tpf){
        fbNode.updateLogicalState(tpf);
        fbNode.updateGeometricState();
    }

    @Override
    public void simpleRender(RenderManager rm){
        Renderer r = rm.getRenderer();

        //do FBO rendering
        r.setFrameBuffer(fb);

        rm.setCamera(cam, false); // FBO uses current camera
        r.clearBuffers(true, true, true);
        rm.renderScene(fbNode, viewPort);
        rm.flushQueue(viewPort);

        //go back to default rendering and let
        //SimpleApplication render the default scene
        r.setFrameBuffer(null);
    }

}
