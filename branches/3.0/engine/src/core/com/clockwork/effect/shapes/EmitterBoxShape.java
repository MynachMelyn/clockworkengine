
package com.clockwork.effect.shapes;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.FastMath;
import com.clockwork.math.Vector3f;
import java.io.IOException;

public class EmitterBoxShape implements EmitterShape {

    private Vector3f min, len;

    public EmitterBoxShape() {
    }

    public EmitterBoxShape(Vector3f min, Vector3f max) {
        if (min == null || max == null) {
            throw new IllegalArgumentException("min or max cannot be null");
        }

        this.min = min;
        this.len = new Vector3f();
        this.len.set(max).subtractLocal(min);
    }

    @Override
    public void getRandomPoint(Vector3f store) {
        store.x = min.x + len.x * FastMath.nextRandomFloat();
        store.y = min.y + len.y * FastMath.nextRandomFloat();
        store.z = min.z + len.z * FastMath.nextRandomFloat();
    }

    /**
     * This method fills the point with data.
     * It does not fill the normal.
     * @param store the variable to store the point data
     * @param normal not used in this class
     */
    @Override
    public void getRandomPointAndNormal(Vector3f store, Vector3f normal) {
        this.getRandomPoint(store);
    }

    @Override
    public EmitterShape deepClone() {
        try {
            EmitterBoxShape clone = (EmitterBoxShape) super.clone();
            clone.min = min.clone();
            clone.len = len.clone();
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    public Vector3f getMin() {
        return min;
    }

    public void setMin(Vector3f min) {
        this.min = min;
    }

    public Vector3f getLen() {
        return len;
    }

    public void setLen(Vector3f len) {
        this.len = len;
    }

    @Override
    public void write(CWExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(min, "min", null);
        oc.write(len, "length", null);
    }

    @Override
    public void read(CWImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        min = (Vector3f) ic.readSavable("min", null);
        len = (Vector3f) ic.readSavable("length", null);
    }
}
