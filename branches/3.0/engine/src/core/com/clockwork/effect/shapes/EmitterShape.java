
package com.clockwork.effect.shapes;

import com.clockwork.export.Savable;
import com.clockwork.math.Vector3f;

/**
 * This interface declares methods used by all shapes that represent particle emitters.
 */
public interface EmitterShape extends Savable, Cloneable {

    /**
     * This method fills in the initial position of the particle.
     * @param store
     *        store variable for initial position
     */
    public void getRandomPoint(Vector3f store);

    /**
     * This method fills in the initial position of the particle and its normal vector.
     * @param store
     *        store variable for initial position
     * @param normal
     *        store variable for initial normal
     */
    public void getRandomPointAndNormal(Vector3f store, Vector3f normal);

    /**
     * This method creates a deep clone of the current instance of the emitter shape.
     * @return deep clone of the current instance of the emitter shape
     */
    public EmitterShape deepClone();
}
