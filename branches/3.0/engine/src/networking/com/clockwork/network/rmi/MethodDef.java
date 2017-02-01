
package com.clockwork.network.rmi;


/**
 * Method definition is used to map methods on an RMI interface
 * to an implementation on a remote machine.
 *
 */
public class MethodDef {

    /**
     * Method name
     */
    public String name;

    /**
     * Return type
     */
    public Class<?> retType;

    /**
     * Parameter types
     */
    public Class<?>[] paramTypes;
}
