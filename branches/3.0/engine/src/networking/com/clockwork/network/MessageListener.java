
package com.clockwork.network;


/**
 *  Listener notified about new messages.
 *
 *  <p>Note about multithreading: on the server, these messages may
 *  be delivered by more than one thread depending on the server
 *  implementation used.  Listener implementations should treat their
 *  shared data structures accordingly and set them up for multithreaded 
 *  access.  The only threading guarantee is that for a single
 *  HostedConnection, there will only ever be one thread at a time
 *  and the messages will always be delivered to that connection in the 
 *  order that they were delivered.</p>   
 *
 *  @version   $Revision$
 */
public interface MessageListener<S>
{
    public void messageReceived( S source, Message m );
}
