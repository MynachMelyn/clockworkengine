
package com.clockwork.scene.plugins.blender.textures.generating;

import com.clockwork.scene.plugins.blender.BlenderContext;
import com.clockwork.scene.plugins.blender.file.Structure;
import com.clockwork.scene.plugins.blender.textures.TexturePixel;
import com.clockwork.scene.plugins.blender.textures.generating.NoiseGenerator.MusgraveFunction;
import com.clockwork.texture.Image.Format;

/**
 * This class generates the 'musgrave' texture.
 */
public class TextureGeneratorMusgrave extends TextureGenerator {
    protected MusgraveData     musgraveData;
    protected MusgraveFunction musgraveFunction;
    protected int              stype;
    protected float            noisesize;

    /**
     * Constructor stores the given noise generator.
     * @param noiseGenerator
     *            the noise generator
     */
    public TextureGeneratorMusgrave(NoiseGenerator noiseGenerator) {
        super(noiseGenerator, Format.Luminance8);
    }

    @Override
    public void readData(Structure tex, BlenderContext blenderContext) {
        super.readData(tex, blenderContext);
        musgraveData = new MusgraveData(tex);
        stype = ((Number) tex.getFieldValue("stype")).intValue();
        noisesize = ((Number) tex.getFieldValue("noisesize")).floatValue();
        musgraveFunction = NoiseGenerator.musgraveFunctions.get(Integer.valueOf(musgraveData.stype));
        if (musgraveFunction == null) {
            throw new IllegalStateException("Unknown type of musgrave texture: " + stype);
        }
    }

    @Override
    public void getPixel(TexturePixel pixel, float x, float y, float z) {
        pixel.intensity = musgraveData.outscale * musgraveFunction.execute(musgraveData, x, y, z);
        if (pixel.intensity > 1) {
            pixel.intensity = 1.0f;
        } else if (pixel.intensity < 0) {
            pixel.intensity = 0.0f;
        }

        if (colorBand != null) {
            int colorbandIndex = (int) (pixel.intensity * 1000.0f);
            pixel.red = colorBand[colorbandIndex][0];
            pixel.green = colorBand[colorbandIndex][1];
            pixel.blue = colorBand[colorbandIndex][2];

            this.applyBrightnessAndContrast(pixel, bacd.contrast, bacd.brightness);
            pixel.alpha = colorBand[colorbandIndex][3];
        } else {
            this.applyBrightnessAndContrast(bacd, pixel);
        }
    }

    protected static class MusgraveData {
        public final int   stype;
        public final float outscale;
        public final float h;
        public final float lacunarity;
        public final float octaves;
        public final int   noisebasis;
        public final float offset;
        public final float gain;

        public MusgraveData(Structure tex) {
            stype = ((Number) tex.getFieldValue("stype")).intValue();
            outscale = ((Number) tex.getFieldValue("ns_outscale")).floatValue();
            h = ((Number) tex.getFieldValue("mg_H")).floatValue();
            lacunarity = ((Number) tex.getFieldValue("mg_lacunarity")).floatValue();
            octaves = ((Number) tex.getFieldValue("mg_octaves")).floatValue();
            noisebasis = ((Number) tex.getFieldValue("noisebasis")).intValue();
            offset = ((Number) tex.getFieldValue("mg_offset")).floatValue();
            gain = ((Number) tex.getFieldValue("mg_gain")).floatValue();
        }
    }
}
