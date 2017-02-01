

package clockworktest.post;

import com.clockwork.app.SimpleApplication;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
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
import com.clockwork.texture.Texture2D;

/**
 * This test renders a scene to a texture, then displays the texture on a cube.
 */
public class TestRenderToTexture extends SimpleApplication implements ActionListener {

    private static final String TOGGLE_UPDATE = "Toggle Update";
    private Geometry offBox;
    private float angle = 0;
    private ViewPort offView;

    public static void main(String[] args){
        TestRenderToTexture app = new TestRenderToTexture();
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
        Texture2D offTex = new Texture2D(512, 512, Format.RGBA8);
        offTex.setMinFilter(Texture.MinFilter.Trilinear);
        offTex.setMagFilter(Texture.MagFilter.Bilinear);

        //setup framebuffer to use texture
        offBuffer.setDepthBuffer(Format.Depth);
        offBuffer.setColorTexture(offTex);
        
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

        //setup main scene
        Geometry quad = new Geometry("box", new Box(Vector3f.ZERO, 1,1,1));

        Texture offTex = setupOffscreenView();

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", offTex);
        quad.setMaterial(mat);
        rootNode.attachChild(quad);
        inputManager.addMapping(TOGGLE_UPDATE, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, TOGGLE_UPDATE);
    }

    @Override
    public void simpleUpdate(float tpf){
        Quaternion q = new Quaternion();
        
        if (offView.isEnabled()) {
            angle += tpf;
            angle %= FastMath.TWO_PI;
            q.fromAngles(angle, 0, angle);
            
            offBox.setLocalRotation(q);
            offBox.updateLogicalState(tpf);
            offBox.updateGeometricState();
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(TOGGLE_UPDATE) && isPressed) {
            offView.setEnabled(!offView.isEnabled());
        }
    }


}
