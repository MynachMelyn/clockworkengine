
package com.clockwork.light;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Spatial;
import java.io.IOException;

/**
 * DirectionalLight is a light coming from a certain direction in world space. 
 * E.g sun or moon light.
 * 
 * Directional lights have no specific position in the scene, they always 
 * come from their direction regardless of where an object is placed.
 */
public class DirectionalLight extends Light {

    protected Vector3f direction = new Vector3f(0f, -1f, 0f);

    @Override
    public void computeLastDistance(Spatial owner) {
        lastDistance = 0; // directional lights are always closest to their owner
    }

    /**
     * Returns the direction vector of the light.
     * 
     * @return The direction vector of the light.
     * 
     * see DirectionalLight#setDirection(com.clockwork.math.Vector3f) 
     */
    public Vector3f getDirection() {
        return direction;
    }

    /**
     * Sets the direction of the light.
     * 
     * Represents the direction the light is shining.
     * (1, 0, 0) would represent light shining in the +X direction.
     * 
     * @param dir the direction of the light.
     */
    public void setDirection(Vector3f dir){
        direction.set(dir);
        if (!direction.isUnitVector()) {
            direction.normalizeLocal();
        }
    }

    @Override
    public Type getType() {
        return Type.Directional;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(direction, "direction", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        direction = (Vector3f) ic.readSavable("direction", null);
    }

}
