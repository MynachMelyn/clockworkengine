
package com.clockwork.niftygui;

import java.io.InputStream;
import java.net.URL;

import com.clockwork.asset.AssetInfo;
import com.clockwork.asset.AssetKey;
import com.clockwork.asset.AssetManager;
import com.clockwork.asset.AssetNotFoundException;
import com.clockwork.audio.AudioRenderer;
import com.clockwork.input.InputManager;
import com.clockwork.input.event.KeyInputEvent;
import com.clockwork.post.SceneProcessor;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.Renderer;
import com.clockwork.renderer.ViewPort;
import com.clockwork.renderer.queue.RenderQueue;
import com.clockwork.texture.FrameBuffer;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.batch.BatchRenderDevice;
import de.lessvoid.nifty.tools.TimeProvider;
import de.lessvoid.nifty.tools.resourceloader.ResourceLocation;

public class NiftyCWDisplay implements SceneProcessor {

    protected boolean inited = false;
    protected Nifty nifty;
    protected AssetManager assetManager;
    protected RenderManager renderManager;
    protected InputManager inputManager;
    protected RenderDeviceCW renderDev;
    protected CWBatchRenderBackend batchRendererBackend;
    protected InputSystemCW inputSys;
    protected SoundDeviceCW soundDev;
    protected Renderer renderer;
    protected ViewPort vp;
    
    protected ResourceLocationCW resourceLocation;

    protected int w, h;

    protected class ResourceLocationCW implements ResourceLocation {

        public InputStream getResourceAsStream(String path) {
            AssetKey<Object> key = new AssetKey<Object>(path);
            AssetInfo info = assetManager.locateAsset(key);
            if (info != null) {
                return info.openStream();
            } else {
                throw new AssetNotFoundException(path);
            }
        }

        public URL getResource(String path) {
            throw new UnsupportedOperationException();
        }
    }

    //Empty constructor needed for jMP to create replacement input system
    public NiftyCWDisplay() {
    }

    /**
     * Create a new NiftyCWDisplay for use with the Batched Nifty Renderer (improved Nifty rendering performance).
     *
     * Nifty will use a single texture of the given dimensions (see atlasWidth and atlasHeight parameters). Every
     * graphical asset you're rendering through Nifty will be placed into this big texture. The goal is to render
     * all Nifty components in a single (or at least very few) draw calls. This should speed up rendering quite a
     * bit.
     *
     * Currently you have to make sure to not use more image space than this single texture provides. However, Nifty
     * tries to be smart about this and internally will make sure that only the images are uploaded that your GUI
     * really needs. So in general this shoudln't be an issue.
     *
     * A complete re-organisation of the texture atlas happens when a Nifty screen ends and another begins. Dynamically
     * adding images while a screen is running is supported as well.
     * 
     * @param assetManager CW AssetManager
     * @param inputManager CW InputManager
     * @param audioRenderer CW AudioRenderer
     * @param viewport Viewport to use
     * @param atlasWidth the width of the texture atlas Nifty uses to speed up rendering (2048 is a good value)
     * @param atlasHeight the height of the texture atlas Nifty uses to speed up rendering (2048 is a good value)
     */
    public NiftyCWDisplay(
        final AssetManager assetManager,
        final InputManager inputManager,
        final AudioRenderer audioRenderer,
        final ViewPort viewport,
        final int atlasWidth,
        final int atlasHeight){
      initialize(assetManager, inputManager, audioRenderer, viewport);

      this.renderDev = null;
      this.batchRendererBackend = new CWBatchRenderBackend(this);

      nifty = new Nifty(
          new BatchRenderDevice(batchRendererBackend, atlasWidth, atlasHeight),
          soundDev,
          inputSys,
          new TimeProvider());
      inputSys.setNifty(nifty);

      resourceLocation = new ResourceLocationCW();
      nifty.getResourceLoader().removeAllResourceLocations();
      nifty.getResourceLoader().addResourceLocation(resourceLocation);
    }

    /**
     * Create a standard NiftyCWDisplay. This uses the old Nifty renderer. It's probably slower then the batched
     * renderer and is mainly here for backwards compatibility.
     *
     * @param assetManager CW AssetManager
     * @param inputManager CW InputManager
     * @param audioRenderer CW AudioRenderer
     * @param viewport Viewport to use
     */
    public NiftyCWDisplay(AssetManager assetManager, 
                           InputManager inputManager,
                           AudioRenderer audioRenderer,
                           ViewPort vp){
        initialize(assetManager, inputManager, audioRenderer, vp);

        this.renderDev = new RenderDeviceCW(this);
        this.batchRendererBackend = null;

        nifty = new Nifty(renderDev, soundDev, inputSys, new TimeProvider());
        inputSys.setNifty(nifty);

        resourceLocation = new ResourceLocationCW();
        nifty.getResourceLoader().removeAllResourceLocations();
        nifty.getResourceLoader().addResourceLocation(resourceLocation);
    }

    private void initialize(
        final AssetManager assetManager,
        final InputManager inputManager,
        final AudioRenderer audioRenderer,
        final ViewPort viewport) {
      this.assetManager = assetManager;
      this.inputManager = inputManager;
      this.w = viewport.getCamera().getWidth();
      this.h = viewport.getCamera().getHeight();
      this.soundDev = new SoundDeviceCW(assetManager, audioRenderer);
      this.inputSys = new InputSystemCW(inputManager);
    }

    public void initialize(RenderManager rm, ViewPort vp) {
        this.renderManager = rm;
        if (renderDev != null) {
          renderDev.setRenderManager(rm);
        } else {
          batchRendererBackend.setRenderManager(rm);
        }

        if (inputManager != null) {
//            inputSys.setInputManager(inputManager);
            inputManager.addRawInputListener(inputSys);
        }
        inited = true;
        this.vp = vp;
        this.renderer = rm.getRenderer();
        
        inputSys.reset();
        inputSys.setHeight(vp.getCamera().getHeight());
    }

    public Nifty getNifty() {
        return nifty;
    }

    public void simulateKeyEvent( KeyInputEvent event ) {
        inputSys.onKeyEvent(event);        
    }

    AssetManager getAssetManager() {
        return assetManager;
    }

    RenderManager getRenderManager() {
        return renderManager;
    }

    int getHeight() {
        return h;
    }

    int getWidth() {
        return w;
    }

    Renderer getRenderer(){
        return renderer;
    }

    public void reshape(ViewPort vp, int w, int h) {
        this.w = w;
        this.h = h;
        inputSys.setHeight(h);
        nifty.resolutionChanged();
    }

    public boolean isInitialized() {
        return inited;
    }

    public void preFrame(float tpf) {
    }

    public void postQueue(RenderQueue rq) {
        // render nifty before anything else
        renderManager.setCamera(vp.getCamera(), true);
        //nifty.update();
        nifty.render(false);
        renderManager.setCamera(vp.getCamera(), false);
    }

    public void postFrame(FrameBuffer out) {
    }

    public void cleanup() {
        inited = false;
        inputSys.reset();
        if (inputManager != null) {
            inputManager.removeRawInputListener(inputSys);
        }
    }

}
