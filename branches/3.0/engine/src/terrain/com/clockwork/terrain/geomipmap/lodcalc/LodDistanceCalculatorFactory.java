
package com.clockwork.terrain.geomipmap.lodcalc;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.terrain.geomipmap.TerrainPatch;
import java.io.IOException;

/**
 *
 * @deprecated phasing out
 */
public class LodDistanceCalculatorFactory implements LodCalculatorFactory {

    private float lodThresholdSize = 2.7f;
    private LodThreshold lodThreshold = null;


    public LodDistanceCalculatorFactory() {
    }
    
    public LodDistanceCalculatorFactory(LodThreshold lodThreshold) {
        this.lodThreshold = lodThreshold;
    }

    public LodCalculator createCalculator() {
        return new DistanceLodCalculator();
    }

    public LodCalculator createCalculator(TerrainPatch terrainPatch) {
        return new DistanceLodCalculator();
    }

    public void write(CWExporter ex) throws IOException {
		OutputCapsule c = ex.getCapsule(this);
		c.write(lodThreshold, "lodThreshold", null);
        c.write(lodThresholdSize, "lodThresholdSize", 2);
    }

    public void read(CWImporter im) throws IOException {
        InputCapsule c = im.getCapsule(this);
		lodThresholdSize = c.readFloat("lodThresholdSize", 2);
        lodThreshold = (LodThreshold) c.readSavable("lodThreshold", null);
    }

    @Override
    public LodDistanceCalculatorFactory clone() {
        LodDistanceCalculatorFactory clone = new LodDistanceCalculatorFactory();
        clone.lodThreshold = lodThreshold.clone();
        clone.lodThresholdSize = lodThresholdSize;
        return clone;
    }

}
