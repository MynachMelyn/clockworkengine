

package com.clockwork.system.lwjgl;

import com.clockwork.system.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.Sys;

/**
 * <code>Timer</code> handles the system's time related functionality. This
 * allows the calculation of the framerate. To keep the framerate calculation
 * accurate, a call to update each frame is required. <code>Timer</code> is a
 * singleton object and must be created via the <code>getTimer</code> method.
 *
 * @version $Id: LWJGLTimer.java,v 1.21 2007/09/22 16:46:35 irrisor Exp $
 */
public class LwjglTimer extends Timer {
    private static final Logger logger = Logger.getLogger(LwjglTimer.class
            .getName());

    //frame rate parameters.
    private long oldTime;
    private long startTime;

    private float lastTPF, lastFPS;

    private final static long LWJGL_TIMER_RES = Sys.getTimerResolution();
    private final static float INV_LWJGL_TIMER_RES = ( 1f / LWJGL_TIMER_RES );

    public final static long LWJGL_TIME_TO_NANOS = (1000000000 / LWJGL_TIMER_RES);

    /**
     * Constructor builds a <code>Timer</code> object. All values will be
     * initialized to it's default values.
     */
    public LwjglTimer() {
        reset();
        logger.log(Level.FINE, "Timer resolution: {0} ticks per second", LWJGL_TIMER_RES);
    }

    public void reset() {
        startTime = Sys.getTime();
        oldTime = getTime();
    }

    @Override
    public float getTimeInSeconds() {
        return getTime() * INV_LWJGL_TIMER_RES;
    }

    /**
     * @see Timer#getTime() 
     */
    public long getTime() {
        return Sys.getTime() - startTime;
    }

    /**
     * @see Timer#getResolution() 
     */
    public long getResolution() {
        return LWJGL_TIMER_RES;
    }

    /**
     * <code>getFrameRate</code> returns the current frame rate since the last
     * call to <code>update</code>.
     *
     * @return the current frame rate.
     */
    public float getFrameRate() {
        return lastFPS;
    }

    public float getTimePerFrame() {
        return lastTPF;
    }

    /**
     * <code>update</code> recalulates the frame rate based on the previous
     * call to update. It is assumed that update is called each frame.
     */
    public void update() {
        long curTime = getTime();
        lastTPF = (curTime - oldTime) * (1.0f / LWJGL_TIMER_RES);
        lastFPS = 1.0f / lastTPF;
        oldTime = curTime;
    }

    /**
     * <code>toString</code> returns the string representation of this timer
     * in the format: <br>
     * <br>
     * jme.utility.Timer@1db699b <br>
     * Time: {LONG} <br>
     * FPS: {LONG} <br>
     *
     * @return the string representation of this object.
     */
    @Override
    public String toString() {
        String string = super.toString();
        string += "\nTime: " + oldTime;
        string += "\nFPS: " + getFrameRate();
        return string;
    }
}