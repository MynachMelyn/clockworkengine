
package com.clockwork.bullet.control;

import com.clockwork.bullet.PhysicsSpace;
import com.clockwork.scene.control.Control;

/**
 *
 */
public interface PhysicsControl extends Control {

    /**
     * Only used internally, do not call.
     * @param space 
     */
    public void setPhysicsSpace(PhysicsSpace space);

    public PhysicsSpace getPhysicsSpace();

    /**
     * The physics object is removed from the physics space when the control
     * is disabled. When the control is enabled  again the physics object is
     * moved to the current location of the spatial and then added to the physics
     * space. This allows disabling/enabling physics to move the spatial freely.
     * @param state
     */
    public void setEnabled(boolean state);
}
