
package com.clockwork.network.kernel;


/**
 *  Provides information about an added or
 *  removed connection.
 *
 *  @version   $Revision$
 */
public class EndpointEvent
{
    public enum Type { ADD, REMOVE };

    private Kernel source;
    private Endpoint endpoint;
    private Type type;

    public EndpointEvent( Kernel source, Endpoint p, Type type )
    {
        this.source = source;
        this.endpoint = p;
        this.type = type;
    }
    
    public static EndpointEvent createAdd( Kernel source, Endpoint p )
    {
        return new EndpointEvent( source, p, Type.ADD );
    }

    public static EndpointEvent createRemove( Kernel source, Endpoint p )
    {
        return new EndpointEvent( source, p, Type.REMOVE );
    }
    
    public Kernel getSource()
    {
        return source;
    }
    
    public Endpoint getEndpoint()
    {
        return endpoint;
    }
    
    public Type getType()
    {
        return type;
    }
    
    public String toString()
    {
        return "EndpointEvent[" + type + ", " + endpoint + "]";
    } 
}
