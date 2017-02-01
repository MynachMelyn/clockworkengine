
package com.clockwork.network.serializing.serializers;

import com.clockwork.network.serializing.Serializer;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Byte serializer.
 *
 */
@SuppressWarnings("unchecked")
public class ByteSerializer extends Serializer {

    public Byte readObject(ByteBuffer data, Class c) throws IOException {
        return data.get();
    }

    public void writeObject(ByteBuffer buffer, Object object) throws IOException {
        buffer.put((Byte)object);
    }
}
