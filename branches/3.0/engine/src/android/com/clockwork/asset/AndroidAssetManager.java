
package com.clockwork.asset;

import com.clockwork.asset.plugins.AndroidLocator;
import com.clockwork.asset.plugins.ClasspathLocator;
import com.clockwork.audio.android.AndroidAudioRenderer;
import com.clockwork.audio.plugins.AndroidAudioLoader;
import com.clockwork.audio.plugins.WAVLoader;
import com.clockwork.system.AppSettings;
import com.clockwork.system.android.JmeAndroidSystem;
import com.clockwork.texture.plugins.AndroidImageLoader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <code>AndroidAssetManager</code> is an implementation of DesktopAssetManager for Android
 *
 * 
 */
public class AndroidAssetManager extends DesktopAssetManager {

    private static final Logger logger = Logger.getLogger(AndroidAssetManager.class.getName());

    public AndroidAssetManager() {
        this(null);
    }

    @Deprecated
    public AndroidAssetManager(boolean loadDefaults) {
        //this(Thread.currentThread().getContextClassLoader().getResource("com/clockwork/asset/Android.cfg"));
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
    public AndroidAssetManager(URL configFile) {
        System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");

        // Set Default Android config
        registerLocator("", AndroidLocator.class);
        registerLocator("", ClasspathLocator.class);

        registerLoader(AndroidImageLoader.class, "jpg", "bmp", "gif", "png", "jpeg");
        if (JmeAndroidSystem.getAudioRendererType().equals(AppSettings.ANDROID_MEDIAPLAYER)) {
            registerLoader(AndroidAudioLoader.class, "ogg", "mp3", "wav");
        } else if (JmeAndroidSystem.getAudioRendererType().equals(AppSettings.ANDROID_OPENAL_SOFT)) {
            registerLoader(WAVLoader.class, "wav");
            // TODO jogg is not in core, need to add some other way to get around compile errors, or not.
//            registerLoader(com.clockwork.audio.plugins.OGGLoader.class, "ogg");
            registerLoaderSafe("com.clockwork.audio.plugins.OGGLoader", "ogg");
        } else {
            throw new IllegalStateException("No Audio Renderer Type defined!");
        }

        registerLoader(com.clockwork.material.plugins.J3MLoader.class, "j3m");
        registerLoader(com.clockwork.material.plugins.J3MLoader.class, "j3md");
        registerLoader(com.clockwork.material.plugins.ShaderNodeDefinitionLoader.class, "j3sn");
        registerLoader(com.clockwork.shader.plugins.GLSLLoader.class, "vert", "frag", "glsl", "glsllib");
        registerLoader(com.clockwork.export.binary.BinaryImporter.class, "j3o");
        registerLoader(com.clockwork.font.plugins.BitmapFontLoader.class, "fnt");

        // Less common loaders (especially on Android)
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


        logger.fine("AndroidAssetManager created.");
    }

}
