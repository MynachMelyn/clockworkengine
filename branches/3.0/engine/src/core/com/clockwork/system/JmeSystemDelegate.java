
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
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * 
 */
public abstract class JmeSystemDelegate {

    protected final Logger logger = Logger.getLogger(JmeSystem.class.getName());
    protected boolean initialized = false;
    protected boolean lowPermissions = false;
    protected Map<JmeSystem.StorageFolderType, File> storageFolders = new EnumMap<JmeSystem.StorageFolderType, File>(JmeSystem.StorageFolderType.class);
    protected SoftTextDialogInput softTextDialogInput = null;

    public synchronized File getStorageFolder(JmeSystem.StorageFolderType type) {
        File storageFolder = null;

        switch (type) {
            // Internal and External are currently the same folder
            case Internal:
            case External:
                if (lowPermissions) {
                    throw new UnsupportedOperationException("File system access restricted");
                }
                storageFolder = storageFolders.get(type);
                if (storageFolder == null) {
                    // Initialize storage folder
                    storageFolder = new File(System.getProperty("user.home"), ".jme3");
                    if (!storageFolder.exists()) {
                        storageFolder.mkdir();
                    }
                    storageFolders.put(type, storageFolder);
                }
                break;
            default:
                break;
        }
        if (storageFolder != null) {
            logger.log(Level.FINE, "Storage Folder Path: {0}", storageFolder.getAbsolutePath());
        } else {
            logger.log(Level.FINE, "Storage Folder not found!");
        }
        return storageFolder;
    }

    public String getFullName() {
        return JmeVersion.FULL_NAME;
    }

    public InputStream getResourceAsStream(String name) {
        return this.getClass().getResourceAsStream(name);
    }

    public URL getResource(String name) {
        return this.getClass().getResource(name);
    }

    public boolean trackDirectMemory() {
        return false;
    }

    public void setLowPermissions(boolean lowPerm) {
        lowPermissions = lowPerm;
    }

    public boolean isLowPermissions() {
        return lowPermissions;
    }

    public void setSoftTextDialogInput(SoftTextDialogInput input) {
        softTextDialogInput = input;
    }
    public SoftTextDialogInput getSoftTextDialogInput() {
        return softTextDialogInput;
    }

    public abstract void writeImageFile(OutputStream outStream, String format, ByteBuffer imageData, int width, int height) throws IOException;

    public abstract AssetManager newAssetManager(URL configFile);

    public abstract AssetManager newAssetManager();

    public abstract void showErrorDialog(String message);

    public abstract boolean showSettingsDialog(AppSettings sourceSettings, boolean loadFromRegistry);

    private boolean is64Bit(String arch) {
        if (arch.equals("x86")) {
            return false;
        } else if (arch.equals("amd64")) {
            return true;
        } else if (arch.equals("x86_64")) {
            return true;
        } else if (arch.equals("ppc") || arch.equals("PowerPC")) {
            return false;
        } else if (arch.equals("ppc64")) {
            return true;
        } else if (arch.equals("i386") || arch.equals("i686")) {
            return false;
        } else if (arch.equals("universal")) {
            return false;
        } else if (arch.equals("arm")) {
            return false;
        } else {
            throw new UnsupportedOperationException("Unsupported architecture: " + arch);
        }
    }

    public Platform getPlatform() {
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();
        boolean is64 = is64Bit(arch);
        if (os.contains("windows")) {
            return is64 ? Platform.Windows64 : Platform.Windows32;
        } else if (os.contains("linux") || os.contains("freebsd") || os.contains("sunos")) {
            return is64 ? Platform.Linux64 : Platform.Linux32;
        } else if (os.contains("mac os x") || os.contains("darwin")) {
            if (arch.startsWith("ppc")) {
                return is64 ? Platform.MacOSX_PPC64 : Platform.MacOSX_PPC32;
            } else {
                return is64 ? Platform.MacOSX64 : Platform.MacOSX32;
            }
        } else {
            throw new UnsupportedOperationException("The specified platform: " + os + " is not supported.");
        }
    }

    public abstract JmeContext newContext(AppSettings settings, JmeContext.Type contextType);

    public abstract AudioRenderer newAudioRenderer(AppSettings settings);

    public abstract void initialize(AppSettings settings);

    public abstract ImageRaster createImageRaster(Image image, int slice);
}
