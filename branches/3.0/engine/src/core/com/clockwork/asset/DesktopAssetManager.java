
package com.clockwork.asset;

import com.clockwork.asset.cache.AssetCache;
import com.clockwork.asset.cache.SimpleAssetCache;
import com.clockwork.audio.AudioData;
import com.clockwork.audio.AudioKey;
import com.clockwork.font.BitmapFont;
import com.clockwork.material.Material;
import com.clockwork.post.FilterPostProcessor;
import com.clockwork.renderer.Caps;
import com.clockwork.scene.Spatial;
import com.clockwork.shader.Glsl100ShaderGenerator;
import com.clockwork.shader.Glsl150ShaderGenerator;
import com.clockwork.shader.Shader;
import com.clockwork.shader.ShaderGenerator;
import com.clockwork.shader.ShaderKey;
import com.clockwork.texture.Texture;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <code>AssetManager</code> is the primary method for managing and loading
 * assets inside jME.
 *
 */
public class DesktopAssetManager implements AssetManager {

    private static final Logger logger = Logger.getLogger(AssetManager.class.getName());
    private ShaderGenerator shaderGenerator;
    
    private final ImplHandler handler = new ImplHandler(this);

    private CopyOnWriteArrayList<AssetEventListener> eventListeners = 
            new CopyOnWriteArrayList<AssetEventListener>();
    
    private List<ClassLoader> classLoaders =
            Collections.synchronizedList(new ArrayList<ClassLoader>());

    public DesktopAssetManager(){
        this(null);
    }

    @Deprecated
    public DesktopAssetManager(boolean loadDefaults){
        this(Thread.currentThread().getContextClassLoader().getResource("/com/clockwork/asset/Desktop.cfg"));
    }

    public DesktopAssetManager(URL configFile){
        if (configFile != null){
            loadConfigFile(configFile);
        }        
        logger.fine("DesktopAssetManager created.");
    }

    private void loadConfigFile(URL configFile){
        InputStream stream = null;
        try{
            AssetConfig cfg = new AssetConfig(this);
            stream = configFile.openStream();
            cfg.loadText(stream);
        }catch (IOException ex){
            logger.log(Level.SEVERE, "Failed to load asset config", ex);
        }finally{
            if (stream != null)
                try{
                    stream.close();
                }catch (IOException ex){
                }
        }
    }
    
    public void addClassLoader(ClassLoader loader) {
        classLoaders.add(loader);
    }
    
    public void removeClassLoader(ClassLoader loader) {
        classLoaders.remove(loader);
    }

    public List<ClassLoader> getClassLoaders(){
        return Collections.unmodifiableList(classLoaders);
    }
    
    public void addAssetEventListener(AssetEventListener listener) {
        eventListeners.add(listener);
    }

    public void removeAssetEventListener(AssetEventListener listener) {
        eventListeners.remove(listener);
    }

    public void clearAssetEventListeners() {
        eventListeners.clear();
    }
    
    public void setAssetEventListener(AssetEventListener listener){
        eventListeners.clear();
        eventListeners.add(listener);
    }

    public void registerLoader(Class<? extends AssetLoader> loader, String ... extensions){
        handler.addLoader(loader, extensions);
        if (logger.isLoggable(Level.FINER)){
            logger.log(Level.FINER, "Registered loader: {0} for extensions {1}",
              new Object[]{loader.getSimpleName(), Arrays.toString(extensions)});
        }
    }

    public void registerLoader(String clsName, String ... extensions){
        Class<? extends AssetLoader> clazz = null;
        try{
            clazz = (Class<? extends AssetLoader>) Class.forName(clsName);
        }catch (ClassNotFoundException ex){
            logger.log(Level.WARNING, "Failed to find loader: "+clsName, ex);
        }catch (NoClassDefFoundError ex){
            logger.log(Level.WARNING, "Failed to find loader: "+clsName, ex);
        }
        if (clazz != null){
            registerLoader(clazz, extensions);
        }
    }
    
    public void unregisterLoader(Class<? extends AssetLoader> loaderClass) {
        handler.removeLoader(loaderClass);
        if (logger.isLoggable(Level.FINER)){
            logger.log(Level.FINER, "Unregistered loader: {0}",
                    loaderClass.getSimpleName());
        }
    }

    public void registerLocator(String rootPath, Class<? extends AssetLocator> locatorClass){
        handler.addLocator(locatorClass, rootPath);
        if (logger.isLoggable(Level.FINER)){
            logger.log(Level.FINER, "Registered locator: {0}",
                    locatorClass.getSimpleName());
        }
    }

    public void registerLocator(String rootPath, String clsName){
        Class<? extends AssetLocator> clazz = null;
        try{
            clazz = (Class<? extends AssetLocator>) Class.forName(clsName);
        }catch (ClassNotFoundException ex){
            logger.log(Level.WARNING, "Failed to find locator: "+clsName, ex);
        }catch (NoClassDefFoundError ex){
            logger.log(Level.WARNING, "Failed to find loader: "+clsName, ex);
        }
        if (clazz != null){
            registerLocator(rootPath, clazz);
        }
    }
    
    public void unregisterLocator(String rootPath, Class<? extends AssetLocator> clazz){
        handler.removeLocator(clazz, rootPath);
        if (logger.isLoggable(Level.FINER)){
            logger.log(Level.FINER, "Unregistered locator: {0}",
                    clazz.getSimpleName());
        }
    }
    
    public AssetInfo locateAsset(AssetKey<?> key){
        AssetInfo info = handler.tryLocate(key);
        if (info == null){
            logger.log(Level.WARNING, "Cannot locate resource: {0}", key);
        }
        return info;
    }
    
    public <T> T getFromCache(AssetKey<T> key) {
        AssetCache cache = handler.getCache(key.getCacheType());
        if (cache != null) {
            T asset = cache.getFromCache(key);
            if (asset != null) {
                // Since getFromCache fills the load stack, it has to be popped
                cache.notifyNoAssetClone();
            }
            return asset;
        } else {
            throw new IllegalArgumentException("Key " + key + " specifies no cache.");
        }
    }
    
    public <T> void addToCache(AssetKey<T> key, T asset) {
        AssetCache cache = handler.getCache(key.getCacheType());
        if (cache != null) {
            cache.addToCache(key, asset);
            cache.notifyNoAssetClone();
        } else {
            throw new IllegalArgumentException("Key " + key + " specifies no cache.");
        }
    }
    
    public <T> boolean deleteFromCache(AssetKey<T> key) {
        AssetCache cache = handler.getCache(key.getCacheType());
        if (cache != null) {
            return cache.deleteFromCache(key);
        } else {
            throw new IllegalArgumentException("Key " + key + " specifies no cache.");
        }
    }
    
    public void clearCache(){
        handler.clearCache();
        if (logger.isLoggable(Level.FINER)){
            logger.log(Level.FINER, "All asset caches cleared.");
        }
    }

    /**
     * <font color="red">Thread-safe.</font>
     *
     * @param <T>
     * @param key
     * @return the loaded asset
     */
      public <T> T loadAsset(AssetKey<T> key){
        if (key == null)
            throw new IllegalArgumentException("key cannot be null");
        
        for (AssetEventListener listener : eventListeners){
            listener.assetRequested(key);
        }
        
        AssetCache cache = handler.getCache(key.getCacheType());
        AssetProcessor proc = handler.getProcessor(key.getProcessorType());
        
        Object obj = cache != null ? cache.getFromCache(key) : null;
        if (obj == null){
            // Asset not in cache, load it from file system.
            AssetLoader loader = handler.aquireLoader(key);
            AssetInfo info = handler.tryLocate(key);
            if (info == null){
                if (handler.getParentKey() != null){
                    // Inform event listener that an asset has failed to load.
                    // If the parent AssetLoader chooses not to propagate
                    // the exception, this is the only means of finding
                    // that something went wrong.
                    for (AssetEventListener listener : eventListeners){
                        listener.assetDependencyNotFound(handler.getParentKey(), key);
                    }
                }
                throw new AssetNotFoundException(key.toString());
            }

            try {
                handler.establishParentKey(key);
                obj = loader.load(info);
            } catch (IOException ex) {
                throw new AssetLoadException("An exception has occured while loading asset: " + key, ex);
            } finally {
                handler.releaseParentKey(key);
            }
            if (obj == null){
                throw new AssetLoadException("Error occured while loading asset \"" + key + "\" using " + loader.getClass().getSimpleName());
            }else{
                if (logger.isLoggable(Level.FINER)){
                    logger.log(Level.FINER, "Loaded {0} with {1}",
                            new Object[]{key, loader.getClass().getSimpleName()});
                }
                
                if (proc != null){
                    // do processing on asset before caching
                    obj = proc.postProcess(key, obj);
                }
                
                if (cache != null){
                    // At this point, obj should be of type T
                    cache.addToCache(key, (T) obj);
                }
                
                for (AssetEventListener listener : eventListeners){
                    listener.assetLoaded(key);
                }
            }
        }

        // object obj is the original asset
        // create an instance for user
        T clone = (T) obj;
        if (clone instanceof CloneableSmartAsset){
            if (proc == null){
                throw new IllegalStateException("Asset implements "
                        + "CloneableSmartAsset but doesn't "
                        + "have processor to handle cloning");
            }else{
                clone = (T) proc.createClone(obj);
                if (cache != null && clone != obj){
                    cache.registerAssetClone(key, clone);
                } else{
                    throw new IllegalStateException("Asset implements "
                        + "CloneableSmartAsset but doesn't have cache or "
                        + "was not cloned");
                }
            }
        }
       
        return clone;
    }

    public Object loadAsset(String name){
        return loadAsset(new AssetKey(name));
    }

    public Texture loadTexture(TextureKey key){
        return (Texture) loadAsset(key);
    }

    public Material loadMaterial(String name){
        return (Material) loadAsset(new MaterialKey(name));
    }

    public Texture loadTexture(String name){
        TextureKey key = new TextureKey(name, true);
        key.setGenerateMips(true);
        Texture tex = loadTexture(key);
        logger.log(Level.FINE, "{0} - {1}", new Object[]{tex, tex.getMinFilter()});
        return tex;
    }

    public AudioData loadAudio(AudioKey key){
        return (AudioData) loadAsset(key);
    }

    public AudioData loadAudio(String name){
        return loadAudio(new AudioKey(name, false));
    }

    public BitmapFont loadFont(String name){
        return (BitmapFont) loadAsset(new AssetKey(name));
    }

    public Spatial loadModel(ModelKey key){
        return (Spatial) loadAsset(key);
    }

    public Spatial loadModel(String name){
        return loadModel(new ModelKey(name));
    }

    public FilterPostProcessor loadFilter(FilterKey key){
        return (FilterPostProcessor) loadAsset(key);
    }

    public FilterPostProcessor loadFilter(String name){
        return loadFilter(new FilterKey(name));
    }

    /**
     * Load a vertex/fragment shader combo.
     *
     * @param key
     * @return the loaded {@link Shader}
     */
    public Shader loadShader(ShaderKey key){
        // cache abuse in method
        // that doesn't use loaders/locators
        AssetCache cache = handler.getCache(SimpleAssetCache.class);
        Shader shader = (Shader) cache.getFromCache(key);
        if (shader == null){
            if (key.isUsesShaderNodes()) {
                if(shaderGenerator == null){
                    throw new UnsupportedOperationException("ShaderGenerator was not initialized, make sure assetManager.getGenerator(caps) has been called");
                }
                shader = shaderGenerator.generateShader();
            } else {
                String vertName = key.getVertName();
                String fragName = key.getFragName();

                String vertSource = (String) loadAsset(new AssetKey(vertName));
                String fragSource = (String) loadAsset(new AssetKey(fragName));

                shader = new Shader();
                shader.initialize();
                shader.addSource(Shader.ShaderType.Vertex, vertName, vertSource, key.getDefines().getCompiled(), key.getVertexShaderLanguage());
                shader.addSource(Shader.ShaderType.Fragment, fragName, fragSource, key.getDefines().getCompiled(), key.getFragmentShaderLanguage());
            }

            cache.addToCache(key, shader);
        }
        return shader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShaderGenerator getShaderGenerator(EnumSet<Caps> caps) {
        if (shaderGenerator == null) {
            if(caps.contains(Caps.GLSL150)){
                shaderGenerator = new Glsl150ShaderGenerator(this);
            }else{
                shaderGenerator = new Glsl100ShaderGenerator(this);
            }
        }
        return shaderGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShaderGenerator(ShaderGenerator shaderGenerator) {
        this.shaderGenerator = shaderGenerator;
    }

    
}
