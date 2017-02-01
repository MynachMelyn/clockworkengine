
package com.clockwork.network.kernel;

import com.clockwork.network.Filter;
import java.nio.ByteBuffer;

/**
 *  Defines the basic byte[] passing messaging
 *  kernel.
 *
 *  @version   $Revision$
 */
public interface Kernel
{
    /**
     *  A marker envelope returned from read() that indicates that
     *  there are events pending.  This allows a single thread to
     *  more easily process the envelopes and endpoint events.
     */
    public static final Envelope EVENTS_PENDING = new Envelope( null, new byte[0], false );     

    /**
     *  Initializes the kernel and starts any internal processing.
     */
    public void initialize();
    
    /**
     *  Gracefully terminates the kernel and stops any internal 
     *  daemon processing.  This method will not return until all
     *  internal threads have been shut down.
     */
    public void terminate() throws InterruptedException;

    /**
     *  Dispatches the data to all endpoints managed by the
     *  kernel that match the specified endpoint filter..
     *  If 'copy' is true then the implementation will copy the byte buffer
     *  before delivering it to endpoints.  This allows the caller to reuse
     *  the data buffer.  Though it is important that the buffer not be changed
     *  by another thread while this call is running.
     *  Only the bytes from data.position() to data.remaining() are sent.  
     */ 
    public void broadcast( Filter<? super Endpoint> filter, ByteBuffer data, boolean reliable, 
                           boolean copy );
 
    /**
     *  Returns true if there are waiting envelopes.
     */   
    public boolean hasEnvelopes();
 
    /**
     *  Removes one envelope from the received messages queue or
     *  blocks until one is available.
     */   
    public Envelope read() throws InterruptedException;
    
    /**
     *  Removes and returnsn one endpoint event from the event queue or
     *  null if there are no endpoint events.     
     */
    public EndpointEvent nextEvent();     
}
