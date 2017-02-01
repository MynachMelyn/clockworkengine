
package com.clockwork.scene.mesh;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * IndexBuffer implementation for {@link ByteBuffer}s.
 * 
 */
public class IndexByteBuffer extends IndexBuffer {

    private ByteBuffer buf;

    public IndexByteBuffer(ByteBuffer buffer) {
        buf = buffer;
        buf.rewind();
    }
    
    @Override
    public int get(int i) {
        return buf.get(i) & 0x000000FF;
    }

    @Override
    public void put(int i, int value) {
        buf.put(i, (byte) value);
    }

    @Override
    public int size() {
        return buf.limit();
    }

    @Override
    public Buffer getBuffer() {
        return buf;
    }

}
