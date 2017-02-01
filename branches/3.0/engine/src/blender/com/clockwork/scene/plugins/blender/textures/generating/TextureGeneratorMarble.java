
package com.clockwork.scene.plugins.blender.textures.generating;

import com.clockwork.scene.plugins.blender.BlenderContext;
import com.clockwork.scene.plugins.blender.file.Structure;
import com.clockwork.scene.plugins.blender.textures.TexturePixel;

/**
 * This class generates the 'marble' texture.
 */
public class TextureGeneratorMarble extends TextureGeneratorWood {
    // tex->stype
    protected static final int TEX_SOFT    = 0;
    protected static final int TEX_SHARP   = 1;
    protected static final int TEX_SHARPER = 2;

    protected MarbleData       marbleData;

    /**
     * Constructor stores the given noise generator.
     * @param noiseGenerator
     *            the noise generator
     */
    public TextureGeneratorMarble(NoiseGenerator noiseGenerator) {
        super(noiseGenerator);
    }

    @Override
    public void readData(Structure tex, BlenderContext blenderContext) {
        super.readData(tex, blenderContext);
        marbleData = new MarbleData(tex);
    }

    @Override
    public void getPixel(TexturePixel pixel, float x, float y, float z) {
        pixel.intensity = this.marbleInt(marbleData, x, y, z);
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

    public float marbleInt(MarbleData marbleData, float x, float y, float z) {
        int waveform;
        if (marbleData.waveform > TEX_TRI || marbleData.waveform < TEX_SIN) {
            waveform = 0;
        } else {
            waveform = marbleData.waveform;
        }

        float n = 5.0f * (x + y + z);
        float mi = n + marbleData.turbul * NoiseGenerator.NoiseFunctions.turbulence(x, y, z, marbleData.noisesize, marbleData.noisedepth, marbleData.noisebasis, marbleData.isHard);

        if (marbleData.stype >= TEX_SOFT) {
            mi = waveformFunctions[waveform].execute(mi);
            if (marbleData.stype == TEX_SHARP) {
                mi = (float) Math.sqrt(mi);
            } else if (marbleData.stype == TEX_SHARPER) {
                mi = (float) Math.sqrt(Math.sqrt(mi));
            }
        }
        return mi;
    }

    private static class MarbleData {
        public final float   noisesize;
        public final int     noisebasis;
        public final int     noisedepth;
        public final int     stype;
        public final float   turbul;
        public final int     waveform;
        public final boolean isHard;

        public MarbleData(Structure tex) {
            noisesize = ((Number) tex.getFieldValue("noisesize")).floatValue();
            noisebasis = ((Number) tex.getFieldValue("noisebasis")).intValue();
            noisedepth = ((Number) tex.getFieldValue("noisedepth")).intValue();
            stype = ((Number) tex.getFieldValue("stype")).intValue();
            turbul = ((Number) tex.getFieldValue("turbul")).floatValue();
            int noisetype = ((Number) tex.getFieldValue("noisetype")).intValue();
            waveform = ((Number) tex.getFieldValue("noisebasis2")).intValue();
            isHard = noisetype != TEX_NOISESOFT;
        }
    }
}
