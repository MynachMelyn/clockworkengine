
package com.clockwork.asset.plugins;

import com.clockwork.asset.*;
import java.io.*;

/**
 * <code>FileLocator</code> allows you to specify a folder where to
 * look for assets. 
 */
public class FileLocator implements AssetLocator {

    private File root;

    public void setRootPath(String rootPath) {
        if (rootPath == null)
            throw new NullPointerException();
        
        try {
            root = new File(rootPath).getCanonicalFile();
            if (!root.isDirectory()){
                throw new IllegalArgumentException("Given root path \"" + root + "\" is not a directory");
            }
        } catch (IOException ex) {
            throw new AssetLoadException("Root path is invalid", ex);
        }
    }

    private static class AssetInfoFile extends AssetInfo {

        private File file;

        public AssetInfoFile(AssetManager manager, AssetKey key, File file){
            super(manager, key);
            this.file = file;
        }

        @Override
        public InputStream openStream() {
            try{
                return new FileInputStream(file);
            }catch (FileNotFoundException ex){
                // NOTE: Can still happen even if file.exists() is true, e.g.
                // permissions issue and similar
                throw new AssetLoadException("Failed to open file: " + file, ex);
            }
        }
    }

    public AssetInfo locate(AssetManager manager, AssetKey key) {
        String name = key.getName();
        File file = new File(root, name);
        if (file.exists() && file.isFile()){
            try {
                // Now, check asset name requirements
                String canonical = file.getCanonicalPath();
                String absolute = file.getAbsolutePath();
                if (!canonical.endsWith(absolute)){
                    throw new AssetNotFoundException("Asset name doesn't match requirements.\n"+
                                                     "\"" + canonical + "\" doesn't match \"" + absolute + "\"");
                }
            } catch (IOException ex) {
                throw new AssetLoadException("Failed to get file canonical path " + file, ex);
            }
            
            return new AssetInfoFile(manager, key, file);
        }else{
            return null;
        }
    }

}
