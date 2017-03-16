
package com.clockwork.scene.control;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Spatial;
import java.io.IOException;

/**
 * An abstract implementation of the Control interface.
 *
 */
public abstract class AbstractControl implements Control {

    protected boolean enabled = true;
    protected Spatial spatial;

    public AbstractControl(){
    }

    public void setSpatial(Spatial spatial) {
        if (this.spatial != null && spatial != null && spatial != this.spatial) {
            throw new IllegalStateException("This control has already been added to a Spatial");
        }   
        this.spatial = spatial;
    }
    
    public Spatial getSpatial(){
        return spatial;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * To be implemented in subclass.
     */
    protected abstract void controlUpdate(float tpf);

    /**
     * To be implemented in subclass.
     */
    protected abstract void controlRender(RenderManager rm, ViewPort vp);

    /**
     *  Default implementation of cloneForSpatial() that
     *  simply clones the control and sets the spatial.
     *  
     *  AbstractControl c = clone();
     *  c.spatial = null;
     *  c.setSpatial(spatial);
     *  
     *
     *  Controls that wish to be persisted must be Cloneable.
     */
    @Override
    public Control cloneForSpatial(Spatial spatial) {
        try {
            AbstractControl c = (AbstractControl)clone();
            c.spatial = null; // to keep setSpatial() from throwing an exception
            c.setSpatial(spatial);
            return c;
        } catch(CloneNotSupportedException e) {
            throw new RuntimeException( "Can't clone control for spatial", e );
        } 
    }

    public void update(float tpf) {
        if (!enabled)
            return;

        controlUpdate(tpf);
    }

    public void render(RenderManager rm, ViewPort vp) {
        if (!enabled)
            return;

        controlRender(rm, vp);
    }

    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(enabled, "enabled", true);
        oc.write(spatial, "spatial", null);
    }

    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        enabled = ic.readBoolean("enabled", true);
        spatial = (Spatial) ic.readSavable("spatial", null);
    }

}
