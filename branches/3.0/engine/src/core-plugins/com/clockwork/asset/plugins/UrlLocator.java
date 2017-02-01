
package com.clockwork.asset.plugins;

import com.clockwork.asset.AssetInfo;
import com.clockwork.asset.AssetKey;
import com.clockwork.asset.AssetLocator;
import com.clockwork.asset.AssetManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <code>UrlLocator</code> is a locator that combines a root URL
 * and the given path in the AssetKey to construct a new URL
 * that allows locating the asset.
 */
public class UrlLocator implements AssetLocator {

    private static final Logger logger = Logger.getLogger(UrlLocator.class.getName());
    private URL root;

    public void setRootPath(String rootPath) {
        try {
            this.root = new URL(rootPath);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Invalid rootUrl specified", ex);
        }
    }

    public AssetInfo locate(AssetManager manager, AssetKey key) {
        String name = key.getName();
        try{
            //TODO: remove workaround for SDK
//            URL url = new URL(root, name);
            if(name.startsWith("/")){
                name = name.substring(1);
            }
            URL url = new URL(root.toExternalForm() + name);
            return UrlAssetInfo.create(manager, key, url);
        }catch (FileNotFoundException e){
            return null;
        }catch (IOException ex){
            logger.log(Level.WARNING, "Error while locating " + name, ex);
            return null;
        }
    }


}
