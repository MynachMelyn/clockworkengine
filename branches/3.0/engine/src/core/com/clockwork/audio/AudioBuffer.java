
package com.clockwork.audio;

import com.clockwork.audio.AudioData.DataType;
import com.clockwork.util.BufferUtils;
import com.clockwork.util.NativeObject;
import java.nio.ByteBuffer;

/**
 * An AudioBuffer is an implementation of AudioData
 * where the audio is buffered (stored in memory). All parts of it
 * are accessible at any time. 
 * AudioBuffers are useful for short sounds, like effects, etc.
 *
 */
public class AudioBuffer extends AudioData {

    /**
     * The audio data buffer. Should be direct and native ordered.
     */
    protected ByteBuffer audioData;

    public AudioBuffer(){
        super();
    }
    
    protected AudioBuffer(int id){
        super(id);
    }

    public DataType getDataType() {
        return DataType.Buffer;
    }

    /**
     * @return The duration of the audio in seconds. It is expected
     * that audio is uncompressed.
     */
    public float getDuration(){
        int bytesPerSec = (bitsPerSample / 8) * channels * sampleRate;
        if (audioData != null)
            return (float) audioData.limit() / bytesPerSec;
        else
            return Float.NaN; // unknown
    }

    @Override
    public String toString(){
        return getClass().getSimpleName() +
               "[id="+id+", ch="+channels+", bits="+bitsPerSample +
               ", rate="+sampleRate+", duration="+getDuration()+"]";
    }

    /**
     * Update the data in the buffer with new data.
     * @param data
     */
    public void updateData(ByteBuffer data){
        this.audioData = data;
        updateNeeded = true;
    }

    /**
     * @return The buffered audio data.
     */
    public ByteBuffer getData(){
        return audioData;
    }

    public void resetObject() {
        id = -1;
        setUpdateNeeded();
    }

    @Override
    protected void deleteNativeBuffers() {
        if (audioData != null) {
            BufferUtils.destroyDirectBuffer(audioData);
        }
    }
    
    @Override
    public void deleteObject(Object rendererObject) {
        ((AudioRenderer)rendererObject).deleteAudioData(this);
    }

    @Override
    public NativeObject createDestructableClone() {
        return new AudioBuffer(id);
    }

    @Override
    public long getUniqueId() {
        return ((long)OBJTYPE_AUDIOBUFFER << 32) | ((long)id);
    }
}
