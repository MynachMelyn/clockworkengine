
package com.clockwork.network;

import com.clockwork.network.serializing.Serializable;

/**
 *  Interface implemented by all network messages.
 *
 *  @version   $Revision$
 */
@Serializable()
public interface Message
{
    /**
     *  Sets this message to 'reliable' or not and returns this
     *  message.
     */
    public Message setReliable(boolean f);
    
    /**
     *  Indicates which way an outgoing message should be sent
     *  or which way an incoming message was sent.
     */
    public boolean isReliable();
}
