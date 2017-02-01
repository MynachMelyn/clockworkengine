
package com.clockwork.renderer.queue;

import com.clockwork.renderer.Camera;
import com.clockwork.scene.Geometry;
import com.clockwork.util.SortUtil;

/**
 * This class is a special purpose list of {@link Geometry} objects for render
 * queuing.
 *
 */
public class GeometryList {

    private static final int DEFAULT_SIZE = 32;

    private Geometry[] geometries;
    private Geometry[] geometries2;
    private int size;
    private GeometryComparator comparator;

    /**
     * Initializes the GeometryList to use the given {@link GeometryComparator}
     * to use for comparing geometries.
     * 
     * @param comparator The comparator to use.
     */
    public GeometryList(GeometryComparator comparator) {
        size = 0;
        geometries = new Geometry[DEFAULT_SIZE];
        geometries2 = new Geometry[DEFAULT_SIZE];
        this.comparator = comparator;
    }

    /**
     * Returns the GeometryComparator that this Geometry list uses
     * for sorting.
     */
    public GeometryComparator getComparator() {
        return comparator;
    }     

    /**
     * Set the camera that will be set on the geometry comparators 
     * via {@link GeometryComparator#setCamera(com.clockwork.renderer.Camera)}.
     * 
     * @param cam Camera to use for sorting.
     */
    public void setCamera(Camera cam){
        this.comparator.setCamera(cam);
    }

    /**
     * Returns the number of elements in this GeometryList.
     * 
     * @return Number of elements in the list
     */
    public int size(){
        return size;
    }

    /**
     * Returns the element at the given index.
     * 
     * @param index The index to lookup
     * @return Geometry at the index
     */
    public Geometry get(int index){
        return geometries[index];
    }

    /**
     * Adds a geometry to the list. 
     * List size is doubled if there is no room.
     *
     * @param g
     *            The geometry to add.
     */
    public void add(Geometry g) {
        if (size == geometries.length) {
            Geometry[] temp = new Geometry[size * 2];
            System.arraycopy(geometries, 0, temp, 0, size);
            geometries = temp; // original list replaced by double-size list
            
            geometries2 = new Geometry[size * 2];
        }
        geometries[size++] = g;
    }

    /**
     * Resets list size to 0.
     */
    public void clear() {
        for (int i = 0; i < size; i++){
            geometries[i] = null;
        }

        size = 0;
    }

    /**
     * Sorts the elements in the list according to their Comparator.
     */
    public void sort() {
        if (size > 1) {
            // sort the spatial list using the comparator
            
//            SortUtil.qsort(geometries, 0, size, comparator);
//            Arrays.sort(geometries, 0, size, comparator);            
            
            System.arraycopy(geometries, 0, geometries2, 0, size);
            SortUtil.msort(geometries2, geometries, 0, size-1, comparator);
            

        }
    }
}