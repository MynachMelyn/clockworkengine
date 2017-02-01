
package com.clockwork.network.base;

import com.clockwork.network.kernel.Connector;
import java.io.IOException;


/**
 *  Creates Connectors for a specific host.
 *
 *  @version   $Revision$
 */
public interface ConnectorFactory
{
    public Connector createConnector( int channel, int port ) throws IOException;
}
