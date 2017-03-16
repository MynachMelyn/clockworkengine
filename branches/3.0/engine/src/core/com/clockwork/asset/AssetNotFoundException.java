
package com.clockwork.asset;

/**
 * AssetNotFoundException is thrown when the AssetManager}
 * is unable to locate the requested asset using any of the registered
 * AssetLocator}s.
 *
 */
public class AssetNotFoundException extends RuntimeException {
    public AssetNotFoundException(String message){
        super(message);
    }
    public AssetNotFoundException(String message, Exception ex){
        super(message, ex);
    }
}
