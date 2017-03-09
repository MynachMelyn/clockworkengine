
package com.clockwork.post.filters;

import com.clockwork.asset.AssetManager;
import com.clockwork.effect.ParticleEmitter;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.post.Filter;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.Renderer;
import com.clockwork.renderer.ViewPort;
import com.clockwork.renderer.queue.RenderQueue;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.texture.FrameBuffer;
import com.clockwork.texture.Texture;
import com.clockwork.texture.Texture2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This filter handles translucent objects when rendering a scene with filters that use depth like WaterFilter and SSAOFilter
 * To use, instantiate a TranslucentBucketFilter and add it to the Filter list of a FilterPostProcessor
 */
public final class TranslucentBucketFilter extends Filter {

    private final static Logger logger = Logger.getLogger(TranslucentBucketFilter.class.getName());
    private RenderManager renderManager;
    private boolean enabledSoftParticles = false;
    private Texture depthTexture;
    private ViewPort viewPort;

    public TranslucentBucketFilter() {
        super("TranslucentBucketFilter");
    }

    public TranslucentBucketFilter(boolean enabledSoftParticles) {
        this();
        this.enabledSoftParticles = enabledSoftParticles;
    }

    @Override
    protected void initFilter(AssetManager manager, RenderManager rm, ViewPort vp, int w, int h) {
        this.renderManager = rm;
        this.viewPort = vp;
        material = new Material(manager, "Common/MatDefs/Post/Overlay.j3md");
        material.setColor("Color", ColorRGBA.White);
        Texture2D tex = processor.getFilterTexture();
        material.setTexture("Texture", tex);
        if (tex.getImage().getMultiSamples() > 1) {
            material.setInt("NumSamples", tex.getImage().getMultiSamples());
        } else {
            material.clearParam("NumSamples");
        }
        renderManager.setHandleTranslucentBucket(false);
        if (enabledSoftParticles && depthTexture != null) {
            initSoftParticles(vp, true);
        }
    }

    private void initSoftParticles(ViewPort vp, boolean enabledSP) {
        if (depthTexture != null) {
            for (Spatial scene : vp.getScenes()) {
                makeSoftParticleEmitter(scene, enabledSP && enabled);
            }
        }

    }

    @Override
    protected void setDepthTexture(Texture depthTexture) {
        this.depthTexture = depthTexture;
        if (enabledSoftParticles && depthTexture != null) {
            initSoftParticles(viewPort, true);
        }
    }

    /**
     * Override this method and return false if your Filter does not need the scene texture
     * @return
     */
    @Override
    protected boolean isRequiresSceneTexture() {
        return false;
    }

    @Override
    protected boolean isRequiresDepthTexture() {
        return enabledSoftParticles;
    }

    @Override
    protected void postFrame(RenderManager renderManager, ViewPort viewPort, FrameBuffer prevFilterBuffer, FrameBuffer sceneBuffer) {
        renderManager.setCamera(viewPort.getCamera(), false);
        if (prevFilterBuffer != sceneBuffer) {
            renderManager.getRenderer().copyFrameBuffer(prevFilterBuffer, sceneBuffer, false);
        }
        renderManager.getRenderer().setFrameBuffer(sceneBuffer);
        viewPort.getQueue().renderQueue(RenderQueue.Bucket.Translucent, renderManager, viewPort.getCamera());
    }

    @Override
    protected void cleanUpFilter(Renderer r) {
        if (renderManager != null) {
            renderManager.setHandleTranslucentBucket(true);
        }

        initSoftParticles(viewPort, false);
    }

    @Override
    protected Material getMaterial() {
        return material;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (renderManager != null) {
            renderManager.setHandleTranslucentBucket(!enabled);
        }
        initSoftParticles(viewPort, enabledSoftParticles);
    }

    private void makeSoftParticleEmitter(Spatial scene, boolean enabled) {
        if (scene instanceof Node) {
            Node n = (Node) scene;
            for (Spatial child : n.getChildren()) {
                makeSoftParticleEmitter(child, enabled);
            }
        }
        if (scene instanceof ParticleEmitter) {
            ParticleEmitter emitter = (ParticleEmitter) scene;
            if (enabled) {
                enabledSoftParticles = enabled;

                if( processor.getNumSamples()>1){
                    emitter.getMaterial().selectTechnique("SoftParticles15", renderManager);
                    emitter.getMaterial().setInt("NumSamplesDepth", processor.getNumSamples());
                }else{
                    emitter.getMaterial().selectTechnique("SoftParticles", renderManager);
                }
                emitter.getMaterial().setTexture("DepthTexture", processor.getDepthTexture());               
                emitter.setQueueBucket(RenderQueue.Bucket.Translucent);

                logger.log(Level.FINE, "Made particle Emitter {0} soft.", emitter.getName());
            } else {
                emitter.getMaterial().clearParam("DepthTexture");
                emitter.getMaterial().selectTechnique("Default", renderManager);
               // emitter.setQueueBucket(RenderQueue.Bucket.Transparent);
                logger.log(Level.FINE, "Particle Emitter {0} is not soft anymore.", emitter.getName());
            }
        }
    }
}
