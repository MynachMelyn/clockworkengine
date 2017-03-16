
package com.clockwork.renderer.queue;

import com.clockwork.renderer.Camera;
import com.clockwork.scene.Geometry;
import java.util.Comparator;

/**
 * GeometryComparator is a special version of Comparator}
 * that is used to sort geometries for rendering in the RenderQueue}.
 * 
 */
public interface GeometryComparator extends Comparator<Geometry> {
    
    /**
     * Set the camera to use for sorting.
     * 
     * @param cam The camera to use for sorting
     */
    public void setCamera(Camera cam);
}
