
package com.clockwork.network.kernel.tcp;

import com.clockwork.network.kernel.Connector;
import com.clockwork.network.kernel.ConnectorException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 *  A straight forward socket-based connector implementation that
 *  does not use any separate threading.  It relies completely on
 *  the buffering in the OS network layer.
 *
 *  @version   $Revision$
 */
public class SocketConnector implements Connector
{
    private Socket sock;
    private InputStream in;
    private OutputStream out;
    private SocketAddress remoteAddress;
    private byte[] buffer = new byte[65535];
    private AtomicBoolean connected = new AtomicBoolean(false);

    public SocketConnector( InetAddress address, int port ) throws IOException
    {
        this.sock = new Socket(address, port);
        remoteAddress = sock.getRemoteSocketAddress(); // for info purposes 
        
        // Disable Nagle's buffering so data goes out when we
        // put it there.
        sock.setTcpNoDelay(true);
        
        in = sock.getInputStream();
        out = sock.getOutputStream();
        
        connected.set(true);
    }
 
    protected void checkClosed()
    {
        if( sock == null )
            throw new ConnectorException( "Connection is closed:" + remoteAddress );
    }
     
    public boolean isConnected()
    {
        if( sock == null )
            return false;
        return sock.isConnected();
    }

    public void close()
    {
        checkClosed();
        try {
            Socket temp = sock;
            sock = null;            
            connected.set(false);
            temp.close();
        } catch( IOException e ) {            
            throw new ConnectorException( "Error closing socket for:" + remoteAddress, e );
        }            
    }     

    public boolean available()
    {
        checkClosed();
        try {
            return in.available() > 0;
        } catch( IOException e ) {
            throw new ConnectorException( "Error retrieving data availability for:" + remoteAddress, e );
        }       
    }     
    
    public ByteBuffer read()
    {
        checkClosed();
        
        try {
            // Read what we can
            int count = in.read(buffer);
            if( count < 0 ) {
                // Socket is closed
                close();
                return null;
            }

            // Wrap it in a ByteBuffer for the caller
            return ByteBuffer.wrap( buffer, 0, count ); 
        } catch( IOException e ) {
            if( !connected.get() ) {
                // Nothing to see here... just move along
                return null;
            }        
            throw new ConnectorException( "Error reading from connection to:" + remoteAddress, e );    
        }                
    }
    
    public void write( ByteBuffer data )
    {
        checkClosed();
        
        try {
            out.write(data.array(), data.position(), data.remaining());
        } catch( IOException e ) {
            throw new ConnectorException( "Error writing to connection:" + remoteAddress, e );
        }
    }   
    
}
