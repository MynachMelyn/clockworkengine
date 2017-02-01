
package com.clockwork.network.kernel;

import java.nio.ByteBuffer;

/**
 *  A single channel remote connection allowing the sending
 *  and receiving of data.  As opposed to the Kernel, this will
 *  only ever receive data from one Endpoint and so bypasses
 *  the envelope wrapping.
 *
 *  @version   $Revision$
 */
public interface Connector
{
    /**
     *  Returns true if this connector is currently connected.
     */
    public boolean isConnected();

    /**
     *  Closes the connection.  Any subsequent attempts to read
     *  or write will fail with an exception.
     */
    public void close();     

    /**
     *  Returns true if there is currently data available for
     *  reading.  Some connector implementations may not be able
     *  to answer this question accurately and will always return
     *  false.
     */
    public boolean available();     
    
    /**
     *  Reads a chunk of data from the connection, blocking if
     *  there is no data available.  The buffer may only be valid
     *  until the next read() call is made.  Callers should copy
     *  the data if they need it for longer than that.
     *
     *  @return The data read or null if there is no more data
     *          because the connection is closed.
     */
    public ByteBuffer read();
    
    /**
     *  Writes a chunk of data to the connection from data.position()
     *  to data.limit().
     */
    public void write( ByteBuffer data );
}
