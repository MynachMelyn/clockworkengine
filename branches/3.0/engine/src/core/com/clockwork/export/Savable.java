
package com.clockwork.export;

import java.io.IOException;

/**
 * <code>Savable</code> is an interface for objects that can be serialised
 * using jME's serialisation system. 
 * 
 */
public interface Savable {
    void write(JmeExporter ex) throws IOException;
    void read(JmeImporter im) throws IOException;
}
