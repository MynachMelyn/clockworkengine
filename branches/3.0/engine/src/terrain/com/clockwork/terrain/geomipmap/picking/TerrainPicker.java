
package com.clockwork.terrain.geomipmap.picking;

import com.clockwork.collision.CollisionResults;
import com.clockwork.math.Ray;
import com.clockwork.math.Vector3f;

/**
 * Pick the location on the terrain from a given ray.
 *
 */
public interface TerrainPicker {

    /**
     * Ask for the point of intersection between the given ray and the terrain.
     *
     * @param worldPick
     *            our pick ray, in world space.
     * @return null if no pick is found. Otherwise it returns a Vector3f  populated with the pick
     *         coordinates.
     */
    public Vector3f getTerrainIntersection(final Ray worldPick, CollisionResults results);

}
