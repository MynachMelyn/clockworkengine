
package com.clockwork.asset;

/**
 * CloneableAssetProcessor simply calls Object#clone() }
 * on assets to clone them. No processing is applied.
 * 
 * 
 */
public class CloneableAssetProcessor implements AssetProcessor {

    public Object postProcess(AssetKey key, Object obj) {
        return obj;
    }

    public Object createClone(Object obj) {
        CloneableSmartAsset asset = (CloneableSmartAsset) obj;
        return asset.clone();
    }
    
}
