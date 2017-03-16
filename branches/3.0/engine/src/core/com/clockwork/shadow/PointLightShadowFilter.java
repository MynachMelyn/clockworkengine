
package com.clockwork.shadow;

import com.clockwork.asset.AssetManager;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.light.PointLight;
import java.io.IOException;

/**
 *
 * This Filter does basically the same as a PointLightShadowRenderer except it
 * renders the post shadow pass as a fulscreen quad pass instead of a geometry
 * pass. It's mostly faster than PointLightShadowRenderer as long as you have
 * more than a about ten shadow recieving objects. The expense is the draw back
 * that the shadow Recieve mode set on spatial is ignored. So basically all and
 * only objects that render depth in the scene receive shadows. See this post
 * for more details
 * http://jmonkeyengine.org/groups/general-2/forum/topic/silly-question-about-shadow-rendering/#post-191599
 *
 * API is basically the same as the PssmShadowRenderer;
 *
 */
public class PointLightShadowFilter extends AbstractShadowFilter<PointLightShadowRenderer> {

    /**
     * Creates a PointLightShadowFilter
     *
     * @param assetManager the application asset manager
     * @param shadowMapSize the size of the rendered shadowmaps (512,1024,2048,
     * etc...)
     */
    public PointLightShadowFilter(AssetManager assetManager, int shadowMapSize) {
        super(assetManager, shadowMapSize, new PointLightShadowRenderer(assetManager, shadowMapSize));
    }

    /**
     * gets the point light used to cast shadows with this processor
     *
     * @return the point light
     */
    public PointLight getLight() {
        return shadowRenderer.getLight();
    }

    /**
     * sets the light to use for casting shadows with this processor
     *
     * @param light the point light
     */
    public void setLight(PointLight light) {
        shadowRenderer.setLight(light);
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
        shadowRenderer = (PointLightShadowRenderer) ic.readSavable("shadowRenderer", null);
    }
}
