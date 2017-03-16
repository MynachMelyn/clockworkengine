
package com.clockwork.bullet.collision.shapes;

import com.bulletphysics.collision.shapes.BoxShape;
import com.clockwork.bullet.util.Converter;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Vector3f;
import java.io.IOException;

/**
 * Basic box collision shape
 */
public class BoxCollisionShape extends CollisionShape {

    private Vector3f halfExtents;

    public BoxCollisionShape() {
    }

    /**
     * creates a collision box from the given halfExtents
     * @param halfExtents the halfExtents of the CollisionBox
     */
    public BoxCollisionShape(Vector3f halfExtents) {
        this.halfExtents = halfExtents;
        createShape();
    }

    public final Vector3f getHalfExtents() {
        return halfExtents;
    }
    
    public void write(CWExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(halfExtents, "halfExtents", new Vector3f(1, 1, 1));
    }

    public void read(CWImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);
        Vector3f halfExtents = (Vector3f) capsule.readSavable("halfExtents", new Vector3f(1, 1, 1));
        this.halfExtents = halfExtents;
        createShape();
    }

    protected void createShape() {
        cShape = new BoxShape(Converter.convert(halfExtents));
        cShape.setLocalScaling(Converter.convert(getScale()));
        cShape.setMargin(margin);
    }

}
