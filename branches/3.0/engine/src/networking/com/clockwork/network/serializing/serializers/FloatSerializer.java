
package com.clockwork.network.serializing.serializers;

import com.clockwork.network.serializing.Serializer;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Float serializer.
 *
 */
@SuppressWarnings("unchecked")
public class FloatSerializer extends Serializer {

    public Float readObject(ByteBuffer data, Class c) throws IOException {
        return data.getFloat();
    }

    public void writeObject(ByteBuffer buffer, Object object) throws IOException {
        buffer.putFloat((Float)object);
    }
}
