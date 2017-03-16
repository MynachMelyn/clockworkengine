
package com.clockwork.terrain.geomipmap;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.control.AbstractControl;
import com.clockwork.scene.control.Control;
import java.io.IOException;


/**
 * Handles the normal vector updates when the terrain changes heights.
 */
public class NormalRecalcControl extends AbstractControl {

    private TerrainQuad terrain;

    public NormalRecalcControl(){}

    public NormalRecalcControl(TerrainQuad terrain) {
        this.terrain = terrain;
    }

    @Override
    protected void controlUpdate(float tpf) {
        terrain.updateNormals();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    public Control cloneForSpatial(Spatial spatial) {
        NormalRecalcControl control = new NormalRecalcControl(terrain);
        control.setSpatial(spatial);
        control.setEnabled(true);
        return control;
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial instanceof TerrainQuad)
            this.terrain = (TerrainQuad)spatial;
    }

    public TerrainQuad getTerrain() {
        return terrain;
    }

    public void setTerrain(TerrainQuad terrain) {
        this.terrain = terrain;
    }

    @Override
    public void write(CWExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(terrain, "terrain", null);
    }

    @Override
    public void read(CWImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        terrain = (TerrainQuad) ic.readSavable("terrain", null);
    }

}
