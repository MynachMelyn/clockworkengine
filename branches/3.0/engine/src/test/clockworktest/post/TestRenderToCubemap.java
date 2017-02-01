

package clockworktest.post;

import com.clockwork.app.SimpleApplication;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.FastMath;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.Camera;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.shape.Box;
import com.clockwork.texture.FrameBuffer;
import com.clockwork.texture.Image.Format;
import com.clockwork.texture.Texture;
import com.clockwork.texture.TextureCubeMap;
import com.clockwork.util.SkyFactory;

/**
 * Renders a rotating box to a cubemap texture, then applies the cubemap
 * texture as a sky.
 */
public class TestRenderToCubemap  extends SimpleApplication {
 
    private Geometry offBox;
    private float angle = 0;
    private ViewPort offView;
 
    public static void main(String[] args){
        TestRenderToCubemap app = new TestRenderToCubemap();
        app.start();
    }
 
    public Texture setupOffscreenView(){
        Camera offCamera = new Camera(512, 512);
 
        offView = renderManager.createPreView("Offscreen View", offCamera);
        offView.setClearFlags(true, true, true);
        offView.setBackgroundColor(ColorRGBA.DarkGray);
 
        // create offscreen framebuffer
        FrameBuffer offBuffer = new FrameBuffer(512, 512, 1);
 
        //setup framebuffer's cam
        offCamera.setFrustumPerspective(45f, 1f, 1f, 1000f);
        offCamera.setLocation(new Vector3f(0f, 0f, -5f));
        offCamera.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);
 
        //setup framebuffer's texture
        TextureCubeMap offTex = new TextureCubeMap(512, 512, Format.RGBA8);
        offTex.setMinFilter(Texture.MinFilter.Trilinear);
        offTex.setMagFilter(Texture.MagFilter.Bilinear);
 
        //setup framebuffer to use texture
        offBuffer.setDepthBuffer(Format.Depth);
        offBuffer.setMultiTarget(true);
        offBuffer.addColorTexture(offTex, TextureCubeMap.Face.NegativeX);
        offBuffer.addColorTexture(offTex, TextureCubeMap.Face.PositiveX);
        offBuffer.addColorTexture(offTex, TextureCubeMap.Face.NegativeY);
        offBuffer.addColorTexture(offTex, TextureCubeMap.Face.PositiveY);
        offBuffer.addColorTexture(offTex, TextureCubeMap.Face.NegativeZ);
        offBuffer.addColorTexture(offTex, TextureCubeMap.Face.PositiveZ);
        
        //set viewport to render to offscreen framebuffer
        offView.setOutputFrameBuffer(offBuffer);
 
        // setup framebuffer's scene
        Box boxMesh = new Box(Vector3f.ZERO, 1,1,1);
        Material material = assetManager.loadMaterial("Interface/Logo/Logo.j3m");
        offBox = new Geometry("box", boxMesh);
        offBox.setMaterial(material);
 
        // attach the scene to the viewport to be rendered
        offView.attachScene(offBox);
 
        return offTex;
    }
 
    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(3, 3, 3));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
 
        Texture offTex = setupOffscreenView();
        rootNode.attachChild(SkyFactory.createSky(assetManager, offTex, false));
    }
 
    @Override
    public void simpleUpdate(float tpf){
        Quaternion q = new Quaternion();
 
        angle += tpf;
        angle %= FastMath.TWO_PI;
        q.fromAngles(angle, 0, angle);

        offBox.setLocalRotation(q);
        offBox.updateLogicalState(tpf);
        offBox.updateGeometricState();
    }
}