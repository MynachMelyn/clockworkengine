
package com.clockwork.scene.plugins.blender.exceptions;

/**
 * This exception is thrown when blend file data is somehow invalid.
 * 
 */
public class BlenderFileException extends Exception {

    private static final long serialVersionUID = 7573482836437866767L;

    /**
     * Constructor. Creates an exception with no description.
     */
    public BlenderFileException() {
        // this constructor has no message
    }

    /**
     * Constructor. Creates an exception containing the given message.
     * @param message
     *            the message describing the problem that occured
     */
    public BlenderFileException(String message) {
        super(message);
    }

    /**
     * Constructor. Creates an exception that is based upon other thrown object. It contains the whole stacktrace then.
     * @param throwable
     *            an exception/error that occured
     */
    public BlenderFileException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor. Creates an exception with both a message and stacktrace.
     * @param message
     *            the message describing the problem that occured
     * @param throwable
     *            an exception/error that occured
     */
    public BlenderFileException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
