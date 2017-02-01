
package com.clockwork.network;


/**
 *  The source of a received message and the common abstract interface
 *  of client->server and server->client objects. 
 *
 *  @version   $Revision$
 */
public interface MessageConnection
{
    /**
     *  Sends a message to the other end of the connection.
     */   
    public void send( Message message );
    
    /**
     *  Sends a message to the other end of the connection using
     *  the specified alternate channel.
     */   
    public void send( int channel, Message message );
}    

