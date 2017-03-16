
package com.clockwork.niftygui;

import com.clockwork.asset.AssetManager;
import com.clockwork.audio.AudioNode;
import com.clockwork.audio.AudioSource.Status;
import com.clockwork.audio.AudioRenderer;
import de.lessvoid.nifty.spi.sound.SoundHandle;

public class SoundHandleCW implements SoundHandle {

    private AudioNode node;
    private AssetManager am;
    private String fileName;
    private float volume = 1;

    public SoundHandleCW(AudioRenderer ar, AudioNode node){
        if (ar == null || node == null) {
            throw new NullPointerException();
        }

        this.node = node;
    }

    /**
     * For streaming music only. (May need to loop..)
     * @param ar
     * @param am
     * @param fileName
     */
    public SoundHandleCW(AudioRenderer ar, AssetManager am, String fileName){
        if (ar == null || am == null) {
            throw new NullPointerException();
        }

        this.am = am;
        if (fileName == null) {
            throw new NullPointerException();
        }
        
        this.fileName = fileName;
    }

    public void play() {
        if (fileName != null){
            if (node != null){
                node.stop();
            }

            node = new AudioNode(am, fileName, true);
            node.setPositional(false);
            node.setVolume(volume);
            node.play();
        }else{
            node.playInstance();
        }
    }

    public void stop() {
        if (node != null){
            node.stop();
            // Do not nullify the node for non-streaming nodes!
            if (fileName != null) {
                // Causes play() to reload the stream on the next playback
                node = null;
            }
        }
    }

    public void setVolume(float f) {
        if (node != null) {
            node.setVolume(f);
        }
        volume = f;
    }

    public float getVolume() {
        return volume;
    }

    public boolean isPlaying() {
        return node != null && node.getStatus() == Status.Playing;
    }

    public void dispose() {
    }
}
