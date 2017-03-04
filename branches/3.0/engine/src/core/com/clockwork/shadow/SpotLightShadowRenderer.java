
package com.clockwork.shadow;

import com.clockwork.asset.AssetManager;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.light.SpotLight;
import com.clockwork.material.Material;
import com.clockwork.math.FastMath;
import com.clockwork.math.Vector2f;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.Camera;
import com.clockwork.renderer.queue.GeometryList;
import com.clockwork.scene.Node;
import java.io.IOException;

/**
 * SpotLightShadowRenderer renderer use Parrallel Split Shadow Mapping technique
 * (pssm)<br> It splits the view frustum in several parts and compute a shadow
 * map for each one.<br> splits are distributed so that the closer they are from
 * the camera, the smaller they are to maximize the resolution used of the
 * shadow map.<br> This result in a better quality shadow than standard shadow
 * mapping.<br> for more informations on this read this <a
 * href="http://http.developer.nvidia.com/GPUGems3/gpugems3_ch10.html">http://http.developer.nvidia.com/GPUGems3/gpugems3_ch10.html</a><br>
 * <p/>
 */
public class SpotLightShadowRenderer extends AbstractShadowRenderer {

    protected float zFarOverride = 0;
    protected Camera shadowCam;    
    protected SpotLight light;
    protected Vector3f[] points = new Vector3f[8];
    //Holding the info for fading shadows in the far distance 
    protected Vector2f fadeInfo;
    protected float fadeLength;

    
    /**
     * Used for serialisation use SpotLightShadowRenderer#SpotLightShadowRenderer(AssetManager assetManager, int shadowMapSize)
     */
    public SpotLightShadowRenderer() {
        super();
    }
    
    /**
     * Create a SpotLightShadowRenderer This use standard shadow mapping
     *
     * @param assetManager the application asset manager
     * @param shadowMapSize the size of the rendered shadowmaps (512,1024,2048,
     * etc...) the more quality, the less fps).
     */
    public SpotLightShadowRenderer(AssetManager assetManager, int shadowMapSize) {
        super(assetManager, shadowMapSize, 1);
        init(shadowMapSize);
    }

    
    private void init(int shadowMapSize) {
        shadowCam = new Camera(shadowMapSize, shadowMapSize);
        for (int i = 0; i < points.length; i++) {
            points[i] = new Vector3f();
        }
    }
    
    /**
     * return the light used to cast shadows
     *
     * @return the SpotLight
     */
    public SpotLight getLight() {
        return light;
    }

    /**
     * Sets the light to use to cast shadows
     *
     * @param light a SpotLight
     */
    public void setLight(SpotLight light) {
        this.light = light;
    }

    @Override
    protected void updateShadowCams(Camera viewCam) {

        float zFar = zFarOverride;
        if (zFar == 0) {
            zFar = viewCam.getFrustumFar();
        }

        //We prevent computing the frustum points and splits with zeroed or negative near clip value
        float frustumNear = Math.max(viewCam.getFrustumNear(), 0.001f);
        ShadowUtil.updateFrustumPoints(viewCam, frustumNear, zFar, 1.0f, points);
        //shadowCam.setDirection(direction);

        shadowCam.setFrustumPerspective(light.getSpotOuterAngle() * FastMath.RAD_TO_DEG * 2.0f, 1, 1f, light.getSpotRange());
        shadowCam.getRotation().lookAt(light.getDirection(), shadowCam.getUp());
        shadowCam.setLocation(light.getPosition());

        shadowCam.update();
        shadowCam.updateViewProjection();

    }

    @Override
    protected GeometryList getOccludersToRender(int shadowMapIndex, GeometryList sceneOccluders, GeometryList sceneReceivers, GeometryList shadowMapOccluders) {
        ShadowUtil.getGeometriesInCamFrustum(sceneOccluders, shadowCam, shadowMapOccluders);
        return shadowMapOccluders;
    }

    @Override
    GeometryList getReceivers(GeometryList sceneReceivers, GeometryList lightReceivers) {
        lightReceivers.clear();
        ShadowUtil.getGeometriesInCamFrustum(sceneReceivers, shadowCam, lightReceivers);
        return lightReceivers;
    }
    
    @Override
    protected Camera getShadowCam(int shadowMapIndex) {
        return shadowCam;
    }

    @Override
    protected void doDisplayFrustumDebug(int shadowMapIndex) {
        Vector3f[] points2 = points.clone();

        ((Node) viewPort.getScenes().get(0)).attachChild(createFrustum(points, shadowMapIndex));
        ShadowUtil.updateFrustumPoints2(shadowCam, points2);
        ((Node) viewPort.getScenes().get(0)).attachChild(createFrustum(points2, shadowMapIndex));
    }

    @Override
    protected void setMaterialParameters(Material material) {    
    }

    /**
     * How far the shadows are rendered in the view
     *
     * @see #setShadowZExtend(float zFar)
     * @return shadowZExtend
     */
    public float getShadowZExtend() {
        return zFarOverride;
    }

    /**
     * Set the distance from the eye where the shadows will be rendered default
     * value is dynamicaly computed to the shadow casters/receivers union bound
     * zFar, capped to view frustum far value.
     *
     * @param zFar the zFar values that override the computed one
     */
    public void setShadowZExtend(float zFar) {
        if (fadeInfo != null) {
            fadeInfo.set(zFar - fadeLength, 1f / fadeLength);
        }
        this.zFarOverride = zFar;

    }

    /**
     * Define the length over which the shadow will fade out when using a
     * shadowZextend This is useful to make dynamic shadows fade into baked
     * shadows in the distance.
     *
     * @param length the fade length in world units
     */
    public void setShadowZFadeLength(float length) {
        if (length == 0) {
            fadeInfo = null;
            fadeLength = 0;
            postshadowMat.clearParam("FadeInfo");
        } else {
            if (zFarOverride == 0) {
                fadeInfo = new Vector2f(0, 0);
            } else {
                fadeInfo = new Vector2f(zFarOverride - length, 1.0f / length);
            }
            fadeLength = length;
            postshadowMat.setVector2("FadeInfo", fadeInfo);
        }
    }

    /**
     * get the length over which the shadow will fade out when using a
     * shadowZextend
     *
     * @return the fade length in world units
     */
    public float getShadowZFadeLength() {
        if (fadeInfo != null) {
            return zFarOverride - fadeInfo.x;
        }
        return 0f;
    }
    
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = (InputCapsule) im.getCapsule(this);
        zFarOverride = ic.readInt("zFarOverride", 0);
        light = (SpotLight) ic.readSavable("light", null);
        fadeInfo = (Vector2f) ic.readSavable("fadeInfo", null);
        fadeLength = ic.readFloat("fadeLength", 0f);
        init((int) shadowMapSize);

    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = (OutputCapsule) ex.getCapsule(this);        
        oc.write(zFarOverride, "zFarOverride", 0);
        oc.write(light, "light", null);
        oc.write(fadeInfo, "fadeInfo", null);
        oc.write(fadeLength, "fadeLength", 0f);
    }

}
