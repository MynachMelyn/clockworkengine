
package com.clockwork.network.serializing.serializers;

import com.clockwork.network.serializing.Serializer;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Char serializer.
 *
 */
@SuppressWarnings("unchecked")
public class CharSerializer extends Serializer {

    public Character readObject(ByteBuffer data, Class c) throws IOException {
        return data.getChar();
    }

    public void writeObject(ByteBuffer buffer, Object object) throws IOException {
        buffer.putChar((Character)object);
    }
}
