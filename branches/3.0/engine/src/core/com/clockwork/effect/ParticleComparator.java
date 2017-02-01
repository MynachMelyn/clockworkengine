
package com.clockwork.effect;

import com.clockwork.renderer.Camera;
import java.util.Comparator;

@Deprecated
class ParticleComparator implements Comparator<Particle> {

    private Camera cam;

    public void setCamera(Camera cam){
        this.cam = cam;
    }

    public int compare(Particle p1, Particle p2) {
        return 0; // unused
        /*
        if (p1.life <= 0 || p2.life <= 0)
            return 0;

//        if (p1.life <= 0)
//            return 1;
//        else if (p2.life <= 0)
//            return -1;

        float d1 = p1.distToCam, d2 = p2.distToCam;

        if (d1 == -1){
            d1 = cam.distanceToNearPlane(p1.position);
            p1.distToCam = d1;
        }
        if (d2 == -1){
            d2 = cam.distanceToNearPlane(p2.position);
            p2.distToCam = d2;
        }

        if (d1 < d2)
            return 1;
        else if (d1 > d2)
            return -1;
        else
            return 0;
        */
    }
}