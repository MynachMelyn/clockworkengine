
package com.clockwork.input.controls;

import com.clockwork.input.event.TouchEvent;

/**
 * <code>TouchListener</code> is used to receive events of inputs from smartphone touch devices 
 *
 */
public interface TouchListener extends InputListener {
    /**
     * @param name the name of the event
     * @param event the touch event
     * @param tpf how much time has passed since the last frame
     */
    public void onTouch(String name, TouchEvent event, float tpf);
}