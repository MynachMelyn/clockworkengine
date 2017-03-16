
package com.clockwork.input;

/**
 * A specific API for interfacing with joysticks or gaming controllers.
 */
public interface JoyInput extends Input {

    /**
     * The X axis for POV (point of view hat).
     */
    public static final int AXIS_POV_X = 254;
    
    /**
     * The Y axis for POV (point of view hat).
     */
    public static final int AXIS_POV_Y = 255;

    /**
     * Causes the joystick at joyId index to rumble with
     * the given amount.
     * 
     * @param joyId The joystick index
     * @param amount Rumble amount. Should be between 0 and 1.
     */
    public void setJoyRumble(int joyId, float amount);
    
    /**
     * Loads a list of joysticks from the system.
     * 
     * @param inputManager The input manager requesting to load joysticks
     * @return A list of joysticks that are installed.
     */
    public Joystick[] loadJoysticks(InputManager inputManager);
}
