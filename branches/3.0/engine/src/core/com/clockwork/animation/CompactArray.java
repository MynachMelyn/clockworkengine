
package com.clockwork.animation;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

/**
 * Object is indexed and stored in primitive float[]
 * @author Lim, YongHoon
 * @param <T>
 */
public abstract class CompactArray<T> {

    private Map<T, Integer> indexPool = new HashMap<T, Integer>();
    protected int[] index;
    protected float[] array;
    private boolean invalid;

    /**
     * Creates a compact array
     */
    public CompactArray() {
    }

    /**
     * create array using serialized data
     * @param compressedArray
     * @param index
     */
    public CompactArray(float[] compressedArray, int[] index) {
        this.array = compressedArray;
        this.index = index;
    }

    /**
     * Add objects.
     * They are serialized automatically when get() method is called.
     * @param objArray
     */
    public void add(T... objArray) {
        if (objArray == null || objArray.length == 0) {
            return;
        }
        invalid = true;
        int base = 0;
        if (index == null) {
            index = new int[objArray.length];
        } else {
            if (indexPool.isEmpty()) {
                throw new RuntimeException("Internal is already fixed");
            }
            base = index.length;

            int[] tmp = new int[base + objArray.length];
            System.arraycopy(index, 0, tmp, 0, index.length);
            index = tmp;
            //index = Arrays.copyOf(index, base+objArray.length);
        }
        for (int j = 0; j < objArray.length; j++) {
            T obj = objArray[j];
            if (obj == null) {
                index[base + j] = -1;
            } else {
                Integer i = indexPool.get(obj);
                if (i == null) {
                    i = indexPool.size();
                    indexPool.put(obj, i);
                }
                index[base + j] = i;
            }
        }
    }

    /**
     * release objects.
     * add() method call is not allowed anymore.
     */
    public void freeze() {
        serialize();
        indexPool.clear();
    }

    /**
     * @param index
     * @param value
     */
    public final void set(int index, T value) {
        int j = getCompactIndex(index);
        serialize(j, value);
    }

    /**
     * returns the object for the given index
     * @param index the index
     * @param store an object to store the result 
     * @return 
     */
    public final T get(int index, T store) {
        serialize();
        int j = getCompactIndex(index);
        return deserialize(j, store);
    }

    /**
     * return a float array of serialized data
     * @return 
     */
    public final float[] getSerializedData() {
        serialize();
        return array;
    }

    /**
     * serialize this compact array
     */
    public final void serialize() {
        if (invalid) {
            int newSize = indexPool.size() * getTupleSize();
            if (array == null || Array.getLength(array) < newSize) {
                array = ensureCapacity(array, newSize);
                for (Map.Entry<T, Integer> entry : indexPool.entrySet()) {
                    int i = entry.getValue();
                    T obj = entry.getKey();
                    serialize(i, obj);
                }
            }
            invalid = false;
        }
    }

    /**
     * @return compacted array's primitive size
     */
    protected final int getSerializedSize() {
        return Array.getLength(getSerializedData());
    }

    /**
     * Ensure the capacity for the given array and the given size
     * @param arr the array
     * @param size the size
     * @return 
     */
    protected float[] ensureCapacity(float[] arr, int size) {
        if (arr == null) {
            return new float[size];
        } else if (arr.length >= size) {
            return arr;
        } else {
            float[] tmp = new float[size];
            System.arraycopy(arr, 0, tmp, 0, arr.length);
            return tmp;
            //return Arrays.copyOf(arr, size);
        }
    }

    /**
     * retrun an array of indices for the given objects
     * @param objArray
     * @return 
     */
    public final int[] getIndex(T... objArray) {
        int[] index = new int[objArray.length];
        for (int i = 0; i < index.length; i++) {
            T obj = objArray[i];
            index[i] = obj != null ? indexPool.get(obj) : -1;
        }
        return index;
    }

    /**
     * returns the corresponding index in the compact array
     * @param objIndex
     * @return object index in the compacted object array
     */
    public int getCompactIndex(int objIndex) {
        return index != null ? index[objIndex] : objIndex;
    }

    /**
     * @return uncompressed object size
     */
    public final int getTotalObjectSize() {
        assert getSerializedSize() % getTupleSize() == 0;
        return index != null ? index.length : getSerializedSize() / getTupleSize();
    }

    /**
     * @return compressed object size
     */
    public final int getCompactObjectSize() {
        assert getSerializedSize() % getTupleSize() == 0;
        return getSerializedSize() / getTupleSize();
    }

    /**
     * decompress and return object array
     * @return decompress and return object array
     */
    public final T[] toObjectArray() {
        try {
            T[] compactArr = (T[]) Array.newInstance(getElementClass(), getSerializedSize() / getTupleSize());
            for (int i = 0; i < compactArr.length; i++) {
                compactArr[i] = getElementClass().newInstance();
                deserialize(i, compactArr[i]);
            }

            T[] objArr = (T[]) Array.newInstance(getElementClass(), getTotalObjectSize());
            for (int i = 0; i < objArr.length; i++) {
                int compactIndex = getCompactIndex(i);
                objArr[i] = compactArr[compactIndex];
            }
            return objArr;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * serialize object
     * @param compactIndex compacted object index
     * @param store
     */
    protected abstract void serialize(int compactIndex, T store);

    /**
     * deserialize object
     * @param compactIndex compacted object index
     * @param store
     */
    protected abstract T deserialize(int compactIndex, T store);

    /**
     * serialized size of one object element
     */
    protected abstract int getTupleSize();

    protected abstract Class<T> getElementClass();
}
