
package com.clockwork.scene.plugins.ogre.matext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <code>MaterialExtensionSet</code> is simply a container for several
 * {@link MaterialExtension}s so that it can be set globally for all
 * {@link OgreMaterialKey}s used.
 */
public class MaterialExtensionSet {

    private HashMap<String, MaterialExtension> extensions = new HashMap<String, MaterialExtension>();
    private HashMap<String, List<String>> nameMappings = new HashMap<String, List<String>>();

    /**
     * Adds a new material extension to the set of extensions.
     *
     * @param extension The {@link MaterialExtension} to add.
     */
    public void addMaterialExtension(MaterialExtension extension) {
        extensions.put(extension.getBaseMaterialName(), extension);
    }

    /**
     * Returns the {@link MaterialExtension} for a given Ogre3D base material
     * name.
     *
     * @param baseMatName The ogre3D base material name.
     * @return {@link MaterialExtension} that is set, or null if not set.
     */
    public MaterialExtension getMaterialExtension(String baseMatName) {
        return extensions.get(baseMatName);
    }

    /**
     * Adds an alternative name for a material
     *
     * @param name The material name to be found in a .mesh.xml file
     * @param alias The material name to be found in a .material file
     */
    public void setNameMapping(String name, String alias) {
        List<String> list = nameMappings.get(name);
        if (list == null) {
            list = new ArrayList<String>();
            nameMappings.put(name, list);
        }
        list.add(alias);
    }

    public List<String> getNameMappings(String name) {
        return nameMappings.get(name);
    }
}
