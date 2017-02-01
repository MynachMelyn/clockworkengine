
package com.clockwork.audio.plugins;

import com.clockwork.asset.AssetInfo;
import com.clockwork.asset.AssetLoader;
import com.clockwork.audio.AudioBuffer;
import com.clockwork.audio.AudioData;
import com.clockwork.audio.AudioKey;
import com.clockwork.audio.AudioStream;
import com.clockwork.util.BufferUtils;
import com.clockwork.util.LittleEndien;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WAVLoader implements AssetLoader {

    private static final Logger logger = Logger.getLogger(WAVLoader.class.getName());

    // all these are in big endian
    private static final int i_RIFF = 0x46464952;
    private static final int i_WAVE = 0x45564157;
    private static final int i_fmt  = 0x20746D66;
    private static final int i_data = 0x61746164;

    private boolean readStream = false;

    private AudioBuffer audioBuffer;
    private AudioStream audioStream;
    private AudioData audioData;
    private int bytesPerSec;
    private float duration;

    private LittleEndien in;

    private void readFormatChunk(int size) throws IOException{
        // if other compressions are supported, size doesn't have to be 16
//        if (size != 16)
//            logger.warning("Expected size of format chunk to be 16");

        int compression = in.readShort();
        if (compression != 1){
            throw new IOException("WAV Loader only supports PCM wave files");
        }

        int channels = in.readShort();
        int sampleRate = in.readInt();

        bytesPerSec = in.readInt(); // used to calculate duration

        int bytesPerSample = in.readShort();
        int bitsPerSample = in.readShort();

        int expectedBytesPerSec = (bitsPerSample * channels * sampleRate) / 8;
        if (expectedBytesPerSec != bytesPerSec){
            logger.log(Level.WARNING, "Expected {0} bytes per second, got {1}",
                    new Object[]{expectedBytesPerSec, bytesPerSec});
        }
        
        if (bitsPerSample != 8 && bitsPerSample != 16)
            throw new IOException("Only 8 and 16 bits per sample are supported!");

        if ( (bitsPerSample / 8) * channels != bytesPerSample)
            throw new IOException("Invalid bytes per sample value");

        if (bytesPerSample * sampleRate != bytesPerSec)
            throw new IOException("Invalid bytes per second value");

        audioData.setupFormat(channels, bitsPerSample, sampleRate);

        int remaining = size - 16;
        if (remaining > 0){
            in.skipBytes(remaining);
        }
    }

    private void readDataChunkForBuffer(int len) throws IOException {
        ByteBuffer data = BufferUtils.createByteBuffer(len);
        byte[] buf = new byte[512];
        int read = 0;
        while ( (read = in.read(buf)) > 0){
            data.put(buf, 0, Math.min(read, data.remaining()) );
        }
        data.flip();
        audioBuffer.updateData(data);
        in.close();
    }

    private void readDataChunkForStream(int len) throws IOException {
        audioStream.updateData(in, duration);
    }

    private AudioData load(InputStream inputStream, boolean stream) throws IOException{
        this.in = new LittleEndien(inputStream);
        
        int sig = in.readInt();
        if (sig != i_RIFF)
            throw new IOException("File is not a WAVE file");
        
        // skip size
        in.readInt();
        if (in.readInt() != i_WAVE)
            throw new IOException("WAVE File does not contain audio");

        readStream = stream;
        if (readStream){
            audioStream = new AudioStream();
            audioData = audioStream;
        }else{
            audioBuffer = new AudioBuffer();
            audioData = audioBuffer;
        }

        while (true) {
            int type = in.readInt();
            int len = in.readInt();

            switch (type) {
                case i_fmt:
                    readFormatChunk(len);
                    break;
                case i_data:
                    // Compute duration based on data chunk size
                    duration = len / bytesPerSec;

                    if (readStream) {
                        readDataChunkForStream(len);
                    } else {
                        readDataChunkForBuffer(len);
                    }
                    return audioData;
                default:
                    int skipped = in.skipBytes(len);
                    if (skipped <= 0) {
                        return null;
                    }
                    break;
            }
        }
    }
    
    public Object load(AssetInfo info) throws IOException {
        AudioData data;
        InputStream inputStream = null;
        try {
            inputStream = info.openStream();
            data = load(inputStream, ((AudioKey)info.getKey()).isStream());
            if (data instanceof AudioStream){
                inputStream = null;
            }
            return data;
        } finally {
            if (inputStream != null){
                inputStream.close();
            }
        }
    }
}
