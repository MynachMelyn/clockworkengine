
package com.clockwork.asset.plugins;

import com.clockwork.asset.*;
import com.clockwork.system.JmeSystem;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * The <code>ClasspathLocator</code> looks up an asset in the classpath.
 */
public class ClasspathLocator implements AssetLocator {

    private static final Logger logger = Logger.getLogger(ClasspathLocator.class.getName());
    private String root = "";

    public ClasspathLocator(){
    }

    public void setRootPath(String rootPath) {
        this.root = rootPath;
        if (root.equals("/"))
            root = "";
        else if (root.length() > 1){
            if (root.startsWith("/")){
                root = root.substring(1);
            }
            if (!root.endsWith("/"))
                root += "/";
        }
    }
    
    public AssetInfo locate(AssetManager manager, AssetKey key) {
        URL url;
        String name = key.getName();
        if (name.startsWith("/"))
            name = name.substring(1);

        name = root + name;
//        if (!name.startsWith(root)){
//            name = root + name;
//        }

        if (JmeSystem.isLowPermissions()){
            url = ClasspathLocator.class.getResource("/" + name);
        }else{
            url = Thread.currentThread().getContextClassLoader().getResource(name);
        }
        if (url == null)
            return null;
        
        if (url.getProtocol().equals("file")){
            try {
                String path = new File(url.toURI()).getCanonicalPath();
                
                // convert to / for windows
                if (File.separatorChar == '\\'){
                    path = path.replace('\\', '/');
                }
                
                // compare path
                if (!path.endsWith(name)){
                    throw new AssetNotFoundException("Asset name doesn't match requirements.\n"+
                                                     "\"" + path + "\" doesn't match \"" + name + "\"");
                }
            } catch (URISyntaxException ex) {
                throw new AssetLoadException("Error converting URL to URI", ex);
            } catch (IOException ex){
                throw new AssetLoadException("Failed to get canonical path for " + url, ex);
            }
        }
        
        try{
            return UrlAssetInfo.create(manager, key, url);
        }catch (IOException ex){
            // This is different handling than URL locator
            // since classpath locating would return null at the getResource() 
            // call, otherwise there's a more critical error...
            throw new AssetLoadException("Failed to read URL " + url, ex);
        }
    }
}
