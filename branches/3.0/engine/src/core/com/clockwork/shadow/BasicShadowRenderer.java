
package com.clockwork.shadow;

import com.clockwork.asset.AssetManager;
import com.clockwork.material.Material;
import com.clockwork.math.Vector3f;
import com.clockwork.post.SceneProcessor;
import com.clockwork.renderer.Camera;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.Renderer;
import com.clockwork.renderer.ViewPort;
import com.clockwork.renderer.queue.GeometryList;
import com.clockwork.renderer.queue.RenderQueue;
import com.clockwork.renderer.queue.RenderQueue.ShadowMode;
import com.clockwork.texture.FrameBuffer;
import com.clockwork.texture.Image.Format;
import com.clockwork.texture.Texture2D;
import com.clockwork.ui.Picture;

/**
 * BasicShadowRenderer uses standard shadow mapping with one map
 * it's useful to render shadows in a small scene, but edges might look a bit jagged.
 * 
 * 
 * @deprecated use {@link DirectionalLightShadowRenderer} with one split.
 */
@Deprecated
public class BasicShadowRenderer implements SceneProcessor {

    private RenderManager renderManager;
    private ViewPort viewPort;
    private FrameBuffer shadowFB;
    private Texture2D shadowMap;
    private Camera shadowCam;
    private Material preshadowMat;
    private Material postshadowMat;
    private Picture dispPic = new Picture("Picture");
    private boolean noOccluders = false;
    private Vector3f[] points = new Vector3f[8];
    private Vector3f direction = new Vector3f();
    protected Texture2D dummyTex;
    private float shadowMapSize;

    /**
     * Creates a BasicShadowRenderer
     * @param manager the asset manager
     * @param size the size of the shadow map (the map is square)
     */
    public BasicShadowRenderer(AssetManager manager, int size) {
        shadowFB = new FrameBuffer(size, size, 1);
        shadowMap = new Texture2D(size, size, Format.Depth);
        shadowFB.setDepthTexture(shadowMap);
        shadowCam = new Camera(size, size);
        
         //DO NOT COMMENT THIS (it prevent the OSX incomplete read buffer crash)
        dummyTex = new Texture2D(size, size, Format.RGBA8);        
        shadowFB.setColorTexture(dummyTex);
        shadowMapSize = (float)size;
        preshadowMat = new Material(manager, "Common/MatDefs/Shadow/PreShadow.j3md");
        postshadowMat = new Material(manager, "Common/MatDefs/Shadow/BasicPostShadow.j3md");
        postshadowMat.setTexture("ShadowMap", shadowMap);

        dispPic.setTexture(manager, shadowMap, false);

        for (int i = 0; i < points.length; i++) {
            points[i] = new Vector3f();
        }
    }

    public void initialize(RenderManager rm, ViewPort vp) {
        renderManager = rm;
        viewPort = vp;

        reshape(vp, vp.getCamera().getWidth(), vp.getCamera().getHeight());
    }

    public boolean isInitialized() {
        return viewPort != null;
    }

    /**
     * returns the light direction used for this processor
     * @return 
     */
    public Vector3f getDirection() {
        return direction;
    }

    /**
     * sets the light direction to use to computs shadows
     * @param direction 
     */
    public void setDirection(Vector3f direction) {
        this.direction.set(direction).normalizeLocal();
    }

    /**
     * debug only
     * @return 
     */
    public Vector3f[] getPoints() {
        return points;
    }

    /**
     * debug only
     * returns the shadow camera 
     * @return 
     */
    public Camera getShadowCamera() {
        return shadowCam;
    }

    public void postQueue(RenderQueue rq) {
        GeometryList occluders = rq.getShadowQueueContent(ShadowMode.Cast);
        if (occluders.size() == 0) {
            noOccluders = true;
            return;
        } else {
            noOccluders = false;
        }

        GeometryList receivers = rq.getShadowQueueContent(ShadowMode.Receive);

        // update frustum points based on current camera
        Camera viewCam = viewPort.getCamera();
        ShadowUtil.updateFrustumPoints(viewCam,
                viewCam.getFrustumNear(),
                viewCam.getFrustumFar(),
                1.0f,
                points);

        Vector3f frustaCenter = new Vector3f();
        for (Vector3f point : points) {
            frustaCenter.addLocal(point);
        }
        frustaCenter.multLocal(1f / 8f);

        // update light direction
        shadowCam.setProjectionMatrix(null);
        shadowCam.setParallelProjection(true);
//        shadowCam.setFrustumPerspective(45, 1, 1, 20);

        shadowCam.lookAtDirection(direction, Vector3f.UNIT_Y);
        shadowCam.update();
        shadowCam.setLocation(frustaCenter);
        shadowCam.update();
        shadowCam.updateViewProjection();

        // render shadow casters to shadow map
        ShadowUtil.updateShadowCamera(occluders, receivers, shadowCam, points, shadowMapSize);

        Renderer r = renderManager.getRenderer();
        renderManager.setCamera(shadowCam, false);
        renderManager.setForcedMaterial(preshadowMat);

        r.setFrameBuffer(shadowFB);
        r.clearBuffers(false, true, false);
        viewPort.getQueue().renderShadowQueue(ShadowMode.Cast, renderManager, shadowCam, true);
        r.setFrameBuffer(viewPort.getOutputFrameBuffer());

        renderManager.setForcedMaterial(null);
        renderManager.setCamera(viewCam, false);
    }

    /**
     * debug only
     * @return 
     */
    public Picture getDisplayPicture() {
        return dispPic;
    }

    public void postFrame(FrameBuffer out) {
        if (!noOccluders) {
            postshadowMat.setMatrix4("LightViewProjectionMatrix", shadowCam.getViewProjectionMatrix());
            renderManager.setForcedMaterial(postshadowMat);
            viewPort.getQueue().renderShadowQueue(ShadowMode.Receive, renderManager, viewPort.getCamera(), true);
            renderManager.setForcedMaterial(null);
        }
    }

    public void preFrame(float tpf) {
    }

    public void cleanup() {
    }

    public void reshape(ViewPort vp, int w, int h) {
        dispPic.setPosition(w / 20f, h / 20f);
        dispPic.setWidth(w / 5f);
        dispPic.setHeight(h / 5f);
    }
}
