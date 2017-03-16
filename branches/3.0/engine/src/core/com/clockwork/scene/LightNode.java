
package com.clockwork.scene;

import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.light.Light;
import com.clockwork.scene.control.LightControl;
import com.clockwork.scene.control.LightControl.ControlDirection;
import java.io.IOException;

/**
 * LightNode is used to link together a Light} object
 * with a Node} object. 
 *
 */
public class LightNode extends Node {

    private LightControl lightControl;

    /**
     * Serialisation only. Do not use.
     */
    public LightNode() {
    }

    public LightNode(String name, Light light) {
        this(name, new LightControl(light));
    }

    public LightNode(String name, LightControl control) {
        super(name);
        addControl(control);
        lightControl = control;
    }

    /**
     * Enable or disable the LightNode functionality.
     * 
     * @param enabled If false, the functionality of LightNode will
     * be disabled.
     */
    public void setEnabled(boolean enabled) {
        lightControl.setEnabled(enabled);
    }

    public boolean isEnabled() {
        return lightControl.isEnabled();
    }

    public void setControlDir(ControlDirection controlDir) {
        lightControl.setControlDir(controlDir);
    }

    public void setLight(Light light) {
        lightControl.setLight(light);
    }

    public ControlDirection getControlDir() {
        return lightControl.getControlDir();
    }

    public Light getLight() {
        return lightControl.getLight();
    }
    
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        lightControl = (LightControl)im.getCapsule(this).readSavable("lightControl", null);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        ex.getCapsule(this).write(lightControl, "lightControl", null);
    }
}
