
package com.clockwork.shader;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.export.Savable;
import com.clockwork.shader.Shader.ShaderType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Shader node definition structure meant for holding loaded datat from a
 * material definition j3md file
 *
 * @author Nehon
 */
public class ShaderNodeDefinition implements Savable {

    private String name;
    private Shader.ShaderType type;
    private List<String> shadersLanguage = new ArrayList<String>();
    private List<String> shadersPath = new ArrayList<String>();
    private String documentation;
    private List<ShaderNodeVariable> inputs = new ArrayList<ShaderNodeVariable>();
    private List<ShaderNodeVariable> outputs = new ArrayList<ShaderNodeVariable>();
    private String path = null;

    /**
     * creates a ShaderNodeDefinition
     *
     * @param name the name of the definition
     * @param type the type of the shader
     * @param shaderPath the path of the shader
     * @param shaderLanguage the shader language (minimum required for this
     * definition)
     */
    public ShaderNodeDefinition(String name, ShaderType type, String shaderPath, String shaderLanguage) {
        this.name = name;
        this.type = type;
        shadersLanguage.add(shaderLanguage);
        shadersPath.add(shaderPath);
    }

    /**
     * creates a ShaderNodeDefinition
     */
    public ShaderNodeDefinition() {
    }

    /**
     * returns the name of the definition
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name of the definition
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return the type of shader the definition applies to
     */
    public ShaderType getType() {
        return type;
    }

    /**
     * sets the type of shader this def applies to
     *
     * @param type the type
     */
    public void setType(ShaderType type) {
        this.type = type;
    }

    /**
     *
     * @return the docuentation for tthis definition
     */
    public String getDocumentation() {
        return documentation;
    }

    /**
     * sets the dcumentation
     *
     * @param documentation the documentation
     */
    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    /**
     *
     * @return the input variables of this definition
     */
    public List<ShaderNodeVariable> getInputs() {
        return inputs;
    }

    /**
     * sets the input variables of this definition
     *
     * @param inputs the inputs
     */
    public void setInputs(List<ShaderNodeVariable> inputs) {
        this.inputs = inputs;
    }

    /**
     *
     * @return the output variables of this definition
     */
    public List<ShaderNodeVariable> getOutputs() {
        return outputs;
    }

    /**
     * sets the output variables of this definition
     *
     * @param inputs the output
     */
    public void setOutputs(List<ShaderNodeVariable> outputs) {
        this.outputs = outputs;
    }

    /**
     * retrun the path of this definition
     * @return 
     */
    public String getPath() {
        return path;
    }

    /**
     * sets the path of this definition
     * @param path 
     */
    public void setPath(String path) {
        this.path = path;
    }
    
    

    /**
     * jme seralization (not used)
     *
     * @param ex the exporter
     * @throws IOException
     */
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = (OutputCapsule) ex.getCapsule(this);
        oc.write(name, "name", "");
        String[] str = new String[shadersLanguage.size()];
        oc.write(shadersLanguage.toArray(str), "shadersLanguage", null);
        oc.write(shadersPath.toArray(str), "shadersPath", null);
        oc.write(type, "type", null);
        oc.writeSavableArrayList((ArrayList) inputs, "inputs", new ArrayList<ShaderNodeVariable>());
        oc.writeSavableArrayList((ArrayList) outputs, "inputs", new ArrayList<ShaderNodeVariable>());
    }

    public List<String> getShadersLanguage() {
        return shadersLanguage;
    }

    public List<String> getShadersPath() {
        return shadersPath;
    }

    
    
    /**
     * jme seralization (not used)
     *
     * @param im the importer
     * @throws IOException
     */
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = (InputCapsule) im.getCapsule(this);
        name = ic.readString("name", "");

        String[] str = ic.readStringArray("shadersLanguage", null);
        if (str != null) {
            shadersLanguage = Arrays.asList(str);
        } else {
            shadersLanguage = new ArrayList<String>();
        }

        str = ic.readStringArray("shadersPath", null);
        if (str != null) {
            shadersPath = Arrays.asList(str);
        } else {
            shadersPath = new ArrayList<String>();
        }

        type = ic.readEnum("type", Shader.ShaderType.class, null);
        inputs = (List<ShaderNodeVariable>) ic.readSavableArrayList("inputs", new ArrayList<ShaderNodeVariable>());
        outputs = (List<ShaderNodeVariable>) ic.readSavableArrayList("outputs", new ArrayList<ShaderNodeVariable>());
    }

    /**
     * convenience tostring
     *
     * @return a string
     */
    @Override
    public String toString() {
        return "\nShaderNodeDefinition{\n" + "name=" + name + "\ntype=" + type + "\nshaderPath=" + shadersPath + "\nshaderLanguage=" + shadersLanguage + "\ndocumentation=" + documentation + "\ninputs=" + inputs + ",\noutputs=" + outputs + '}';
    }
}
