
package com.clockwork.post.filters;

import com.clockwork.asset.AssetManager;
import com.clockwork.material.Material;
import com.clockwork.post.Filter;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;

/**
 * A Post Processing filter to change colors appear with sharp edges as if the
 * available amount of colors available was not enough to draw the true image.
 * Possibly useful in cartoon styled games. Use the strength variable to lessen
 * influence of this filter on the total result. Values from 0.2 to 0.7 appear
 * to give nice results.
 *
 * Based on an article from Geeks3D:
 *    <a href="http://www.geeks3d.com/20091027/shader-library-posterization-post-processing-effect-glsl/" rel="nofollow">http://www.geeks3d.com/20091027/shader-library-posterization-post-processing-effect-glsl/</a>
 *
 */
public class PosterizationFilter extends Filter {

    private int numColors = 8;
    private float gamma = 0.6f;
    private float strength = 1.0f;

    /**
     * Creates a posterization Filter
     */
    public PosterizationFilter() {
        super("PosterizationFilter");
    }

    /**
     * Creates a posterization Filter with the given number of colors
     * @param numColors 
     */
    public PosterizationFilter(int numColors) {
        this();
        this.numColors = numColors;
    }

    /**
     * Creates a posterization Filter with the given number of colors and gamma
     * @param numColors
     * @param gamma 
     */
    public PosterizationFilter(int numColors, float gamma) {
        this(numColors);
        this.gamma = gamma;
    }

    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        material = new Material(manager, "Common/MatDefs/Post/Posterization.j3md");
        material.setInt("NumColors", numColors);
        material.setFloat("Gamma", gamma);
        material.setFloat("Strength", strength);
    }

    @Override
    protected Material getMaterial() {
        return material;
    }

    /**
     * Sets number of color levels used to draw the screen
     */
    public void setNumColors(int numColors) {
        this.numColors = numColors;
        if (material != null) {
            material.setInt("NumColors", numColors);
        }
    }

    /**
     * Sets gamma level used to enhange visual quality
     */
    public void setGamma(float gamma) {
        this.gamma = gamma;
        if (material != null) {
            material.setFloat("Gamma", gamma);
        }
    }

    /**
     * Sets urrent strength value, i.e. influence on final image
     */
    public void setStrength(float strength) {
        this.strength = strength;
        if (material != null) {
            material.setFloat("Strength", strength);
        }
    }

    /**
     * Returns number of color levels used
     */
    public int getNumColors() {
        return numColors;
    }

    /**
     * Returns current gamma value
     */
    public float getGamma() {
        return gamma;
    }

    /**
     * Returns current strength value, i.e. influence on final image
     */
    public float getStrength() {
        return strength;
    }
}