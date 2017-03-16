
package com.clockwork.input.controls;

import com.clockwork.input.MouseInput;

/**
 * A MouseAxisTrigger is used as a mapping to mouse axis,
 * a mouse axis is movement along the X axis (left/right), Y axis (up/down)
 * and the mouse wheel (scroll up/down).
 *
 */
public class MouseAxisTrigger implements Trigger {

    private int mouseAxis;
    private boolean negative;

    /**
     * Create a new MouseAxisTrigger.
     * 
     * @param mouseAxis Mouse axis. See AXIS_*** constants in MouseInput}
     * @param negative True if listen to negative axis events, false if
     * listen to positive axis events.
     */
    public MouseAxisTrigger(int mouseAxis, boolean negative){
        if (mouseAxis < 0 || mouseAxis > 2)
            throw new IllegalArgumentException("Mouse Axis must be between 0 and 2");

        this.mouseAxis = mouseAxis;
        this.negative = negative;
    }

    public int getMouseAxis(){
        return mouseAxis;
    }

    public boolean isNegative() {
        return negative;
    }

    public String getName() {
        String sign = negative ? "Negative" : "Positive";
        switch (mouseAxis){
            case MouseInput.AXIS_X: return "Mouse X Axis " + sign;
            case MouseInput.AXIS_Y: return "Mouse Y Axis " + sign;
            case MouseInput.AXIS_WHEEL: return "Mouse Wheel " + sign;
            default: throw new AssertionError();
        }
    }

    public static int mouseAxisHash(int mouseAxis, boolean negative){
        assert mouseAxis >= 0 && mouseAxis <= 255;
        return (negative ? 768 : 512) | (mouseAxis & 0xff);
    }

    public int triggerHashCode() {
        return mouseAxisHash(mouseAxis, negative);
    }
}
