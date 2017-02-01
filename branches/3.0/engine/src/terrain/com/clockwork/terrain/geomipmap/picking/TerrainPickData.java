
package com.clockwork.terrain.geomipmap.picking;

import com.clockwork.collision.CollisionResult;
import com.clockwork.terrain.geomipmap.TerrainPatch;

/**
 * Pick result on a terrain patch with the intersection on the bounding box
 * of that terrain patch.
 *
 */
public class TerrainPickData implements Comparable {

    protected TerrainPatch targetPatch;
    protected CollisionResult cr;

    public TerrainPickData() {
    }

    public TerrainPickData(TerrainPatch patch, CollisionResult cr) {
        this.targetPatch = patch;
        this.cr = cr;
    }

    public int compareTo(Object o) {
        if (o instanceof TerrainPickData) {
            TerrainPickData tpd = (TerrainPickData) o;
            if (this.cr.getDistance() < tpd.cr.getDistance())
                return -1;
            else if (this.cr.getDistance() == tpd.cr.getDistance())
                return 0;
            else
                return 1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TerrainPickData){
            return ((TerrainPickData)obj).compareTo(this) == 0;
        }
        return super.equals(obj);
    }
    
}
