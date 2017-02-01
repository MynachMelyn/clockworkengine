
package com.clockwork.scene.plugins.ogre;

import com.clockwork.asset.ModelKey;
import com.clockwork.material.MaterialList;

/**
 * OgreMeshKey is used to load Ogre3D mesh.xml models with a specific
 * material file or list. This allows customizing from where the materials
 * are retrieved, instead of loading the material file as the same
 * name as the model (the default).
 * 
 */
public class OgreMeshKey extends ModelKey {

    private MaterialList materialList;
    private String materialName;

    public OgreMeshKey(){
        super();
    }

    public OgreMeshKey(String name){
        super(name);
    }
    
    public OgreMeshKey(String name, MaterialList materialList){
        super(name);
        this.materialList = materialList;
    }
    
    public OgreMeshKey(String name, String materialName){
        super(name);
        this.materialName = materialName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OgreMeshKey other = (OgreMeshKey) obj;
        if (!super.equals(other)) {
            return false;
        }
        if (this.materialList != other.materialList && (this.materialList == null || !this.materialList.equals(other.materialList))) {
            return false;
        }
        if ((this.materialName == null) ? (other.materialName != null) : !this.materialName.equals(other.materialName)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + (super.hashCode());
        hash = 31 * hash + (this.materialList != null ? this.materialList.hashCode() : 0);
        hash = 31 * hash + (this.materialName != null ? this.materialName.hashCode() : 0);
        return hash;
    }
    
    public MaterialList getMaterialList() {
        return materialList;
    }
    
    public void setMaterialList(MaterialList materialList){
        this.materialList = materialList;
    }
    
    public String getMaterialName() {
        return materialName;
    }
    
    public void setMaterialName(String name) {
        materialName = name;
    }

}
