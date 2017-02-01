
package com.clockwork.scene.control;

import com.clockwork.export.Savable;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Spatial;

/**
 * An interface for scene-graph controls. 
 * <p>
 * <code>Control</code>s are used to specify certain update and render logic
 * for a {@link Spatial}. 
 *
 */
public interface Control extends Savable {

    /**
     * Creates a clone of the Control, the given Spatial is the cloned
     * version of the spatial to which this control is attached to.
     * @param spatial
     * @return A clone of this control for the spatial
     */
    public Control cloneForSpatial(Spatial spatial);

    /**
     * @param spatial the spatial to be controlled. This should not be called
     * from user code.
     */
    public void setSpatial(Spatial spatial);

    /**
     * Updates the control. This should not be called from user code.
     * @param tpf Time per frame.
     */
    public void update(float tpf);

    /**
     * Should be called prior to queuing the spatial by the RenderManager. This
     * should not be called from user code.
     *
     * @param rm
     * @param vp
     */
    public void render(RenderManager rm, ViewPort vp);
}
