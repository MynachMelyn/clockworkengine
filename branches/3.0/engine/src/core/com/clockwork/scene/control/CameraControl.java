
package com.clockwork.scene.control;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.Camera;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Spatial;
import com.clockwork.util.TempVars;
import java.io.IOException;

/**
 * This Control maintains a reference to a Camera,
 * which will be synched with the position (worldTranslation)
 * of the current spatial.
 */
public class CameraControl extends AbstractControl {

    public static enum ControlDirection {

        /**
         * Means, that the Camera's transform is "copied"
         * to the Transform of the Spatial.
         */
        CameraToSpatial,
        /**
         * Means, that the Spatial's transform is "copied"
         * to the Transform of the Camera.
         */
        SpatialToCamera;
    }
    private Camera camera;
    private ControlDirection controlDir = ControlDirection.SpatialToCamera;

    /**
     * Constructor used for Serialisation.
     */
    public CameraControl() {
    }

    /**
     * @param camera The Camera to be synced.
     */
    public CameraControl(Camera camera) {
        this.camera = camera;
    }

    /**
     * @param camera The Camera to be synced.
     */
    public CameraControl(Camera camera, ControlDirection controlDir) {
        this.camera = camera;
        this.controlDir = controlDir;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public ControlDirection getControlDir() {
        return controlDir;
    }

    public void setControlDir(ControlDirection controlDir) {
        this.controlDir = controlDir;
    }

    // fields used, when inversing ControlDirection:
    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null && camera != null) {
            switch (controlDir) {
                case SpatialToCamera:
                    camera.setLocation(spatial.getWorldTranslation());
                    camera.setRotation(spatial.getWorldRotation());
                    break;
                case CameraToSpatial:
                    // set the localtransform, so that the worldtransform would be equal to the camera's transform.
                    // Location:
                    TempVars vars = TempVars.get();

                    Vector3f vecDiff = vars.vect1.set(camera.getLocation()).subtractLocal(spatial.getWorldTranslation());
                    spatial.setLocalTranslation(vecDiff.addLocal(spatial.getLocalTranslation()));

                    // Rotation:
                    Quaternion worldDiff = vars.quat1.set(camera.getRotation()).subtractLocal(spatial.getWorldRotation());
                    spatial.setLocalRotation(worldDiff.addLocal(spatial.getLocalRotation()));
                    vars.release();
                    break;
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // nothing to do
    }

    @Override
    public Control cloneForSpatial(Spatial newSpatial) {
        CameraControl control = new CameraControl(camera, controlDir);
        control.setSpatial(newSpatial);
        control.setEnabled(isEnabled());
        return control;
    }
    private static final String CONTROL_DIR_NAME = "controlDir";
    private static final String CAMERA_NAME = "camera";
    
    @Override
    public void read(CWImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        controlDir = ic.readEnum(CONTROL_DIR_NAME, ControlDirection.class, ControlDirection.SpatialToCamera);
        camera = (Camera)ic.readSavable(CAMERA_NAME, null);
    }

    @Override
    public void write(CWExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(controlDir, CONTROL_DIR_NAME, ControlDirection.SpatialToCamera);
        oc.write(camera, CAMERA_NAME, null);
    }
}