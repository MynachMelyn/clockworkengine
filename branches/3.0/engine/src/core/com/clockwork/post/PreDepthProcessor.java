
package com.clockwork.post;

import com.clockwork.asset.AssetManager;
import com.clockwork.material.Material;
import com.clockwork.material.RenderState;
import com.clockwork.material.RenderState.FaceCullMode;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.renderer.queue.RenderQueue;
import com.clockwork.texture.FrameBuffer;

/**
 * Processor that lays depth first, this can improve performance in complex
 * scenes.
 */
public class PreDepthProcessor implements SceneProcessor {

    private RenderManager rm;
    private ViewPort vp;
    private AssetManager assetManager;
    private Material preDepth;
    private RenderState forcedRS;

    public PreDepthProcessor(AssetManager assetManager){
        this.assetManager = assetManager;
        preDepth = new Material(assetManager, "Common/MatDefs/Shadow/PreShadow.j3md");
        preDepth.getAdditionalRenderState().setPolyOffset(0, 0);
        preDepth.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);

        forcedRS = new RenderState();
        forcedRS.setDepthTest(true);
        forcedRS.setDepthWrite(false);
    }

    public void initialize(RenderManager rm, ViewPort vp) {
        this.rm = rm;
        this.vp = vp;
    }

    public void reshape(ViewPort vp, int w, int h) {
        this.vp = vp;
    }

    public boolean isInitialized() {
        return vp != null;
    }

    public void preFrame(float tpf) {
    }

    public void postQueue(RenderQueue rq) {
        // lay depth first
        rm.setForcedMaterial(preDepth);
        rq.renderQueue(RenderQueue.Bucket.Opaque, rm, vp.getCamera(), false);
        rm.setForcedMaterial(null);

        rm.setForcedRenderState(forcedRS);
    }

    public void postFrame(FrameBuffer out) {
        rm.setForcedRenderState(null);
    }

    public void cleanup() {
        vp = null;
    }

}
