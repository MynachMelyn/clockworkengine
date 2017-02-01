
package com.clockwork.bullet.collision.shapes;

import com.clockwork.export.*;
import com.clockwork.math.Vector3f;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Object holds information about a jbullet CollisionShape to be able to reuse
 * CollisionShapes (as suggested in bullet manuals)
 * TODO: add static methods to create shapes from nodes (like jbullet-jme constructor)
 */
public abstract class CollisionShape implements Savable {

    protected long objectId = 0;
    protected Vector3f scale = new Vector3f(1, 1, 1);
    protected float margin = 0.0f;

    public CollisionShape() {
    }

//    /**
//     * used internally, not safe
//     */
//    public void calculateLocalInertia(long objectId, float mass) {
//        if (this.objectId == 0) {
//            return;
//        }
////        if (this instanceof MeshCollisionShape) {
////            vector.set(0, 0, 0);
////        } else {
//        calculateLocalInertia(objectId, this.objectId, mass);
////            objectId.calculateLocalInertia(mass, vector);
////        }
//    }
//    
//    private native void calculateLocalInertia(long objectId, long shapeId, float mass);

    /**
     * used internally
     */
    public long getObjectId() {
        return objectId;
    }

    /**
     * used internally
     */
    public void setObjectId(long id) {
        this.objectId = id;
    }

    public void setScale(Vector3f scale) {
        this.scale.set(scale);
        setLocalScaling(objectId, scale);
    }
    
    public Vector3f getScale() {
        return scale;
    }

    public float getMargin() {
        return getMargin(objectId);
    }
    
    private native float getMargin(long objectId);

    public void setMargin(float margin) {
        setMargin(objectId, margin);
        this.margin = margin;
    }
    
    private native void setLocalScaling(long obectId, Vector3f scale);
    
    private native void setMargin(long objectId, float margin);

    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(scale, "scale", new Vector3f(1, 1, 1));
        capsule.write(getMargin(), "margin", 0.0f);
    }

    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        this.scale = (Vector3f) capsule.readSavable("scale", new Vector3f(1, 1, 1));
        this.margin = capsule.readFloat("margin", 0.0f);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Finalizing CollisionShape {0}", Long.toHexString(objectId));
        finalizeNative(objectId);
    }

    private native void finalizeNative(long objectId);
}
