
package com.clockwork.scene.control;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.light.DirectionalLight;
import com.clockwork.light.Light;
import com.clockwork.light.PointLight;
import com.clockwork.light.SpotLight;
import com.clockwork.math.Vector3f;
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
public class LightControl extends AbstractControl {

    public static enum ControlDirection {

        /**
         * Means, that the Light's transform is "copied"
         * to the Transform of the Spatial.
         */
        LightToSpatial,
        /**
         * Means, that the Spatial's transform is "copied"
         * to the Transform of the light.
         */
        SpatialToLight;
    }
    private Light light;
    private ControlDirection controlDir = ControlDirection.SpatialToLight;

    /**
     * Constructor used for Serialization.
     */
    public LightControl() {
    }

    /**
     * @param light The light to be synced.
     */
    public LightControl(Light light) {
        this.light = light;
    }

    /**
     * @param light The light to be synced.
     */
    public LightControl(Light light, ControlDirection controlDir) {
        this.light = light;
        this.controlDir = controlDir;
    }

    public Light getLight() {
        return light;
    }

    public void setLight(Light light) {
        this.light = light;
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
        if (spatial != null && light != null) {
            switch (controlDir) {
                case SpatialToLight:
                    spatialTolight(light);
                    break;
                case LightToSpatial:
                    lightToSpatial(light);
                    break;
            }
        }
    }

    private void spatialTolight(Light light) {
        if (light instanceof PointLight) {
            ((PointLight) light).setPosition(spatial.getWorldTranslation());
        }
        TempVars vars = TempVars.get();

        if (light instanceof DirectionalLight) {
            ((DirectionalLight) light).setDirection(vars.vect1.set(spatial.getWorldTranslation()).multLocal(-1.0f));
        }

        if (light instanceof SpotLight) {
            ((SpotLight) light).setPosition(spatial.getWorldTranslation());            
            ((SpotLight) light).setDirection(spatial.getWorldRotation().multLocal(vars.vect1.set(Vector3f.UNIT_Y).multLocal(-1)));
        }
        vars.release();

    }

    private void lightToSpatial(Light light) {
        TempVars vars = TempVars.get();
        if (light instanceof PointLight) {

            PointLight pLight = (PointLight) light;

            Vector3f vecDiff = vars.vect1.set(pLight.getPosition()).subtractLocal(spatial.getWorldTranslation());
            spatial.setLocalTranslation(vecDiff.addLocal(spatial.getLocalTranslation()));
        }

        if (light instanceof DirectionalLight) {
            DirectionalLight dLight = (DirectionalLight) light;
            vars.vect1.set(dLight.getDirection()).multLocal(-1.0f);
            Vector3f vecDiff = vars.vect1.subtractLocal(spatial.getWorldTranslation());
            spatial.setLocalTranslation(vecDiff.addLocal(spatial.getLocalTranslation()));
        }
        vars.release();
        //TODO add code for Spot light here when it's done


    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // nothing to do
    }

    @Override
    public Control cloneForSpatial(Spatial newSpatial) {
        LightControl control = new LightControl(light, controlDir);
        control.setSpatial(newSpatial);
        control.setEnabled(isEnabled());
        return control;
    }
    private static final String CONTROL_DIR_NAME = "controlDir";
    private static final String LIGHT_NAME = "light";
    
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        controlDir = ic.readEnum(CONTROL_DIR_NAME, ControlDirection.class, ControlDirection.SpatialToLight);
        light = (Light)ic.readSavable(LIGHT_NAME, null);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(controlDir, CONTROL_DIR_NAME, ControlDirection.SpatialToLight);
        oc.write(light, LIGHT_NAME, null);
    }
}