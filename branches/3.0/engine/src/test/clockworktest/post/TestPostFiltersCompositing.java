
package clockworktest.post;

import com.clockwork.app.SimpleApplication;
import com.clockwork.asset.plugins.ZipLocator;
import com.clockwork.light.DirectionalLight;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.post.FilterPostProcessor;
import com.clockwork.post.filters.BloomFilter;
import com.clockwork.post.filters.ColorOverlayFilter;
import com.clockwork.post.filters.ComposeFilter;
import com.clockwork.scene.Spatial;
import com.clockwork.texture.FrameBuffer;
import com.clockwork.texture.Image;
import com.clockwork.texture.Texture2D;
import com.clockwork.util.SkyFactory;

/**
 * This test showcases the possibility to compose the post filtered outputs of several viewports.
 * The usual use case is when you want to apply some post process to the main viewport and then other post process to the gui viewport
 */
public class TestPostFiltersCompositing extends SimpleApplication {

    public static void main(String[] args) {
        TestPostFiltersCompositing app = new TestPostFiltersCompositing();
        app.start();
    }

    public void simpleInitApp() {
        this.flyCam.setMoveSpeed(10);
        cam.setLocation(new Vector3f(6.0344796f, 1.5054002f, 55.572033f));
        cam.setRotation(new Quaternion(0.0016069f, 0.9810479f, -0.008143323f, 0.19358753f));

        makeScene();

        //Creating the main view port post processor
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(new ColorOverlayFilter(ColorRGBA.Blue));
        viewPort.addProcessor(fpp);

        //creating a frame buffer for the mainviewport
        FrameBuffer mainVPFrameBuffer = new FrameBuffer(cam.getWidth(), cam.getHeight(), 1);
        Texture2D mainVPTexture = new Texture2D(cam.getWidth(), cam.getHeight(), Image.Format.RGBA8);
        mainVPFrameBuffer.addColorTexture(mainVPTexture);
        mainVPFrameBuffer.setDepthBuffer(Image.Format.Depth);
        viewPort.setOutputFrameBuffer(mainVPFrameBuffer);

        //creating the post processor for the gui viewport
        final FilterPostProcessor guifpp = new FilterPostProcessor(assetManager);           
        guifpp.addFilter(new ColorOverlayFilter(ColorRGBA.Red));       
        //this will compose the main viewport texture with the guiviewport back buffer.
        //Note that you can swich the order of the filters so that guiviewport filters are applied or not to the main viewport texture    
        guifpp.addFilter(new ComposeFilter(mainVPTexture));
        
        guiViewPort.addProcessor(guifpp);
        
        //compositing is done my mixing texture depending on the alpha channel, 
        //it's important that the guiviewport clear color alpha value is set to 0
        guiViewPort.setBackgroundColor(new ColorRGBA(0, 0, 0, 0));


    }

    private void makeScene() {
        // load sky
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
        assetManager.registerLocator("wildhouse.zip", ZipLocator.class);
        Spatial scene = assetManager.loadModel("main.scene");
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.4790551f, -0.39247334f, -0.7851566f));
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
        scene.addLight(sun);
        rootNode.attachChild(scene);
    }
}
