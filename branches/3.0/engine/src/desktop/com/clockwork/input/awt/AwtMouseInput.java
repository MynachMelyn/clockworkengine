
package com.clockwork.input.awt;

import com.clockwork.cursors.plugins.JmeCursor;
import com.clockwork.input.MouseInput;
import com.clockwork.input.RawInputListener;
import com.clockwork.input.event.MouseButtonEvent;
import com.clockwork.input.event.MouseMotionEvent;
import com.clockwork.system.JmeSystem;
import com.clockwork.system.Platform;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * <code>AwtMouseInput</code>
 *
 *
 * @version $Revision$
 */
public class AwtMouseInput implements MouseInput, MouseListener, MouseWheelListener, MouseMotionListener {

    public static int WHEEL_AMP = 40;   // arbitrary...  Java's mouse wheel seems to report something a lot lower than lwjgl's

    private static final Logger logger = Logger.getLogger(AwtMouseInput.class.getName());

    private boolean visible = true;

    private RawInputListener listener;

    private Component component;

    private final ArrayList<MouseButtonEvent> eventQueue = new ArrayList<MouseButtonEvent>();
    private final ArrayList<MouseButtonEvent> eventQueueCopy = new ArrayList<MouseButtonEvent>();

    private int lastEventX;
    private int lastEventY;
    private int lastEventWheel;

    private Cursor transparentCursor;

    private Robot robot;
    private int wheelPos;
    private Point location;
    private Point centerLocation;
    private Point centerLocationOnScreen;
    private Point lastKnownLocation;
    private boolean isRecentering;
    private boolean cursorMoved;
    private int eventsSinceRecenter;

    public AwtMouseInput() {
        location = new Point();
        centerLocation = new Point();
        centerLocationOnScreen = new Point();
        lastKnownLocation = new Point();

        try {
            robot = new Robot();
        } catch (java.awt.AWTException e) {
            logger.log(Level.SEVERE, "Could not create a robot, so the mouse cannot be grabbed! ", e);
        }
    }

    public void setInputSource(Component comp) {
        if (component != null) {
            component.removeMouseListener(this);
            component.removeMouseMotionListener(this);
            component.removeMouseWheelListener(this);

            eventQueue.clear();

            wheelPos = 0;
            isRecentering = false;
            eventsSinceRecenter = 0;
            lastEventX = 0;
            lastEventY = 0;
            lastEventWheel = 0;
            location = new Point();
            centerLocation = new Point();
            centerLocationOnScreen = new Point();
            lastKnownLocation = new Point();
        }

        component = comp;
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
        component.addMouseWheelListener(this);
    }

    public void initialize() {
    }

    public void destroy() {
    }

    public boolean isInitialized() {
        return true;
    }

    public void setInputListener(RawInputListener listener) {
        this.listener = listener;
    }

    public long getInputTimeNanos() {
        return System.nanoTime();
    }

    public void setCursorVisible(boolean visible) {
//        if(JmeSystem.getPlatform() != Platform.MacOSX32 &&
//                JmeSystem.getPlatform() != Platform.MacOSX64 &&
//                JmeSystem.getPlatform() != Platform.MacOSX_PPC32 &&
//                JmeSystem.getPlatform() != Platform.MacOSX_PPC64){
        if (this.visible != visible) {
            lastKnownLocation.x = lastKnownLocation.y = 0;

            this.visible = visible;
            final boolean newVisible = visible;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    component.setCursor(newVisible ? null : getTransparentCursor());
                    if (!newVisible) {
                        recenterMouse(component);
                    }
                }
            });
//        }
        }
    }

    public void update() {
        if (cursorMoved) {
            int newX = location.x;
            int newY = location.y;
            int newWheel = wheelPos;

            // invert DY
            int actualX = lastKnownLocation.x;
            int actualY = component.getHeight() - lastKnownLocation.y;
            MouseMotionEvent evt = new MouseMotionEvent(actualX, actualY,
                                                        newX - lastEventX,
                                                        lastEventY - newY,
                                                        wheelPos, lastEventWheel - wheelPos);
            listener.onMouseMotionEvent(evt);

            lastEventX = newX;
            lastEventY = newY;
            lastEventWheel = newWheel;

            cursorMoved = false;
        }

        synchronized (eventQueue) {
            eventQueueCopy.clear();
            eventQueueCopy.addAll(eventQueue);
            eventQueue.clear();
        }

        int size = eventQueueCopy.size();
        for (int i = 0; i < size; i++) {
            listener.onMouseButtonEvent(eventQueueCopy.get(i));
        }
    }

    private Cursor getTransparentCursor() {
        if (transparentCursor == null) {
            BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            cursorImage.setRGB(0, 0, 0);
            transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "empty cursor");
        }
        return transparentCursor;
    }

//	public void setHardwareCursor(URL file, int xHotspot, int yHotspot) {
//	    //Create the image from the provided url
//	    java.awt.Image cursorImage = new ImageIcon( file ).getImage( );
//	    //Create a custom cursor with this image
//	    opaqueCursor = Toolkit.getDefaultToolkit().createCustomCursor( cursorImage , new Point( xHotspot , yHotspot ) , "custom cursor" );
//	    //Use this cursor
//	    setCursorVisible( isCursorVisible );
//	}


    public int getButtonCount() {
        return 3;
    }

    public void mouseClicked(MouseEvent awtEvt) {
//        MouseButtonEvent evt = new MouseButtonEvent(getJMEButtonIndex(arg0), false);
//        listener.onMouseButtonEvent(evt);
    }

    public void mousePressed(MouseEvent awtEvt) {
        MouseButtonEvent evt = new MouseButtonEvent(getJMEButtonIndex(awtEvt), true, awtEvt.getX(), awtEvt.getY());
        evt.setTime(awtEvt.getWhen());
        synchronized (eventQueue) {
            eventQueue.add(evt);
        }
    }

    public void mouseReleased(MouseEvent awtEvt) {
        MouseButtonEvent evt = new MouseButtonEvent(getJMEButtonIndex(awtEvt), false, awtEvt.getX(), awtEvt.getY());
        evt.setTime(awtEvt.getWhen());
        synchronized (eventQueue) {
            eventQueue.add(evt);
        }
    }

    public void mouseEntered(MouseEvent awtEvt) {
        if (!visible) {
            recenterMouse(awtEvt.getComponent());
        }
    }

    public void mouseExited(MouseEvent awtEvt) {
        if (!visible) {
            recenterMouse(awtEvt.getComponent());
        }
    }

    public void mouseWheelMoved(MouseWheelEvent awtEvt) {
        int dwheel = awtEvt.getUnitsToScroll();
        wheelPos += dwheel * WHEEL_AMP;
        cursorMoved = true;
    }

    public void mouseDragged(MouseEvent awtEvt) {
        mouseMoved(awtEvt);
    }

    public void mouseMoved(MouseEvent awtEvt) {
        if (isRecentering) {
            // MHenze (cylab) Fix Issue 35:
            // As long as the MouseInput is in recentering mode, nothing is done until the mouse is entered in the component
            // by the events generated by the robot. If this happens, the last known location is resetted.
            if ((centerLocation.x == awtEvt.getX() && centerLocation.y == awtEvt.getY()) || eventsSinceRecenter++ == 5) {
                lastKnownLocation.x = awtEvt.getX();
                lastKnownLocation.y = awtEvt.getY();
                isRecentering = false;
            }
        } else {
            // MHenze (cylab) Fix Issue 35:
            // Compute the delta and absolute coordinates and recenter the mouse if necessary
            int dx = awtEvt.getX() - lastKnownLocation.x;
            int dy = awtEvt.getY() - lastKnownLocation.y;
            location.x += dx;
            location.y += dy;
            if (!visible) {
                recenterMouse(awtEvt.getComponent());
            }
            lastKnownLocation.x = awtEvt.getX();
            lastKnownLocation.y = awtEvt.getY();

            cursorMoved = true;
        }
    }

    // MHenze (cylab) Fix Issue 35: A method to generate recenter the mouse to allow the InputSystem to "grab" the mouse
    private void recenterMouse(final Component component) {
        if (robot != null) {
            eventsSinceRecenter = 0;
            isRecentering = true;
            centerLocation.setLocation(component.getWidth() / 2, component.getHeight() / 2);
            centerLocationOnScreen.setLocation(centerLocation);
            SwingUtilities.convertPointToScreen(centerLocationOnScreen, component);
            robot.mouseMove(centerLocationOnScreen.x, centerLocationOnScreen.y);
        }
    }

    private int getJMEButtonIndex(MouseEvent awtEvt) {
        int index;
        switch (awtEvt.getButton()) {
            default:
            case MouseEvent.BUTTON1: //left
                index = MouseInput.BUTTON_LEFT;
                break;
            case MouseEvent.BUTTON2: //middle
                index = MouseInput.BUTTON_MIDDLE;
                break;
            case MouseEvent.BUTTON3: //right
                index = MouseInput.BUTTON_RIGHT;
                break;
        }
        return index;
    }

    public void setNativeCursor(JmeCursor cursor) {
    }
}
