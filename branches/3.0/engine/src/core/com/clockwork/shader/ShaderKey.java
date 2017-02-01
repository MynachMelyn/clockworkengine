
package com.clockwork.shader;

import com.clockwork.asset.AssetKey;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import java.io.IOException;

public class ShaderKey extends AssetKey<Shader> {

    protected String fragName;
    protected DefineList defines;
    protected String vertLanguage;
    protected String fragLanguage;
    protected int cachedHashedCode = 0;
    protected boolean usesShaderNodes = false;

    public ShaderKey(){
    }

    public ShaderKey(String vertName, String fragName, DefineList defines, String vertLanguage, String fragLanguage){
        super(vertName);
        this.fragName = fragName;
        this.defines = defines;
        this.vertLanguage = vertLanguage;
        this.fragLanguage = fragLanguage;
    }
    
    @Override
    public ShaderKey clone() {
        ShaderKey clone = (ShaderKey) super.clone();
        clone.cachedHashedCode = 0;
        clone.defines = defines.clone();
        return clone;
    }
    
    @Override
    public String toString(){
        return "V="+name + " F=" + fragName + (defines != null ? defines : "");
    }

    @Override
    public boolean equals(Object obj) {
        final ShaderKey other = (ShaderKey) obj;
        if (name.equals(other.name) && fragName.equals(other.fragName)){
            if (defines != null && other.defines != null) {
                return defines.equals(other.defines);
            } else if (defines != null || other.defines != null) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (cachedHashedCode == 0) {
            int hash = 7;
            hash = 41 * hash + name.hashCode();
            hash = 41 * hash + fragName.hashCode();
            hash = 41 * hash + (defines != null ? defines.hashCode() : 0);
            cachedHashedCode = hash;
        }
        return cachedHashedCode;
    }

    public DefineList getDefines() {
        return defines;
    }

    public String getVertName(){
        return name;
    }

    public String getFragName() {
        return fragName;
    }

    /**
     * @deprecated Use {@link #getVertexShaderLanguage() } instead.
     */
    @Deprecated
    public String getLanguage() {
        return vertLanguage;
    }
    
    public String getVertexShaderLanguage() { 
        return vertLanguage;
    }
    
    public String getFragmentShaderLanguage() {
        return fragLanguage;
    }

    public boolean isUsesShaderNodes() {
        return usesShaderNodes;
    }

    public void setUsesShaderNodes(boolean usesShaderNodes) {
        this.usesShaderNodes = usesShaderNodes;
    }

    @Override
    public void write(JmeExporter ex) throws IOException{
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(fragName, "fragment_name", null);
        oc.write(vertLanguage, "language", null);
        oc.write(fragLanguage, "frag_language", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException{
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        fragName = ic.readString("fragment_name", null);
        vertLanguage = ic.readString("language", null);
        fragLanguage = ic.readString("frag_language", null);
    }

}
