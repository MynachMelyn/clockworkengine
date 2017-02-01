
package com.clockwork.network.serializing.serializers;

import com.clockwork.network.serializing.Serializer;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * The Long serializer.
 *
 */
@SuppressWarnings("unchecked")
public class LongSerializer extends Serializer {

    public Long readObject(ByteBuffer data, Class c) throws IOException {
        return data.getLong();
    }

    public void writeObject(ByteBuffer buffer, Object object) throws IOException {
        buffer.putLong((Long)object);
    }
}
