
package com.clockwork.audio;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.util.NativeObject;
import java.io.IOException;

public class LowPassFilter extends Filter {

    protected float volume, highFreqVolume;

    public LowPassFilter(float volume, float highFreqVolume) {
        super();
        setVolume(volume);
        setHighFreqVolume(highFreqVolume);
    }
    
    protected LowPassFilter(int id){
        super(id);
    }

    public float getHighFreqVolume() {
        return highFreqVolume;
    }

    public void setHighFreqVolume(float highFreqVolume) {
        if (highFreqVolume < 0 || highFreqVolume > 1)
            throw new IllegalArgumentException("High freq volume must be between 0 and 1");

        this.highFreqVolume = highFreqVolume;
        this.updateNeeded = true;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        if (volume < 0 || volume > 1)
            throw new IllegalArgumentException("Volume must be between 0 and 1");
        
        this.volume = volume;
        this.updateNeeded = true;
    }

    public void write(JmeExporter ex) throws IOException{
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(volume, "volume", 0);
        oc.write(highFreqVolume, "hf_volume", 0);
    }

    @Override
    public void read(JmeImporter im) throws IOException{
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        volume = ic.readFloat("volume", 0);
        highFreqVolume = ic.readFloat("hf_volume", 0);
    }

    @Override
    public NativeObject createDestructableClone() {
        return new LowPassFilter(id);
    }

    @Override
    public long getUniqueId() {
        return ((long)OBJTYPE_FILTER << 32) | ((long)id);
    }
}
