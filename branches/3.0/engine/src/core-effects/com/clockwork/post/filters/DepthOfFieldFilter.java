
package com.clockwork.post.filters;

import com.clockwork.asset.AssetManager;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.material.Material;
import com.clockwork.post.Filter;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import java.io.IOException;

/**
 *  A post-processing filter that performs a depth range
 *  blur using a scaled convolution filter.
 *
 *  @version   $Revision: 779 $
 */
public class DepthOfFieldFilter extends Filter {

    private float focusDistance = 50f;
    private float focusRange = 10f;
    private float blurScale = 1f;
    // These values are set internally based on the
    // viewport size.
    private float xScale;
    private float yScale;

    /**
     * Creates a DepthOfField filter
     */
    public DepthOfFieldFilter() {
        super("Depth Of Field");
    }

    @Override
    protected boolean isRequiresDepthTexture() {
        return true;
    }

    @Override
    protected Material getMaterial() {

        return material;
    }

    @Override
    protected void initFilter(AssetManager assets, RenderManager renderManager,
            ViewPort vp, int w, int h) {
        material = new Material(assets, "Common/MatDefs/Post/DepthOfField.j3md");
        material.setFloat("FocusDistance", focusDistance);
        material.setFloat("FocusRange", focusRange);


        xScale = 1.0f / w;
        yScale = 1.0f / h;

        material.setFloat("XScale", blurScale * xScale);
        material.setFloat("YScale", blurScale * yScale);
    }

    /**
     *  Sets the distance at which objects are purely in focus.
     */
    public void setFocusDistance(float f) {

        this.focusDistance = f;
        if (material != null) {
            material.setFloat("FocusDistance", focusDistance);
        }

    }

    /**
     * returns the focus distance
     * @return 
     */
    public float getFocusDistance() {
        return focusDistance;
    }

    /**
     *  Sets the range to either side of focusDistance where the
     *  objects go gradually out of focus.  Less than focusDistance - focusRange
     *  and greater than focusDistance + focusRange, objects are maximally "blurred".
     */
    public void setFocusRange(float f) {
        this.focusRange = f;
        if (material != null) {
            material.setFloat("FocusRange", focusRange);
        }

    }

    /**
     * returns the focus range
     * @return 
     */
    public float getFocusRange() {
        return focusRange;
    }

    /**
     *  Sets the blur amount by scaling the convolution filter up or
     *  down.  A value of 1 (the default) performs a sparse 5x5 evenly
     *  distribubted convolution at pixel level accuracy.  Higher values skip
     *  more pixels, and so on until you are no longer blurring the image
     *  but simply hashing it.
     *
     *  The sparse convolution is as follows:
     *%MINIFYHTMLc3d0cd9fab65de6875a381fd3f83e1b338%*
     *  Where 'x' is the texel being modified.  Setting blur scale higher
     *  than 1 spaces the samples out.
     */
    public void setBlurScale(float f) {
        this.blurScale = f;
        if (material != null) {
            material.setFloat("XScale", blurScale * xScale);
            material.setFloat("YScale", blurScale * yScale);
        }
    }

    /**
     * returns the blur scale
     * @return 
     */
    public float getBlurScale() {
        return blurScale;
    }

    @Override
    public void write(CWExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(blurScale, "blurScale", 1f);
        oc.write(focusDistance, "focusDistance", 50f);
        oc.write(focusRange, "focusRange", 10f);
    }

    @Override
    public void read(CWImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        blurScale = ic.readFloat("blurScale", 1f);
        focusDistance = ic.readFloat("focusDistance", 50f);
        focusRange = ic.readFloat("focusRange", 10f);
    }
}
