
package com.clockwork.asset;

import com.clockwork.asset.cache.AssetCache;
import com.clockwork.post.FilterPostProcessor;

/**
 * Used to load FilterPostProcessors which are not cached.
 * 
 */
public class FilterKey extends AssetKey<FilterPostProcessor> {

    public FilterKey(String name) {
        super(name);
    }

    public FilterKey() {
        super();
    }

    @Override
    public Class<? extends AssetCache> getCacheType(){
        // Do not cache filter processors
        return null;
    }
}
