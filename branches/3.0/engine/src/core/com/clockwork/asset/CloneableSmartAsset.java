
package com.clockwork.asset;

import com.clockwork.asset.cache.WeakRefCloneAssetCache;

/**
 * Implementing the <code>CloneableSmartAsset</code> interface allows use 
 * of cloneable smart asset management.
 * <p>
 * Smart asset management requires cooperation from the {@link AssetKey}. 
 * In particular, the AssetKey should return {@link WeakRefCloneAssetCache} in its 
 * {@link AssetKey#getCacheType()} method. Also smart assets MUST
 * create a clone of the asset and cannot return the same reference,
 * e.g. {@link AssetProcessor#createClone(java.lang.Object) createClone(someAsset)} <code>!= someAsset</code>.
 * <p>
 * If the {@link AssetManager#loadAsset(com.clockwork.asset.AssetKey) } method
 * is called twice with the same asset key (equals() wise, not necessarily reference wise)
 * then both assets will have the same asset key set (reference wise) via
 * {@link AssetKey#AssetKey() }, then this asset key
 * is used to track all instances of that asset. Once all clones of the asset 
 * are garbage collected, the shared asset key becomes unreachable and at that 
 * point it is removed from the smart asset cache. 
 */
public interface CloneableSmartAsset extends Cloneable {
    
    /**
     * Creates a clone of the asset. 
     * 
     * Please see {@link Object#clone() } for more info on how this method
     * should be implemented. 
     * 
     * @return A clone of this asset. 
     * The cloned asset cannot reference equal this asset.
     */
    public Object clone();
    
    /**
     * Set by the {@link AssetManager} to track this asset. 
     * 
     * Only clones of the asset has this set, the original copy that
     * was loaded has this key set to null so that only the clones are tracked
     * for garbage collection. 
     * 
     * @param key The AssetKey to set
     */
    public void setKey(AssetKey key);
    
    /**
     * Returns the asset key that is used to track this asset for garbage
     * collection.
     * 
     * @return the asset key that is used to track this asset for garbage
     * collection.
     */
    public AssetKey getKey();
}
