
package com.clockwork.scene.plugins.ogre.matext;

import java.util.HashMap;

/**
 * MaterialExtension defines a mapping from an Ogre3D "base" material
 * to a CW material definition.
 */
public class MaterialExtension {

    private String baseMatName;
    private String CWMatDefName;
    private HashMap<String, String> textureMappings = new HashMap<String, String>();

    /**
     * Material extension defines a mapping from an Ogre3D "base" material
     * to a CW material definition.
     *
     * @param baseMatName The base material name for Ogre3D
     * @param CWMatDefName The material definition name for CW
     */
    public MaterialExtension(String baseMatName, String CWMatDefName) {
        this.baseMatName = baseMatName;
        this.CWMatDefName = CWMatDefName;
    }

    public String getBaseMaterialName() {
        return baseMatName;
    }

    public String getCWMatDefName() {
        return CWMatDefName;
    }

    /**
     * Set mapping from an Ogre3D base material texture alias to a
     * CW texture param
     * @param ogreTexAlias The texture alias in the Ogre3D base material
     * @param CWTexParam The texture param name in the CW material definition.
     */
    public void setTextureMapping(String ogreTexAlias, String CWTexParam){
        textureMappings.put(ogreTexAlias, CWTexParam);
    }

    /**
     * Retreives a mapping from an Ogre3D base material texture alias
     * to a CW texture param
     * @param ogreTexAlias The texture alias in the Ogre3D base material
     * @return The texture alias in the Ogre3D base material
     */
    public String getTextureMapping(String ogreTexAlias){
        return textureMappings.get(ogreTexAlias);
    }
}
