
package com.clockwork.export;

import com.clockwork.animation.Animation;
import com.clockwork.effect.shapes.*;
import com.clockwork.material.MatParamTexture;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <code>SavableClassUtil</code> contains various utilities to handle
 * Savable classes. The methods are general enough to not be specific to any
 * particular implementation.
 * Currently it will remap any classes from old paths to new paths
 * so that old J3O models can still be loaded.
 *
 */
public class SavableClassUtil {

    private final static HashMap<String, String> classRemappings = new HashMap<String, String>();
    
    private static void addRemapping(String oldClass, Class<? extends Savable> newClass){
        classRemappings.put(oldClass, newClass.getName());
    }
    
    static {
        addRemapping("com.clockwork.effect.EmitterSphereShape", EmitterSphereShape.class);
        addRemapping("com.clockwork.effect.EmitterBoxShape", EmitterBoxShape.class);
        addRemapping("com.clockwork.effect.EmitterMeshConvexHullShape", EmitterMeshConvexHullShape.class);
        addRemapping("com.clockwork.effect.EmitterMeshFaceShape", EmitterMeshFaceShape.class);
        addRemapping("com.clockwork.effect.EmitterMeshVertexShape", EmitterMeshVertexShape.class);
        addRemapping("com.clockwork.effect.EmitterPointShape", EmitterPointShape.class);
        addRemapping("com.clockwork.material.Material$MatParamTexture", MatParamTexture.class);
        addRemapping("com.clockwork.animation.BoneAnimation", Animation.class);
        addRemapping("com.clockwork.animation.SpatialAnimation", Animation.class);
        addRemapping("com.clockwork.scene.plugins.blender.objects.Properties", NullSavable.class);
    }
    
    private static String remapClass(String className) throws ClassNotFoundException {
        String result = classRemappings.get(className);
        if (result == null) {
            return className;
        } else {
            return result;
        }
    }
    
    public static boolean isImplementingSavable(Class clazz){
        boolean result = Savable.class.isAssignableFrom(clazz);
        return result;
    }

    public static int[] getSavableVersions(Class<? extends Savable> clazz) throws IOException{
        ArrayList<Integer> versionList = new ArrayList<Integer>();
        Class superclass = clazz;
        do {
            versionList.add(getSavableVersion(superclass));
            superclass = superclass.getSuperclass();
        } while (superclass != null && SavableClassUtil.isImplementingSavable(superclass));
        
        int[] versions = new int[versionList.size()];
        for (int i = 0; i < versionList.size(); i++){
            versions[i] = versionList.get(i);
        }
        return versions;
    }
    
    public static int getSavableVersion(Class<? extends Savable> clazz) throws IOException{
        try {
            Field field = clazz.getField("SAVABLE_VERSION");
            Class<? extends Savable> declaringClass = (Class<? extends Savable>) field.getDeclaringClass();
            if (declaringClass == clazz){
                return field.getInt(null); 
            }else{
                return 0; // This class doesn't declare this field, e.g. version == 0
            }
        } catch (IllegalAccessException ex) {
            IOException ioEx = new IOException();
            ioEx.initCause(ex);
            throw ioEx;
        } catch (IllegalArgumentException ex) {
            throw ex; // can happen if SAVABLE_VERSION is not static
        } catch (NoSuchFieldException ex) {
            return 0; // not using versions
        }
    }
    
    public static int getSavedSavableVersion(Object savable, Class<? extends Savable> desiredClass, int[] versions, int formatVersion){
        Class thisClass = savable.getClass();
        int count = 0;
        
        while (thisClass != desiredClass) {
            thisClass = thisClass.getSuperclass();
            if (thisClass != null && SavableClassUtil.isImplementingSavable(thisClass)){
                count ++;
            }else{
                break;
            }
        }

        if (thisClass == null){
            throw new IllegalArgumentException(savable.getClass().getName() + 
                                               " does not extend " + 
                                               desiredClass.getName() + "!");
        }else if (count >= versions.length){
            if (formatVersion <= 1){
                return 0; // for buggy versions of j3o
            }else{
                throw new IllegalArgumentException(savable.getClass().getName() + 
                                                   " cannot access version of " +
                                                   desiredClass.getName() + 
                                                   " because it doesn't implement Savable");
            }
        }
        return versions[count];
    }
    
    /**
     * fromName creates a new Savable from the provided class name. First registered modules
     * are checked to handle special cases, if the modules do not handle the class name, the
     * class is instantiated directly. 
     * @param className the class name to create.
     * @return the Savable instance of the class.
     * @throws InstantiationException thrown if the class does not have an empty constructor.
     * @throws IllegalAccessException thrown if the class is not accessable.
     * @throws ClassNotFoundException thrown if the class name is not in the classpath.
     * @throws IOException when loading ctor parameters fails
     */
    public static Savable fromName(String className) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException, IOException {

        className = remapClass(className);
        try {
            return (Savable) Class.forName(className).newInstance();
        } catch (InstantiationException e) {
            Logger.getLogger(SavableClassUtil.class.getName()).log(
                    Level.SEVERE, "Could not access constructor of class ''{0}" + "''! \n"
                    + "Some types need to have the BinaryImporter set up in a special way. Please doublecheck the setup.", className);
            throw e;
        } catch (IllegalAccessException e) {
            Logger.getLogger(SavableClassUtil.class.getName()).log(
                    Level.SEVERE, "{0} \n"
                    + "Some types need to have the BinaryImporter set up in a special way. Please doublecheck the setup.", e.getMessage());
            throw e;
        }
    }

    public static Savable fromName(String className, List<ClassLoader> loaders) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException, IOException {
        if (loaders == null) {
            return fromName(className);
        }
        
        String newClassName = remapClass(className);
        synchronized(loaders) {
            for (ClassLoader classLoader : loaders){
                try {
                    return (Savable) classLoader.loadClass(newClassName).newInstance();
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                }

            }
        }

        return fromName(className);
    }
}
