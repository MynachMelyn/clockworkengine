
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
 * Basic sphere collision shape
 */
public class SphereCollisionShape extends CollisionShape {

    protected float radius;

    public SphereCollisionShape() {
    }

    /**
     * creates a SphereCollisionShape with the given radius
     * @param radius
     */
    public SphereCollisionShape(float radius) {
        this.radius = radius;
        createShape();
    }

    public float getRadius() {
        return radius;
    }

    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(radius, "radius", 0.5f);
    }

    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);
        radius = capsule.readFloat("radius", 0.5f);
        createShape();
    }

    /**
     * WARNING - CompoundCollisionShape scaling has no effect.
     */
    @Override
    public void setScale(Vector3f scale) {
        Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "SphereCollisionShape cannot be scaled");
    }

    protected void createShape() {
        objectId = createShape(radius);
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Created Shape {0}", Long.toHexString(objectId));
//        new SphereShape(radius);
//        objectId.setLocalScaling(Converter.convert(getScale()));
//        objectId.setMargin(margin);
        setScale(scale);
        setMargin(margin);
    }
    
    private native long createShape(float radius);

}
