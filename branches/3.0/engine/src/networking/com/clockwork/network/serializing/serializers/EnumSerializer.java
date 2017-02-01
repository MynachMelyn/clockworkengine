
package com.clockwork.network.serializing.serializers;

import com.clockwork.network.serializing.Serializer;
import com.clockwork.network.serializing.SerializerException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Enum serializer.
 *
 */
public class EnumSerializer extends Serializer {
    public <T> T readObject(ByteBuffer data, Class<T> c) throws IOException {
        try {
            int ordinal = data.getInt();

            if (ordinal == -1) return null;
            T[] enumConstants = c.getEnumConstants();
            if (enumConstants == null)
                throw new SerializerException( "Class has no enum constants:" + c );
            return enumConstants[ordinal];
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    public void writeObject(ByteBuffer buffer, Object object) throws IOException {
        if (object == null) {
            buffer.putInt(-1);
        } else {
            buffer.putInt(((Enum)object).ordinal());
        }
    }
}
