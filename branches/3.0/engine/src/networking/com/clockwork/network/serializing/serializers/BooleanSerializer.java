
package com.clockwork.network.serializing.serializers;

import com.clockwork.network.serializing.Serializer;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Boolean serializer.
 *
 */
@SuppressWarnings("unchecked")
public class BooleanSerializer extends Serializer {

    public Boolean readObject(ByteBuffer data, Class c) throws IOException {
        return data.get() == 1;
    }

    public void writeObject(ByteBuffer buffer, Object object) throws IOException {
        buffer.put(((Boolean)object) ? (byte)1 : (byte)0);
    }
}
