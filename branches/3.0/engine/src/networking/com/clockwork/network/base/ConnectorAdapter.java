
package com.clockwork.network.base;

import com.clockwork.network.ErrorListener;
import com.clockwork.network.Message;
import com.clockwork.network.MessageListener;
import com.clockwork.network.kernel.Connector;
import com.clockwork.network.kernel.ConnectorException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *  Wraps a single Connector and forwards new messages
 *  to the supplied message dispatcher.  This is used
 *  by DefaultClient to manage its connector objects.
 *  This is only responsible for message reading and provides
 *  no support for buffering writes.
 *
 *  <p>This adapter assumes a simple protocol where two
 *  bytes define a (short) object size with the object data
 *  to follow.  Note: this limits the size of serialized
 *  objects to 32676 bytes... even though, for example,
 *  datagram packets can hold twice that. :P</p>  
 *
 *  @version   $Revision$
 */
public class ConnectorAdapter extends Thread
{
    private static final int OUTBOUND_BACKLOG = 16000;

    private Connector connector;
    private MessageListener<Object> dispatcher;
    private ErrorListener<Object> errorHandler;
    private AtomicBoolean go = new AtomicBoolean(true);

    private BlockingQueue<ByteBuffer> outbound;
     
    // Writes messages out on a background thread
    private WriterThread writer;
   
    // Marks the messages as reliable or not if they came
    // through this connector.
    private boolean reliable;
 
    public ConnectorAdapter( Connector connector, MessageListener<Object> dispatcher, 
                             ErrorListener<Object> errorHandler, boolean reliable )
    {
        super( String.valueOf(connector) );
        this.connector = connector;        
        this.dispatcher = dispatcher;
        this.errorHandler = errorHandler;
        this.reliable = reliable;
        setDaemon(true);
 
        // The backlog makes sure that the outbound channel blocks once
        // a certain backlog level is reached.  It is set high so that it
        // is only reached in the worst cases... which are usually things like
        // raw throughput tests.  Technically, a saturated TCP channel could
        // back up quite a bit if the buffers are full and the socket has
        // stalled but 16,000 messages is still a big backlog.       
        outbound = new ArrayBlockingQueue<ByteBuffer>(OUTBOUND_BACKLOG); 
 
        // Note: this technically adds a potential deadlock case
        // with the above code where there wasn't one before.  For example,
        // if a TCP outbound queue fills to capacity and a client sends
        // in such a way that they block TCP message handling then if the HostedConnection
        // on the server is similarly blocked then the TCP network buffers may
        // all get full and no outbound messages move and we forever block
        // on the queue.
        // However, in practice this can't really happen... or at least it's
        // the sign of other really bad things.
        // First, currently the server-side outbound queues are all unbounded and
        // so won't ever block the handling of messages if the outbound channel is full.
        // Second, there would have to be a huge amount of data backlog for this
        // to ever occur anyway.
        // Third, it's a sign of a really poor architecture if 16,000 messages
        // can go out in a way that blocks reads. 
        
        writer = new WriterThread();
        writer.start();                                           
    }
 
    public void close()
    {
        go.set(false);

        // Kill the writer service
        writer.shutdown();
 
        if( connector.isConnected() )
            {       
            // Kill the connector
            connector.close();
            }
    }
 
    protected void dispatch( Message m )
    {
        dispatcher.messageReceived( null, m );                        
    }
 
    public void write( ByteBuffer data )
    {
        try {
            outbound.put( data );
        } catch( InterruptedException e ) {
            throw new RuntimeException( "Interrupted while waiting for queue to drain", e );
        }
    }
 
    protected void handleError( Exception e )
    {
        if( !go.get() )
            return;
        
        errorHandler.handleError( this, e );
    }
 
    public void run()
    {
        MessageProtocol protocol = new MessageProtocol();
 
        try {                  
            while( go.get() ) {
                ByteBuffer buffer = connector.read();
                if( buffer == null ) {
                    if( go.get() ) {
                        throw new ConnectorException( "Connector closed." ); 
                    } else {
                        // Just dump out because a null buffer is expected
                        // from a closed/closing connector
                        break;
                    }
                }
                
                protocol.addBuffer( buffer );
                
                Message m = null;
                while( (m = protocol.getMessage()) != null ) {
                    m.setReliable( reliable );
                    dispatch( m );
                }
            }
        } catch( Exception e ) {
            handleError( e );
        }            
    }
 
    protected class WriterThread extends Thread
    {
        public WriterThread()
        {
            super( String.valueOf(connector) + "-writer" );
        }

        public void shutdown()
        {
            interrupt();
        }
 
        private void write( ByteBuffer data )
        {
            try {                                        
                connector.write(data);
            } catch( Exception e ) {
                handleError( e );
            }
        }
        
        public void run()
        {
            while( go.get() ) {
                try {           
                    ByteBuffer data = outbound.take();
                    write(data);                                       
                } catch( InterruptedException e ) {
                    if( !go.get() )
                        return;
                    throw new RuntimeException( "Interrupted waiting for data", e );
                } 
            }
        }
    }
}
