
package com.clockwork.network;

import java.io.IOException;
import java.net.InetAddress;

/**
 *  A Client whose network connection information can 
 *  be provided post-creation.  The actual connection stack
 *  will be setup the same as if Network.connectToServer
 *  had been called.  
 *
 *  @version   $Revision$
 */
public interface NetworkClient extends Client
{
    /**
     *  Connects this client to the specified remote server and ports.
     */
    public void connectToServer( String host, int port, int remoteUdpPort ) throws IOException;
 
    /**
     *  Connects this client to the specified remote server and ports.
     *  
     *  @param address  The hosts internet address.
     *  @param port  The remote TCP port on the server to which this client should
     *                  send reliable messages. 
     *  @param remoteUdpPort  The remote UDP port on the server to which this client should
     *                  send 'fast'/unreliable messages.   Set to -1 if 'fast' traffic should 
     *                  go over TCP.  This will completely disable UDP traffic for this
     *                  client.
     */                               
    public void connectToServer( InetAddress address, int port, int remoteUdpPort ) throws IOException;
    
}
