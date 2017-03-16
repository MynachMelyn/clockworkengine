
package com.clockwork.export;

import java.io.IOException;

/**
 * NullSavable is an implementation of Savable with no data.
 * It is used for backward compatibility with versions of the J3O 
 * format that wrote Blender importer's "Properties" class.
 * 
 */
public class NullSavable implements Savable {
    public void write(CWExporter ex) throws IOException {
    }
    public void read(CWImporter im) throws IOException {
    }
}
