
package com.clockwork.shadow;

import com.clockwork.asset.AssetManager;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.light.SpotLight;
import java.io.IOException;

/**
 *
 * This Filter does basically the same as a SpotLightShadowRenderer except it
 * renders the post shadow pass as a fulscreen quad pass instead of a geometry
 * pass. It's mostly faster than PssmShadowRenderer as long as you have more
 * than a about ten shadow recieving objects. The expense is the draw back that
 * the shadow Recieve mode set on spatial is ignored. So basically all and only
 * objects that render depth in the scene receive shadows. See this post for
 * more details
 * http://jmonkeyengine.org/groups/general-2/forum/topic/silly-question-about-shadow-rendering/#post-191599
 *
 * API is basically the same as the PssmShadowRenderer;
 *
 */
public class SpotLightShadowFilter extends AbstractShadowFilter<SpotLightShadowRenderer> {

    /**
     * Creates a SpotLight Shadow Filter
     *
     * @param assetManager the application asset manager
     * @param shadowMapSize the size of the rendered shadowmaps (512,1024,2048,
     * etc...) the more quality, the less fps).
     */
    public SpotLightShadowFilter(AssetManager assetManager, int shadowMapSize) {
        super(assetManager, shadowMapSize, new SpotLightShadowRenderer(assetManager, shadowMapSize));
    }

    /**
     * return the light used to cast shadows
     *
     * @return the SpotLight
     */
    public SpotLight getLight() {
        return shadowRenderer.getLight();
    }

    /**
     * Sets the light to use to cast shadows
     *
     * @param light a SpotLight
     */
    public void setLight(SpotLight light) {
        shadowRenderer.setLight(light);
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

    @Override
    public void write(CWExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(shadowRenderer, "shadowRenderer", null);

    }

    @Override
    public void read(CWImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        shadowRenderer = (SpotLightShadowRenderer) ic.readSavable("shadowRenderer", null);
    }
}
