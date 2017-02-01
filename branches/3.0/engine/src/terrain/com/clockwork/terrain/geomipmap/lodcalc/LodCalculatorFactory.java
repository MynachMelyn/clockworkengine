
package com.clockwork.terrain.geomipmap.lodcalc;

import com.clockwork.export.Savable;
import com.clockwork.terrain.geomipmap.TerrainPatch;

/**
 * Creates LOD Calculator objects for the terrain patches.
 *
 * @deprecated phasing this out
 */
public interface LodCalculatorFactory extends Savable, Cloneable {

    public LodCalculator createCalculator();
    public LodCalculator createCalculator(TerrainPatch terrainPatch);

    public LodCalculatorFactory clone();
}
