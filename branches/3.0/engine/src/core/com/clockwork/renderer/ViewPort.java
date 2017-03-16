
package com.clockwork.renderer;

import com.clockwork.math.ColorRGBA;
import com.clockwork.post.SceneProcessor;
import com.clockwork.renderer.queue.RenderQueue;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Spatial;
import com.clockwork.texture.FrameBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * A ViewPort represents a view inside the display
 * window or a FrameBuffer} to which scenes will be rendered. 
 * 
 * A viewport has a #ViewPort(java.lang.String, com.clockwork.renderer.Camera) camera}
 * which is used to render a set of #attachScene(com.clockwork.scene.Spatial) scenes}.
 * A view port has a location on the screen as set by the 
 * Camera#setViewPort(float, float, float, float) } method.
 * By default, a view port does not clear the framebuffer, but it can be
 * set to #setClearFlags(boolean, boolean, boolean) clear the framebuffer}.
 * The background color which the color buffer is cleared to can be specified 
 * via the #setBackgroundColor(com.clockwork.math.ColorRGBA)} method.
 * 
 * A ViewPort has a list of SceneProcessor}s which can
 * control how the ViewPort is rendered by the RenderManager}.
 * 
 * 
 * see RenderManager
 * see SceneProcessor
 * see Spatial
 * see Camera
 */
public class ViewPort {

    protected final String name;
    protected final Camera cam;
    protected final RenderQueue queue = new RenderQueue();
    protected final ArrayList<Spatial> sceneList = new ArrayList<Spatial>();
    protected final ArrayList<SceneProcessor> processors = new ArrayList<SceneProcessor>();
    protected FrameBuffer out = null;

    protected final ColorRGBA backColor = new ColorRGBA(0,0,0,0);
    protected boolean clearDepth = false, clearColor = false, clearStencil = false;
    private boolean enabled = true;

    /**
     * Create a new viewport. User code should generally use these methods instead:
     * 
     * RenderManager#createPreView(java.lang.String, com.clockwork.renderer.Camera) }
     * RenderManager#createMainView(java.lang.String, com.clockwork.renderer.Camera)  }
     * RenderManager#createPostView(java.lang.String, com.clockwork.renderer.Camera)  }
     * 
     * 
     * @param name The name of the viewport. Used for debugging only.
     * @param cam The camera through which the viewport is rendered. The camera
     * cannot be swapped to a different one after creating the viewport.
     */
    public ViewPort(String name, Camera cam) {
        this.name = name;
        this.cam = cam;
    }

    /**
     * Returns the name of the viewport as set in the constructor.
     * 
     * @return the name of the viewport
     * 
     * see #ViewPort(java.lang.String, com.clockwork.renderer.Camera) 
     */
    public String getName() {
        return name;
    }

    /**
     * Get the list of SceneProcessor scene processors} that were
     * added to this ViewPort
     * 
     * @return the list of processors attached to this ViewPort
     * 
     * see #addProcessor(com.clockwork.post.SceneProcessor) 
     */
    public List<SceneProcessor> getProcessors(){
        return processors;
    }

    /**
     * Adds a SceneProcessor} to this ViewPort.
     * 
     * SceneProcessors that are added to the ViewPort will be notified
     * of events as the ViewPort is being rendered by the RenderManager}.
     * 
     * @param processor The processor to add
     * 
     * see SceneProcessor
     */
    public void addProcessor(SceneProcessor processor){
        if (processor == null) {
            throw new IllegalArgumentException( "Processor cannot be null." );
        }
        processors.add(processor);
    }

    /**
     * Removes a SceneProcessor} from this ViewPort.
     * 
     * The processor will no longer receive events occurring to this ViewPort.
     * 
     * @param processor The processor to remove
     * 
     * see SceneProcessor
     */
    public void removeProcessor(SceneProcessor processor){
        if (processor == null) {
            throw new IllegalArgumentException( "Processor cannot be null." );
        }
        processors.remove(processor);
        processor.cleanup();
    }
    
    /**
     * Removes all SceneProcessor scene processors} from this
     * ViewPort. 
     * 
     * see SceneProcessor
     */
    public void clearProcessors() {
        for (SceneProcessor proc : processors) {
            proc.cleanup();
        }
        processors.clear();
    }

    /**
     * Check if depth buffer clearing is enabled.
     * 
     * @return true if depth buffer clearing is enabled.
     * 
     * see #setClearDepth(boolean) 
     */
    public boolean isClearDepth() {
        return clearDepth;
    }

    /**
     * Enable or disable clearing of the depth buffer for this ViewPort.
     * 
     * By default depth clearing is disabled.
     * 
     * @param clearDepth Enable/disable depth buffer clearing.
     */
    public void setClearDepth(boolean clearDepth) {
        this.clearDepth = clearDepth;
    }

    /**
     * Check if color buffer clearing is enabled.
     * 
     * @return true if color buffer clearing is enabled.
     * 
     * see #setClearColor(boolean) 
     */
    public boolean isClearColor() {
        return clearColor;
    }

    /**
     * Enable or disable clearing of the color buffer for this ViewPort.
     * 
     * By default color clearing is disabled.
     * 
     * @param clearColor Enable/disable color buffer clearing.
     */
    public void setClearColor(boolean clearColor) {
        this.clearColor = clearColor;
    }

    /**
     * Check if stencil buffer clearing is enabled.
     * 
     * @return true if stencil buffer clearing is enabled.
     * 
     * see #setClearStencil(boolean) 
     */
    public boolean isClearStencil() {
        return clearStencil;
    }

    /**
     * Enable or disable clearing of the stencil buffer for this ViewPort.
     * 
     * By default stencil clearing is disabled.
     * 
     * @param clearStencil Enable/disable stencil buffer clearing.
     */
    public void setClearStencil(boolean clearStencil) {
        this.clearStencil = clearStencil;
    }

    /**
     * Set the clear flags (color, depth, stencil) in one call.
     * 
     * @param color If color buffer clearing should be enabled.
     * @param depth If depth buffer clearing should be enabled.
     * @param stencil If stencil buffer clearing should be enabled.
     * 
     * see #setClearColor(boolean) 
     * see #setClearDepth(boolean) 
     * see #setClearStencil(boolean) 
     */
    public void setClearFlags(boolean color, boolean depth, boolean stencil){
        this.clearColor = color;
        this.clearDepth = depth;
        this.clearStencil = stencil;
    }

    /**
     * Returns the framebuffer where this ViewPort's scenes are
     * rendered to.
     * 
     * @return the framebuffer where this ViewPort's scenes are
     * rendered to.
     * 
     * see #setOutputFrameBuffer(com.clockwork.texture.FrameBuffer) 
     */
    public FrameBuffer getOutputFrameBuffer() {
        return out;
    }

    /**
     * Sets the output framebuffer for the ViewPort.
     * 
     * The output framebuffer specifies where the scenes attached
     * to this ViewPort are rendered to. By default this is null
     * which indicates the scenes are rendered to the display window.
     * 
     * @param out The framebuffer to render scenes to, or null if to render
     * to the screen.
     */
    public void setOutputFrameBuffer(FrameBuffer out) {
        this.out = out;
    }

    /**
     * Returns the camera which renders the attached scenes.
     * 
     * @return the camera which renders the attached scenes.
     * 
     * see Camera
     */
    public Camera getCamera() {
        return cam;
    }

    /**
     * Internal use only.
     */
    public RenderQueue getQueue() {
        return queue;
    }

    /**
     * Attaches a new scene to render in this ViewPort.
     * 
     * @param scene The scene to attach
     * 
     * see Spatial
     */
    public void attachScene(Spatial scene){
        if (scene == null) {
            throw new IllegalArgumentException( "Scene cannot be null." );
        }
        sceneList.add(scene);
        if (scene instanceof Geometry) {
            scene.forceRefresh(true, false, true);
        }
    }

    /**
     * Detaches a scene from rendering.
     * 
     * @param scene The scene to detach
     * 
     * see #attachScene(com.clockwork.scene.Spatial) 
     */
    public void detachScene(Spatial scene){
        if (scene == null) {
            throw new IllegalArgumentException( "Scene cannot be null." );
        }
        sceneList.remove(scene);
        if (scene instanceof Geometry) {
            scene.forceRefresh(true, false, true);
        }
    }

    /**
     * Removes all attached scenes.
     * 
     * see #attachScene(com.clockwork.scene.Spatial) 
     */
    public void clearScenes() {
        sceneList.clear();
    }

    /**
     * Returns a list of all attached scenes.
     * 
     * @return a list of all attached scenes.
     * 
     * see #attachScene(com.clockwork.scene.Spatial) 
     */
    public List<Spatial> getScenes(){
        return sceneList;
    }

    /**
     * Sets the background color.
     * 
     * When the ViewPort's color buffer is cleared 
     * (if #setClearColor(boolean) color clearing} is enabled), 
     * this specifies the color to which the color buffer is set to.
     * By default the background color is black without alpha.
     * 
     * @param background the background color.
     */
    public void setBackgroundColor(ColorRGBA background){
        backColor.set(background);
    }

    /**
     * Returns the background color of this ViewPort
     * 
     * @return the background color of this ViewPort
     * 
     * see #setBackgroundColor(com.clockwork.math.ColorRGBA) 
     */
    public ColorRGBA getBackgroundColor(){
        return backColor;
    }
    
    /**
     * Enable or disable this ViewPort.
     * 
     * Disabled ViewPorts are skipped by the RenderManager} when
     * rendering. By default all ViewPorts are enabled.
     * 
     * @param enable If the viewport should be disabled or enabled.
     */
    public void setEnabled(boolean enable) {
        this.enabled = enable;
    }
    
    /**
     * Returns true if the viewport is enabled, false otherwise.
     * @return true if the viewport is enabled, false otherwise.
     * see #setEnabled(boolean) 
     */
    public boolean isEnabled() {
        return enabled;
    }

}
