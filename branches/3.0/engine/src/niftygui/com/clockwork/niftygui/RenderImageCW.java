
package com.clockwork.niftygui;

import com.clockwork.asset.TextureKey;
import com.clockwork.texture.Image;
import com.clockwork.texture.Texture.MagFilter;
import com.clockwork.texture.Texture.MinFilter;
import com.clockwork.texture.Texture2D;
import de.lessvoid.nifty.spi.render.RenderImage;

public class RenderImageCW implements RenderImage {

    private Texture2D texture;
    private Image image;
    private int width;
    private int height;

    public RenderImageCW(String filename, boolean linear, NiftyCWDisplay display){
        TextureKey key = new TextureKey(filename, true);

        key.setAnisotropy(0);
        key.setAsCube(false);
        key.setGenerateMips(false);
        
        texture = (Texture2D) display.getAssetManager().loadTexture(key);
        texture.setMagFilter(linear ? MagFilter.Bilinear : MagFilter.Nearest);
        texture.setMinFilter(linear ? MinFilter.BilinearNoMipMaps : MinFilter.NearestNoMipMaps);
        image = texture.getImage();

        width = image.getWidth();
        height = image.getHeight();
    }

    public RenderImageCW(Texture2D texture){
        if (texture.getImage() == null) {
            throw new IllegalArgumentException("texture.getImage() cannot be null");
        }
        
        this.texture = texture;
        this.image = texture.getImage();
        width = image.getWidth();
        height = image.getHeight();
    }

    public Texture2D getTexture(){
        return texture;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void dispose() {
    }
}
