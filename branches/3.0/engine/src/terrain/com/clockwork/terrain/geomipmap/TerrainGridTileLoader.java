
package com.clockwork.terrain.geomipmap;

import com.clockwork.export.Savable;
import com.clockwork.math.Vector3f;

/**
 *
 */
public interface TerrainGridTileLoader extends Savable {

    public TerrainQuad getTerrainQuadAt(Vector3f location);

    public void setPatchSize(int patchSize);

    public void setQuadSize(int quadSize);
}
