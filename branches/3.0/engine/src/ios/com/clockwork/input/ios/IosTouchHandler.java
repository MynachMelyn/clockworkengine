

package com.clockwork.input.ios;

import com.clockwork.input.event.InputEvent;
import com.clockwork.input.event.MouseButtonEvent;
import com.clockwork.input.event.MouseMotionEvent;
import com.clockwork.input.event.TouchEvent;
import static com.clockwork.input.event.TouchEvent.Type.DOWN;
import static com.clockwork.input.event.TouchEvent.Type.MOVE;
import static com.clockwork.input.event.TouchEvent.Type.UP;
import com.clockwork.math.Vector2f;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AndroidTouchHandler is the base class that receives touch inputs from the 
 * Android system and creates the TouchEvents for jME.  This class is designed
 * to handle the base touch events for Android rev 9 (Android 2.3).  This is
 * extended by other classes to add features that were introducted after
 * Android rev 9.
 * 
 * 
 */
public class IosTouchHandler {
    private static final Logger logger = Logger.getLogger(IosTouchHandler.class.getName());
    
    final private HashMap<Integer, Vector2f> lastPositions = new HashMap<Integer, Vector2f>();

    protected int numPointers = 1;
    
    protected IosInputHandler iosInput;

    public IosTouchHandler(IosInputHandler iosInput) {
        this.iosInput = iosInput;
    }

    public void initialize() {
    }
    
    public void destroy() {
    }
    
    public void actionDown(int pointerId, long time, float x, float y) {
        logger.log(Level.FINE, "Inject input pointer: {0}, time: {1}, x: {2}, y: {3}", 
                new Object[]{pointerId, time, x, y});
        float jmeX = iosInput.getJmeX(x);
        float jmeY = iosInput.invertY(iosInput.getJmeY(y));
        TouchEvent touch = iosInput.getFreeTouchEvent();
        touch.set(TouchEvent.Type.DOWN, jmeX, jmeY, 0, 0);
        touch.setPointerId(pointerId);//TODO: pointer ID
        touch.setTime(time);
        touch.setPressure(1.0f);
        //touch.setPressure(event.getPressure(pointerIndex)); //TODO: preassure

        lastPositions.put(pointerId, new Vector2f(jmeX, jmeY));

        processEvent(touch);
    }
    
    public void actionUp(int pointerId, long time, float x, float y) {
        float jmeX = iosInput.getJmeX(x);
        float jmeY = iosInput.invertY(iosInput.getJmeY(y));
        TouchEvent touch = iosInput.getFreeTouchEvent();
        touch.set(TouchEvent.Type.UP, jmeX, jmeY, 0, 0);
        touch.setPointerId(pointerId);//TODO: pointer ID
        touch.setTime(time);
        touch.setPressure(1.0f);
        //touch.setPressure(event.getPressure(pointerIndex)); //TODO: preassure
        lastPositions.remove(pointerId);

        processEvent(touch);
    }
    
    public void actionMove(int pointerId, long time, float x, float y) {
        float jmeX = iosInput.getJmeX(x);
        float jmeY = iosInput.invertY(iosInput.getJmeY(y));
        Vector2f lastPos = lastPositions.get(pointerId);
        if (lastPos == null) {
            lastPos = new Vector2f(jmeX, jmeY);
            lastPositions.put(pointerId, lastPos);
        }

        float dX = jmeX - lastPos.x;
        float dY = jmeY - lastPos.y;
        if (dX != 0 || dY != 0) {
            TouchEvent touch = iosInput.getFreeTouchEvent();
            touch.set(TouchEvent.Type.MOVE, jmeX, jmeY, dX, dY);
            touch.setPointerId(pointerId);
            touch.setTime(time);
            touch.setPressure(1.0f);
            //touch.setPressure(event.getPressure(p));
            lastPos.set(jmeX, jmeY);

            processEvent(touch);
        }
    }

    protected void processEvent(TouchEvent event) {
        // Add the touch event
        iosInput.addEvent(event);
        // MouseEvents do not support multi-touch, so only evaluate 1 finger pointer events
        if (iosInput.isSimulateMouse() && numPointers == 1) {
            InputEvent mouseEvent = generateMouseEvent(event);
            if (mouseEvent != null) {
                // Add the mouse event
                iosInput.addEvent(mouseEvent);
            }
        }
        
    }

    // TODO: Ring Buffer for mouse events?
    protected InputEvent generateMouseEvent(TouchEvent event) {
        InputEvent inputEvent = null;
        int newX;
        int newY;
        int newDX;
        int newDY;

        if (iosInput.isMouseEventsInvertX()) {
            newX = (int) (iosInput.invertX(event.getX()));
            newDX = (int)event.getDeltaX() * -1;
        } else {
            newX = (int) event.getX();
            newDX = (int)event.getDeltaX();
        }

        if (iosInput.isMouseEventsInvertY()) {
            newY = (int) (iosInput.invertY(event.getY()));
            newDY = (int)event.getDeltaY() * -1;
        } else {
            newY = (int) event.getY();
            newDY = (int)event.getDeltaY();
        }

        switch (event.getType()) {
            case DOWN:
                // Handle mouse down event
                inputEvent = new MouseButtonEvent(0, true, newX, newY);
                inputEvent.setTime(event.getTime());
                break;

            case UP:
                // Handle mouse up event
                inputEvent = new MouseButtonEvent(0, false, newX, newY);
                inputEvent.setTime(event.getTime());
                break;

//            case HOVER_MOVE:
            case MOVE:
                inputEvent = new MouseMotionEvent(newX, newY, newDX, newDY, (int)event.getScaleSpan(), (int)event.getDeltaScaleSpan());
                inputEvent.setTime(event.getTime());
                break;
        }

        return inputEvent;
    }
    
}
