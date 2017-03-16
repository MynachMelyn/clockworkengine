
package com.clockwork.bullet.collision.shapes;

import com.clockwork.bullet.PhysicsSpace;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ConeCollisionShape extends CollisionShape {

    protected float radius;
    protected float height;
    protected int axis;

    public ConeCollisionShape() {
    }

    public ConeCollisionShape(float radius, float height, int axis) {
        this.radius = radius;
        this.height = height;
        this.axis = axis;
        createShape();
    }

    public ConeCollisionShape(float radius, float height) {
        this.radius = radius;
        this.height = height;
        this.axis = PhysicsSpace.AXIS_Y;
        createShape();
    }

    public float getRadius() {
        return radius;
    }

    public void write(CWExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(radius, "radius", 0.5f);
        capsule.write(height, "height", 0.5f);
        capsule.write(axis, "axis", PhysicsSpace.AXIS_Y);
    }

    public void read(CWImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);
        radius = capsule.readFloat("radius", 0.5f);
        height = capsule.readFloat("height", 0.5f);
        axis = capsule.readInt("axis", PhysicsSpace.AXIS_Y);
        createShape();
    }

    protected void createShape() {
        objectId = createShape(axis, radius, height);
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Created Shape {0}", Long.toHexString(objectId));
//        if (axis == PhysicsSpace.AXIS_X) {
//            objectId = new ConeShapeX(radius, height);
//        } else if (axis == PhysicsSpace.AXIS_Y) {
//            objectId = new ConeShape(radius, height);
//        } else if (axis == PhysicsSpace.AXIS_Z) {
//            objectId = new ConeShapeZ(radius, height);
//        }
//        objectId.setLocalScaling(Converter.convert(getScale()));
//        objectId.setMargin(margin);
        setScale(scale);
        setMargin(margin);
    }

    private native long createShape(int axis, float radius, float height);
}
