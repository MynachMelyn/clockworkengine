
package com.clockwork.terrain.heightmap;

import com.clockwork.math.Vector3f;

/**
 *
 */
@Deprecated
/**
 * @Deprecated in favor of TerrainGridTileLoader
 */
public interface HeightMapGrid {

    public HeightMap getHeightMapAt(Vector3f location);

    public void setSize(int size);

}
