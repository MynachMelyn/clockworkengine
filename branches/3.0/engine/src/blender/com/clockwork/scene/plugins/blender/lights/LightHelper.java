
package com.clockwork.scene.plugins.blender.lights;

import com.clockwork.asset.BlenderKey.FeaturesToLoad;
import com.clockwork.light.DirectionalLight;
import com.clockwork.light.Light;
import com.clockwork.light.PointLight;
import com.clockwork.light.SpotLight;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.FastMath;
import com.clockwork.scene.LightNode;
import com.clockwork.scene.plugins.blender.AbstractBlenderHelper;
import com.clockwork.scene.plugins.blender.BlenderContext;
import com.clockwork.scene.plugins.blender.BlenderContext.LoadedFeatureDataType;
import com.clockwork.scene.plugins.blender.exceptions.BlenderFileException;
import com.clockwork.scene.plugins.blender.file.Structure;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that is used in light calculations.
 * @author Marcin Roguski
 */
public class LightHelper extends AbstractBlenderHelper {

    private static final Logger LOGGER = Logger.getLogger(LightHelper.class.getName());

    /**
     * This constructor parses the given blender version and stores the result. Some functionalities may differ in
     * different blender versions.
     * @param blenderVersion
     *            the version read from the blend file
     * @param blenderContext
     *            the blender context
     */
    public LightHelper(String blenderVersion, BlenderContext blenderContext) {
        super(blenderVersion, blenderContext);
    }

    public LightNode toLight(Structure structure, BlenderContext blenderContext) throws BlenderFileException {
        LightNode result = (LightNode) blenderContext.getLoadedFeature(structure.getOldMemoryAddress(), LoadedFeatureDataType.LOADED_FEATURE);
        if (result != null) {
            return result;
        }
        Light light = null;
        int type = ((Number) structure.getFieldValue("type")).intValue();
        switch (type) {
            case 0:// Lamp
                light = new PointLight();
                float distance = ((Number) structure.getFieldValue("dist")).floatValue();
                ((PointLight) light).setRadius(distance);
                break;
            case 1:// Sun
                LOGGER.log(Level.WARNING, "'Sun' lamp is not supported in engine.");
                break;
            case 2:// Spot
                light = new SpotLight();
                // range
                ((SpotLight) light).setSpotRange(((Number) structure.getFieldValue("dist")).floatValue());
                // outer angle
                float outerAngle = ((Number) structure.getFieldValue("spotsize")).floatValue() * FastMath.DEG_TO_RAD * 0.5f;
                ((SpotLight) light).setSpotOuterAngle(outerAngle);

                // inner angle
                float spotblend = ((Number) structure.getFieldValue("spotblend")).floatValue();
                spotblend = FastMath.clamp(spotblend, 0, 1);
                float innerAngle = outerAngle * (1 - spotblend);
                ((SpotLight) light).setSpotInnerAngle(innerAngle);
                break;
            case 3:// Hemi
                LOGGER.log(Level.WARNING, "'Hemi' lamp is not supported in engine.");
                break;
            case 4:// Area
                light = new DirectionalLight();
                break;
            default:
                throw new BlenderFileException("Unknown light source type: " + type);
        }
        if (light != null) {
            float r = ((Number) structure.getFieldValue("r")).floatValue();
            float g = ((Number) structure.getFieldValue("g")).floatValue();
            float b = ((Number) structure.getFieldValue("b")).floatValue();
            light.setColor(new ColorRGBA(r, g, b, 1.0f));
            result = new LightNode(null, light);
        }
        return result;
    }

    @Override
    public boolean shouldBeLoaded(Structure structure, BlenderContext blenderContext) {
        return (blenderContext.getBlenderKey().getFeaturesToLoad() & FeaturesToLoad.LIGHTS) != 0;
    }
}
