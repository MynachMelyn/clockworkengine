
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
import com.clockwork.texture.Texture2D;
import java.io.IOException;

/**
 * This filter compose a texture with the viewport texture. This is used to
 * compose post processed texture from another viewport.
 *
 * the compositing is done using the alpha value of the viewportTexture :
 * mix(compositeTextureColor, viewPortColor, viewportColor.alpha);
 *
 * It's important for a good result that the viewport clear color alpha be 0.
 *
 */
public class ComposeFilter extends Filter {

    private Texture2D compositeTexture;

    /**
     * creates a ComposeFilter
     */
    public ComposeFilter() {
        super("Compose Filter");
    }

    /**
     * creates a ComposeFilter with the given texture
     *
     * @param color
     */
    public ComposeFilter(Texture2D compositeTexture) {
        this();
        this.compositeTexture = compositeTexture;
    }

    @Override
    protected Material getMaterial() {

        material.setTexture("CompositeTexture", compositeTexture);
        return material;
    }

    /**
     *
     * @return the compositeTexture
     */
    public Texture2D getCompositeTexture() {
        return compositeTexture;
    }

    /**
     * sets the compositeTexture
     *
     * @param compositeTexture
     */
    public void setCompositeTexture(Texture2D compositeTexture) {
        this.compositeTexture = compositeTexture;
    }

    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        material = new Material(manager, "Common/MatDefs/Post/Compose.j3md");
    }

    @Override
    public void write(CWExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
    }

    @Override
    public void read(CWImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
    }
}
