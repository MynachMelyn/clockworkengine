
package com.clockwork.shader;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.export.Savable;
import java.io.IOException;

/**
 * represents a mapping between 2 ShaderNodeVariables
 *
 * 
 */
public class VariableMapping implements Savable {

    private ShaderNodeVariable leftVariable;
    private ShaderNodeVariable rightVariable;
    private String condition;
    private String leftSwizzling = "";
    private String rightSwizzling = "";

    /**
     * creates a VariableMapping
     */
    public VariableMapping() {
    }

    /**
     * creates a VariableMapping
     *
     * @param leftVariable the left hand side variable of the expression
     * @param leftSwizzling the swizzling of the left variable
     * @param rightVariable the right hand side variable of the expression
     * @param rightSwizzling the swizzling of the right variable
     * @param condition the condition for this mapping
     */
    public VariableMapping(ShaderNodeVariable leftVariable, String leftSwizzling, ShaderNodeVariable rightVariable, String rightSwizzling, String condition) {
        this.leftVariable = leftVariable;
        this.rightVariable = rightVariable;
        this.condition = condition;
        this.leftSwizzling = leftSwizzling;
        this.rightSwizzling = rightSwizzling;
    }

    /**
     *
     * @return the left variable
     */
    public ShaderNodeVariable getLeftVariable() {
        return leftVariable;
    }

    /**
     * sets the left variable
     *
     * @param leftVariable the left variable
     */
    public void setLeftVariable(ShaderNodeVariable leftVariable) {
        this.leftVariable = leftVariable;
    }

    /**
     *
     * @return the right variable
     */
    public ShaderNodeVariable getRightVariable() {
        return rightVariable;
    }

    /**
     * sets the right variable
     *
     * @param leftVariable the right variable
     */
    public void setRightVariable(ShaderNodeVariable rightVariable) {
        this.rightVariable = rightVariable;
    }

    /**
     *
     * @return the condition
     */
    public String getCondition() {
        return condition;
    }

    /**
     * sets the condition
     *
     * @param condition the condition
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     *
     * @return the left swizzle
     */
    public String getLeftSwizzling() {
        return leftSwizzling;
    }

    /**
     * sets the left swizzle
     *
     * @param leftSwizzling the left swizzle
     */
    public void setLeftSwizzling(String leftSwizzling) {
        this.leftSwizzling = leftSwizzling;
    }

    /**
     *
     * @return the right swizzle
     */
    public String getRightSwizzling() {
        return rightSwizzling;
    }

    /**
     * sets the right swizzle
     *
     * @param leftSwizzling the right swizzle
     */
    public void setRightSwizzling(String rightSwizzling) {
        this.rightSwizzling = rightSwizzling;
    }

    /**
     * jme seralization (not used)
     *
     * @param ex the exporter
     * @throws IOException
     */
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = (OutputCapsule) ex.getCapsule(this);
        oc.write(leftVariable, "leftVariable", null);
        oc.write(rightVariable, "rightVariable", null);
        oc.write(condition, "condition", "");
        oc.write(leftSwizzling, "leftSwizzling", "");
        oc.write(rightSwizzling, "rightSwizzling", "");
    }

    /**
     * jme seralization (not used)
     *
     * @param im the importer
     * @throws IOException
     */
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = (InputCapsule) im.getCapsule(this);
        leftVariable = (ShaderNodeVariable) ic.readSavable("leftVariable", null);
        rightVariable = (ShaderNodeVariable) ic.readSavable("rightVariable", null);
        condition = ic.readString("condition", "");
        leftSwizzling = ic.readString("leftSwizzling", "");
        rightSwizzling = ic.readString("rightSwizzling", "");
    }

    @Override
    public String toString() {
        return "\n{" + leftVariable.toString() + (leftSwizzling.length() > 0 ? ("." + leftSwizzling) : "") + " = " + rightVariable.getType() + " " + rightVariable.getNameSpace() + "." + rightVariable.getName() + (rightSwizzling.length() > 0 ? ("." + rightSwizzling) : "") + " : " + condition + "}";
    }
}
