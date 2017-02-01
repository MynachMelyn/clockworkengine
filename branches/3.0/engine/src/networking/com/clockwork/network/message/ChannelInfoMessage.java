
package com.clockwork.network.message;

import com.clockwork.network.AbstractMessage;
import com.clockwork.network.serializing.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 *  Contains information about any extra server channels (if they exist).  
 *
 */
@Serializable()
public class ChannelInfoMessage extends AbstractMessage {
    private long id;
    private int[] ports;

    public ChannelInfoMessage() {
        super( true );        
    }

    public ChannelInfoMessage( long id, List<Integer> ports ) {
        super( true );
        this.id = id;
        this.ports = new int[ports.size()];
        for( int i = 0; i < ports.size(); i++ ) {
            this.ports[i] = ports.get(i);
        }        
    }

    public long getId() {
        return id;
    }

    public int[] getPorts() {
        return ports;
    }
    
    public String toString() {
        return "ChannelInfoMessage[" + id + ", " + Arrays.asList(ports) + "]";
    }
}
