
package com.clockwork.network.base;

import com.clockwork.network.kernel.Kernel;
import java.io.IOException;


/**
 *  Supplied to the DefaultServer to create any additional
 *  channel kernels that might be required.
 *
 *  @version   $Revision$
 */
public interface KernelFactory
{
    public static final KernelFactory DEFAULT = new NioKernelFactory();

    public Kernel createKernel( int channel, int port ) throws IOException;
}
