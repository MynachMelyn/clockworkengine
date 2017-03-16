
package com.clockwork.post;

import com.clockwork.asset.AssetManager;
import com.clockwork.export.*;
import com.clockwork.material.Material;
import com.clockwork.renderer.*;
import com.clockwork.renderer.queue.RenderQueue;
import com.clockwork.texture.FrameBuffer;
import com.clockwork.texture.Image.Format;
import com.clockwork.texture.Texture2D;
import com.clockwork.ui.Picture;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A FilterPostProcessor is a processor that can apply several filters to a rendered scene
 * It manages a list of filters that will be applied in the order in which they've been added to the list
 */
public class FilterPostProcessor implements SceneProcessor, Savable {

    private RenderManager renderManager;
    private Renderer renderer;
    private ViewPort viewPort;
    private FrameBuffer renderFrameBufferMS;
    private int numSamples = 1;
    private FrameBuffer renderFrameBuffer;
    private Texture2D filterTexture;
    private Texture2D depthTexture;
    private List<Filter> filters = new ArrayList<Filter>();
    private AssetManager assetManager;
    private Camera filterCam = new Camera(1, 1);
    private Picture fsQuad;
    private boolean computeDepth = false;
    private FrameBuffer outputBuffer;
    private int width;
    private int height;
    private float bottom;
    private float left;
    private float right;
    private float top;
    private int originalWidth;
    private int originalHeight;
    private int lastFilterIndex = -1;
    private boolean cameraInit = false;
    private boolean clearColor= true;

    /**
     * Create a FilterProcessor 
     * @param assetManager the assetManager
     */
    public FilterPostProcessor(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /**
     * Don't use this constructor, use #FilterPostProcessor(AssetManager assetManager)}
     * This constructor is used for serialisation only
     */
    public FilterPostProcessor() {
    }

    /**
     * Adds a filter to the filters list
     * @param filter the filter to add
     */
    public void addFilter(Filter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("Filter cannot be null.");
        }
        filters.add(filter);

        if (isInitialized()) {
            initFilter(filter, viewPort);
        }

        setFilterState(filter, filter.isEnabled());
    }

    /**
     * removes this filters from the filters list
     * @param filter 
     */
    public void removeFilter(Filter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("Filter cannot be null.");
        }
        filters.remove(filter);
        filter.cleanup(renderer);
        updateLastFilterIndex();
    }

    public Iterator<Filter> getFilterIterator() {
        return filters.iterator();
    }

    public void initialize(RenderManager rm, ViewPort vp) {        
        renderManager = rm;
        renderer = rm.getRenderer();
        viewPort = vp;
        fsQuad = new Picture("filter full screen quad");

        Camera cam = vp.getCamera();

        //save view port diensions
        left = cam.getViewPortLeft();
        right = cam.getViewPortRight();
        top = cam.getViewPortTop();
        bottom = cam.getViewPortBottom();
        originalWidth = cam.getWidth();
        originalHeight = cam.getHeight();
        //first call to reshape
        reshape(vp, cam.getWidth(), cam.getHeight());
    }

    /**
     * init the given filter
     * @param filter
     * @param vp 
     */
    private void initFilter(Filter filter, ViewPort vp) {
        filter.setProcessor(this);
        if (filter.isRequiresDepthTexture()) {
            if (!computeDepth && renderFrameBuffer != null) {
                depthTexture = new Texture2D(width, height, Format.Depth24);
                renderFrameBuffer.setDepthTexture(depthTexture);
            }
            computeDepth = true;
            filter.init(assetManager, renderManager, vp, width, height);
            filter.setDepthTexture(depthTexture);
        } else {
            filter.init(assetManager, renderManager, vp, width, height);
        }
    }

    /**
     * renders a filter on a fullscreen quad
     * @param r
     * @param buff
     * @param mat 
     */
    private void renderProcessing(Renderer r, FrameBuffer buff, Material mat) {
        if (buff == outputBuffer) {
            fsQuad.setWidth(width);
            fsQuad.setHeight(height);
            filterCam.resize(originalWidth, originalHeight, true);
            fsQuad.setPosition(left * originalWidth, bottom * originalHeight);
        } else {
            fsQuad.setWidth(buff.getWidth());
            fsQuad.setHeight(buff.getHeight());
            filterCam.resize(buff.getWidth(), buff.getHeight(), true);
            fsQuad.setPosition(0, 0);
        }

        if (mat.getAdditionalRenderState().isDepthWrite()) {
            mat.getAdditionalRenderState().setDepthTest(false);
            mat.getAdditionalRenderState().setDepthWrite(false);
        }

        fsQuad.setMaterial(mat);
        fsQuad.updateGeometricState();

        renderManager.setCamera(filterCam, true);
        r.setFrameBuffer(buff);        
        r.clearBuffers(clearColor, true, true);
        renderManager.renderGeometry(fsQuad);

    }
    
    public boolean isInitialized() {
        return viewPort != null;
    }

    public void postQueue(RenderQueue rq) {

        for (Iterator<Filter> it = filters.iterator(); it.hasNext();) {
            Filter filter = it.next();
            if (filter.isEnabled()) {
                filter.postQueue(rq);
            }
        }

    }
    Picture pic = new Picture("debug");

    /**
     * iterate through the filter list and renders filters
     * @param r
     * @param sceneFb 
     */
    private void renderFilterChain(Renderer r, FrameBuffer sceneFb) {
        Texture2D tex = filterTexture;
        FrameBuffer buff = sceneFb;
        boolean msDepth = depthTexture != null && depthTexture.getImage().getMultiSamples() > 1;
        for (int i = 0; i < filters.size(); i++) {
            Filter filter = filters.get(i);
            if (filter.isEnabled()) {
                if (filter.getPostRenderPasses() != null) {
                    for (Iterator<Filter.Pass> it1 = filter.getPostRenderPasses().iterator(); it1.hasNext();) {
                        Filter.Pass pass = it1.next();
                        pass.beforeRender();
                        if (pass.requiresSceneAsTexture()) {
                            pass.getPassMaterial().setTexture("Texture", tex);
                            if (tex.getImage().getMultiSamples() > 1) {
                                pass.getPassMaterial().setInt("NumSamples", tex.getImage().getMultiSamples());
                            } else {
                                pass.getPassMaterial().clearParam("NumSamples");

                            }
                        }
                        if (pass.requiresDepthAsTexture()) {
                            pass.getPassMaterial().setTexture("DepthTexture", depthTexture);
                            if (msDepth) {
                                pass.getPassMaterial().setInt("NumSamplesDepth", depthTexture.getImage().getMultiSamples());
                            } else {
                                pass.getPassMaterial().clearParam("NumSamplesDepth");
                            }
                        }
                        renderProcessing(r, pass.getRenderFrameBuffer(), pass.getPassMaterial());
                    }
                }

                filter.postFrame(renderManager, viewPort, buff, sceneFb);

                Material mat = filter.getMaterial();
                if (msDepth && filter.isRequiresDepthTexture()) {
                    mat.setInt("NumSamplesDepth", depthTexture.getImage().getMultiSamples());
                }

                if (filter.isRequiresSceneTexture()) {
                    mat.setTexture("Texture", tex);
                    if (tex.getImage().getMultiSamples() > 1) {
                        mat.setInt("NumSamples", tex.getImage().getMultiSamples());
                    } else {
                        mat.clearParam("NumSamples");
                    }
                }

                buff = outputBuffer;
                if (i != lastFilterIndex) {
                    buff = filter.getRenderFrameBuffer();
                    tex = filter.getRenderedTexture();

                }
                renderProcessing(r, buff, mat);
            }
        }
    }

    public void postFrame(FrameBuffer out) {

        FrameBuffer sceneBuffer = renderFrameBuffer;
        if (renderFrameBufferMS != null && !renderer.getCaps().contains(Caps.OpenGL31)) {
            renderer.copyFrameBuffer(renderFrameBufferMS, renderFrameBuffer);
        } else if (renderFrameBufferMS != null) {
            sceneBuffer = renderFrameBufferMS;
        }
        renderFilterChain(renderer, sceneBuffer);
        renderer.setFrameBuffer(outputBuffer);

        //viewport can be null if no filters are enabled
        if (viewPort != null) {
            renderManager.setCamera(viewPort.getCamera(), false);
        }

    }

    public void preFrame(float tpf) {
        if (filters.isEmpty() || lastFilterIndex == -1) {
            //If the camera is initialized and there are no filter to render, the camera viewport is restored as it was
            if (cameraInit) {
                viewPort.getCamera().resize(originalWidth, originalHeight, true);
                viewPort.getCamera().setViewPort(left, right, bottom, top);
                viewPort.setOutputFrameBuffer(outputBuffer);
                cameraInit = false;
            }

        } else {
           setupViewPortFrameBuffer();
            //init of the camera if it wasn't already
            if (!cameraInit) {
                viewPort.getCamera().resize(width, height, true);
                viewPort.getCamera().setViewPort(0, 1, 0, 1);
            }
        }

        for (Iterator<Filter> it = filters.iterator(); it.hasNext();) {
            Filter filter = it.next();
            if (filter.isEnabled()) {
                filter.preFrame(tpf);
            }
        }

    }

    /**
     * sets the filter to enabled or disabled
     * @param filter
     * @param enabled 
     */
    protected void setFilterState(Filter filter, boolean enabled) {
        if (filters.contains(filter)) {
            filter.enabled = enabled;
            updateLastFilterIndex();
        }
    }

    /**
     * compute the index of the last filter to render
     */
    private void updateLastFilterIndex() {
        lastFilterIndex = -1;
        for (int i = filters.size() - 1; i >= 0 && lastFilterIndex == -1; i--) {
            if (filters.get(i).isEnabled()) {
                lastFilterIndex = i;
                //the Fpp is initialized, but the viwport framebuffer is the 
                //original out framebuffer so we must recover from a situation 
                //where no filter was enabled. So we set th correc framebuffer 
                //on the viewport
                if(isInitialized() && viewPort.getOutputFrameBuffer()==outputBuffer){
                    setupViewPortFrameBuffer();
                }
                return;
            }
        }
        if (isInitialized() && lastFilterIndex == -1) {
            //There is no enabled filter, we restore the original framebuffer 
            //to the viewport to bypass the fpp.
            viewPort.setOutputFrameBuffer(outputBuffer);
        }
    }

    public void cleanup() {
        if (viewPort != null) {
            //reseting the viewport camera viewport to its initial value
            viewPort.getCamera().resize(originalWidth, originalHeight, true);
            viewPort.getCamera().setViewPort(left, right, bottom, top);
            viewPort.setOutputFrameBuffer(outputBuffer);
            viewPort = null;
            for (Filter filter : filters) {
                filter.cleanup(renderer);
            }
        }

    }

    public void reshape(ViewPort vp, int w, int h) {
        //this has no effect at first init but is useful when resizing the canvas with multi views
        Camera cam = vp.getCamera();
        cam.setViewPort(left, right, bottom, top);
        //resizing the camera to fit the new viewport and saving original dimensions
        cam.resize(w, h, false);
        left = cam.getViewPortLeft();
        right = cam.getViewPortRight();
        top = cam.getViewPortTop();
        bottom = cam.getViewPortBottom();
        originalWidth = w;
        originalHeight = h;
        cam.setViewPort(0, 1, 0, 1);

        //computing real dimension of the viewport and resizing he camera 
        width = (int) (w * (Math.abs(right - left)));
        height = (int) (h * (Math.abs(bottom - top)));
        width = Math.max(1, width);
        height = Math.max(1, height);
        
        //Testing original versus actual viewport dimension.
        //If they are different we are in a multiview situation and color from other view port must not be cleared.
        //However, not clearing the color can cause issues when AlphaToCoverage is active on the renderer.        
        if(originalWidth!=width || originalHeight!=height){
            clearColor = false;
        }else{
            clearColor = true;
        }
        
        cam.resize(width, height, false);
        cameraInit = true;
        computeDepth = false;

        if (renderFrameBuffer == null) {
            outputBuffer = viewPort.getOutputFrameBuffer();
        }

        Collection<Caps> caps = renderer.getCaps();

        //antialiasing on filters only supported in opengl 3 due to depth read problem
        if (numSamples > 1 && caps.contains(Caps.FrameBufferMultisample)) {
            renderFrameBufferMS = new FrameBuffer(width, height, numSamples);
            if (caps.contains(Caps.OpenGL31)) {
                Texture2D msColor = new Texture2D(width, height, numSamples, Format.RGBA8);
                Texture2D msDepth = new Texture2D(width, height, numSamples, Format.Depth);
                renderFrameBufferMS.setDepthTexture(msDepth);
                renderFrameBufferMS.setColorTexture(msColor);
                filterTexture = msColor;
                depthTexture = msDepth;
            } else {
                renderFrameBufferMS.setDepthBuffer(Format.Depth);
                renderFrameBufferMS.setColorBuffer(Format.RGBA8);
            }
        }

        if (numSamples <= 1 || !caps.contains(Caps.OpenGL31)) {
            renderFrameBuffer = new FrameBuffer(width, height, 1);
            renderFrameBuffer.setDepthBuffer(Format.Depth);
            filterTexture = new Texture2D(width, height, Format.RGBA8);
            renderFrameBuffer.setColorTexture(filterTexture);
        }

        for (Iterator<Filter> it = filters.iterator(); it.hasNext();) {
            Filter filter = it.next();
            initFilter(filter, vp);
        }
        setupViewPortFrameBuffer();
    }

    /**
     * return the number of samples for antialiasing
     * @return numSamples
     */
    public int getNumSamples() {
        return numSamples;
    }

    /**
     *
     * Removes all the filters from this processor
     */
    public void removeAllFilters() {
        filters.clear();
        updateLastFilterIndex();
    }

    /**
     * Sets the number of samples for antialiasing
     * @param numSamples the number of Samples
     */
    public void setNumSamples(int numSamples) {
        if (numSamples <= 0) {
            throw new IllegalArgumentException("numSamples must be > 0");
        }

        this.numSamples = numSamples;
    }

    /**
     * Sets the asset manager for this processor
     * @param assetManager
     */
    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void write(CWExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(numSamples, "numSamples", 0);
        oc.writeSavableArrayList((ArrayList) filters, "filters", null);
    }

    public void read(CWImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        numSamples = ic.readInt("numSamples", 0);
        filters = ic.readSavableArrayList("filters", null);
        for (Filter filter : filters) {
            filter.setProcessor(this);
            setFilterState(filter, filter.isEnabled());
        }
        assetManager = im.getAssetManager();
    }

    /**
     * For internal use only
     * returns the depth texture of the scene
     * @return the depth texture
     */
    public Texture2D getDepthTexture() {
        return depthTexture;
    }

    /**
     * For internal use only
     * returns the rendered texture of the scene
     * @return the filter texture
     */
    public Texture2D getFilterTexture() {
        return filterTexture;
    }
    
    /**
     * returns the first filter in the list assignable form the given type 
     * @param <T> 
     * @param filterType the filter type
     * @return a filter assignable form the given type 
     */
    public <T extends Filter> T getFilter(Class<T> filterType) {
        for (Filter c : filters) {
            if (filterType.isAssignableFrom(c.getClass())) {
                return (T) c;
            }
        }
        return null;
    }
    
    /**
     * returns an unmodifiable version of the filter list.
     * @return the filters list
     */
    public List<Filter> getFilterList(){
        return Collections.unmodifiableList(filters);
    }

    private void setupViewPortFrameBuffer() {
        if (renderFrameBufferMS != null) {
            viewPort.setOutputFrameBuffer(renderFrameBufferMS);
        } else {
            viewPort.setOutputFrameBuffer(renderFrameBuffer);
        }
    }
}
