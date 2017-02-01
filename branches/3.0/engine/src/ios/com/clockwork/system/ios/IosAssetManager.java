
package com.clockwork.system.ios;

import com.clockwork.asset.AssetLoader;
import com.clockwork.asset.DesktopAssetManager;
import com.clockwork.asset.TextureKey;
import com.clockwork.asset.plugins.ClasspathLocator;
import com.clockwork.audio.plugins.WAVLoader;
import com.clockwork.texture.Texture;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class IosAssetManager extends DesktopAssetManager {

    private static final Logger logger = Logger.getLogger(IosAssetManager.class.getName());

    public IosAssetManager() {
        this(null);
    }

    @Deprecated
    public IosAssetManager(boolean loadDefaults) {
        //this(Thread.currentThread().getContextClassLoader().getResource("com/clockwork3/asset/Android.cfg"));
        this(null);
    }
    
    private void registerLoaderSafe(String loaderClass, String ... extensions) {
        try {
            Class<? extends AssetLoader> loader = (Class<? extends AssetLoader>) Class.forName(loaderClass);
            registerLoader(loader, extensions);
        } catch (Exception e){
            logger.log(Level.WARNING, "Failed to load AssetLoader", e);
        }
    }

    /**
     * AndroidAssetManager constructor
     * If URL == null then a default list of locators and loaders for android is set
     * @param configFile
     */
    public IosAssetManager(URL configFile) {
        System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
        
        // Set Default iOS config
        registerLocator("", ClasspathLocator.class);
        
        registerLoader(IosImageLoader.class, "jpg", "bmp", "gif", "png", "jpeg");
        //registerLoader(AndroidImageLoader.class, "jpg", "bmp", "gif", "png", "jpeg");
        //registerLoader(AndroidAudioLoader.class, "ogg", "mp3", "wav");
        registerLoader(com.clockwork.material.plugins.J3MLoader.class, "j3m");
        registerLoader(com.clockwork.material.plugins.J3MLoader.class, "j3md");
        registerLoader(com.clockwork.shader.plugins.GLSLLoader.class, "vert", "frag", "glsl", "glsllib");
        registerLoader(com.clockwork.export.binary.BinaryImporter.class, "j3o");
        registerLoader(com.clockwork.font.plugins.BitmapFontLoader.class, "fnt");
        registerLoader(WAVLoader.class, "wav");
        
        // Less common loaders (especially on iOS)
        registerLoaderSafe("com.clockwork.audio.plugins.OGGLoader", "ogg");
        registerLoaderSafe("com.clockwork.texture.plugins.DDSLoader", "dds");
        registerLoaderSafe("com.clockwork.texture.plugins.PFMLoader", "pfm");
        registerLoaderSafe("com.clockwork.texture.plugins.HDRLoader", "hdr");
        registerLoaderSafe("com.clockwork.texture.plugins.TGALoader", "tga");
        registerLoaderSafe("com.clockwork.scene.plugins.OBJLoader", "obj");
        registerLoaderSafe("com.clockwork.scene.plugins.MTLLoader", "mtl");
        registerLoaderSafe("com.clockwork.scene.plugins.ogre.MeshLoader", "mesh.xml");
        registerLoaderSafe("com.clockwork.scene.plugins.ogre.SkeletonLoader", "skeleton.xml");
        registerLoaderSafe("com.clockwork.scene.plugins.ogre.MaterialLoader", "material");
        registerLoaderSafe("com.clockwork.scene.plugins.ogre.SceneLoader", "scene");
        

        logger.fine("IosAssetManager created.");
    }

    /**
     * Loads a texture. 
     *
     * @return the texture
     */
    @Override
    public Texture loadTexture(TextureKey key) {
        Texture tex = (Texture) loadAsset(key);

        // XXX: This will improve performance on some really
        // low end GPUs (e.g. ones with OpenGL ES 1 support only)
        // but otherwise won't help on the higher ones. 
        // Strongly consider removing this.
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setAnisotropicFilter(0);
        if (tex.getMinFilter().usesMipMapLevels()) {
            tex.setMinFilter(Texture.MinFilter.NearestNearestMipMap);
        } else {
            tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        }
        return tex;
    }
}
