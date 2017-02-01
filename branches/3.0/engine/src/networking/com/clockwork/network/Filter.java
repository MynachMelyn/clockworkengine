
package com.clockwork.network;


/**
 *  Determines a true or false value for a given input. 
 *
 *  @version   $Revision$
 */
public interface Filter<T>
{
    /**
     *  Returns true if the specified input is accepted by this
     *  filter. 
     */
    public boolean apply( T input ); 
}


