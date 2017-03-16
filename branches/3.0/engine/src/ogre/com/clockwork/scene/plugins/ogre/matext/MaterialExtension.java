
package com.clockwork.scene.plugins.ogre.matext;

import java.util.HashMap;

/**
 * MaterialExtension defines a mapping from an Ogre3D "base" material
 * to a jME3 material definition.
 */
public class MaterialExtension {

    private String baseMatName;
    private String jmeMatDefName;
    private HashMap<String, String> textureMappings = new HashMap<String, String>();

    /**
     * Material extension defines a mapping from an Ogre3D "base" material
     * to a jME3 material definition.
     *
     * @param baseMatName The base material name for Ogre3D
     * @param jmeMatDefName The material definition name for jME3
     */
    public MaterialExtension(String baseMatName, String jmeMatDefName) {
        this.baseMatName = baseMatName;
        this.jmeMatDefName = jmeMatDefName;
    }

    public String getBaseMaterialName() {
        return baseMatName;
    }

    public String getJmeMatDefName() {
        return jmeMatDefName;
    }

    /**
     * Set mapping from an Ogre3D base material texture alias to a
     * jME3 texture param
     * @param ogreTexAlias The texture alias in the Ogre3D base material
     * @param jmeTexParam The texture param name in the jME3 material definition.
     */
    public void setTextureMapping(String ogreTexAlias, String jmeTexParam){
        textureMappings.put(ogreTexAlias, jmeTexParam);
    }

    /**
     * Retreives a mapping from an Ogre3D base material texture alias
     * to a jME3 texture param
     * @param ogreTexAlias The texture alias in the Ogre3D base material
     * @return The texture alias in the Ogre3D base material
     */
    public String getTextureMapping(String ogreTexAlias){
        return textureMappings.get(ogreTexAlias);
    }
}
