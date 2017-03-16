
package com.clockwork.input.controls;

import com.clockwork.input.KeyInput;

/**
 * A KeyTrigger is used as a mapping to keyboard keys.
 *
 */
public class KeyTrigger implements Trigger {

    private final int keyCode;

    /**
     * Create a new KeyTrigger for the given keycode.
     * 
     * @param keyCode the code for the key, see constants in KeyInput}.
     */
    public KeyTrigger(int keyCode){
        this.keyCode = keyCode;
    }

    public String getName() {
        return "KeyCode " + keyCode;
    }

    public int getKeyCode(){
        return keyCode;
    }

    public static int keyHash(int keyCode){
        assert keyCode >= 0 && keyCode <= 255;
        return keyCode & 0xff;
    }

    public int triggerHashCode() {
        return keyHash(keyCode);
    }

}
