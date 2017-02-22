

package com.clockwork.asset;

/**
 * This key is mostly used to distinguish between textures that are loaded from
 * the given assets and those being generated automatically. Every generated
 * texture will have this kind of key attached.
 * 
 * 
 */
public class GeneratedTextureKey extends TextureKey {

    /**
     * Constructor. Stores the name. Extension and folder name are empty
     * strings.
     * 
     * @param name
     *            the name of the texture
     */
    public GeneratedTextureKey(String name) {
        super(name);
    }

    @Override
    public String getExtension() {
        return "";
    }

    @Override
    public String getFolder() {
        return "";
    }

    @Override
    public String toString() {
        return "Generated texture [" + name + "]";
    }
}
