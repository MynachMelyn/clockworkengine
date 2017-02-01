
package com.clockwork.network.serializing.serializers;

import com.clockwork.network.serializing.Serializer;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Serializes uses Java built-in method.
 *
 * TODO
 */
@SuppressWarnings("unchecked")
public class SerializableSerializer extends Serializer {

    public Serializable readObject(ByteBuffer data, Class c) throws IOException {
        throw new UnsupportedOperationException( "Serializable serialization not supported." );
    }

    public void writeObject(ByteBuffer buffer, Object object) throws IOException {
        throw new UnsupportedOperationException( "Serializable serialization not supported." );
    }
}
