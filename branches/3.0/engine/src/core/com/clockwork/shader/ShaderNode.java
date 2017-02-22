
package com.clockwork.shader;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.export.Savable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A ShaderNode is the unit brick part of a shader program. A shader can be
 * describe with several shader nodes that are plugged together through inputs
 * and outputs.
 *
 * A ShaderNode is based on a definition that has a shader code, inputs and
 * output variables. This node can be activated based on a condition, and has
 * input and ouput mapping.
 *
 * This class is not intended to be used by JME users directly. It's the
 * stucture for loading shader nodes from a J3md ùaterial definition file
 *
 * 
 */
public class ShaderNode implements Savable {

    private String name;
    private ShaderNodeDefinition definition;
    private String condition;
    private List<VariableMapping> inputMapping = new ArrayList<VariableMapping>();
    private List<VariableMapping> outputMapping = new ArrayList<VariableMapping>();

    /**
     * creates a ShaderNode
     *
     * @param name the name
     * @param definition the ShaderNodeDefinition
     * @param condition the conditionto activate this node
     */
    public ShaderNode(String name, ShaderNodeDefinition definition, String condition) {
        this.name = name;
        this.definition = definition;
        this.condition = condition;
    }

    /**
     * creates a ShaderNode
     */
    public ShaderNode() {
    }

    /**
     *
     * @return the name of the node
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name of th node
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns the definition
     *
     * @return the ShaderNodeDefinition
     */
    public ShaderNodeDefinition getDefinition() {
        return definition;
    }

    /**
     * sets the definition
     *
     * @param definition the ShaderNodeDefinition
     */
    public void setDefinition(ShaderNodeDefinition definition) {
        this.definition = definition;
    }

    /**
     *
     * @return the condition
     */
    public String getCondition() {
        return condition;
    }

    /**
     * sets the ocndition
     *
     * @param condition the condition
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * return a list of VariableMapping representing the input mappings of this
     * node
     *
     * @return the input mappings
     */
    public List<VariableMapping> getInputMapping() {
        return inputMapping;
    }

    /**
     * sets the input mappings
     *
     * @param inputMapping the input mappings
     */
    public void setInputMapping(List<VariableMapping> inputMapping) {
        this.inputMapping = inputMapping;
    }

    /**
     * return a list of VariableMapping representing the output mappings of this
     * node
     *
     * @return the output mappings
     */
    public List<VariableMapping> getOutputMapping() {
        return outputMapping;
    }

    /**
     * sets the output mappings
     *
     * @param inputMapping the output mappings
     */
    public void setOutputMapping(List<VariableMapping> outputMapping) {
        this.outputMapping = outputMapping;
    }

    /**
     * jme seralization
     *
     * @param ex the exporter
     * @throws IOException
     */
    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = (OutputCapsule) ex.getCapsule(this);
        oc.write(name, "name", "");
        oc.write(definition, "definition", null);
        oc.write(condition, "condition", null);
        oc.writeSavableArrayList((ArrayList) inputMapping, "inputMapping", new ArrayList<VariableMapping>());
        oc.writeSavableArrayList((ArrayList) outputMapping, "outputMapping", new ArrayList<VariableMapping>());
    }

    /**
     * jme seralization 
     *
     * @param im the importer
     * @throws IOException
     */
    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = (InputCapsule) im.getCapsule(this);
        name = ic.readString("name", "");
        definition = (ShaderNodeDefinition) ic.readSavable("definition", null);
        condition = ic.readString("condition", null);
        inputMapping = (List<VariableMapping>) ic.readSavableArrayList("inputMapping", new ArrayList<VariableMapping>());
        outputMapping = (List<VariableMapping>) ic.readSavableArrayList("outputMapping", new ArrayList<VariableMapping>());
    }

    /**
     * convenience tostring
     *
     * @return a string
     */
    @Override
    public String toString() {
        return "\nShaderNode{" + "\nname=" + name + ", \ndefinition=" + definition.getName() + ", \ncondition=" + condition + ", \ninputMapping=" + inputMapping + ", \noutputMapping=" + outputMapping + '}';
    }
}
