
package com.clockwork.renderer;

/**
 * RendererException is raised when a renderer encounters
 * a fatal rendering error.
 * 
 */
public class RendererException extends RuntimeException {
    
    /**
     * Creates a new instance of RendererException
     */
    public RendererException(String message){
        super(message);
    }
}
