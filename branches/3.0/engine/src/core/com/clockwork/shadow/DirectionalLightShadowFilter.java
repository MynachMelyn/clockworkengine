
package com.clockwork.shadow;

import com.clockwork.asset.AssetManager;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.light.DirectionalLight;
import java.io.IOException;

/**
 *
 * This Filter does basically the same as a DirectionalLightShadowRenderer
 * except it renders the post shadow pass as a fulscreen quad pass instead of a
 * geometry pass. It's mostly faster than PssmShadowRenderer as long as you have
 * more than a about ten shadow recieving objects. The expense is the draw back
 * that the shadow Recieve mode set on spatial is ignored. So basically all and
 * only objects that render depth in the scene receive shadows.
 *
 * API is basically the same as the PssmShadowRenderer;
 *
 */
public class DirectionalLightShadowFilter extends AbstractShadowFilter<DirectionalLightShadowRenderer> {

    /**
     * Creates a DirectionalLightShadowFilter Shadow Filter More info on the
     * technique at <a
     * href="http://http.developer.nvidia.com/GPUGems3/gpugems3_ch10.html">http://http.developer.nvidia.com/GPUGems3/gpugems3_ch10.html</a>
     *
     * @param assetManager the application asset manager
     * @param shadowMapSize the size of the rendered shadowmaps (512,1024,2048,
     * etc...)
     * @param nbSplits the number of shadow maps rendered (the more shadow maps
     * the more quality, the less fps).
     */
    public DirectionalLightShadowFilter(AssetManager assetManager, int shadowMapSize, int nbSplits) {
        super(assetManager, shadowMapSize, new DirectionalLightShadowRenderer(assetManager, shadowMapSize, nbSplits));
    }

    /**
     * return the light used to cast shadows
     *
     * @return the DirectionalLight
     */
    public DirectionalLight getLight() {
        return shadowRenderer.getLight();
    }

    /**
     * Sets the light to use to cast shadows
     *
     * @param light a DirectionalLight
     */
    public void setLight(DirectionalLight light) {
        shadowRenderer.setLight(light);
    }

    /**
     * returns the labda parameter
     *
     * see #setLambda(float lambda)
     * @return lambda
     */
    public float getLambda() {
        return shadowRenderer.getLambda();
    }

    /**
     * Adjust the repartition of the different shadow maps in the shadow extend
     * usualy goes from 0.0 to 1.0 a low value give a more linear repartition
     * resulting in a constant quality in the shadow over the extends, but near
     * shadows could look very jagged a high value give a more logarithmic
     * repartition resulting in a high quality for near shadows, but the quality
     * quickly decrease over the extend. the default value is set to 0.65f
     * (theoric optimal value).
     *
     * @param lambda the lambda value.
     */
    public void setLambda(float lambda) {
        shadowRenderer.setLambda(lambda);
    }

    /**
     * How far the shadows are rendered in the view
     *
     * see setShadowZExtend(float zFar)
     * @return shadowZExtend
     */
    public float getShadowZExtend() {
        return shadowRenderer.getShadowZExtend();
    }

    /**
     * Set the distance from the eye where the shadows will be rendered default
     * value is dynamicaly computed to the shadow casters/receivers union bound
     * zFar, capped to view frustum far value.
     *
     * @param zFar the zFar values that override the computed one
     */
    public void setShadowZExtend(float zFar) {
        shadowRenderer.setShadowZExtend(zFar);
    }

    /**
     * Define the length over which the shadow will fade out when using a
     * shadowZextend
     *
     * @param length the fade length in world units
     */
    public void setShadowZFadeLength(float length) {
        shadowRenderer.setShadowZFadeLength(length);
    }

    /**
     * get the length over which the shadow will fade out when using a
     * shadowZextend
     *
     * @return the fade length in world units
     */
    public float getShadowZFadeLength() {
        return shadowRenderer.getShadowZFadeLength();
    }
    
    /**
     * retruns true if stabilization is enabled
     * @return 
     */
    public boolean isEnabledStabilization() {
        return shadowRenderer.isEnabledStabilization();
    }
    
    /**
     * Enables the stabilization of the shadows's edges. (default is true)
     * This prevents shadows' edges to flicker when the camera moves
     * However it can lead to some shadow quality loss in some particular scenes.
     * @param stabilize 
     */
    public void setEnabledStabilization(boolean stabilize) {
        shadowRenderer.setEnabledStabilization(stabilize);        
    }    

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(shadowRenderer, "shadowRenderer", null);

    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        shadowRenderer = (DirectionalLightShadowRenderer) ic.readSavable("shadowRenderer", null);
    }
}
