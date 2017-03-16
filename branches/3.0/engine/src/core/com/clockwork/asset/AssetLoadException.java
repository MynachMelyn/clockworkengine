
package com.clockwork.asset;

/**
 * AssetLoadException is thrown when the AssetManager}
 * is able to find the requested asset, but there was a problem while loading
 * it.
 *
 */
public class AssetLoadException extends RuntimeException {
    public AssetLoadException(String message){
        super(message);
    }
    public AssetLoadException(String message, Throwable cause){
        super(message, cause);
    }
}
