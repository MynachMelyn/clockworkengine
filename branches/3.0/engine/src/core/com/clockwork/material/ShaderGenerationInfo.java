
package com.clockwork.material;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.export.Savable;
import com.clockwork.shader.ShaderNodeVariable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * this class is basically a struct that contains the ShaderNodes informations
 * in an appropriate way to ease the shader generation process and make it
 * faster.
 *
 * 
 */
public class ShaderGenerationInfo implements Savable {

    /**
     * the list of attributes of the vertex shader
     */
    protected List<ShaderNodeVariable> attributes = new ArrayList<ShaderNodeVariable>();
    /**
     * the list of all the uniforms to declare in the vertex shader
     */
    protected List<ShaderNodeVariable> vertexUniforms = new ArrayList<ShaderNodeVariable>();
    /**
     * the global output of the vertex shader (to assign ot gl_Position)
     */
    protected ShaderNodeVariable vertexGlobal = null;
    /**
     * the list of varyings
     */
    protected List<ShaderNodeVariable> varyings = new ArrayList<ShaderNodeVariable>();
    /**
     * the list of all the uniforms to declare in the fragment shader
     */
    protected List<ShaderNodeVariable> fragmentUniforms = new ArrayList<ShaderNodeVariable>();
    /**
     * the list of all the fragment shader global outputs (to assign ot gl_FragColor or gl_Fragdata[n])
     */
    protected List<ShaderNodeVariable> fragmentGlobals = new ArrayList<ShaderNodeVariable>();
    /**
     * the unused node names of this shader (node whose output are never used)
     */
    protected List<String> unusedNodes = new ArrayList<String>();

    /**
     *
     * @return the attributes
     */
    public List<ShaderNodeVariable> getAttributes() {
        return attributes;
    }

    /**
     *
     * @return the vertex shader uniforms
     */
    public List<ShaderNodeVariable> getVertexUniforms() {
        return vertexUniforms;
    }

    /**
     *
     * @return the fragment shader uniforms
     */
    public List<ShaderNodeVariable> getFragmentUniforms() {
        return fragmentUniforms;
    }

    /**
     *
     * @return the vertex shader global ouput
     */
    public ShaderNodeVariable getVertexGlobal() {
        return vertexGlobal;
    }

    /**
     *
     * @return the fragment shader global outputs
     */
    public List<ShaderNodeVariable> getFragmentGlobals() {
        return fragmentGlobals;
    }

    /**
     *
     * @return the varyings
     */
    public List<ShaderNodeVariable> getVaryings() {
        return varyings;
    }

    /**
     * sets the vertex shader global output
     *
     * @param vertexGlobal the global output
     */
    public void setVertexGlobal(ShaderNodeVariable vertexGlobal) {
        this.vertexGlobal = vertexGlobal;
    }

    /**
     * 
     * @return the list on unused node names
     */
    public List<String> getUnusedNodes() {
        return unusedNodes;
    }

    /**
     * the list of unused node names
     * @param unusedNodes 
     */
    public void setUnusedNodes(List<String> unusedNodes) {
        this.unusedNodes = unusedNodes;
    }
    
    /**
     * convenient toString method
     *
     * @return the informations
     */
    @Override
    public String toString() {
        return "ShaderGenerationInfo{" + "attributes=" + attributes + ", vertexUniforms=" + vertexUniforms + ", vertexGlobal=" + vertexGlobal + ", varyings=" + varyings + ", fragmentUniforms=" + fragmentUniforms + ", fragmentGlobals=" + fragmentGlobals + '}';
    }

    
    

    @Override
    public void write(CWExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.writeSavableArrayList((ArrayList) attributes, "attributes", new ArrayList<ShaderNodeVariable>());
        oc.writeSavableArrayList((ArrayList) vertexUniforms, "vertexUniforms", new ArrayList<ShaderNodeVariable>());
        oc.writeSavableArrayList((ArrayList) varyings, "varyings", new ArrayList<ShaderNodeVariable>());
        oc.writeSavableArrayList((ArrayList) fragmentUniforms, "fragmentUniforms", new ArrayList<ShaderNodeVariable>());
        oc.writeSavableArrayList((ArrayList) fragmentGlobals, "fragmentGlobals", new ArrayList<ShaderNodeVariable>());
        oc.write(vertexGlobal, "vertexGlobal", null);
    }

    @Override
    public void read(CWImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        attributes = ic.readSavableArrayList("attributes", new ArrayList<ShaderNodeVariable>());
        vertexUniforms = ic.readSavableArrayList("vertexUniforms", new ArrayList<ShaderNodeVariable>());
        varyings = ic.readSavableArrayList("varyings", new ArrayList<ShaderNodeVariable>());
        fragmentUniforms = ic.readSavableArrayList("fragmentUniforms", new ArrayList<ShaderNodeVariable>());
        fragmentGlobals = ic.readSavableArrayList("fragmentGlobals", new ArrayList<ShaderNodeVariable>());
        vertexGlobal = (ShaderNodeVariable) ic.readSavable("vertexGlobal", null);

    }
}
