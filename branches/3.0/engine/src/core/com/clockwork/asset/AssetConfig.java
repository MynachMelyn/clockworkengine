
package com.clockwork.asset;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <code>AssetConfig</code> loads a config file to configure the asset manager.
 * <br/><br/>
 * The config file is specified with the following format:
 * <code>
 * "LOADER" <class> : (<extension> ",")* <extension>
 * "LOCATOR" <path> <class> : (<extension> ",")* <extension>
 * </code>
 *
 */
public class AssetConfig {

    private AssetManager manager;

    public AssetConfig(AssetManager manager){
        this.manager = manager;
    }

    public void loadText(InputStream in) throws IOException{
        Scanner scan = new Scanner(in);
        while (scan.hasNext()){
            String cmd = scan.next();
            if (cmd.equals("LOADER")){
                String loaderClass = scan.next();
                String colon = scan.next();
                if (!colon.equals(":")){
                    throw new IOException("Expected ':', got '"+colon+"'");
                }
                String extensionsList = scan.nextLine();
                String[] extensions = extensionsList.split(",");
                for (int i = 0; i < extensions.length; i++){
                    extensions[i] = extensions[i].trim();
                }
                Class clazz = acquireClass(loaderClass);
                if (clazz != null) {
                    manager.registerLoader(clazz, extensions);
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot find loader {0}", loaderClass);
                }
            } else if (cmd.equals("LOCATOR")) {
                String rootPath = scan.next();
                String locatorClass = scan.nextLine().trim();
                Class clazz = acquireClass(locatorClass);
                if (clazz != null) {
                    manager.registerLocator(rootPath, clazz);
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot find locator {0}", locatorClass);
                }
            } else {
                throw new IOException("Expected command, got '" + cmd + "'");
            }
        }
    }
    
    private Class acquireClass(String name) {
        try {
            Class clazz = Class.forName(name);
            return clazz;
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }
    
    /*
    private static String readString(DataInput dataIn) throws IOException{
        int length = dataIn.readUnsignedShort();
        char[] chrs = new char[length];
        for (int i = 0; i < length; i++){
            chrs[i] = (char) dataIn.readUnsignedByte();
        }
        return String.valueOf(chrs);
    }

    public void loadBinary(DataInput dataIn) throws IOException{
        // read signature and version

        // how many locator entries?
        int locatorEntries = dataIn.readUnsignedShort();
        for (int i = 0; i < locatorEntries; i++){
            String locatorClazz = readString(dataIn);
            String rootPath = readString(dataIn);
            manager.registerLocator(rootPath, locatorClazz);
        }

        int loaderEntries = dataIn.readUnsignedShort();
        for (int i = 0; i < loaderEntries; i++){
            String loaderClazz = readString(dataIn);
            int numExtensions = dataIn.readUnsignedByte();
            String[] extensions = new String[numExtensions];
            for (int j = 0; j < numExtensions; j++){
                extensions[j] = readString(dataIn);
            }

            manager.registerLoader(loaderClazz, extensions);
        }
    }
    */
}
