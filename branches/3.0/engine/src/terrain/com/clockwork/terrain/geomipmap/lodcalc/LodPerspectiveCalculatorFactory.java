
package com.clockwork.terrain.geomipmap.lodcalc;

import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.renderer.Camera;
import com.clockwork.terrain.geomipmap.TerrainPatch;
import java.io.IOException;

/**
 * TODO: Make it work with multiple cameras
 * TODO: Fix the cracks when the lod differences are greater than 1
 * for two adjacent blocks.
 * @deprecated phasing out
 */
public class LodPerspectiveCalculatorFactory implements LodCalculatorFactory {

    private Camera cam;
    private float pixelError;

    public LodPerspectiveCalculatorFactory(Camera cam, float pixelError){
        this.cam = cam;
        this.pixelError = pixelError;
    }

    public LodCalculator createCalculator() {
        return new PerspectiveLodCalculator(cam, pixelError);
    }

    public LodCalculator createCalculator(TerrainPatch terrainPatch) {
        PerspectiveLodCalculator p = new PerspectiveLodCalculator(cam, pixelError);
        return p;
    }

    public void write(CWExporter ex) throws IOException {
    }

    public void read(CWImporter im) throws IOException {
    }

    @Override
    public LodCalculatorFactory clone() {
        try {
            return (LodCalculatorFactory) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

}
