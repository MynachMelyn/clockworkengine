package com.clockwork.scene.plugins.blender.textures.io;

import com.clockwork.scene.plugins.blender.textures.TexturePixel;
import com.clockwork.texture.Image;

/**
 * Implemens read/write operations for images.
 * 
 * 
 */
public interface PixelInputOutput {
    /**
     * This method reads a pixel that starts at the given index.
     * 
     * @param image
     *            the image we read pixel from
     * @param pixel
     *            the pixel where the result is stored
     * @param index
     *            the index where the pixel begins in the image data
     */
    void read(Image image, int layer, TexturePixel pixel, int index);

    /**
     * This method reads a pixel that is located at the given position on the
     * image.
     * 
     * @param image
     *            the image we read pixel from
     * @param pixel
     *            the pixel where the result is stored
     * @param x
     *            the X coordinate of the pixel
     * @param y
     *            the Y coordinate of the pixel
     */
    void read(Image image, int layer, TexturePixel pixel, int x, int y);

    /**
     * This method writes a pixel that starts at the given index.
     * 
     * @param image
     *            the image we read pixel from
     * @param pixel
     *            the pixel where the result is stored
     * @param index
     *            the index where the pixel begins in the image data
     */
    void write(Image image, int layer, TexturePixel pixel, int index);

    /**
     * This method writes a pixel that is located at the given position on the
     * image.
     * 
     * @param image
     *            the image we read pixel from
     * @param pixel
     *            the pixel where the result is stored
     * @param x
     *            the X coordinate of the pixel
     * @param y
     *            the Y coordinate of the pixel
     */
    void write(Image image, int layer, TexturePixel pixel, int x, int y);
}
