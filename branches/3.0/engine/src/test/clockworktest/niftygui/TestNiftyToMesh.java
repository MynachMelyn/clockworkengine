

package clockworktest.niftygui;

import com.clockwork.app.SimpleApplication;
import com.clockwork.material.Material;
import com.clockwork.math.Vector3f;
import com.clockwork.niftygui.NiftyCWDisplay;
import com.clockwork.renderer.Camera;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.shape.Box;
import com.clockwork.texture.FrameBuffer;
import com.clockwork.texture.Image.Format;
import com.clockwork.texture.Texture.MagFilter;
import com.clockwork.texture.Texture.MinFilter;
import com.clockwork.texture.Texture2D;
import de.lessvoid.nifty.Nifty;

public class TestNiftyToMesh extends SimpleApplication{

    private Nifty nifty;

    public static void main(String[] args){
        TestNiftyToMesh app = new TestNiftyToMesh();
        app.start();
    }

    public void simpleInitApp() {
       ViewPort niftyView = renderManager.createPreView("NiftyView", new Camera(1024, 768));
       niftyView.setClearFlags(true, true, true);
        NiftyCWDisplay niftyDisplay = new NiftyCWDisplay(assetManager,
                                                          inputManager,
                                                          audioRenderer,
                                                          niftyView);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("all/intro.xml", "start");
        niftyView.addProcessor(niftyDisplay);

        Texture2D depthTex = new Texture2D(1024, 768, Format.Depth);
        FrameBuffer fb = new FrameBuffer(1024, 768, 1);
        fb.setDepthTexture(depthTex);

        Texture2D tex = new Texture2D(1024, 768, Format.RGBA8);
        tex.setMinFilter(MinFilter.Trilinear);
        tex.setMagFilter(MagFilter.Bilinear);

        fb.setColorTexture(tex);
        niftyView.setClearFlags(true, true, true);
        niftyView.setOutputFrameBuffer(fb);

        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        geom.setMaterial(mat);
        rootNode.attachChild(geom);
    }
}
