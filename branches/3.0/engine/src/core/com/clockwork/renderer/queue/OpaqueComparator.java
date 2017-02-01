
package com.clockwork.renderer.queue;

import com.clockwork.material.Material;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.Camera;
import com.clockwork.scene.Geometry;

public class OpaqueComparator implements GeometryComparator {

    private Camera cam;
    private final Vector3f tempVec  = new Vector3f();
    private final Vector3f tempVec2 = new Vector3f();

    public void setCamera(Camera cam){
        this.cam = cam;
    }

    public float distanceToCam(Geometry spat){
        if (spat == null)
            return Float.NEGATIVE_INFINITY;
 
        if (spat.queueDistance != Float.NEGATIVE_INFINITY)
                return spat.queueDistance;
 
        Vector3f camPosition = cam.getLocation();
        Vector3f viewVector = cam.getDirection(tempVec2);
        Vector3f spatPosition = null;
 
        if (spat.getWorldBound() != null){
            spatPosition = spat.getWorldBound().getCenter();
        }else{
            spatPosition = spat.getWorldTranslation();
        }
 
        spatPosition.subtract(camPosition, tempVec);
        spat.queueDistance = tempVec.dot(viewVector);
 
        return spat.queueDistance;
    }

    public int compare(Geometry o1, Geometry o2) {
        Material m1 = o1.getMaterial();
        Material m2 = o2.getMaterial();

        int compareResult = m2.getSortId() - m1.getSortId();
        if (compareResult == 0){
            // use the same shader.
            // sort front-to-back then.
            float d1 = distanceToCam(o1);
            float d2 = distanceToCam(o2);

            if (d1 == d2)
                return 0;
            else if (d1 < d2)
                return -1;
            else
                return 1;
        }else{
            return compareResult;
        }
    }

}
