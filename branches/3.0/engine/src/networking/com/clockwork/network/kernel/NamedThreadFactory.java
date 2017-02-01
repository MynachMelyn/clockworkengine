
package com.clockwork.network.kernel;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 *  A simple factory that delegates to java.util.concurrent's
 *  default thread factory but adds a prefix to the beginning
 *  of the thread name.
 *
 *  @version   $Revision$
 */
public class NamedThreadFactory implements ThreadFactory
{
    private String name;
    private boolean daemon;
    private ThreadFactory delegate;
    
    public NamedThreadFactory( String name )
    {
        this( name, Executors.defaultThreadFactory() );
    }
    
    public NamedThreadFactory( String name, boolean daemon )
    {
        this( name, daemon, Executors.defaultThreadFactory() );
    }
    
    public NamedThreadFactory( String name, ThreadFactory delegate )
    {
        this( name, false, delegate );
    }

    public NamedThreadFactory( String name, boolean daemon, ThreadFactory delegate )
    {
        this.name = name;
        this.daemon = daemon;
        this.delegate = delegate;
    }
    
    public Thread newThread( Runnable r )
    {
        Thread result = delegate.newThread(r);
        String s = result.getName();
        result.setName( name + "[" + s + "]" );
        result.setDaemon(daemon);
        return result;
    } 
}

