
package com.clockwork.scene.plugins.blender.textures.generating;

import com.clockwork.scene.plugins.blender.BlenderContext;
import com.clockwork.scene.plugins.blender.file.Structure;
import com.clockwork.scene.plugins.blender.textures.TexturePixel;
import com.clockwork.texture.Image.Format;

/**
 * This class generates the 'stucci' texture.
 */
public class TextureGeneratorStucci extends TextureGenerator {
    protected static final int TEX_NOISESOFT = 0;

    protected float            noisesize;
    protected int              noisebasis;
    protected int              noisetype;
    protected float            turbul;
    protected boolean          isHard;
    protected int              stype;

    /**
     * Constructor stores the given noise generator.
     * @param noiseGenerator
     *            the noise generator
     */
    public TextureGeneratorStucci(NoiseGenerator noiseGenerator) {
        super(noiseGenerator, Format.Luminance8);
    }

    @Override
    public void readData(Structure tex, BlenderContext blenderContext) {
        super.readData(tex, blenderContext);
        noisesize = ((Number) tex.getFieldValue("noisesize")).floatValue();
        noisebasis = ((Number) tex.getFieldValue("noisebasis")).intValue();
        noisetype = ((Number) tex.getFieldValue("noisetype")).intValue();
        turbul = ((Number) tex.getFieldValue("turbul")).floatValue();
        isHard = noisetype != TEX_NOISESOFT;
        stype = ((Number) tex.getFieldValue("stype")).intValue();
        if (noisesize <= 0.001f) {// the texture goes black if this value is lower than 0.001f
            noisesize = 0.001f;
        }
    }

    @Override
    public void getPixel(TexturePixel pixel, float x, float y, float z) {
        float noiseValue = NoiseGenerator.NoiseFunctions.noise(x, y, z, noisesize, 0, noisebasis, isHard);
        float ofs = turbul / 200.0f;
        if (stype != 0) {
            ofs *= noiseValue * noiseValue;
        }

        pixel.intensity = NoiseGenerator.NoiseFunctions.noise(x, y, z + ofs, noisesize, 0, noisebasis, isHard);
        if (colorBand != null) {
            int colorbandIndex = (int) (pixel.intensity * 1000.0f);
            pixel.red = colorBand[colorbandIndex][0];
            pixel.green = colorBand[colorbandIndex][1];
            pixel.blue = colorBand[colorbandIndex][2];
            pixel.alpha = colorBand[colorbandIndex][3];
        }

        if (stype == NoiseGenerator.TEX_WALLOUT) {
            pixel.intensity = 1.0f - pixel.intensity;
        }
        if (pixel.intensity < 0.0f) {
            pixel.intensity = 0.0f;
        }
        // no brightness and contrast needed for stucci (it doesn't affect the texture)
    }
}
