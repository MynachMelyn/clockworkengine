
package com.clockwork.terrain.heightmap;

/**
 *
 * 
 */
public interface HeightMap {

    /**
     * getHeightMap returns the entire grid of height data.
     *
     * @return the grid of height data.
     */
    float[] getHeightMap();

    float[] getScaledHeightMap();

    /**
     * getInterpolatedHeight returns the height of a point that
     * does not fall directly on the height posts.
     *
     * @param x
     * the x coordinate of the point.
     * @param z
     * the y coordinate of the point.
     * @return the interpolated height at this point.
     */
    float getInterpolatedHeight(float x, float z);

    /**
     * getScaledHeightAtPoint returns the scaled value at the
     * point provided.
     *
     * @param x
     * the x (east/west) coordinate.
     * @param z
     * the z (north/south) coordinate.
     * @return the scaled value at (x, z).
     */
    float getScaledHeightAtPoint(int x, int z);

    /**
     * getSize returns the size of one side the height map. Where
     * the area of the height map is size x size.
     *
     * @return the size of a single side.
     */
    int getSize();

    /**
     * getTrueHeightAtPoint returns the non-scaled value at the
     * point provided.
     *
     * @param x
     * the x (east/west) coordinate.
     * @param z
     * the z (north/south) coordinate.
     * @return the value at (x,z).
     */
    float getTrueHeightAtPoint(int x, int z);

    /**
     * load populates the height map data. This is dependent on
     * the subclass's implementation.
     *
     * @return true if the load was successful, false otherwise.
     */
    boolean load();

    /**
     * setHeightAtPoint sets the height value for a given
     * coordinate. It is recommended that the height value be within the 0 - 255
     * range.
     *
     * @param height
     * the new height for the coordinate.
     * @param x
     * the x (east/west) coordinate.
     * @param z
     * the z (north/south) coordinate.
     */
    void setHeightAtPoint(float height, int x, int z);

    /**
     * setHeightScale sets the scale of the height values.
     * Typically, the height is a little too extreme and should be scaled to a
     * smaller value (i.e. 0.25), to produce cleaner slopes.
     *
     * @param scale
     * the scale to multiply height values by.
     */
    void setHeightScale(float scale);

    /**
     * setFilter sets the erosion value for the filter. This
     * value must be between 0 and 1, where 0.2 - 0.4 produces arguably the best
     * results.
     *
     * @param filter
     * the erosion value.
     * @throws Exception
     * @throws JmeException
     * if filter is less than 0 or greater than 1.
     */
    void setMagnificationFilter(float filter) throws Exception;

    /**
     * setSize sets the size of the terrain where the area is
     * size x size.
     *
     * @param size
     * the new size of the terrain.
     * @throws Exception
     *
     * @throws JmeException
     * if the size is less than or equal to zero.
     */
    void setSize(int size) throws Exception;

    /**
     * unloadHeightMap clears the data of the height map. This
     * insures it is ready for reloading.
     */
    void unloadHeightMap();

}
