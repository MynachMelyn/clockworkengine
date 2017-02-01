
package com.clockwork.network.kernel;

/**
 *  Encapsulates a received piece of data.  This is used by the Kernel
 *  to track incoming chunks of data.  
 *
 *  @version   $Revision$
 */
public class Envelope
{
    private Endpoint source;  
    private byte[] data;
    private boolean reliable;
    
    /**
     *  Creates an incoming envelope holding the data from the specified
     *  source.  The 'reliable' flag further indicates on which mode of
     *  transport the data arrrived.
     */
    public Envelope( Endpoint source, byte[] data, boolean reliable )
    {
        this.source = source;
        this.data = data;
        this.reliable = reliable;
    }
    
    public Endpoint getSource()
    {
        return source;
    }
    
    public byte[] getData()
    {
        return data;
    }
    
    public boolean isReliable()
    {
        return reliable;
    }
    
    public String toString()
    {
        return "Envelope[" + source + ", " + (reliable?"reliable":"unreliable") + ", " + data.length + "]";
    }
}
