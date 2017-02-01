
package com.clockwork.animation;

import com.clockwork.export.*;
import com.clockwork.math.Vector3f;
import java.io.IOException;

/**
 * Serialize and compress Vector3f[] by indexing same values
 * @author Lim, YongHoon
 */
public class CompactVector3Array extends CompactArray<Vector3f> implements Savable {

    /**
     * Creates a compact vector array
     */
    public CompactVector3Array() {
    }

    /**
     * creates a compact vector array
     * @param dataArray the data array
     * @param index the indices
     */
    public CompactVector3Array(float[] dataArray, int[] index) {
        super(dataArray, index);
    }

    @Override
    protected final int getTupleSize() {
        return 3;
    }

    @Override
    protected final Class<Vector3f> getElementClass() {
        return Vector3f.class;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        serialize();
        OutputCapsule out = ex.getCapsule(this);
        out.write(array, "array", null);
        out.write(index, "index", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        array = in.readFloatArray("array", null);
        index = in.readIntArray("index", null);
    }
    
    @Override
    protected void serialize(int i, Vector3f store) {
        int j = i*getTupleSize();
        array[j] = store.getX();
        array[j+1] = store.getY();
        array[j+2] = store.getZ();
    }

    @Override
    protected Vector3f deserialize(int i, Vector3f store) {
        int j = i*getTupleSize();
        store.set(array[j], array[j+1], array[j+2]);
        return store;
    }
}