
package com.clockwork.input.controls;

import com.clockwork.input.Joystick;

public class JoyAxisTrigger implements Trigger {

    private final int joyId, axisId;
    private final boolean negative;

    /**
     * Use Joystick#assignAxis(java.lang.String, java.lang.String, int) }
     * instead.
     */
    public JoyAxisTrigger(int joyId, int axisId, boolean negative) {
        this.joyId = joyId;
        this.axisId = axisId;
        this.negative = negative;
    }

    public static int joyAxisHash(int joyId, int joyAxis, boolean negative){
        assert joyAxis >= 0 && joyAxis <= 255;
        return (2048 * joyId) | (negative ? 1280 : 1024) | (joyAxis & 0xff);
    }

    public int getAxisId() {
        return axisId;
    }

    public int getJoyId() {
        return joyId;
    }

    public boolean isNegative() {
        return negative;
    }

    public String getName() {
        return "JoyAxis[joyId="+joyId+", axisId="+axisId+", neg="+negative+"]";
    }

    public int triggerHashCode() {
        return joyAxisHash(joyId, axisId, negative);
    }
    
}
