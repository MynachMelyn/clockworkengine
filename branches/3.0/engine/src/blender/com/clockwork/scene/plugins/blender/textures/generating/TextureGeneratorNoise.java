
package com.clockwork.scene.plugins.blender.textures.generating;

import com.clockwork.math.FastMath;
import com.clockwork.scene.plugins.blender.BlenderContext;
import com.clockwork.scene.plugins.blender.file.Structure;
import com.clockwork.scene.plugins.blender.textures.TexturePixel;
import com.clockwork.texture.Image.Format;

/**
 * This class generates the 'noise' texture.
 */
public class TextureGeneratorNoise extends TextureGenerator {
    protected int noisedepth;

    /**
     * Constructor stores the given noise generator.
     * @param noiseGenerator
     *            the noise generator
     */
    public TextureGeneratorNoise(NoiseGenerator noiseGenerator) {
        super(noiseGenerator, Format.Luminance8);
    }

    @Override
    public void readData(Structure tex, BlenderContext blenderContext) {
        super.readData(tex, blenderContext);
        noisedepth = ((Number) tex.getFieldValue("noisedepth")).intValue();
    }

    @Override
    public void getPixel(TexturePixel pixel, float x, float y, float z) {
        int random = FastMath.rand.nextInt();
        int val = random & 3;

        int loop = noisedepth;
        while (loop-- != 0) {
            random >>= 2;
            val *= random & 3;
        }
        pixel.intensity = FastMath.clamp(val, 0.0f, 1.0f);
        if (colorBand != null) {
            int colorbandIndex = (int) (pixel.intensity * 1000.0f);
            pixel.red = colorBand[colorbandIndex][0];
            pixel.green = colorBand[colorbandIndex][1];
            pixel.blue = colorBand[colorbandIndex][2];

            this.applyBrightnessAndContrast(bacd, pixel);
            pixel.alpha = colorBand[colorbandIndex][3];
        } else {
            this.applyBrightnessAndContrast(pixel, bacd.contrast, bacd.brightness);
        }
    }
}
