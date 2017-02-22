
package com.clockwork.material.plugins;

import com.clockwork.util.blockparser.Statement;
import java.io.IOException;

/**
 * Custom Exception to report a j3md Material definition file parsing error.
 * This exception reports the line number where the error occured.
 *
 * 
 */
public class MatParseException extends IOException {

    /**
     * creates a MatParseException
     *
     * @param expected the expected value
     * @param got the actual value
     * @param statement the read statement
     */
    public MatParseException(String expected, String got, Statement statement) {
        super("Error On line " + statement.getLineNumber() + " : " + statement.getLine() + "\n->Expected " + (expected == null ? "a statement" : expected) + ", got '" + got + "'!");

    }

    /**
     * creates a MatParseException
     *
     * @param text the error message
     * @param statement the statement where the error occur
     */
    public MatParseException(String text, Statement statement) {
        super("Error On line " + statement.getLineNumber() + " : " + statement.getLine() + "\n->" + text);
    }

    /**
     * creates a MatParseException
     *
     * @param expected the expected value
     * @param got the actual value
     * @param statement the read statement
     * @param cause the embed exception that occured
     */
    public MatParseException(String expected, String got, Statement statement, Throwable cause) {
        super("Error On line " + statement.getLineNumber() + " : " + statement.getLine() + "\n->Expected " + (expected == null ? "a statement" : expected) + ", got '" + got + "'!", cause);

    }

    /**
     * creates a MatParseException
     *
     * @param text the error message
     * @param statement the statement where the error occur
     * @param cause the embed exception that occured
     */
    public MatParseException(String text, Statement statement, Throwable cause) {
        super("Error On line " + statement.getLineNumber() + " : " + statement.getLine() + "\n->" + text, cause);
    }
}
