
package com.clockwork.audio;

/**
 *  Holds render thread specific audio context information.
 *
 */
public class AudioContext {

    private static ThreadLocal<AudioRenderer> audioRenderer = new ThreadLocal<AudioRenderer>();
 
    public static void setAudioRenderer( AudioRenderer ar ) {
        audioRenderer.set(ar);       
    }
    
    public static AudioRenderer getAudioRenderer() {
        return audioRenderer.get();
    }
}
