
package com.clockwork.input.controls;

import com.clockwork.input.Joystick;

public class JoyButtonTrigger implements Trigger {

    private final int joyId, buttonId;

    /**
     * Use {@link Joystick#assignButton(java.lang.String, int) } instead.
     * 
     * @param joyId
     * @param axisId 
     */
    public JoyButtonTrigger(int joyId, int axisId) {
        this.joyId = joyId;
        this.buttonId = axisId;
    }

    public static int joyButtonHash(int joyId, int joyButton){
        assert joyButton >= 0 && joyButton <= 255;
        return (2048 * joyId) | 1536 | (joyButton & 0xff);
    }

    public int getAxisId() {
        return buttonId;
    }

    public int getJoyId() {
        return joyId;
    }

    public String getName() {
        return "JoyButton[joyId="+joyId+", axisId="+buttonId+"]";
    }

    public int triggerHashCode() {
        return joyButtonHash(joyId, buttonId);
    }

}
