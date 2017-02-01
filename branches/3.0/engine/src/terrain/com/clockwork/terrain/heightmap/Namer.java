
package com.clockwork.terrain.heightmap;

/**
 *
 */
public interface Namer {

    /**
     * Gets a name for a heightmap tile given it's cell id
     * @param x
     * @param y
     * @return
     */
    public String getName(int x, int y);

}
