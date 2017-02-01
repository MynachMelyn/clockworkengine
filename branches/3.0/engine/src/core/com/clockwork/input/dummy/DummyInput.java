
package com.clockwork.input.dummy;

import com.clockwork.input.Input;
import com.clockwork.input.RawInputListener;

/**
 * DummyInput as an implementation of <code>Input</code> that raises no
 * input events.
 * 
 */
public class DummyInput implements Input {

    protected boolean inited = false;

    public void initialize() {
        if (inited)
            throw new IllegalStateException("Input already initialized.");

        inited = true;
    }

    public void update() {
        if (!inited)
            throw new IllegalStateException("Input not initialized.");
    }

    public void destroy() {
        if (!inited)
            throw new IllegalStateException("Input not initialized.");

        inited = false;
    }

    public boolean isInitialized() {
        return inited;
    }

    public void setInputListener(RawInputListener listener) {
    }

    public long getInputTimeNanos() {
        return System.currentTimeMillis() * 1000000;
    }

}
