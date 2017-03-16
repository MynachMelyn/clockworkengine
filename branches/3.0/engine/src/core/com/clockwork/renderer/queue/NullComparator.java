
package com.clockwork.renderer.queue;

import com.clockwork.renderer.Camera;
import com.clockwork.scene.Geometry;

/**
 * NullComparator does not sort geometries. They will be in
 * arbitrary order.
 * 
 */
public class NullComparator implements GeometryComparator {
    public int compare(Geometry o1, Geometry o2) {
        return 0;
    }

    public void setCamera(Camera cam) {
    }
}
