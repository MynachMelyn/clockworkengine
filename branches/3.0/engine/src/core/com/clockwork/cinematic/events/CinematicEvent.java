
package com.clockwork.cinematic.events;

import com.clockwork.animation.LoopMode;
import com.clockwork.app.Application;
import com.clockwork.cinematic.Cinematic;
import com.clockwork.cinematic.PlayState;
import com.clockwork.export.Savable;

/**
 *
 */
public interface CinematicEvent extends Savable {

    /**
     * Starts the animation
     */
    public void play();

    /**
     * Stops the animation
     */
    public void stop();
    
    /**
     * this method can be implemented if the event needs different handling when 
     * stopped naturally (when the event reach its end)
     * or when it was forced stopped during playback
     * otherwise it just call regular stop()
     */
    public void forceStop();

    /**
     * Pauses the animation
     */
    public void pause();

    /**
     * Returns the actual duration of the animation
     * @return the duration
     */
    public float getDuration();

    /**
     * Sets the speed of the animation (1 is normal speed, 2 is twice faster)
     * @param speed
     */
    public void setSpeed(float speed);

    /**
     * returns the speed of the animation
     * @return the speed
     */
    public float getSpeed();

    /**
     * returns the PlayState of the animation
     * @return the plat state
     */
    public PlayState getPlayState();

    /**
     * @param loop Set the loop mode for the channel. The loop mode
     * determines what will happen to the animation once it finishes
     * playing.
     *
     * For more information, see the LoopMode enum class.
     * @see LoopMode
     */
    public void setLoopMode(LoopMode loop);

    /**
     * @return The loop mode currently set for the animation. The loop mode
     * determines what will happen to the animation once it finishes
     * playing.
     *
     * For more information, see the LoopMode enum class.
     * @see LoopMode
     */
    public LoopMode getLoopMode();

    /**
     * returns the initial duration of the animation at speed = 1 in seconds.
     * @return the initial duration
     */
    public float getInitialDuration();

    /**
     * Sets the duration of the antionamtion at speed = 1 in seconds
     * @param initialDuration
     */
    public void setInitialDuration(float initialDuration);

    /**
     * called internally in the update method, place here anything you want to run in the update loop
     * @param tpf time per frame
     */
    public void internalUpdate(float tpf);

    /**
     * initialize this event
     * @param app the application
     * @param cinematic the cinematic
     */
    public void initEvent(Application app, Cinematic cinematic);
    
    /**
     * When this method is invoked, the event should fast forward to the given time according tim 0 is the start of the event.
     * @param time the time to fast forward to
     */
    public void setTime(float time);    
   
    /**
     * returns the current time of the cinematic event
     * @return the time
     */
    public float getTime();
    
    /**
     * method called when an event is removed from a cinematic
     * this method should remove any reference to any external objects.
     */
    public void dispose();
    
}
