
package com.clockwork.export;

/**
 * Specifies the version of the format for jME3 object (j3o) files.
 * 
 */
public final class FormatVersion {
    
    /**
     * Version number of the format
     */
    public static final int VERSION = 2;
    
    /**
     * Signature of the format. Currently "JME3" as ASCII
     */
    public static final int SIGNATURE = 0x4A4D4533;
    
    private FormatVersion(){
    }
}
