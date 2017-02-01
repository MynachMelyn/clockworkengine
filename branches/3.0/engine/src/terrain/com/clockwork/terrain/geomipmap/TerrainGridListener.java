
package com.clockwork.terrain.geomipmap;

import com.clockwork.math.Vector3f;

/**
 * Notifies the user of grid change events, such as moving to new grid cells.
 */
public interface TerrainGridListener {

    /**
     * Called whenever the camera has moved full grid cells. This triggers new tiles to load.
     * @param newCenter 
     */
    public void gridMoved(Vector3f newCenter);

    /**
     * Called when a TerrainQuad is attached to the scene and is visible (attached to the root TerrainGrid)
     * @param cell the cell that is moved into
     * @param quad the quad that was just attached
     */
    public void tileAttached( Vector3f cell, TerrainQuad quad );

    /**
     * Called when a TerrainQuad is detached from its TerrainGrid parent: it is no longer on the scene graph.
     * @param cell the cell that is moved into
     * @param quad the quad that was just detached
     */
    public void tileDetached( Vector3f cell, TerrainQuad quad );
}
