
package com.clockwork.input;

import com.clockwork.cursors.plugins.CWCursor;

/**
 * A specific API for interfacing with the mouse.
 */
public interface MouseInput extends Input {

    /**
     * Mouse X axis.
     */
    public static final int AXIS_X = 0;

    /**
     * Mouse Y axis.
     */
    public static final int AXIS_Y = 1;

    /**
     * Mouse wheel axis.
     */
    public static final int AXIS_WHEEL = 2;

    /**
     * Left mouse button.
     */
    public static final int BUTTON_LEFT   = 0;

    /**
     * Right mouse button.
     */
    public static final int BUTTON_RIGHT  = 1;

    /**
     * Middle mouse button.
     */
    public static final int BUTTON_MIDDLE = 2;

    /**
     * Set whether the mouse cursor should be visible or not.
     *
     * @param visible Whether the mouse cursor should be visible or not.
     */
    public void setCursorVisible(boolean visible);

    /**
     * Returns the number of buttons the mouse has. Typically 3 for most mice.
     *
     * @return the number of buttons the mouse has.
     */
    public int getButtonCount();

    /**
     * Sets the cursor to use.
     * @param cursor The cursor to use.
     */
    public void setNativeCursor(CWCursor cursor);
}
