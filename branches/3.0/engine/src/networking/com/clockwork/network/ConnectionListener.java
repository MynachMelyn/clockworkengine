
package com.clockwork.network;


/**
 *  Listener that is notified about connection arrivals and
 *  removals within a server.
 *
 *  @version   $Revision$
 */
public interface ConnectionListener
{
    /**
     *  Called when a connection has been added to the specified server and
     *  is fully setup.
     */
    public void connectionAdded( Server server, HostedConnection conn );
    
    /**
     *  Called when a connection has been removed from the specified
     *  server. 
     */
    public void connectionRemoved( Server server, HostedConnection conn );
}
