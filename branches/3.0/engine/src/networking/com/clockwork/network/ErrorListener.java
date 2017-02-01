
package com.clockwork.network;


/**
 *  Notified when errors happen on a connection.
 *
 *  @version   $Revision$
 */
public interface ErrorListener<S>
{
    public void handleError( S source, Throwable t );
}
