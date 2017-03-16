
package com.clockwork.system;

import com.clockwork.asset.AssetManager;
import com.clockwork.audio.AudioRenderer;
import com.clockwork.input.SoftTextDialogInput;
import com.clockwork.texture.Image;
import com.clockwork.texture.image.ImageRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CWSystem {

    private static final Logger logger = Logger.getLogger(CWSystem.class.getName());
    public static enum StorageFolderType {
        Internal,
        External,
    }

    private static CWSystemDelegate systemDelegate;

    public static void setSystemDelegate(CWSystemDelegate systemDelegate) {
        CWSystem.systemDelegate = systemDelegate;
    }

    public static synchronized File getStorageFolder() {
        return getStorageFolder(StorageFolderType.External);
    }

    public static synchronized File getStorageFolder(StorageFolderType type) {
        checkDelegate();
        return systemDelegate.getStorageFolder(type);
    }

    public static String getFullName() {
        checkDelegate();
        return systemDelegate.getFullName();
    }

    public static InputStream getResourceAsStream(String name) {
        checkDelegate();
        return systemDelegate.getResourceAsStream(name);
    }

    public static URL getResource(String name) {
        checkDelegate();
        return systemDelegate.getResource(name);
    }

    public static boolean trackDirectMemory() {
        checkDelegate();
        return systemDelegate.trackDirectMemory();
    }

    public static void setLowPermissions(boolean lowPerm) {
        checkDelegate();
        systemDelegate.setLowPermissions(lowPerm);
    }

    public static boolean isLowPermissions() {
        checkDelegate();
        return systemDelegate.isLowPermissions();
    }

    public static void setSoftTextDialogInput(SoftTextDialogInput input) {
        checkDelegate();
        systemDelegate.setSoftTextDialogInput(input);
    }

    public static SoftTextDialogInput getSoftTextDialogInput() {
        checkDelegate();
        return systemDelegate.getSoftTextDialogInput();
    }

    public static void writeImageFile(OutputStream outStream, String format, ByteBuffer imageData, int width, int height) throws IOException {
        checkDelegate();
        systemDelegate.writeImageFile(outStream, format, imageData, width, height);
    }

    public static AssetManager newAssetManager(URL configFile) {
        checkDelegate();
        return systemDelegate.newAssetManager(configFile);
    }

    public static AssetManager newAssetManager() {
        checkDelegate();
        return systemDelegate.newAssetManager();
    }

    public static boolean showSettingsDialog(AppSettings sourceSettings, final boolean loadFromRegistry) {
        checkDelegate();
        return systemDelegate.showSettingsDialog(sourceSettings, loadFromRegistry);
    }

    public static Platform getPlatform() {
        checkDelegate();
        return systemDelegate.getPlatform();
    }

    public static CWContext newContext(AppSettings settings, CWContext.Type contextType) {
        checkDelegate();
        return systemDelegate.newContext(settings, contextType);
    }

    public static AudioRenderer newAudioRenderer(AppSettings settings) {
        checkDelegate();
        return systemDelegate.newAudioRenderer(settings);
    }

    public static ImageRaster createImageRaster(Image image, int slice) {
        checkDelegate();
        return systemDelegate.createImageRaster(image, slice);
    }

    /**
     * Displays an error message to the user in whichever way the context
     * feels is appropriate. If this is a headless or an offscreen surface
     * context, this method should do nothing.
     *
     * @param message The error message to display. May contain new line
     * characters.
     */
    public static void showErrorDialog(String message){
        checkDelegate();
        systemDelegate.showErrorDialog(message);
    }

    public static void initialize(AppSettings settings) {
        checkDelegate();
        systemDelegate.initialize(settings);
    }

    private static CWSystemDelegate tryLoadDelegate(String className) throws InstantiationException, IllegalAccessException {
        try {
            return (CWSystemDelegate) Class.forName(className).newInstance();
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static void checkDelegate() {
        if (systemDelegate == null) {
            try {
                systemDelegate = tryLoadDelegate("com.clockwork.system.CWDesktopSystem");
                if (systemDelegate == null) {
                    systemDelegate = tryLoadDelegate("com.clockwork.system.android.CWAndroidSystem");
                    if (systemDelegate == null) {
                        systemDelegate = tryLoadDelegate("com.clockwork.system.ios.CWIosSystem");
                        if (systemDelegate == null) {
                            // None of the system delegates were found ..
                            Logger.getLogger(CWSystem.class.getName()).log(Level.SEVERE,
                                    "Failed to find a CWSystem delegate!\n"
                                    + "Ensure either desktop or android CW jar is in the classpath.");
                        }
                    }
                }
            } catch (InstantiationException ex) {
                Logger.getLogger(CWSystem.class.getName()).log(Level.SEVERE, "Failed to create CWSystem delegate:\n{0}", ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(CWSystem.class.getName()).log(Level.SEVERE, "Failed to create CWSystem delegate:\n{0}", ex);
            }
        }
    }
}
