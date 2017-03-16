
package com.clockwork.scene.mesh;

import com.clockwork.util.BufferUtils;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * IndexBuffer is an abstraction for integer index buffers,
 * it is used to retrieve indices without knowing in which format they 
 * are stored (ushort or uint).
 *
 */
public abstract class IndexBuffer {
    
    public static IndexBuffer wrapIndexBuffer(Buffer buf) {
        if (buf instanceof ByteBuffer) {
            return new IndexByteBuffer((ByteBuffer) buf);
        } else if (buf instanceof ShortBuffer) {
            return new IndexShortBuffer((ShortBuffer) buf);
        } else if (buf instanceof IntBuffer) {
            return new IndexIntBuffer((IntBuffer) buf);
        } else {
            throw new UnsupportedOperationException("Index buffer type unsupported: "+ buf.getClass());
        }
    }
    
    /**
     * Creates an index buffer that can contain the given amount
     * of vertices.
     * Returns IndexShortBuffer}
     * 
     * @param vertexCount The amount of vertices to contain
     * @param indexCount The amount of indices
     * to contain.
     * @return A new index buffer
     */
    public static IndexBuffer createIndexBuffer(int vertexCount, int indexCount){
        if (vertexCount > 65535){
            return new IndexIntBuffer(BufferUtils.createIntBuffer(indexCount));
        }else{
            return new IndexShortBuffer(BufferUtils.createShortBuffer(indexCount));
        }
    }
    
    /**
     * Returns the vertex index for the given index in the index buffer.
     * 
     * @param i The index inside the index buffer
     * @return 
     */
    public abstract int get(int i);
    
    /**
     * Puts the vertex index at the index buffer's index.
     * Implementations may throw an UnsupportedOperationException}
     * if modifying the IndexBuffer is not supported (e.g. virtual index
     * buffers).
     */
    public abstract void put(int i, int value);
    
    /**
     * Returns the size of the index buffer.
     * 
     * @return the size of the index buffer.
     */
    public abstract int size();
    
    /**
     * Returns the underlying data-type specific Buffer}.
     * Implementations may return null if there's no underlying
     * buffer.
     * 
     * @return the underlying Buffer}.
     */
    public abstract Buffer getBuffer();
}
