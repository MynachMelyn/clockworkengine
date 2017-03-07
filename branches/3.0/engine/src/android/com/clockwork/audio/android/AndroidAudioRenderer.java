package com.clockwork.audio.android;

import com.clockwork.audio.AudioRenderer;

/**
 * Android specific AudioRenderer interface that supports pausing and resuming
 * audio files when the app is minimized or placed in the background
 *
 * 
 */
public interface AndroidAudioRenderer extends AudioRenderer {

    /**
     * Pauses all Playing audio. To be used when the app is placed in the
     * background.
     */
    public void pauseAll();

    /**
     * Resumes all Paused audio. To be used when the app is brought back to
     * the foreground.
     */
    public void resumeAll();
}
