
package com.clockwork.scene;

import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.renderer.Camera;
import com.clockwork.scene.control.CameraControl;
import com.clockwork.scene.control.CameraControl.ControlDirection;
import java.io.IOException;

/**
 * <code>CameraNode</code> simply uses {@link CameraControl} to implement
 * linking of camera and node data.
 *
 */
public class CameraNode extends Node {

    private CameraControl camControl;

    /**
     * Serialisation only. Do not use.
     */
    public CameraNode() {
    }

    public CameraNode(String name, Camera camera) {
        this(name, new CameraControl(camera));
    }

    public CameraNode(String name, CameraControl control) {
        super(name);
        addControl(control);
        camControl = control;
    }

    public void setEnabled(boolean enabled) {
        camControl.setEnabled(enabled);
    }

    public boolean isEnabled() {
        return camControl.isEnabled();
    }

    public void setControlDir(ControlDirection controlDir) {
        camControl.setControlDir(controlDir);
    }

    public void setCamera(Camera camera) {
        camControl.setCamera(camera);
    }

    public ControlDirection getControlDir() {
        return camControl.getControlDir();
    }

    public Camera getCamera() {
        return camControl.getCamera();
    }

//    @Override
//    public void lookAt(Vector3f position, Vector3f upVector) {
//        this.lookAt(position, upVector);
//        camControl.getCamera().lookAt(position, upVector);
//    }
    
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        camControl = (CameraControl)im.getCapsule(this).readSavable("camControl", null);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        ex.getCapsule(this).write(camControl, "camControl", null);
    }
}
