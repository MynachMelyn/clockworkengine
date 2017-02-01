
package com.clockwork.bullet.collision.shapes;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Vector3f;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basic cylinder collision shape
 */
public class CylinderCollisionShape extends CollisionShape {

    protected Vector3f halfExtents;
    protected int axis;

    public CylinderCollisionShape() {
    }

    /**
     * creates a cylinder shape from the given halfextents
     * @param halfExtents the halfextents to use
     */
    public CylinderCollisionShape(Vector3f halfExtents) {
        this.halfExtents = halfExtents;
        this.axis = 2;
        createShape();
    }

    /**
     * Creates a cylinder shape around the given axis from the given halfextents
     * @param halfExtents the halfextents to use
     * @param axis (0=X,1=Y,2=Z)
     */
    public CylinderCollisionShape(Vector3f halfExtents, int axis) {
        this.halfExtents = halfExtents;
        this.axis = axis;
        createShape();
    }

    public final Vector3f getHalfExtents() {
        return halfExtents;
    }

    public int getAxis() {
        return axis;
    }

    /**
     * WARNING - CompoundCollisionShape scaling has no effect.
     */
    @Override
    public void setScale(Vector3f scale) {
        Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "CylinderCollisionShape cannot be scaled");
    }

    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(halfExtents, "halfExtents", new Vector3f(0.5f, 0.5f, 0.5f));
        capsule.write(axis, "axis", 1);
    }

    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);
        halfExtents = (Vector3f) capsule.readSavable("halfExtents", new Vector3f(0.5f, 0.5f, 0.5f));
        axis = capsule.readInt("axis", 1);
        createShape();
    }

    protected void createShape() {
        objectId = createShape(axis, halfExtents);
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Created Shape {0}", Long.toHexString(objectId));
//        switch (axis) {
//            case 0:
//                objectId = new CylinderShapeX(Converter.convert(halfExtents));
//                break;
//            case 1:
//                objectId = new CylinderShape(Converter.convert(halfExtents));
//                break;
//            case 2:
//                objectId = new CylinderShapeZ(Converter.convert(halfExtents));
//                break;
//        }
//        objectId.setLocalScaling(Converter.convert(getScale()));
//        objectId.setMargin(margin);
        setScale(scale);
        setMargin(margin);
    }
    
    private native long createShape(int axis, Vector3f halfExtents);

}
