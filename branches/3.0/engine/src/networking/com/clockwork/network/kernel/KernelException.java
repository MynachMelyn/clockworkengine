
package com.clockwork.network.kernel;


/**
 *  Represents a kernel-level error, usually encapsulating
 *  an IOException as its cause.
 *
 *  @version   $Revision$
 */
public class KernelException extends RuntimeException
{
    public KernelException( String message, Throwable cause )
    {
        super( message, cause );
    }
    
    public KernelException( String message )
    {
        super( message );
    }
}
