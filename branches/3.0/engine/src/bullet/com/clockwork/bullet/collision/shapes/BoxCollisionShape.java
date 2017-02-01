
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
    
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(halfExtents, "halfExtents", new Vector3f(1, 1, 1));
    }

    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);
        Vector3f halfExtents = (Vector3f) capsule.readSavable("halfExtents", new Vector3f(1, 1, 1));
        this.halfExtents = halfExtents;
        createShape();
    }

    protected void createShape() {
        objectId = createShape(halfExtents);
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Created Shape {0}", Long.toHexString(objectId));
//        cShape = new BoxShape(Converter.convert(halfExtents));
        setScale(scale);
        setMargin(margin);
    }
    
    private native long createShape(Vector3f halfExtents);

}
