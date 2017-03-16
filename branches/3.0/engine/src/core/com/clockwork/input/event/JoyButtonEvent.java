
package com.clockwork.input.event;

import com.clockwork.input.Joystick;
import com.clockwork.input.JoystickButton;

/**
 * Joystick button event.
 * 
 */
public class JoyButtonEvent extends InputEvent {

    private JoystickButton button;
    private boolean pressed;

    public JoyButtonEvent(JoystickButton button, boolean pressed) {
        this.button = button;
        this.pressed = pressed;
    }

    /**
     * Returns the JoystickButton that triggered this event.
     *
     * see JoystickAxis#assignAxis(java.lang.String, java.lang.String, int) 
     */
    public JoystickButton getButton() {
        return button;
    }

    /**
     * The button index.
     * 
     * @return button index.
     * 
     * see Joystick#assignButton(java.lang.String, int) 
     */
    public int getButtonIndex() {
        return button.getButtonId();
    }

    /**
     * The joystick index.
     * 
     * @return joystick index.
     * 
     * see com.clockwork.input.InputManager#getJoysticks() 
     */
    public int getJoyIndex() {
        return button.getJoystick().getJoyId();
    }

    /**
     * Returns true if the event was a button press,
     * returns false if the event was a button release.
     * 
     * @return true if the event was a button press,
     * false if the event was a button release.
     */
    public boolean isPressed() {
        return pressed;
    }



}
