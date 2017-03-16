
package com.clockwork.audio;

import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.Savable;
import com.clockwork.util.NativeObject;
import java.io.IOException;

public abstract class Filter extends NativeObject implements Savable {

    public Filter(){
        super();
    }
    
    protected Filter(int id){
        super(id);
    }
    
    public void write(CWExporter ex) throws IOException {
        // nothing to save
    }

    public void read(CWImporter im) throws IOException {
        // nothing to read
    }

    @Override
    public void resetObject() {
        this.id = -1;
        setUpdateNeeded();
    }

    @Override
    public void deleteObject(Object rendererObject) {
        ((AudioRenderer)rendererObject).deleteFilter(this);
    }

    @Override
    public abstract NativeObject createDestructableClone();

}
