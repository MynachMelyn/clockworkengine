
package com.clockwork.network.rmi;

import com.clockwork.network.AbstractMessage;
import com.clockwork.network.serializing.Serializable;

/**
 * Sent to a remote client to make a remote method invocation.
 *
 */
@Serializable
public class RemoteMethodCallMessage extends AbstractMessage {

    public RemoteMethodCallMessage(){
        super(true);
    }

    /**
     * The object ID on which the call is being made.
     */
    public int objectId;

    /**
     * The method ID used for look-up in the LocalObject.methods array.
     */
    public short methodId;

    /**
     * Invocation ID is used to identify a particular call if the calling
     * client needs the return value of the called RMI method.
     * This is set to zero if the method does not return a value.
     */
    public short invocationId;

    /**
     * Arguments of the remote method invocation.
     */
    public Object[] args;

    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("RemoteMethodCallMessage[objectID=").append(objectId).append(", methodID=")
          .append(methodId);
        if (args != null && args.length > 0){
            sb.append(", args={");
            for (Object arg : args){
                sb.append(arg.toString()).append(", ");
            }
            sb.setLength(sb.length()-2);
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }
}
