
package com.clockwork.light;

import com.clockwork.bounding.BoundingVolume;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Spatial;
import java.io.IOException;

/**
 * Represents a point light.
 * A point light emits light from a given position into all directions in space.
 * E.g a lamp or a bright effect. Point light positions are in world space.
 * 
 * In addition to a position, point lights also have a radius which 
 * can be used to attenuate the influence of the light depending on the 
 * distance between the light and the effected object.
 * 
 */
public class PointLight extends Light {

    protected Vector3f position = new Vector3f();
    protected float radius = 0;
    protected float invRadius = 0;

    @Override
    public void computeLastDistance(Spatial owner) {
        if (owner.getWorldBound() != null) {
            BoundingVolume bv = owner.getWorldBound();
            lastDistance = bv.distanceSquaredTo(position);
        } else {
            lastDistance = owner.getWorldTranslation().distanceSquared(position);
        }
    }

    /**
     * Returns the world space position of the light.
     * 
     * @return the world space position of the light.
     * 
     * see PointLight#setPosition(com.clockwork.math.Vector3f) 
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Set the world space position of the light.
     * 
     * @param position the world space position of the light.
     */
    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    /**
     * Returns the radius of the light influence. A radius of 0 means
     * the light has no attenuation.
     * 
     * @return the radius of the light
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Set the radius of the light influence.
     * 
     * Setting a non-zero radius indicates the light should use attenuation.
     * If a pixel's distance to this light's position
     * is greater than the light's radius, then the pixel will not be
     * effected by this light, if the distance is less than the radius, then
     * the magnitude of the influence is equal to distance / radius.
     * 
     * @param radius the radius of the light influence.
     * 
     * @throws IllegalArgumentException If radius is negative
     */
    public void setRadius(float radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Light radius cannot be negative");
        }
        this.radius = radius;
        if(radius!=0){
            this.invRadius = 1 / radius;
        }else{
            this.invRadius = 0;
        }
    }

    /**
     * for internal use only
     * @return the inverse of the radius
     */
    public float getInvRadius() {
        return invRadius;
    }

    @Override
    public Light.Type getType() {
        return Light.Type.Point;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(position, "position", null);
        oc.write(radius, "radius", 0f);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        position = (Vector3f) ic.readSavable("position", null);
        radius = ic.readFloat("radius", 0f);
        if(radius!=0){
            this.invRadius = 1 / radius;
        }else{
            this.invRadius = 0;
        }
    }
}
