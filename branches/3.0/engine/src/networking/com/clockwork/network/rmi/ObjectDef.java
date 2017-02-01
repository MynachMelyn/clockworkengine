
package com.clockwork.network.rmi;


import com.clockwork.network.serializing.Serializable;
import java.lang.reflect.Method;

@Serializable
public class ObjectDef {

    /**
     * The object name, can be null if undefined.
     */
    public String   objectName;

    /**
     * Object ID
     */
    public int    objectId;

    /**
     * Methods of the implementation on the local client. Set to null
     * on remote clients.
     */
    public Method[] methods;

    /**
     * Method definitions of the implementation. Set to null on
     * the local client.
     */
    public MethodDef[] methodDefs;

    @Override
    public String toString(){
        return "ObjectDef[name=" + objectName + ", objectId=" + objectId+"]";
    }

}
