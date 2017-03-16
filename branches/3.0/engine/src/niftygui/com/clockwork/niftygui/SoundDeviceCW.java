
package com.clockwork.niftygui;

import com.clockwork.asset.AssetManager;
import com.clockwork.audio.AudioNode;
import com.clockwork.audio.AudioRenderer;
import de.lessvoid.nifty.sound.SoundSystem;
import de.lessvoid.nifty.spi.sound.SoundDevice;
import de.lessvoid.nifty.spi.sound.SoundHandle;
import de.lessvoid.nifty.tools.resourceloader.NiftyResourceLoader;

public class SoundDeviceCW implements SoundDevice {

    protected AssetManager assetManager;
    protected AudioRenderer ar;

    public SoundDeviceCW(AssetManager assetManager, AudioRenderer ar){
        this.assetManager = assetManager;
        this.ar = ar;
    }

    public void setResourceLoader(NiftyResourceLoader niftyResourceLoader) {
    }

    public SoundHandle loadSound(SoundSystem soundSystem, String filename) {
        AudioNode an = new AudioNode(assetManager, filename, false);
        an.setPositional(false);
        return new SoundHandleCW(ar, an);
    }

    public SoundHandle loadMusic(SoundSystem soundSystem, String filename) {
        return new SoundHandleCW(ar, assetManager, filename);
    }

    public void update(int delta) {
    }
    
}
