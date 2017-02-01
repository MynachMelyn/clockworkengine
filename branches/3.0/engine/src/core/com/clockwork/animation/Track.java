
package com.clockwork.animation;

import com.clockwork.export.Savable;
import com.clockwork.util.TempVars;

public interface Track extends Savable, Cloneable {

    /**
     * Sets the time of the animation.
     * 
     * Internally, the track will retrieve objects from the control
     * and modify them according to the properties of the channel and the
     * given parameters.
     * 
     * @param time The time in the animation
     * @param weight The weight from 0 to 1 on how much to apply the track 
     * @param control The control which the track should effect
     * @param channel The channel which the track should effect
     */
    public void setTime(float time, float weight, AnimControl control, AnimChannel channel, TempVars vars);

    /**
     * @return the length of the track
     */
    public float getLength();

    /**
     * This method creates a clone of the current object.
     * @return a clone of the current object
     */
    public Track clone();
}
