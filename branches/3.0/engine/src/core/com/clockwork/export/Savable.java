
package com.clockwork.export;

import java.io.IOException;

/**
 * Savable is an interface for objects that can be serialised
 * using CW's serialisation system. 
 * 
 */
public interface Savable {
    void write(CWExporter ex) throws IOException;
    void read(CWImporter im) throws IOException;
}
