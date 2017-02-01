
package com.clockwork.network.base;

import com.clockwork.network.kernel.Connector;
import com.clockwork.network.kernel.tcp.SocketConnector;
import java.io.IOException;
import java.net.InetAddress;


/**
 *  Creates TCP connectors to a specific remote address.  
 *
 *  @version   $Revision$
 */
public class TcpConnectorFactory implements ConnectorFactory
{
    private InetAddress remoteAddress;
    
    public TcpConnectorFactory( InetAddress remoteAddress )
    {
        this.remoteAddress = remoteAddress;
    }

    public Connector createConnector( int channel, int port ) throws IOException
    {
        return new SocketConnector( remoteAddress, port );        
    }    
}
