
package com.clockwork.network.message;

import com.clockwork.network.AbstractMessage;
import com.clockwork.network.serializing.*;
import com.clockwork.network.serializing.serializers.StringSerializer;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Represents a disconnect message.
 *
 */
@Serializable()
public class DisconnectMessage extends AbstractMessage {

    public static final short SERIALIZER_ID = -42;

    public static final String KICK = "Kick";
    public static final String USER_REQUESTED = "User requested";
    public static final String ERROR = "Error";
    public static final String FILTERED = "Filtered";

    private String reason;
    private String type;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String toString() {
        return getClass().getName() + "[reason=" + reason + ", type=" + type + "]";
    }
    
    /**
     *  A message-specific serializer to avoid compatibility issues
     *  between versions.  This serializer is registered to the specific
     *  SERIALIZER_ID which is compatible with previous versions of the 
     *  SM serializer registrations... and now will be forever.
     */   
    public static class DisconnectSerializer extends Serializer {
     
        public DisconnectMessage readObject( ByteBuffer data, Class c ) throws IOException {
    
            // Read the null/non-null marker
            if (data.get() == 0x0)
                return null;
 
            DisconnectMessage msg = new DisconnectMessage();
            
            msg.reason = StringSerializer.readString(data);
            msg.type = StringSerializer.readString(data);
            
            return msg;
        }

        public void writeObject(ByteBuffer buffer, Object object) throws IOException {
    
            // Add the null/non-null marker
            buffer.put( (byte)(object != null ? 0x1 : 0x0) );
            if (object == null) {
                // Nothing left to do
                return;
            }
            
            DisconnectMessage msg = (DisconnectMessage)object;
            StringSerializer.writeString( msg.reason, buffer );           
            StringSerializer.writeString( msg.type, buffer );           
        }
    }     
}
