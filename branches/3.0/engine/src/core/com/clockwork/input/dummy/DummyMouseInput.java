
package com.clockwork.input.dummy;

import com.clockwork.cursors.plugins.CWCursor;
import com.clockwork.input.MouseInput;

/**
 * DummyMouseInput as an implementation of MouseInput that raises no
 * input events.
 *
 */
public class DummyMouseInput extends DummyInput implements MouseInput {

    public void setCursorVisible(boolean visible) {
        if (!inited)
            throw new IllegalStateException("Input not initialized.");
    }

    public int getButtonCount() {
        return 0;
    }

    public void setNativeCursor(CWCursor cursor) {
    }

}
