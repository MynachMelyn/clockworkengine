
package clockworktools.savegame;

import com.clockwork.asset.AssetManager;
import com.clockwork.export.Savable;
import com.clockwork.export.binary.BinaryExporter;
import com.clockwork.export.binary.BinaryImporter;
import com.clockwork.system.JmeSystem;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Tool for saving Savables as SaveGame entries in a system-dependent way.
 */
public class SaveGame {

    /**
     * Saves a savable in a system-dependent way.
     * @param gamePath A unique path for this game, e.g. com/mycompany/mygame
     * @param dataName A unique name for this savegame, e.g. "save_001"
     * @param data The Savable to save
     */
    public static void saveGame(String gamePath, String dataName, Savable data) {
        saveGame(gamePath, dataName, data, JmeSystem.StorageFolderType.External);
    }

    /**
     * Saves a savable in a system-dependent way.
     * @param gamePath A unique path for this game, e.g. com/mycompany/mygame
     * @param dataName A unique name for this savegame, e.g. "save_001"
     * @param data The Savable to save
     * @param storageType The specific type of folder to use to save the data
     */
    public static void saveGame(String gamePath, String dataName, Savable data, JmeSystem.StorageFolderType storageType) {
        if (storageType == null) {
            Logger.getLogger(SaveGame.class.getName()).log(Level.SEVERE, "Base Storage Folder Type is null, using External!");
            storageType = JmeSystem.StorageFolderType.External;
        }

        BinaryExporter ex = BinaryExporter.getInstance();
        OutputStream os = null;
        try {
            File baseFolder = JmeSystem.getStorageFolder(storageType);
            if (baseFolder == null) {
                Logger.getLogger(SaveGame.class.getName()).log(Level.SEVERE, "Error creating save file!");
                throw new IllegalStateException("SaveGame dataset cannot be created");
            }
            File daveFolder = new File(baseFolder.getAbsolutePath() + File.separator + gamePath.replace('/', File.separatorChar));
            if (!daveFolder.exists() && !daveFolder.mkdirs()) {
                Logger.getLogger(SaveGame.class.getName()).log(Level.SEVERE, "Error creating save file!");
                throw new IllegalStateException("SaveGame dataset cannot be created");
            }
            File saveFile = new File(daveFolder.getAbsolutePath() + File.separator + dataName);
            if (!saveFile.exists()) {
                if (!saveFile.createNewFile()) {
                    Logger.getLogger(SaveGame.class.getName()).log(Level.SEVERE, "Error creating save file!");
                    throw new IllegalStateException("SaveGame dataset cannot be created");
                }
            }
            os = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(saveFile)));
            ex.save(data, os);
            Logger.getLogger(SaveGame.class.getName()).log(Level.FINE, "Saving data to: {0}", saveFile.getAbsolutePath());
        } catch (IOException ex1) {
            Logger.getLogger(SaveGame.class.getName()).log(Level.SEVERE, "Error saving data: {0}", ex1);
            ex1.printStackTrace();
            throw new IllegalStateException("SaveGame dataset cannot be saved");
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException ex1) {
                Logger.getLogger(SaveGame.class.getName()).log(Level.SEVERE, "Error saving data: {0}", ex1);
                ex1.printStackTrace();
                throw new IllegalStateException("SaveGame dataset cannot be saved");
            }
        }
    }

    /**
     * Loads a savable that has been saved on this system with saveGame() before.
     * @param gamePath A unique path for this game, e.g. com/mycompany/mygame
     * @param dataName A unique name for this savegame, e.g. "save_001"
     * @return The savable that was saved
     */
    public static Savable loadGame(String gamePath, String dataName) {
        return loadGame(gamePath, dataName, null, JmeSystem.StorageFolderType.External);
    }

    /**
     * Loads a savable that has been saved on this system with saveGame() before.
     * @param gamePath A unique path for this game, e.g. com/mycompany/mygame
     * @param dataName A unique name for this savegame, e.g. "save_001"
     * @param storageType The specific type of folder to use to save the data
     * @return The savable that was saved
     */
    public static Savable loadGame(String gamePath, String dataName, JmeSystem.StorageFolderType storageType) {
        return loadGame(gamePath, dataName, null, storageType);
    }

    /**
     * Loads a savable that has been saved on this system with saveGame() before.
     * @param gamePath A unique path for this game, e.g. com/mycompany/mygame
     * @param dataName A unique name for this savegame, e.g. "save_001"
     * @param manager Link to an AssetManager if required for loading the data (e.g. models with textures)
     * @return The savable that was saved or null if none was found
     */
    public static Savable loadGame(String gamePath, String dataName, AssetManager manager) {
        return loadGame(gamePath, dataName, manager, JmeSystem.StorageFolderType.External);
    }

    /**
     * Loads a savable that has been saved on this system with saveGame() before.
     * @param gamePath A unique path for this game, e.g. com/mycompany/mygame
     * @param dataName A unique name for this savegame, e.g. "save_001"
     * @param manager Link to an AssetManager if required for loading the data (e.g. models with textures)
     * @param storageType The specific type of folder to use to save the data
     * @return The savable that was saved or null if none was found
     */
    public static Savable loadGame(String gamePath, String dataName, AssetManager manager, JmeSystem.StorageFolderType storageType) {
        if (storageType == null) {
            Logger.getLogger(SaveGame.class.getName()).log(Level.SEVERE, "Base Storage Folder Type is null, using External!");
            storageType = JmeSystem.StorageFolderType.External;
        }

        InputStream is = null;
        Savable sav = null;
        try {
            File baseFolder = JmeSystem.getStorageFolder(storageType);
            if (baseFolder == null) {
                Logger.getLogger(SaveGame.class.getName()).log(Level.SEVERE, "Error reading base storage folder!");
                return null;
            }
            File file = new File(baseFolder.getAbsolutePath() + File.separator + gamePath.replace('/', File.separatorChar) + File.separator + dataName);
            if(!file.exists()){
                return null;
            }
            is = new GZIPInputStream(new BufferedInputStream(new FileInputStream(file)));
            BinaryImporter imp = BinaryImporter.getInstance();
            if (manager != null) {
                imp.setAssetManager(manager);
            }
            sav = imp.load(is);
            Logger.getLogger(SaveGame.class.getName()).log(Level.FINE, "Loading data from: {0}", file.getAbsolutePath());
        } catch (IOException ex) {
            Logger.getLogger(SaveGame.class.getName()).log(Level.SEVERE, "Error loading data: {0}", ex);
            ex.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(SaveGame.class.getName()).log(Level.SEVERE, "Error loading data: {0}", ex);
                    ex.printStackTrace();
                }
            }
        }
        return sav;
    }
}
