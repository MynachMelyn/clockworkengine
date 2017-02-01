

package com.clockwork.input.lwjgl;

import com.clockwork.cursors.plugins.JmeCursor;
import com.clockwork.input.MouseInput;
import com.clockwork.input.RawInputListener;
import com.clockwork.input.event.MouseButtonEvent;
import com.clockwork.input.event.MouseMotionEvent;
import com.clockwork.system.lwjgl.LwjglAbstractDisplay;
import com.clockwork.system.lwjgl.LwjglTimer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

public class LwjglMouseInput implements MouseInput {

    private static final Logger logger = Logger.getLogger(LwjglMouseInput.class.getName());

    private LwjglAbstractDisplay context;

    private RawInputListener listener;

    private boolean supportHardwareCursor = false;
    private boolean cursorVisible = true;

    private int curX, curY, curWheel;

    public LwjglMouseInput(LwjglAbstractDisplay context){
        this.context = context;
    }

    public void initialize() {
        if (!context.isRenderable())
            return;

        try {
            Mouse.create();
            logger.fine("Mouse created.");
            supportHardwareCursor = (Cursor.getCapabilities() & Cursor.CURSOR_ONE_BIT_TRANSPARENCY) != 0;

            // Recall state that was set before initialization
            Mouse.setGrabbed(!cursorVisible);
        } catch (LWJGLException ex) {
            logger.log(Level.SEVERE, "Error while creating mouse", ex);
        }
    }

    public boolean isInitialized(){
        return Mouse.isCreated();
    }

    public int getButtonCount(){
        return Mouse.getButtonCount();
    }

    public void update() {
        if (!context.isRenderable())
            return;

        while (Mouse.next()){
            int btn = Mouse.getEventButton();

            int wheelDelta = Mouse.getEventDWheel();
            int xDelta = Mouse.getEventDX();
            int yDelta = Mouse.getEventDY();
            int x = Mouse.getX();
            int y = Mouse.getY();

            curWheel += wheelDelta;
            if (cursorVisible){
                xDelta = x - curX;
                yDelta = y - curY;
                curX = x;
                curY = y;
            }else{
                x = curX + xDelta;
                y = curY + yDelta;
                curX = x;
                curY = y;
            }

            if (xDelta != 0 || yDelta != 0 || wheelDelta != 0){
                MouseMotionEvent evt = new MouseMotionEvent(x, y, xDelta, yDelta, curWheel, wheelDelta);
                evt.setTime(Mouse.getEventNanoseconds());
                listener.onMouseMotionEvent(evt);
            }
            if (btn != -1){
                MouseButtonEvent evt = new MouseButtonEvent(btn,
                                                            Mouse.getEventButtonState(), x, y);
                evt.setTime(Mouse.getEventNanoseconds());
                listener.onMouseButtonEvent(evt);
            }
        }
    }

    public void destroy() {
        if (!context.isRenderable())
            return;

        Mouse.destroy();
        logger.fine("Mouse destroyed.");
    }

    public void setCursorVisible(boolean visible){
        cursorVisible = visible;
        if (!context.isRenderable())
            return;

        Mouse.setGrabbed(!visible);
    }

    public void setInputListener(RawInputListener listener) {
        this.listener = listener;
    }

    public long getInputTimeNanos() {
        return Sys.getTime() * LwjglTimer.LWJGL_TIME_TO_NANOS;
    }

    public void setNativeCursor(JmeCursor jmeCursor) {
        try {
            Cursor newCursor = null;
            if (jmeCursor != null) {
                 newCursor = new Cursor(
                        jmeCursor.getWidth(),
                        jmeCursor.getHeight(),
                        jmeCursor.getXHotSpot(),
                        jmeCursor.getYHotSpot(),
                        jmeCursor.getNumImages(),
                        jmeCursor.getImagesData(),
                        jmeCursor.getImagesDelay());
            }
            Mouse.setNativeCursor(newCursor);
        } catch (LWJGLException ex) {
            Logger.getLogger(LwjglMouseInput.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
