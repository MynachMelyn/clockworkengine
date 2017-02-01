
package com.clockwork.scene.plugins.blender.textures.generating;

import com.clockwork.math.FastMath;
import com.clockwork.scene.plugins.blender.BlenderContext;
import com.clockwork.scene.plugins.blender.file.Structure;
import com.clockwork.scene.plugins.blender.textures.TexturePixel;
import com.clockwork.texture.Image.Format;

/**
 * This class generates the 'magic' texture.
 */
public class TextureGeneratorMagic extends TextureGenerator {
    private static NoiseDepthFunction[] noiseDepthFunctions = new NoiseDepthFunction[10];
    static {
        noiseDepthFunctions[0] = new NoiseDepthFunction() {
            public void compute(float[] xyz, float turbulence) {
                xyz[1] = -(float) Math.cos(xyz[0] - xyz[1] + xyz[2]) * turbulence;
            }
        };
        noiseDepthFunctions[1] = new NoiseDepthFunction() {
            public void compute(float[] xyz, float turbulence) {
                xyz[0] = (float) Math.cos(xyz[0] - xyz[1] - xyz[2]) * turbulence;
            }
        };
        noiseDepthFunctions[2] = new NoiseDepthFunction() {
            public void compute(float[] xyz, float turbulence) {
                xyz[2] = (float) Math.sin(-xyz[0] - xyz[1] - xyz[2]) * turbulence;
            }
        };
        noiseDepthFunctions[3] = new NoiseDepthFunction() {
            public void compute(float[] xyz, float turbulence) {
                xyz[0] = -(float) Math.cos(-xyz[0] + xyz[1] - xyz[2]) * turbulence;
            }
        };
        noiseDepthFunctions[4] = new NoiseDepthFunction() {
            public void compute(float[] xyz, float turbulence) {
                xyz[1] = -(float) Math.sin(-xyz[0] + xyz[1] + xyz[2]) * turbulence;
            }
        };
        noiseDepthFunctions[5] = new NoiseDepthFunction() {
            public void compute(float[] xyz, float turbulence) {
                xyz[1] = -(float) Math.cos(-xyz[0] + xyz[1] + xyz[2]) * turbulence;
            }
        };
        noiseDepthFunctions[6] = new NoiseDepthFunction() {
            public void compute(float[] xyz, float turbulence) {
                xyz[0] = (float) Math.cos(xyz[0] + xyz[1] + xyz[2]) * turbulence;
            }
        };
        noiseDepthFunctions[7] = new NoiseDepthFunction() {
            public void compute(float[] xyz, float turbulence) {
                xyz[2] = (float) Math.sin(xyz[0] + xyz[1] - xyz[2]) * turbulence;
            }
        };
        noiseDepthFunctions[8] = new NoiseDepthFunction() {
            public void compute(float[] xyz, float turbulence) {
                xyz[0] = -(float) Math.cos(-xyz[0] - xyz[1] + xyz[2]) * turbulence;
            }
        };
        noiseDepthFunctions[9] = new NoiseDepthFunction() {
            public void compute(float[] xyz, float turbulence) {
                xyz[1] = -(float) Math.sin(xyz[0] - xyz[1] + xyz[2]) * turbulence;
            }
        };
    }

    protected int                       noisedepth;
    protected float                     turbul;
    protected float[]                   xyz                 = new float[3];

    /**
     * Constructor stores the given noise generator.
     * @param noiseGenerator
     *            the noise generator
     */
    public TextureGeneratorMagic(NoiseGenerator noiseGenerator) {
        super(noiseGenerator, Format.RGBA8);
    }

    @Override
    public void readData(Structure tex, BlenderContext blenderContext) {
        super.readData(tex, blenderContext);
        noisedepth = ((Number) tex.getFieldValue("noisedepth")).intValue();
        turbul = ((Number) tex.getFieldValue("turbul")).floatValue() / 5.0f;
    }

    @Override
    public void getPixel(TexturePixel pixel, float x, float y, float z) {
        float turb = turbul;
        xyz[0] = (float) Math.sin((x + y + z) * 5.0f);
        xyz[1] = (float) Math.cos((-x + y - z) * 5.0f);
        xyz[2] = -(float) Math.cos((-x - y + z) * 5.0f);

        if (colorBand != null) {
            pixel.intensity = FastMath.clamp(0.3333f * (xyz[0] + xyz[1] + xyz[2]), 0.0f, 1.0f);
            int colorbandIndex = (int) (pixel.intensity * 1000.0f);
            pixel.red = colorBand[colorbandIndex][0];
            pixel.green = colorBand[colorbandIndex][1];
            pixel.blue = colorBand[colorbandIndex][2];
            pixel.alpha = colorBand[colorbandIndex][3];
        } else {
            if (noisedepth > 0) {
                xyz[0] *= turb;
                xyz[1] *= turb;
                xyz[2] *= turb;
                for (int m = 0; m < noisedepth; ++m) {
                    noiseDepthFunctions[m].compute(xyz, turb);
                }
            }

            if (turb != 0.0f) {
                turb *= 2.0f;
                xyz[0] /= turb;
                xyz[1] /= turb;
                xyz[2] /= turb;
            }
            pixel.red = 0.5f - xyz[0];
            pixel.green = 0.5f - xyz[1];
            pixel.blue = 0.5f - xyz[2];
            pixel.alpha = 1.0f;
        }
        this.applyBrightnessAndContrast(bacd, pixel);
    }

    private static interface NoiseDepthFunction {
        void compute(float[] xyz, float turbulence);
    }
}
