
package com.clockwork.scene.plugins.blender.textures.blending;

import com.clockwork.scene.plugins.blender.BlenderContext;
import com.clockwork.texture.Image;
import com.clockwork.texture.Image.Format;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class creates the texture blending class depending on the texture type.
 * 
 * @author Marcin Roguski (Kaelthas)
 */
public class TextureBlenderFactory {
    private static final Logger LOGGER = Logger.getLogger(TextureBlenderFactory.class.getName());

    /**
     * This method creates the blending class.
     * 
     * @param format
     *            the texture format
     * @return texture blending class
     */
    public static TextureBlender createTextureBlender(Format format, int flag, boolean negate, int blendType, float[] materialColor, float[] color, float colfac) {
        switch (format) {
            case Luminance8:
            case Luminance8Alpha8:
            case Luminance16:
            case Luminance16Alpha16:
            case Luminance16F:
            case Luminance16FAlpha16F:
            case Luminance32F:
                return new TextureBlenderLuminance(flag, negate, blendType, materialColor, color, colfac);
            case RGBA8:
            case ABGR8:
            case BGR8:
            case RGB8:
            case RGB10:
            case RGB111110F:
            case RGB16:
            case RGB16F:
            case RGB16F_to_RGB111110F:
            case RGB16F_to_RGB9E5:
            case RGB32F:
            case RGB565:
            case RGB5A1:
            case RGB9E5:
            case RGBA16:
            case RGBA16F:
            case RGBA32F:
                return new TextureBlenderAWT(flag, negate, blendType, materialColor, color, colfac);
            case DXT1:
            case DXT1A:
            case DXT3:
            case DXT5:
                return new TextureBlenderDDS(flag, negate, blendType, materialColor, color, colfac);
            case Alpha16:
            case Alpha8:
            case ARGB4444:
            case Depth:
            case Depth16:
            case Depth24:
            case Depth32:
            case Depth32F:
            case Intensity16:
            case Intensity8:
            case LATC:
            case LTC:
                LOGGER.log(Level.WARNING, "Image type not yet supported for blending: {0}. Returning a blender that does not change the texture.", format);
                return new TextureBlender() {
                    public Image blend(Image image, Image baseImage, BlenderContext blenderContext) {
                        return image;
                    }

                    public void copyBlendingData(TextureBlender textureBlender) {
                    }
                };
            default:
                throw new IllegalStateException("Unknown image format type: " + format);
        }
    }

    /**
     * This method changes the image format in the texture blender.
     * 
     * @param format
     *            the new image format
     * @param textureBlender
     *            the texture blender that will be altered
     * @return altered texture blender
     */
    public static TextureBlender alterTextureType(Format format, TextureBlender textureBlender) {
        TextureBlender result = TextureBlenderFactory.createTextureBlender(format, 0, false, 0, null, null, 0);
        result.copyBlendingData(textureBlender);
        return result;
    }
}
