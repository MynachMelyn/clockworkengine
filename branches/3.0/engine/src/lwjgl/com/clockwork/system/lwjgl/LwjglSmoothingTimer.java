

package com.clockwork.system.lwjgl;

import com.clockwork.math.FastMath;
import com.clockwork.system.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.Sys;

/**
 * Timer handles the system's time related functionality. This
 * allows the calculation of the framerate. To keep the framerate calculation
 * accurate, a call to update each frame is required. Timer is a
 * singleton object and must be created via the getTimer method.
 *
 * @version $Id: LWJGLTimer.java,v 1.21 2007/09/22 16:46:35 irrisor Exp $
 */
public class LwjglSmoothingTimer extends Timer {
    private static final Logger logger = Logger.getLogger(LwjglSmoothingTimer.class
            .getName());

    private long lastFrameDiff;

    //frame rate parameters.
    private long oldTime;

    private float lastTPF, lastFPS;

    public static int TIMER_SMOOTHNESS = 32;

    private long[] tpf;

    private int smoothIndex;

    private final static long LWJGL_TIMER_RES = Sys.getTimerResolution();
    private final static float INV_LWJGL_TIMER_RES = ( 1f / LWJGL_TIMER_RES );
    private static float invTimerRezSmooth;

    public final static long LWJGL_TIME_TO_NANOS = (1000000000 / LWJGL_TIMER_RES);

    private long startTime;

    private boolean allSmooth = false;

    /**
     * Constructor builds a Timer object. All values will be
     * initialized to it's default values.
     */
    public LwjglSmoothingTimer() {
        reset();

        //print timer resolution info
        logger.log(Level.FINE, "Timer resolution: {0} ticks per second", LWJGL_TIMER_RES);
    }

    public void reset() {
        lastFrameDiff = 0;
        lastFPS = 0;
        lastTPF = 0;

        // init to -1 to indicate this is a new timer.
        oldTime = -1;
        //reset time
        startTime = Sys.getTime();

        tpf = new long[TIMER_SMOOTHNESS];
        smoothIndex = TIMER_SMOOTHNESS - 1;
        invTimerRezSmooth = ( 1f / (LWJGL_TIMER_RES * TIMER_SMOOTHNESS));

        // set tpf... -1 values will not be used for calculating the average in update()
        for ( int i = tpf.length; --i >= 0; ) {
            tpf[i] = -1;
        }
    }

    /**
     * see Timer#getTime() 
     */
    public long getTime() {
        return Sys.getTime() - startTime;
    }

    /**
     * see Timer#getResolution() 
     */
    public long getResolution() {
        return LWJGL_TIMER_RES;
    }

    /**
     * getFrameRate returns the current frame rate since the last
     * call to update.
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
     * update recalulates the frame rate based on the previous
     * call to update. It is assumed that update is called each frame.
     */
    public void update() {
        long newTime = Sys.getTime();
        long oldTime = this.oldTime;
        this.oldTime = newTime;
        if ( oldTime == -1 ) {
            // For the first frame use 60 fps. This value will not be counted in further averages.
            // This is done so initialization code between creating the timer and the first
            // frame is not counted as a single frame on it's own.
            lastTPF = 1 / 60f;
            lastFPS = 1f / lastTPF;
            return;
        }

        long frameDiff = newTime - oldTime;
        long lastFrameDiff = this.lastFrameDiff;
        if ( lastFrameDiff > 0 && frameDiff > lastFrameDiff *100 ) {
            frameDiff = lastFrameDiff *100;
        }
        this.lastFrameDiff = frameDiff;
        tpf[smoothIndex] = frameDiff;
        smoothIndex--;
        if ( smoothIndex < 0 ) {
            smoothIndex = tpf.length - 1;
        }

        lastTPF = 0.0f;
        if (!allSmooth) {
            int smoothCount = 0;
            for ( int i = tpf.length; --i >= 0; ) {
                if ( tpf[i] != -1 ) {
                    lastTPF += tpf[i];
                    smoothCount++;
                }
            }
            if (smoothCount == tpf.length)
                allSmooth  = true;
            lastTPF *= ( INV_LWJGL_TIMER_RES / smoothCount );
        } else {
            for ( int i = tpf.length; --i >= 0; ) {
                if ( tpf[i] != -1 ) {
                    lastTPF += tpf[i];
                }
            }
            lastTPF *= invTimerRezSmooth;
        }
        if ( lastTPF < FastMath.FLT_EPSILON ) {
            lastTPF = FastMath.FLT_EPSILON;
        }

        lastFPS = 1f / lastTPF;
    }

    /**
     * toString returns the string representation of this timer
     * in the format: 
     * 
     * jme.utility.Timer@1db699b 
     * Time: {LONG} 
     * FPS: {LONG} 
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