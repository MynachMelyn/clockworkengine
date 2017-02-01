
package com.clockwork.animation;

import com.clockwork.export.*;
import com.clockwork.math.Quaternion;
import java.io.IOException;

/**
 * Serialize and compress {@link Quaternion}[] by indexing same values
 * It is converted to float[]
 * @author Lim, YongHoon
 */
public class CompactQuaternionArray extends CompactArray<Quaternion> implements Savable {

    /**
     * creates a compact Quaternion array
     */
    public CompactQuaternionArray() {
    }

    /**
     * creates a compact Quaternion array
     * @param dataArray the data array
     * @param index  the indices array
     */
    public CompactQuaternionArray(float[] dataArray, int[] index) {
        super(dataArray, index);
    }

    @Override
    protected final int getTupleSize() {
        return 4;
    }

    @Override
    protected final Class<Quaternion> getElementClass() {
        return Quaternion.class;
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
    protected void serialize(int i, Quaternion store) {
        int j = i * getTupleSize();
        array[j] = store.getX();
        array[j + 1] = store.getY();
        array[j + 2] = store.getZ();
        array[j + 3] = store.getW();
    }

    @Override
    protected Quaternion deserialize(int i, Quaternion store) {
        int j = i * getTupleSize();
        store.set(array[j], array[j + 1], array[j + 2], array[j + 3]);
        return store;
    }
}
