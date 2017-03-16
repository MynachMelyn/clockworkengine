
package com.clockwork.bullet.collision.shapes;

import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.clockwork.bullet.util.Converter;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Plane;
import java.io.IOException;

/**
 *
 */
public class PlaneCollisionShape extends CollisionShape{
    private Plane plane;

    public PlaneCollisionShape() {
    }

    /**
     * Creates a plane Collision shape
     * @param plane the plane that defines the shape
     */
    public PlaneCollisionShape(Plane plane) {
        this.plane = plane;
        createShape();
    }

    public final Plane getPlane() {
        return plane;
    }

    public void write(CWExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(plane, "collisionPlane", new Plane());
    }

    public void read(CWImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);
        plane = (Plane) capsule.readSavable("collisionPlane", new Plane());
        createShape();
    }

    protected void createShape() {
        cShape = new StaticPlaneShape(Converter.convert(plane.getNormal()),plane.getConstant());
        cShape.setLocalScaling(Converter.convert(getScale()));
        cShape.setMargin(margin);
    }

}
