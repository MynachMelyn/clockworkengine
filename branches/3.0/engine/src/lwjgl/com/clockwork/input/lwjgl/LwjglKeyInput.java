

package com.clockwork.input.lwjgl;

import com.clockwork.input.KeyInput;
import com.clockwork.input.RawInputListener;
import com.clockwork.input.event.KeyInputEvent;
import com.clockwork.system.lwjgl.LwjglAbstractDisplay;
import com.clockwork.system.lwjgl.LwjglTimer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;

public class LwjglKeyInput implements KeyInput {

    private static final Logger logger = Logger.getLogger(LwjglKeyInput.class.getName());

    private LwjglAbstractDisplay context;

    private RawInputListener listener;

    public LwjglKeyInput(LwjglAbstractDisplay context){
        this.context = context;
    }

    public void initialize() {
        if (!context.isRenderable())
            return;
        
        try {
            Keyboard.create();
            Keyboard.enableRepeatEvents(true);
            logger.fine("Keyboard created.");
        } catch (LWJGLException ex) {
            logger.log(Level.SEVERE, "Error while creating keyboard.", ex);
        }
    }

    public int getKeyCount(){
        return Keyboard.KEYBOARD_SIZE;
    }

    public void update() {
        if (!context.isRenderable())
            return;
        
        Keyboard.poll();
        while (Keyboard.next()){
            int keyCode = Keyboard.getEventKey();
            char keyChar = Keyboard.getEventCharacter();
            boolean pressed = Keyboard.getEventKeyState();
            boolean down = Keyboard.isRepeatEvent();
            long time = Keyboard.getEventNanoseconds();
            KeyInputEvent evt = new KeyInputEvent(keyCode, keyChar, pressed, down);
            evt.setTime(time);
            listener.onKeyEvent(evt);
        }
    }

    public void destroy() {
        if (!context.isRenderable())
            return;
        
        Keyboard.destroy();
        logger.fine("Keyboard destroyed.");
    }

    public boolean isInitialized() {
        return Keyboard.isCreated();
    }

    public void setInputListener(RawInputListener listener) {
        this.listener = listener;
    }

    public long getInputTimeNanos() {
        return Sys.getTime() * LwjglTimer.LWJGL_TIME_TO_NANOS;
    }

}
