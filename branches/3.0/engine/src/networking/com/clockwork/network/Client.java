
package com.clockwork.network;


/**
 *  Represents a remote connection to a server that can be used
 *  for sending and receiving messages.
 *
 *  @version   $Revision$
 */
public interface Client extends MessageConnection
{
    /**
     *  Starts the client allowing it to begin processing incoming
     *  messages and delivering them to listeners.
     */
    public void start();

    /**
     *  Returns true if this client is fully connected to the
     *  host.
     */
    public boolean isConnected();     

    /**
     *  Returns a unique ID for this client within the remote
     *  server or -1 if this client isn't fully connected to the
     *  server.
     */
    public int getId();     
 
    /**
     *  Returns the 'game name' for servers to which this client should be able
     *  to connect.  This should match the 'game name' set on the server or this
     *  client will be turned away.
     */
    public String getGameName();
 
    /**
     *  Returns the game-specific version of the server this client should
     *  be able to connect to.
     */   
    public int getVersion();
 
    /**
     *  Sends a message to the server.
     */   
    public void send( Message message );
 
    /**
     *  Sends a message to the other end of the connection using
     *  the specified alternate channel.
     */   
    public void send( int channel, Message message );
 
    /**
     *  Closes this connection to the server.
     */
    public void close();         

    /**
     *  Adds a listener that will be notified about connection
     *  state changes.
     */
    public void addClientStateListener( ClientStateListener listener ); 

    /**
     *  Removes a previously registered connection listener.
     */
    public void removeClientStateListener( ClientStateListener listener ); 

    /**
     *  Adds a listener that will be notified when any message or object
     *  is received from the server.
     */
    public void addMessageListener( MessageListener<? super Client> listener ); 

    /**
     *  Adds a listener that will be notified when messages of the specified
     *  types are received.
     */
    public void addMessageListener( MessageListener<? super Client> listener, Class... classes ); 

    /**
     *  Removes a previously registered wildcard listener.  This does
     *  not remove this listener from any type-specific registrations.
     */
    public void removeMessageListener( MessageListener<? super Client> listener ); 

    /**
     *  Removes a previously registered type-specific listener from
     *  the specified types.
     */
    public void removeMessageListener( MessageListener<? super Client> listener, Class... classes ); 
    
    /**
     *  Adds a listener that will be notified when any connection errors
     *  occur.  If a client has no error listeners then the default behavior
     *  is to close the connection and provide an appropriate DisconnectInfo
     *  to any ClientStateListeners.  If the application adds its own error
     *  listeners then it must take care of closing the connection itself.
     */
    public void addErrorListener( ErrorListener<? super Client> listener ); 

    /**
     *  Removes a previously registered error listener.
     */
    public void removeErrorListener( ErrorListener<? super Client> listener ); 
}


