
package com.clockwork.cinematic.events;

/**
 *
 */
public interface CinematicEventListener {

    public void onPlay(CinematicEvent cinematic);
    public void onPause(CinematicEvent cinematic);
    public void onStop(CinematicEvent cinematic);
}
