
package com.clockwork.texture.image;

import com.clockwork.math.ColorRGBA;
import com.clockwork.system.JmeSystem;
import com.clockwork.texture.Image;

/**
 * Utility class for reading and writing from jME3 Image images}.
 * 
 * Allows directly manipulating pixels of the image by writing and 
 * reading ColorRGBA colors} at any coordinate, without
 * regard to the underlying Image.Format format} of the image.
 * NOTE: compressed and depth formats are <strong>not supported</strong>.
 * Special RGB formats like RGB111110F and RGB9E5 are not supported
 * at the moment, but may be added later on. For now 
 * use RGB16F_to_RGB111110F and RGB16F_to_RGB9E5 to handle
 * the conversion on the GPU.
 * 
 * If direct manipulations are done to the image, such as replacing
 * the image data, or changing the width, height, or format, then
 * all current instances of ImageReadWrite become invalid, and
 * new instances must be created in order to properly access
 * the image data.
 * 
 * Usage example:
 * 
 * Image myImage = ...
 * ImageRaster raster = ImageRaster.create(myImage);
 * raster.setPixel(1, 5, ColorRGBA.Green);
 * System.out.println( raster.getPixel(1, 5) ); // Will print [0.0, 1.0, 0.0, 1.0].
 * 
 * 
 * 
 */
public abstract class ImageRaster {

    /**
     * Create new image reader / writer.
     *
     * @param image The image to read / write to.
     * @param slice Which slice to use. Only applies to 3D images, 2D image
     * arrays or cubemaps.
     */
    public static ImageRaster create(Image image, int slices) {
        return JmeSystem.createImageRaster(image, slices);
    }
    
    /**
     * Create new image reader / writer for 2D images.
     * 
     * @param image The image to read / write to.
     */
    public static ImageRaster create(Image image) {
        if (image.getData().size() > 1) {
            throw new IllegalStateException("Use constructor that takes slices argument to read from multislice image");
        }
        return JmeSystem.createImageRaster(image, 0);
    }
    
    public ImageRaster() {
    }
    
    /**
     * Returns the pixel width of the underlying image.
     * 
     * @return the pixel width of the underlying image.
     */
    public abstract int getWidth();
    
    /**
     * Returns the pixel height of the underlying image.
     * 
     * @return the pixel height of the underlying image.
     */
    public abstract int getHeight();
    
    /**
     * Sets the pixel at the given coordinate to the given color.
     * 
     * For all integer based formats (those not ending in "F"), the 
     * color is first clamped to 0.0 - 1.0 before converting it to
     * an integer to avoid overflow. For floating point based formats, 
     * components larger than 1.0 can be represented, but components
     * lower than 0.0 are still not allowed (as all formats are unsigned).
     * 
     * If the underlying format is grayscale (e.g. one of the luminance formats,
     * such as Image.Format#Luminance8}) then a color to grayscale
     * conversion is done first, before writing the result into the image.
     * 
     * If the image does not have some of the components in the color (such
     * as alpha, or any of the color components), then these components
     * will be ignored. The only exception to this is luminance formats
     * for which the color is converted to luminance first (see above).
     * 
     * After writing the color, the image shall be marked as requiring an
     * update. The next time it is used for rendering, all pixel changes
     * will be reflected when the image is rendered.
     * 
     * @param x The x coordinate, from 0 to width - 1.
     * @param y The y coordinate, from 0 to height - 1.
     * @param color The color to write. 
     * @throws IllegalArgumentException If x or y are outside the image dimensions.
     */
    public abstract void setPixel(int x, int y, ColorRGBA color);
    
    /**
     * Retrieve the color at the given coordinate.
     * 
     * Any components that are not defined in the image format
     * will be set to 1.0 in the returned color. For example,
     * reading from an Image.Format#Alpha8} format will
     * return a ColorRGBA with the R, G, and B components set to 1.0, and
     * the A component set to the alpha in the image.
     * 
     * For grayscale or luminance formats, the luminance value is replicated
     * in the R, G, and B components. 
     * 
     * Integer formats are converted to the range 0.0 - 1.0, based
     * on the maximum possible integer value that can be represented
     * by the number of bits the component has.
     * For example, the Image.Format#RGB5A1} format can
     * contain the integer values 0 - 31, a conversion to floating point
     * is done by diving the integer value by 31 (done with floating point
     * precision).
     * 
     * @param x The x coordinate, from 0 to width - 1.
     * @param y The y coordinate, from 0 to height - 1.
     * @param store Storage location for the read color, if null, 
     * then a new ColorRGBA is created and returned with the read color.
     * @return The store parameter, if it is null, then a new ColorRGBA
     * with the read color.
     * @throws IllegalArgumentException If x or y are outside the image dimensions.
     */
    public abstract ColorRGBA getPixel(int x, int y, ColorRGBA store);
    
    /**
     * Retrieve the color at the given coordinate.
     * 
     * Convenience method that does not take a store argument. Equivalent
     * to calling getPixel(x, y, null). 
     * See #getPixel(int, int, com.clockwork.math.ColorRGBA) } for
     * more information.
     * 
     * @param x The x coordinate, from 0 to width - 1.
     * @param y The y coordinate, from 0 to height - 1.
     * @return A new ColorRGBA with the read color.
     * @throws IllegalArgumentException If x or y are outside the image dimensions
     */
    public ColorRGBA getPixel(int x, int y) { 
        return getPixel(x, y, null);
    }
}
