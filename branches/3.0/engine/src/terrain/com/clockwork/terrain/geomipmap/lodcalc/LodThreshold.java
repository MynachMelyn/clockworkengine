
package com.clockwork.terrain.geomipmap.lodcalc;

import com.clockwork.export.Savable;


/**
 * Calculates the LOD value based on where the camera is.
 * This is plugged into the Terrain system and any terrain
 * using LOD will use this to determine when a patch of the 
 * terrain should switch Levels of Detail.
 * 
 * 
 */
public interface LodThreshold extends Savable, Cloneable {

    /**
     * A distance of how far between each LOD threshold.
     */
    public float getLodDistanceThreshold();

    public LodThreshold clone();
}
