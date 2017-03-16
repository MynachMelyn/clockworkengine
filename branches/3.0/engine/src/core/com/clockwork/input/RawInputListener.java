
package com.clockwork.input;

import com.clockwork.input.event.*;

/**
 * An interface used for receiving raw input from devices.
 */
public interface RawInputListener {

    /**
     * Called before a batch of input will be sent to this
     * RawInputListener.
     */
    public void beginInput();

    /**
     * Called after a batch of input was sent to this
     * RawInputListener.
     *
     * The listener should set the InputEvent#setConsumed() consumed flag}
     * on any events that have been consumed either at this call or previous calls.
     */
    public void endInput();

    /**
     * Invoked on joystick axis events.
     *
     * @param evt
     */
    public void onJoyAxisEvent(JoyAxisEvent evt);

    /**
     * Invoked on joystick button presses.
     *
     * @param evt
     */
    public void onJoyButtonEvent(JoyButtonEvent evt);

    /**
     * Invoked on mouse movement/motion events.
     *
     * @param evt
     */
    public void onMouseMotionEvent(MouseMotionEvent evt);

    /**
     * Invoked on mouse button events.
     *
     * @param evt
     */
    public void onMouseButtonEvent(MouseButtonEvent evt);

    /**
     * Invoked on keyboard key press or release events.
     *
     * @param evt
     */
    public void onKeyEvent(KeyInputEvent evt);


    /**
     * Invoked on touchscreen touch events.
     *
     * @param evt
     */
    public void onTouchEvent(TouchEvent evt);

}
