
package com.clockwork.scene.plugins.blender.textures.generating;

import com.clockwork.math.FastMath;
import com.clockwork.scene.plugins.blender.BlenderContext;
import com.clockwork.scene.plugins.blender.file.Structure;
import com.clockwork.scene.plugins.blender.textures.TexturePixel;
import com.clockwork.texture.Image.Format;

/**
 * This class generates the 'blend' texture.
 */
public final class TextureGeneratorBlend extends TextureGenerator {

    private static final IntensityFunction INTENSITY_FUNCTION[] = new IntensityFunction[7];
    static {
        INTENSITY_FUNCTION[0] = new IntensityFunction() {// Linear: stype = 0 (TEX_LIN)
            public float getIntensity(float x, float y, float z) {
                return (1.0f + x) * 0.5f;
            }
        };
        INTENSITY_FUNCTION[1] = new IntensityFunction() {// Quad: stype = 1 (TEX_QUAD)
            public float getIntensity(float x, float y, float z) {
                float result = (1.0f + x) * 0.5f;
                return result * result;
            }
        };
        INTENSITY_FUNCTION[2] = new IntensityFunction() {// Ease: stype = 2 (TEX_EASE)
            public float getIntensity(float x, float y, float z) {
                float result = (1.0f + x) * 0.5f;
                if (result <= 0.0f) {
                    return 0.0f;
                } else if (result >= 1.0f) {
                    return 1.0f;
                } else {
                    return result * result * (3.0f - 2.0f * result);
                }
            }
        };
        INTENSITY_FUNCTION[3] = new IntensityFunction() {// Diagonal: stype = 3 (TEX_DIAG)
            public float getIntensity(float x, float y, float z) {
                return (2.0f + x + y) * 0.25f;
            }
        };
        INTENSITY_FUNCTION[4] = new IntensityFunction() {// Sphere: stype = 4 (TEX_SPHERE)
            public float getIntensity(float x, float y, float z) {
                float result = 1.0f - (float) Math.sqrt(x * x + y * y + z * z);
                return result < 0.0f ? 0.0f : result;
            }
        };
        INTENSITY_FUNCTION[5] = new IntensityFunction() {// Halo: stype = 5 (TEX_HALO)
            public float getIntensity(float x, float y, float z) {
                float result = 1.0f - (float) Math.sqrt(x * x + y * y + z * z);
                return result <= 0.0f ? 0.0f : result * result;
            }
        };
        INTENSITY_FUNCTION[6] = new IntensityFunction() {// Radial: stype = 6 (TEX_RAD)
            public float getIntensity(float x, float y, float z) {
                return (float) Math.atan2(y, x) * FastMath.INV_TWO_PI + 0.5f;
            }
        };
    }

    /**
     * Constructor stores the given noise generator.
     * @param noiseGenerator
     *            the noise generator
     */
    public TextureGeneratorBlend(NoiseGenerator noiseGenerator) {
        super(noiseGenerator, Format.Luminance8);
    }

    protected int stype;

    @Override
    public void readData(Structure tex, BlenderContext blenderContext) {
        super.readData(tex, blenderContext);
        stype = ((Number) tex.getFieldValue("stype")).intValue();
    }

    @Override
    public void getPixel(TexturePixel pixel, float x, float y, float z) {
        pixel.intensity = INTENSITY_FUNCTION[stype].getIntensity(x, y, z);

        if (colorBand != null) {
            int colorbandIndex = (int) (pixel.intensity * 1000.0f);
            pixel.red = colorBand[colorbandIndex][0];
            pixel.green = colorBand[colorbandIndex][1];
            pixel.blue = colorBand[colorbandIndex][2];

            this.applyBrightnessAndContrast(bacd, pixel);
        } else {
            this.applyBrightnessAndContrast(pixel, bacd.contrast, bacd.brightness);
        }
    }

    private static interface IntensityFunction {
        float getIntensity(float x, float y, float z);
    }
}
