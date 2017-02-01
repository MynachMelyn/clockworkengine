
package com.clockwork.input.controls;

/**
 * Class to trigger TouchEvents, keycode can be TouchInput.ALL(=0) or TouchInput.KEYCODE_*
 *
 */
public class TouchTrigger implements Trigger {
    
    private final int keyCode;
    
    /**
     * Constructor
     * @param keyCode can be zero to get all events or TouchInput.KEYCODE_*
     */
    public TouchTrigger(int keyCode) {
        super();
        this.keyCode = keyCode;
    }
    
    @Override
    public String getName() {
        if (keyCode != 0)
            return "TouchInput";
        else
            return "TouchInput KeyCode " + keyCode;
    }
    
    public static int touchHash(int keyCode){
        assert keyCode >= 0 && keyCode <= 255;
        return 0xfedcba98 + keyCode;
    }

    public int triggerHashCode() {
        return touchHash(keyCode);
    }
    
    public int getKeyCode(){
        return keyCode;
    }
}
