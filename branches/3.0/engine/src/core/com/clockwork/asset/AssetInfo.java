
package com.clockwork.asset;

import java.io.InputStream;

/**
 * The result of locating an asset through an AssetKey. Provides
 * a means to read the asset data through an InputStream.
 *
 */
public abstract class AssetInfo {

    protected AssetManager manager;
    protected AssetKey key;

    public AssetInfo(AssetManager manager, AssetKey key) {
        this.manager = manager;
        this.key = key;
    }

    public AssetKey getKey() {
        return key;
    }

    public AssetManager getManager() {
        return manager;
    }

    @Override
    public String toString(){
        return getClass().getName() + "[" + "key=" + key + "]";
    }

    /**
     * Implementations of this method should return an InputStream}
     * allowing access to the data represented by the AssetKey}.
     * 
     * Each invocation of this method should return a new stream to the
     * asset data, starting at the beginning of the file.
     * 
     * @return The asset data.
     */
    public abstract InputStream openStream();

}
