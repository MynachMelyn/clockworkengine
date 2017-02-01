
package com.clockwork.cinematic.events;

import com.clockwork.animation.LoopMode;

/**
 * A sound track to be played in a cinematic.
 * @deprecated use SoundEvent instead
 */
@Deprecated
public class SoundTrack extends SoundEvent {

    /**
     * creates a sound track from the given resource path
     * @param path the path to an audi file (ie : "Sounds/mySound.wav")
     */    
    public SoundTrack(String path) {
        super(path);
    }

    /**
     * creates a sound track from the given resource path
     * @param path the path to an audi file (ie : "Sounds/mySound.wav")
     * @param stream true to make the audio data streamed
     */    
    public SoundTrack(String path, boolean stream) {
        super(path, stream);
    }

    public SoundTrack(String path, boolean stream, float initialDuration) {
        super(path, stream, initialDuration);
    }

    public SoundTrack(String path, boolean stream, LoopMode loopMode) {
        super(path, stream, loopMode);
    }

    public SoundTrack(String path, boolean stream, float initialDuration, LoopMode loopMode) {
        super(path, stream, initialDuration, loopMode);

    }

    public SoundTrack(String path, float initialDuration) {
        super(path, initialDuration);
    }

    public SoundTrack(String path, LoopMode loopMode) {
        super(path, loopMode);
    }

    public SoundTrack(String path, float initialDuration, LoopMode loopMode) {
        super(path, initialDuration, loopMode);
    }

    public SoundTrack() {
        super();
    }
}
