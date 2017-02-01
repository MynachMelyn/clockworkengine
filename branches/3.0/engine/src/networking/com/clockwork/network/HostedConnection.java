
package com.clockwork.network;

import java.util.Set;

/**
 *  This is the connection back to a client that is being
 *  hosted in a server instance.  
 *
 *  @version   $Revision$
 */
public interface HostedConnection extends MessageConnection
{
    /**
     *  Returns the Server instance that is hosting this connection.
     */
    public Server getServer();     

    /**
     *  Returns the server-unique ID for this client.
     */
    public int getId();

    /**
     *  Returns the transport specific remote address of this connection
     *  as a string.  This may or may not be unique per connection depending
     *  on the type of transport.  It is provided for information and filtering
     *  purposes. 
     */
    public String getAddress();
   
    /**
     *  Closes and removes this connection from the server
     *  sending the optional reason to the remote client.
     */
    public void close( String reason );
    
    /**
     *  Sets a session attribute specific to this connection.  If the value
     *  is set to null then the attribute is removed.
     *
     *  @return The previous session value for this key or null
     *          if there was no previous value.
     */
    public Object setAttribute( String name, Object value );
    
    /**
     *  Retrieves a previosly stored session attribute or
     *  null if no such attribute exists.
     */
    public <T> T getAttribute( String name );
    
    /**
     *  Returns a read-only set of attribute names currently stored
     *  for this client session.
     */
    public Set<String> attributeNames();     
}
