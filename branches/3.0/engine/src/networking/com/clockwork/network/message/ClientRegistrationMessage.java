
package com.clockwork.network.message;

import com.clockwork.network.AbstractMessage;
import com.clockwork.network.serializing.*;
import com.clockwork.network.serializing.serializers.StringSerializer;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *  Client registration is a message that contains a unique ID. This ID
 *  is simply the current time in milliseconds, providing multiple clients
 *  will not connect to the same server within one millisecond. This is used
 *  to couple the TCP and UDP connections together into one 'Client' on the
 *  server.
 *
 */
@Serializable()
public class ClientRegistrationMessage extends AbstractMessage {

    public static final short SERIALIZER_ID = -44;
    
    private long id;
    private String gameName;
    private int version;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public void setGameName( String name ) {
        this.gameName = name;
    }
 
    public String getGameName() {
        return gameName;
    }
    
    public void setVersion( int version ) {
        this.version = version;
    }
    
    public int getVersion() {
        return version;
    }
    
    public String toString() {
        return getClass().getName() + "[id=" + id + ", gameName=" + gameName + ", version=" + version + "]";
    }
 
    /**
     *  A message-specific serializer to avoid compatibility issues
     *  between versions.  This serializer is registered to the specific
     *  SERIALIZER_ID which is compatible with previous versions of the 
     *  SM serializer registrations... and now will be forever.
     */   
    public static class ClientRegistrationSerializer extends Serializer {
     
        public ClientRegistrationMessage readObject( ByteBuffer data, Class c ) throws IOException {
    
            // Read the null/non-null marker
            if (data.get() == 0x0)
                return null;
 
            ClientRegistrationMessage msg = new ClientRegistrationMessage();
            
            msg.gameName = StringSerializer.readString(data);
            msg.id = data.getLong();
            msg.version = data.getInt();
            
            return msg;
        }

        public void writeObject(ByteBuffer buffer, Object object) throws IOException {
    
            // Add the null/non-null marker
            buffer.put( (byte)(object != null ? 0x1 : 0x0) );
            if (object == null) {
                // Nothing left to do
                return;
            }
            
            ClientRegistrationMessage msg = (ClientRegistrationMessage)object;
            StringSerializer.writeString( msg.gameName, buffer );           

            buffer.putLong(msg.id);
            buffer.putInt(msg.version);                    
        }
    }
     
}
