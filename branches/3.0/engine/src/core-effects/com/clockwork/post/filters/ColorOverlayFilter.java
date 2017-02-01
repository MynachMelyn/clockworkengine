
package com.clockwork.post.filters;

import com.clockwork.asset.AssetManager;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.post.Filter;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import java.io.IOException;

/** 
 * This filter simply multiply the whole scene by a color
 */
public class ColorOverlayFilter extends Filter {

    private ColorRGBA color = ColorRGBA.White;

    /**
     * creates a colorOverlayFilter with a white coor (transparent)
     */
    public ColorOverlayFilter() {
        super("Color Overlay");
    }

    /**
     * creates a colorOverlayFilter with the given color
     * @param color 
     */
    public ColorOverlayFilter(ColorRGBA color) {
        this();
        this.color = color;
    }

    @Override
    protected Material getMaterial() {

        material.setColor("Color", color);
        return material;
    }

    /**
     * returns the color
     * @return color
     */
    public ColorRGBA getColor() {
        return color;
    }

    /**
     * sets the color 
     * @param color 
     */
    public void setColor(ColorRGBA color) {
        this.color = color;
    }

    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        material = new Material(manager, "Common/MatDefs/Post/Overlay.j3md");
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(color, "color", ColorRGBA.White);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        color = (ColorRGBA) ic.readSavable("color", ColorRGBA.White);
    }
}
