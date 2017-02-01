
package com.clockwork.scene.plugins.blender.textures.generating;

import com.clockwork.math.FastMath;
import com.clockwork.scene.plugins.blender.BlenderContext;
import com.clockwork.scene.plugins.blender.file.Structure;
import com.clockwork.scene.plugins.blender.textures.TexturePixel;
import com.clockwork.scene.plugins.blender.textures.generating.NoiseGenerator.NoiseFunction;
import com.clockwork.texture.Image.Format;

/**
 * This class generates the 'distorted noise' texture.
 */
public class TextureGeneratorDistnoise extends TextureGenerator {
    protected float noisesize;
    protected float distAmount;
    protected int   noisebasis;
    protected int   noisebasis2;

    /**
     * Constructor stores the given noise generator.
     * @param noiseGenerator
     *            the noise generator
     */
    public TextureGeneratorDistnoise(NoiseGenerator noiseGenerator) {
        super(noiseGenerator, Format.Luminance8);
    }

    @Override
    public void readData(Structure tex, BlenderContext blenderContext) {
        super.readData(tex, blenderContext);
        noisesize = ((Number) tex.getFieldValue("noisesize")).floatValue();
        distAmount = ((Number) tex.getFieldValue("dist_amount")).floatValue();
        noisebasis = ((Number) tex.getFieldValue("noisebasis")).intValue();
        noisebasis2 = ((Number) tex.getFieldValue("noisebasis2")).intValue();
    }

    @Override
    public void getPixel(TexturePixel pixel, float x, float y, float z) {
        pixel.intensity = this.musgraveVariableLunacrityNoise(x * 4, y * 4, z * 4, distAmount, noisebasis, noisebasis2);
        pixel.intensity = FastMath.clamp(pixel.intensity, 0.0f, 1.0f);
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

    /**
     * "Variable Lacunarity Noise" A distorted variety of Perlin noise. This method is used to calculate distorted noise
     * texture.
     * @param x
     * @param y
     * @param z
     * @param distortion
     * @param nbas1
     * @param nbas2
     * @return
     */
    private float musgraveVariableLunacrityNoise(float x, float y, float z, float distortion, int nbas1, int nbas2) {
        NoiseFunction abstractNoiseFunc1 = NoiseGenerator.noiseFunctions.get(Integer.valueOf(nbas1));
        if (abstractNoiseFunc1 == null) {
            abstractNoiseFunc1 = NoiseGenerator.noiseFunctions.get(Integer.valueOf(0));
        }
        NoiseFunction abstractNoiseFunc2 = NoiseGenerator.noiseFunctions.get(Integer.valueOf(nbas2));
        if (abstractNoiseFunc2 == null) {
            abstractNoiseFunc2 = NoiseGenerator.noiseFunctions.get(Integer.valueOf(0));
        }
        // get a random vector and scale the randomization
        float rx = abstractNoiseFunc1.execute(x + 13.5f, y + 13.5f, z + 13.5f) * distortion;
        float ry = abstractNoiseFunc1.execute(x, y, z) * distortion;
        float rz = abstractNoiseFunc1.execute(x - 13.5f, y - 13.5f, z - 13.5f) * distortion;
        return abstractNoiseFunc2.executeSigned(x + rx, y + ry, z + rz); // distorted-domain noise
    }
}
