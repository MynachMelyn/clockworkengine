
package com.clockwork.scene.plugins.blender.file;

import com.clockwork.scene.plugins.blender.exceptions.BlenderFileException;

/**
 * An array that can be dynamically modified/
 * @author Marcin Roguski
 * @param <T>
 *            the type of stored data in the array
 */
public class DynamicArray<T> implements Cloneable {

    /** An array object that holds the required data. */
    private T[]   array;
    /**
     * This table holds the sizes of dimetions of the dynamic table. It's length specifies the table dimension or a
     * pointer level. For example: if tableSizes.length == 3 then it either specifies a dynamic table of fixed lengths:
     * dynTable[a][b][c], where a,b,c are stored in the tableSizes table.
     */
    private int[] tableSizes;

    /**
     * Constructor. Builds an empty array of the specified sizes.
     * @param tableSizes
     *            the sizes of the table
     * @throws BlenderFileException
     *             an exception is thrown if one of the sizes is not a positive number
     */
    public DynamicArray(int[] tableSizes, T[] data) throws BlenderFileException {
        this.tableSizes = tableSizes;
        int totalSize = 1;
        for (int size : tableSizes) {
            if (size <= 0) {
                throw new BlenderFileException("The size of the table must be positive!");
            }
            totalSize *= size;
        }
        if (totalSize != data.length) {
            throw new IllegalArgumentException("The size of the table does not match the size of the given data!");
        }
        this.array = data;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * This method returns a value on the specified position. The dimension of the table is not taken into
     * consideration.
     * @param position
     *            the position of the data
     * @return required data
     */
    public T get(int position) {
        return array[position];
    }

    /**
     * This method returns a value on the specified position in multidimensional array. Be careful not to exceed the
     * table boundaries. Check the table's dimension first.
     * @param position
     *            the position of the data indices of data position
     * @return required data required data
     */
    public T get(int... position) {
        if (position.length != tableSizes.length) {
            throw new ArrayIndexOutOfBoundsException("The table accepts " + tableSizes.length + " indexing number(s)!");
        }
        int index = 0;
        for (int i = 0; i < position.length - 1; ++i) {
            index += position[i] * tableSizes[i + 1];
        }
        index += position[position.length - 1];
        return array[index];
    }

    /**
     * This method returns the total amount of data stored in the array.
     * @return the total amount of data stored in the array
     */
    public int getTotalSize() {
        return array.length;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (array instanceof Character[]) {// in case of character array we convert it to String
            for (int i = 0; i < array.length && (Character) array[i] != '\0'; ++i) {// strings are terminater with '0'
                result.append(array[i]);
            }
        } else {
            result.append('[');
            for (int i = 0; i < array.length; ++i) {
                result.append(array[i].toString());
                if (i + 1 < array.length) {
                    result.append(',');
                }
            }
            result.append(']');
        }
        return result.toString();
    }
}