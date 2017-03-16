
package com.clockwork.input;

/**
 * A specific API for interfacing with smartphone touch devices
 */
public interface TouchInput extends Input {

    /**
     * No filter, get all events
     */
    public static final int ALL = 0x00;
    /**
     * Home key
     */
    public static final int KEYCODE_HOME = 0x03;
    /**
     * Escape key.
     */
    public static final int KEYCODE_BACK = 0x04;
    /**
     * Context Menu key.
     */
    public static final int KEYCODE_MENU = 0x52;
    /**
     * Search key.
     */
    public static final int KEYCODE_SEARCH = 0x54;
    /**
     * Volume up key.
     */
    public static final int KEYCODE_VOLUME_UP = 0x18;        
    /**
     * Volume down key.
     */
    public static final int KEYCODE_VOLUME_DOWN = 0x19;    

    
    /**
     * Set if mouse events should be generated
     * 
     * @param simulate if mouse events should be generated
     */
    public void setSimulateMouse(boolean simulate);
    
    /**
     * Get if mouse events are generated
     * @deprecated Use #isSimulateMouse() }.
     */
    @Deprecated
    public boolean getSimulateMouse();
    
    /**
     * @return true if mouse event simulation is enabled, false otherwise.
     */
    public boolean isSimulateMouse();

    /**
     * Set if keyboard events should be generated
     * 
     * @param simulate if keyboard events should be generated
     */
    public void setSimulateKeyboard(boolean simulate);
    
    /**
     * Set if historic android events should be transmitted, can be used to get better performance and less mem
     * see <a href="http://developer.android.com/reference/android/view/MotionEvent.html#getHistoricalX%28int,%20int%29">
     * http://developer.android.com/reference/android/view/MotionEvent.html#getHistoricalX%28int,%20int%29</a>
     * @param dontSendHistory turn of historic events if true, false else and default
     */
    public void setOmitHistoricEvents(boolean dontSendHistory);
    
}