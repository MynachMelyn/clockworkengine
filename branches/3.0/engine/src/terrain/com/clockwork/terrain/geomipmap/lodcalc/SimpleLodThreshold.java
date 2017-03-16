
package com.clockwork.terrain.geomipmap.lodcalc;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.terrain.Terrain;
import com.clockwork.terrain.geomipmap.TerrainQuad;
import java.io.IOException;


/**
 * Just multiplies the terrain patch size by 2. So every two
 * patches away the camera is, the LOD changes.
 * 
 * Set it higher to have the LOD change less frequently.
 * 
 * 
 */
public class SimpleLodThreshold implements LodThreshold {
	
    private int size; // size of a terrain patch
    private float lodMultiplier = 2;

    
    public SimpleLodThreshold() {
    }
    
    public SimpleLodThreshold(Terrain terrain) {
        if (terrain instanceof TerrainQuad)
            this.size = ((TerrainQuad)terrain).getPatchSize();
    }

    public SimpleLodThreshold(int patchSize, float lodMultiplier) {
        this.size = patchSize;
    }

    public float getLodMultiplier() {
        return lodMultiplier;
    }

    public void setLodMultiplier(float lodMultiplier) {
        this.lodMultiplier = lodMultiplier;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
	

    public float getLodDistanceThreshold() {
        return size*lodMultiplier;
    }

    public void write(CWExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(size, "size", 16);
        oc.write(lodMultiplier, "lodMultiplier", 2);
    }

    public void read(CWImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        size = ic.readInt("size", 16);
        lodMultiplier = ic.readInt("lodMultiplier", 2);
    }

    @Override
    public LodThreshold clone() {
        SimpleLodThreshold clone = new SimpleLodThreshold();
        clone.size = size;
        clone.lodMultiplier = lodMultiplier;
        
        return clone;
    }

    @Override
    public String toString() {
        return "SimpleLodThreshold "+size+", "+lodMultiplier;
    }
}
