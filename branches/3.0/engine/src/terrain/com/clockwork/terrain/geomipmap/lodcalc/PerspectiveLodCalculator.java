
package com.clockwork.terrain.geomipmap.lodcalc;

import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.math.FastMath;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.Camera;
import com.clockwork.terrain.geomipmap.TerrainPatch;
import com.clockwork.terrain.geomipmap.UpdatedTerrainPatch;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class PerspectiveLodCalculator implements LodCalculator {

    private Camera cam;
    private float pixelError;
    private boolean turnOffLod = false;

    public PerspectiveLodCalculator() {}
    
    public PerspectiveLodCalculator(Camera cam, float pixelError) {
        this.cam = cam;
        this.pixelError = pixelError;
    }

    /**
     * This computes the "C" value in the geomipmapping paper.
     * See section "2.3.1.2 Pre-calculating d"
     * 
     * @param cam
     * @param pixelLimit
     * @return
     */
    private float getCameraConstant(Camera cam, float pixelLimit){
        float n = cam.getFrustumNear();
        float t = FastMath.abs(cam.getFrustumTop());
        float A = n / t;
        float v_res = cam.getHeight();
        float T = (2f * pixelLimit) / v_res;
        return A / T;
    }
    
    public boolean calculateLod(TerrainPatch patch, List<Vector3f> locations, HashMap<String, UpdatedTerrainPatch> updates) {
        if (turnOffLod) {
            // set to full detail
            int prevLOD = patch.getLod();
            UpdatedTerrainPatch utp = updates.get(patch.getName());
            if (utp == null) {
                utp = new UpdatedTerrainPatch(patch);
                updates.put(utp.getName(), utp);
            }
            utp.setNewLod(0);
            utp.setPreviousLod(prevLOD);
            //utp.setReIndexNeeded(true);
            return true;
        }
        
        float[] lodEntropies = patch.getLodEntropies();
        float cameraConstant = getCameraConstant(cam, pixelError);
        
        Vector3f patchPos = getCenterLocation(patch);

        // vector from camera to patch
        //Vector3f toPatchDir = locations.get(0).subtract(patchPos).normalizeLocal();
        //float facing = cam.getDirection().dot(toPatchDir);
        float distance = patchPos.distance(locations.get(0));

        // go through each lod level to find the one we are in
        for (int i = 0; i <= patch.getMaxLod(); i++) {
            if (distance < lodEntropies[i] * cameraConstant || i == patch.getMaxLod()){
                boolean reIndexNeeded = false;
                if (i != patch.getLod()) {
                    reIndexNeeded = true;
//                    System.out.println("lod change: "+lod+" > "+i+"    dist: "+distance);
                }
                int prevLOD = patch.getLod();

                
                UpdatedTerrainPatch utp = updates.get(patch.getName());
                if (utp == null) {
                    utp = new UpdatedTerrainPatch(patch);//save in here, do not update actual variables
                    updates.put(utp.getName(), utp);
                }
                utp.setNewLod(i);
                utp.setPreviousLod(prevLOD);
                //utp.setReIndexNeeded(reIndexNeeded);
                return reIndexNeeded;
            }
        }

        return false;
    }

    public Vector3f getCenterLocation(TerrainPatch patch) {
        Vector3f loc = patch.getWorldTranslation().clone();
        loc.x += patch.getSize() / 2;
        loc.z += patch.getSize() / 2;
        return loc;
    }

    @Override
    public LodCalculator clone() {
        try {
            return (LodCalculator) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    public void write(JmeExporter ex) throws IOException {
        
    }

    public void read(JmeImporter im) throws IOException {
    }

    public boolean usesVariableLod() {
        return true;
    }

    public float getPixelError() {
        return pixelError;
    }

    public void setPixelError(float pixelError) {
        this.pixelError = pixelError;
    }

    public void setCam(Camera cam) {
        this.cam = cam;
    }

    public void turnOffLod() {
        turnOffLod = true;
    }
    
    public boolean isLodOff() {
        return turnOffLod;
    }
    
    public void turnOnLod() {
        turnOffLod = false;
    }
    
}
