
package com.clockwork.asset;

import com.clockwork.asset.cache.AssetCache;
import com.clockwork.shader.ShaderNodeDefinition;
import java.util.List;

/**
 * Used for loading ShaderNodeDefinition shader nodes definition}
 *
 * Tells if the defintion has to be loaded with or without its documentation
 *
 */
public class ShaderNodeDefinitionKey extends AssetKey<List<ShaderNodeDefinition>> {

    private boolean loadDocumentation = false;

    /**
     * creates a ShaderNodeDefinitionKey
     *
     * @param name the name of the asset to load
     */
    public ShaderNodeDefinitionKey(String name) {
        super(name);
    }

    /**
     * creates a ShaderNodeDefinitionKey
     */
    public ShaderNodeDefinitionKey() {
        super();
    }

    @Override
    public Class<? extends AssetCache> getCacheType() {
        return null;
    }

    /**
     *
     * @return true if the asset loaded with this key will contain its
     * documentation
     */
    public boolean isLoadDocumentation() {
        return loadDocumentation;
    }

    /**
     * sets to true to load the documentation along with the
     * ShaderNodeDefinition
     *
     * @param loadDocumentation true to load the documentation along with the
     * ShaderNodeDefinition
     */
    public void setLoadDocumentation(boolean loadDocumentation) {
        this.loadDocumentation = loadDocumentation;
    }
}
