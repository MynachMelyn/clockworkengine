
package com.clockwork.bullet.collision.shapes;

import com.clockwork.bullet.util.Converter;
import com.clockwork.export.*;
import com.clockwork.math.Vector3f;
import java.io.IOException;

/**
 * This Object holds information about a jbullet CollisionShape to be able to reuse
 * CollisionShapes (as suggested in bullet manuals)
 * TODO: add static methods to create shapes from nodes (like jbullet-CW constructor)
 */
public abstract class CollisionShape implements Savable {

    protected com.bulletphysics.collision.shapes.CollisionShape cShape;
    protected Vector3f scale = new Vector3f(1, 1, 1);
    protected float margin = 0.0f;

    public CollisionShape() {
    }

    /**
     * used internally, not safe
     */
    public void calculateLocalInertia(float mass, javax.vecmath.Vector3f vector) {
        if (cShape == null) {
            return;
        }
        if (this instanceof MeshCollisionShape) {
            vector.set(0, 0, 0);
        } else {
            cShape.calculateLocalInertia(mass, vector);
        }
    }

    /**
     * used internally
     */
    public com.bulletphysics.collision.shapes.CollisionShape getCShape() {
        return cShape;
    }

    /**
     * used internally
     */
    public void setCShape(com.bulletphysics.collision.shapes.CollisionShape cShape) {
        this.cShape = cShape;
    }

    public void setScale(Vector3f scale) {
        this.scale.set(scale);
        cShape.setLocalScaling(Converter.convert(scale));
    }

    public float getMargin() {
        return cShape.getMargin();
    }

    public void setMargin(float margin) {
        cShape.setMargin(margin);
        this.margin = margin;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void write(CWExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(scale, "scale", new Vector3f(1, 1, 1));
        capsule.write(getMargin(), "margin", 0.0f);
    }

    public void read(CWImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        this.scale = (Vector3f) capsule.readSavable("scale", new Vector3f(1, 1, 1));
        this.margin = capsule.readFloat("margin", 0.0f);
    }
}
