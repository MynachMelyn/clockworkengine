
package com.clockwork.scene.plugins.blender.textures.generating;

import com.clockwork.math.FastMath;
import com.clockwork.scene.plugins.blender.BlenderContext;
import com.clockwork.scene.plugins.blender.file.Structure;
import com.clockwork.scene.plugins.blender.textures.TexturePixel;
import com.clockwork.scene.plugins.blender.textures.generating.NoiseGenerator.NoiseMath;
import com.clockwork.texture.Image.Format;

/**
 * This class generates the 'voronoi' texture.
 */
public class TextureGeneratorVoronoi extends TextureGenerator {
    protected float noisesize;
    protected float outscale;
    protected float mexp;
    protected int   distanceType;
    protected int   voronoiColorType;
    protected float[] da = new float[4], pa = new float[12];
    protected float[] hashPoint;
    protected float[] voronoiWeights;
    protected float   weightSum;

    /**
     * Constructor stores the given noise generator.
     * @param noiseGenerator
     *            the noise generator
     */
    public TextureGeneratorVoronoi(NoiseGenerator noiseGenerator) {
        super(noiseGenerator, Format.Luminance8);
    }

    @Override
    public void readData(Structure tex, BlenderContext blenderContext) {
        super.readData(tex, blenderContext);
        voronoiWeights = new float[4];
        voronoiWeights[0] = ((Number) tex.getFieldValue("vn_w1")).floatValue();
        voronoiWeights[1] = ((Number) tex.getFieldValue("vn_w2")).floatValue();
        voronoiWeights[2] = ((Number) tex.getFieldValue("vn_w3")).floatValue();
        voronoiWeights[3] = ((Number) tex.getFieldValue("vn_w4")).floatValue();
        noisesize = ((Number) tex.getFieldValue("noisesize")).floatValue();
        outscale = ((Number) tex.getFieldValue("ns_outscale")).floatValue();
        mexp = ((Number) tex.getFieldValue("vn_mexp")).floatValue();
        distanceType = ((Number) tex.getFieldValue("vn_distm")).intValue();
        voronoiColorType = ((Number) tex.getFieldValue("vn_coltype")).intValue();
        hashPoint = voronoiColorType != 0 ? new float[3] : null;
        weightSum = voronoiWeights[0] + voronoiWeights[1] + voronoiWeights[2] + voronoiWeights[3];
        if (weightSum != 0.0f) {
            weightSum = outscale / weightSum;
        }
        if (voronoiColorType != 0 || colorBand != null) {
            this.imageFormat = Format.RGBA8;
        }
    }

    @Override
    public void getPixel(TexturePixel pixel, float x, float y, float z) {
        // for voronoi we need to widen the range a little
        NoiseGenerator.NoiseFunctions.voronoi(x * 4, y * 4, z * 4, da, pa, mexp, distanceType);
        pixel.intensity = weightSum * FastMath.abs(voronoiWeights[0] * da[0] + voronoiWeights[1] * da[1] + voronoiWeights[2] * da[2] + voronoiWeights[3] * da[3]);
        if (pixel.intensity > 1.0f) {
            pixel.intensity = 1.0f;
        } else if (pixel.intensity < 0.0f) {
            pixel.intensity = 0.0f;
        }

        if (colorBand != null) {// colorband ALWAYS goes first and covers the color (if set)
            int colorbandIndex = (int) (pixel.intensity * 1000.0f);
            pixel.red = colorBand[colorbandIndex][0];
            pixel.green = colorBand[colorbandIndex][1];
            pixel.blue = colorBand[colorbandIndex][2];
            pixel.alpha = colorBand[colorbandIndex][3];
        } else if (voronoiColorType != 0) {
            pixel.red = pixel.green = pixel.blue = 0.0f;
            pixel.alpha = 1.0f;
            for (int m = 0; m < 12; m += 3) {
                float weight = voronoiWeights[m / 3];
                NoiseMath.hash((int) pa[m], (int) pa[m + 1], (int) pa[m + 2], hashPoint);
                pixel.red += weight * hashPoint[0];
                pixel.green += weight * hashPoint[1];
                pixel.blue += weight * hashPoint[2];
            }
            if (voronoiColorType >= 2) {
                float t1 = (da[1] - da[0]) * 10.0f;
                if (t1 > 1.0f) {
                    t1 = 1.0f;
                }
                if (voronoiColorType == 3) {
                    t1 *= pixel.intensity;
                } else {
                    t1 *= weightSum;
                }
                pixel.red *= t1;
                pixel.green *= t1;
                pixel.blue *= t1;
            } else {
                pixel.red *= weightSum;
                pixel.green *= weightSum;
                pixel.blue *= weightSum;
            }
        }

        if (voronoiColorType != 0 || colorBand != null) {
            this.applyBrightnessAndContrast(bacd, pixel);
        } else {
            this.applyBrightnessAndContrast(pixel, bacd.contrast, bacd.brightness);
        }
    }
}
