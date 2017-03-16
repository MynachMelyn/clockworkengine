
package com.clockwork.light;

import com.clockwork.export.*;
import com.clockwork.math.ColorRGBA;
import com.clockwork.scene.Spatial;
import java.io.IOException;

/**
 * Abstract class for representing a light source.
 * 
 * All light source types have a color.
 */
public abstract class Light implements Savable, Cloneable {

    /**
     * Describes the light type.
     */
    public enum Type {

        /**
         * Directional light
         * 
         * see DirectionalLight
         */
        Directional(0),
        
        /**
         * Point light
         * 
         * see PointLight
         */
        Point(1),
        
        /**
         * Spot light.
         * 
         * Not supported by engine
         */
        Spot(2),
        
        /**
         * Ambient light
         * 
         * see AmbientLight
         */
        Ambient(3);

        private int typeId;

        Type(int type){
            this.typeId = type;
        }

        /**
         * Returns an index for the light type
         * @return an index for the light type
         */
        public int getId(){
            return typeId;
        }
    }

    protected ColorRGBA color = new ColorRGBA(1f,1f,1f,1f);
    
    /**
     * Used in LightList for caching the distance 
     * to the owner spatial. Should be reset after the sorting.
     */
    protected transient float lastDistance = -1;

    /**
     * If light is disabled, it will not have any 
     */
    protected boolean enabled = true;

    /** 
     * The light name. 
     */
    protected String name;

    /**
     * Returns the color of the light.
     * 
     * @return The color of the light.
     */
    public ColorRGBA getColor() {
        return color;
    }

    /**
     * This method sets the light name.
     * 
     * @param name the light name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the light name.
     * 
     * @return the light name
     */
    public String getName() {
        return name;
    }

    /*
    public void setLastDistance(float lastDistance){
        this.lastDistance = lastDistance;
    }

    public float getLastDistance(){
        return lastDistance;
    }
    */

    /**
     * Sets the light color.
     * 
     * @param color the light color.
     */
    public void setColor(ColorRGBA color){
        this.color.set(color);
    }

    
    /*
     * Returns true if the light is enabled
     * 
     * @return true if the light is enabled
     * 
     * see Light#setEnabled(boolean)
     */
    /*
    public boolean isEnabled() {
        return enabled;
    }
    */
    
    @Override
    public Light clone(){
        try {
            return (Light) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    public void write(CWExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(color, "color", null);
        oc.write(enabled, "enabled", true);
        oc.write(name, "name", null);
    }

    public void read(CWImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        color = (ColorRGBA) ic.readSavable("color", null);
        enabled = ic.readBoolean("enabled", true);
        name = ic.readString("name", null);
    }

    /**
     * Used internally to compute the last distance value.
     */
    protected abstract void computeLastDistance(Spatial owner);
    
    /**
     * Returns the light type
     * 
     * @return the light type
     * 
     * see Type
     */
    public abstract Type getType();

}
