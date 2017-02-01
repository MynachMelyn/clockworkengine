
package com.clockwork.network;


/**
 *  Listener that is notified about the connection state of
 *  a Client.
 *
 *  @version   $Revision$
 */
public interface ClientStateListener
{
    /**
     *  Called when the specified client is fully connected to
     *  the remote server.
     */
    public void clientConnected( Client c );
 
    /**
     *  Called when the client has disconnected from the remote
     *  server.  If info is null then the client shut down the
     *  connection normally, otherwise the info object contains
     *  additional information about the disconnect.
     */   
    public void clientDisconnected( Client c, DisconnectInfo info );
 
    /**
     *  Provided with the clientDisconnected() notification to
     *  include additional information about the disconnect.
     */   
    public class DisconnectInfo
    {
        public String reason;
        public Throwable error;
    }
}
