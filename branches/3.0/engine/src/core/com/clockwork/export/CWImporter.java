
package com.clockwork.export;

import com.clockwork.asset.AssetLoader;
import com.clockwork.asset.AssetManager;

public interface CWImporter extends AssetLoader {
    public InputCapsule getCapsule(Savable id);
    public AssetManager getAssetManager();
    
    /**
     * Returns the version number written in the header of the J3O/XML
     * file.
     * 
     * @return Global version number for the file
     */
    public int getFormatVersion();
}
