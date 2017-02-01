package com.clockwork.newvideo;

import com.clockwork.texture.Image;
import com.clockwork.texture.Image.Format;
import com.clockwork.texture.Texture2D;
import com.clockwork.util.BufferUtils;
import java.util.concurrent.BlockingQueue;

public final class VideoTexture extends Texture2D {

    private BlockingQueue<VideoTexture> ownerQueue;

    public VideoTexture(int width, int height, Format format, BlockingQueue<VideoTexture> ownerQueue){
        super(new Image(format, width, height,
                        BufferUtils.createByteBuffer(width*height*format.getBitsPerPixel()/8)));
        this.ownerQueue = ownerQueue;
    }

    public void free(){
        try {
            ownerQueue.put(this);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

}
