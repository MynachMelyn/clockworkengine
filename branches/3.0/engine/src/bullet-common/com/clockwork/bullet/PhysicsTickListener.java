
package com.clockwork.bullet;

/**
 * Implement this interface to be called from the physics thread on a physics update.
 */
public interface PhysicsTickListener {

    /**
     * Called before the physics is actually stepped, use to apply forces etc.
     * @param space the physics space
     * @param tpf the time per frame in seconds 
     */
    public void prePhysicsTick(PhysicsSpace space, float tpf);

    /**
     * Called after the physics has been stepped, use to check for forces etc.
     * @param space the physics space
     * @param tpf the time per frame in seconds
     */
    public void physicsTick(PhysicsSpace space, float tpf);

}
