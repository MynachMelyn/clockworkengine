
package com.clockwork.network.kernel.udp;

import com.clockwork.network.kernel.Endpoint;
import com.clockwork.network.kernel.Kernel;
import com.clockwork.network.kernel.KernelException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;


/**
 *  Endpoint implementation that encapsulates the
 *  UDP connection information for return messaging,
 *  identification of envelope sources, etc.
 *
 *  @version   $Revision$
 */
public class UdpEndpoint implements Endpoint
{
    private long id;    
    private SocketAddress address;
    private DatagramSocket socket;
    private UdpKernel kernel;
    private boolean connected = true; // it's connectionless but we track logical state

    public UdpEndpoint( UdpKernel kernel, long id, SocketAddress address, DatagramSocket socket )
    {
        this.id = id;
        this.address = address;
        this.socket = socket;
        this.kernel = kernel;
    }

    public Kernel getKernel()
    {
        return kernel;
    }

    protected SocketAddress getRemoteAddress()
    {
        return address;
    }

    public void close()
    {
        close( false );
    }

    public void close( boolean flush )
    {
        // No real reason to flush UDP traffic yet... especially
        // when considering that the outbound UDP isn't even
        // queued.
    
        try {
            kernel.closeEndpoint(this);
            connected = false;
        } catch( IOException e ) {
            throw new KernelException( "Error closing endpoint for socket:" + socket, e );
        }
    }

    public long getId()
    {
        return id;
    }

    public String getAddress()
    {
        return String.valueOf(address); 
    }     

    public boolean isConnected()
    {
        // The socket is always unconnected anyway so we track our
        // own logical state for the kernel's benefit.
        return connected;
    }

    public void send( ByteBuffer data )
    {
        if( !isConnected() ) {
            throw new KernelException( "Endpoint is not connected:" + this );
        }
        
        
        try {
            DatagramPacket p = new DatagramPacket( data.array(), data.position(), 
                                                   data.remaining(), address );
                                                   
            // Just queue it up for the kernel threads to write
            // out
            kernel.enqueueWrite( this, p );
                                                               
            //socket.send(p);
        } catch( Exception e ) { //was IOException??
            throw new KernelException( "Error sending datagram to:" + address, e );
        }
    }

    public String toString()
    {
        return "UdpEndpoint[" + id + ", " + address + "]";
    }
}
