
package com.clockwork.input.event;

import com.clockwork.input.InputManager;
import com.clockwork.input.Joystick;
import com.clockwork.input.JoystickAxis;

/**
 * Joystick axis event.
 * 
 */
public class JoyAxisEvent extends InputEvent {

    private JoystickAxis axis;
    private float value;

    public JoyAxisEvent(JoystickAxis axis, float value) {
        this.axis = axis;
        this.value = value;
    }

    /**
     * Returns the JoystickAxis that triggered this event.
     *
     * see JoystickAxis#assignAxis(java.lang.String, java.lang.String, int) 
     */
    public JoystickAxis getAxis() {
        return axis;
    }

    /**
     * Returns the joystick axis index.
     * 
     * @return joystick axis index.
     * 
     * see Joystick#assignAxis(java.lang.String, java.lang.String, int) 
     */
    public int getAxisIndex() {
        return axis.getAxisId();
    }

    /**
     * The joystick index.
     * 
     * @return joystick index.
     * 
     * see InputManager#getJoysticks() 
     */
    public int getJoyIndex() {
        return axis.getJoystick().getJoyId();
    }

    /**
     * The value of the axis.
     * 
     * @return value of the axis.
     */
    public float getValue() {
        return value;
    }
}
