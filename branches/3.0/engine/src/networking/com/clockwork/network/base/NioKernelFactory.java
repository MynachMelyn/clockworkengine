
package com.clockwork.network.base;

import com.clockwork.network.kernel.Kernel;
import com.clockwork.network.kernel.tcp.SelectorKernel;
import java.io.IOException;


/**
 *  KernelFactory implemention for creating TCP kernels
 *  using the NIO selector model.
 *
 *  @version   $Revision$
 */
public class NioKernelFactory implements KernelFactory
{
    public Kernel createKernel( int channel, int port ) throws IOException
    {
        return new SelectorKernel(port);
    }
}
