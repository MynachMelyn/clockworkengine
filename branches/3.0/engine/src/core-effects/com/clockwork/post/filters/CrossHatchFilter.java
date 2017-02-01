
package com.clockwork.post.filters;

import com.clockwork.asset.AssetManager;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.post.Filter;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;

/**
 * A Post Processing filter that makes the screen look like it was drawn as
 * diagonal lines with a pen.
 * Try combining this with a cartoon edge filter to obtain manga style visuals.
 *
 * Based on an article from Geeks3D:
 *    <a href="http://www.geeks3d.com/20110219/shader-library-crosshatching-glsl-filter/" rel="nofollow">http://www.geeks3d.com/20110219/shader-library-crosshatching-glsl-filter/</a>
 *
 */
public class CrossHatchFilter extends Filter {

    private ColorRGBA lineColor = ColorRGBA.Black.clone();
    private ColorRGBA paperColor = ColorRGBA.White.clone();
    private float colorInfluenceLine = 0.8f;
    private float colorInfluencePaper = 0.1f;
    private float fillValue = 0.9f;
    private float luminance1 = 0.9f;
    private float luminance2 = 0.7f;
    private float luminance3 = 0.5f;
    private float luminance4 = 0.3f;
    private float luminance5 = 0.0f;
    private float lineThickness = 1.0f;
    private float lineDistance = 4.0f;

    /**
     * Creates a crossHatch filter
     */
    public CrossHatchFilter() {
        super("CrossHatchFilter");
    }

    /**
     * Creates a crossHatch filter
     * @param lineColor the colors of the lines
     * @param paperColor the paper color
     */
    public CrossHatchFilter(ColorRGBA lineColor, ColorRGBA paperColor) {
        this();
        this.lineColor = lineColor;
        this.paperColor = paperColor;
    }

    @Override
    protected boolean isRequiresDepthTexture() {
        return false;
    }

    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        material = new Material(manager, "Common/MatDefs/Post/CrossHatch.j3md");
        material.setColor("LineColor", lineColor);
        material.setColor("PaperColor", paperColor);

        material.setFloat("ColorInfluenceLine", colorInfluenceLine);
        material.setFloat("ColorInfluencePaper", colorInfluencePaper);

        material.setFloat("FillValue", fillValue);

        material.setFloat("Luminance1", luminance1);
        material.setFloat("Luminance2", luminance2);
        material.setFloat("Luminance3", luminance3);
        material.setFloat("Luminance4", luminance4);
        material.setFloat("Luminance5", luminance5);

        material.setFloat("LineThickness", lineThickness);
        material.setFloat("LineDistance", lineDistance);
    }

    @Override
    protected Material getMaterial() {
        return material;
    }

    /**
     * Sets color used to draw lines
     * @param lineColor 
     */
    public void setLineColor(ColorRGBA lineColor) {
        this.lineColor = lineColor;
        if (material != null) {
            material.setColor("LineColor", lineColor);
        }
    }

    /**
     * Sets color used as background
     * @param paperColor 
     */
    public void setPaperColor(ColorRGBA paperColor) {
        this.paperColor = paperColor;
        if (material != null) {
            material.setColor("PaperColor", paperColor);
        }
    }

    /**
     * Sets color influence of original image on lines drawn
     * @param colorInfluenceLine 
     */
    public void setColorInfluenceLine(float colorInfluenceLine) {
        this.colorInfluenceLine = colorInfluenceLine;
        if (material != null) {
            material.setFloat("ColorInfluenceLine", colorInfluenceLine);
        }
    }

    /**
     * Sets color influence of original image on non-line areas
     * @param colorInfluencePaper 
     */
    public void setColorInfluencePaper(float colorInfluencePaper) {
        this.colorInfluencePaper = colorInfluencePaper;
        if (material != null) {
            material.setFloat("ColorInfluencePaper", colorInfluencePaper);
        }
    }

    /**
     * Sets line/paper color ratio for areas with values < luminance5,
     * really dark areas get no lines but a filled blob instead
     * @param fillValue 
     */
    public void setFillValue(float fillValue) {
        this.fillValue = fillValue;
        if (material != null) {
            material.setFloat("FillValue", fillValue);
        }
    }

    /**
     *
     * Sets minimum luminance levels for lines drawn
     * @param luminance1 Top-left to down right 1
     * @param luminance2 Top-right to bottom left 1
     * @param luminance3 Top-left to down right 2
     * @param luminance4 Top-right to bottom left 2
     * @param luminance5 Blobs
     */
    public void setLuminanceLevels(float luminance1, float luminance2, float luminance3, float luminance4, float luminance5) {
        this.luminance1 = luminance1;
        this.luminance2 = luminance2;
        this.luminance3 = luminance3;
        this.luminance4 = luminance4;
        this.luminance5 = luminance5;

        if (material != null) {
            material.setFloat("Luminance1", luminance1);
            material.setFloat("Luminance2", luminance2);
            material.setFloat("Luminance3", luminance3);
            material.setFloat("Luminance4", luminance4);
            material.setFloat("Luminance5", luminance5);
        }
    }

    /**
     * Sets the thickness of lines drawn
     * @param lineThickness 
     */
    public void setLineThickness(float lineThickness) {
        this.lineThickness = lineThickness;
        if (material != null) {
            material.setFloat("LineThickness", lineThickness);
        }
    }

    /**
     * Sets minimum distance between lines drawn
     * Primary lines are drawn at 2*lineDistance
     * Secondary lines are drawn at lineDistance
     * @param lineDistance 
     */
    public void setLineDistance(float lineDistance) {
        this.lineDistance = lineDistance;
        if (material != null) {
            material.setFloat("LineDistance", lineDistance);
        }
    }

    /**
     * Returns line color
     * @return 
     */
    public ColorRGBA getLineColor() {
        return lineColor;
    }

    /**
     * Returns paper background color
     * @return 
     */
    public ColorRGBA getPaperColor() {
        return paperColor;
    }

    /**
     * Returns current influence of image colors on lines
     */
    public float getColorInfluenceLine() {
        return colorInfluenceLine;
    }

    /**
     * Returns current influence of image colors on paper background
     */
    public float getColorInfluencePaper() {
        return colorInfluencePaper;
    }

    /**
     * Returns line/paper color ratio for blobs
     */
    public float getFillValue() {
        return fillValue;
    }

    /**
     * Returns the thickness of the lines drawn
     */
    public float getLineThickness() {
        return lineThickness;
    }

    /**
     * Returns minimum distance between lines
     */
    public float getLineDistance() {
        return lineDistance;
    }

    /**
     * Returns treshold for lines 1
     */
    public float getLuminance1() {
        return luminance1;
    }

    /**
     * Returns treshold for lines 2
     */
    public float getLuminance2() {
        return luminance2;
    }

    /**
     * Returns treshold for lines 3
     */
    public float getLuminance3() {
        return luminance3;
    }

    /**
     * Returns treshold for lines 4
     */
    public float getLuminance4() {
        return luminance4;
    }

    /**
     * Returns treshold for blobs
     */
    public float getLuminance5() {
        return luminance5;
    }
}