
package com.clockwork.renderer.queue;

import com.clockwork.renderer.Camera;
import com.clockwork.scene.Geometry;

/**
 * <code>GuiComparator</code> sorts geometries back-to-front based
 * on their Z position.
 *
 */
public class GuiComparator implements GeometryComparator {

    public int compare(Geometry o1, Geometry o2) {
        float z1 = o1.getWorldTranslation().getZ();
        float z2 = o2.getWorldTranslation().getZ();
        if (z1 > z2)
            return 1;
        else if (z1 < z2)
            return -1;
        else
            return 0;
    }

    public void setCamera(Camera cam) {
    }

}
