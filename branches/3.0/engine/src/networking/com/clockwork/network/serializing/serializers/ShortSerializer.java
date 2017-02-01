
package com.clockwork.network.serializing.serializers;

import com.clockwork.network.serializing.Serializer;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Short serializer.
 *
 */
@SuppressWarnings("unchecked")
public class ShortSerializer extends Serializer {
    public Short readObject(ByteBuffer data, Class c) throws IOException {
        return data.getShort();
    }

    public void writeObject(ByteBuffer buffer, Object object) throws IOException {
        buffer.putShort((Short)object);
    }
}
