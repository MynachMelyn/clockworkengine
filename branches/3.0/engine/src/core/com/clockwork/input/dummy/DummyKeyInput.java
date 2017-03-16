
package com.clockwork.input.dummy;

import com.clockwork.input.KeyInput;

/**
 * DummyKeyInput as an implementation of KeyInput that raises no
 * input events.
 * 
 */
public class DummyKeyInput extends DummyInput implements KeyInput {

    public int getKeyCount() {
        if (!inited)
            throw new IllegalStateException("Input not initialized.");

        return 0;
    }

}
