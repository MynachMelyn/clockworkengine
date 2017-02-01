
package com.clockwork.collision;

/**
 * Thrown by {@link Collidable} when the requested collision query could not
 * be completed because one of the collidables does not support colliding with
 * the other.
 * 
 */
public class UnsupportedCollisionException extends UnsupportedOperationException {

    public UnsupportedCollisionException(Throwable arg0) {
        super(arg0);
    }

    public UnsupportedCollisionException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public UnsupportedCollisionException(String arg0) {
        super(arg0);
    }

    public UnsupportedCollisionException() {
        super();
    }
    
}
