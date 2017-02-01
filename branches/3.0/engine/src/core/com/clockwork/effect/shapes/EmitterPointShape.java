
package com.clockwork.effect.shapes;

import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Vector3f;
import java.io.IOException;

public class EmitterPointShape implements EmitterShape {

    private Vector3f point;

    public EmitterPointShape() {
    }

    public EmitterPointShape(Vector3f point) {
        this.point = point;
    }

    @Override
    public EmitterShape deepClone() {
        try {
            EmitterPointShape clone = (EmitterPointShape) super.clone();
            clone.point = point.clone();
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    @Override
    public void getRandomPoint(Vector3f store) {
        store.set(point);
    }

    /**
     * This method fills the point with data.
     * It does not fill the normal.
     * @param store the variable to store the point data
     * @param normal not used in this class
     */
    @Override
    public void getRandomPointAndNormal(Vector3f store, Vector3f normal) {
        store.set(point);
    }

    public Vector3f getPoint() {
        return point;
    }

    public void setPoint(Vector3f point) {
        this.point = point;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(point, "point", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        this.point = (Vector3f) im.getCapsule(this).readSavable("point", null);
    }
}
