
package com.clockwork.bullet.objects;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.PairCachingGhostObject;
import com.bulletphysics.linearmath.Transform;
import com.clockwork.bullet.collision.PhysicsCollisionObject;
import com.clockwork.bullet.collision.shapes.CollisionShape;
import com.clockwork.bullet.util.Converter;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Matrix3f;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Spatial;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * <i>From Bullet manual:</i><br>
 * GhostObject can keep track of all objects that are overlapping.
 * By default, this overlap is based on the AABB.
 * This is useful for creating a character controller,
 * collision sensors/triggers, explosions etc.<br>
 */
public class PhysicsGhostObject extends PhysicsCollisionObject {

    protected PairCachingGhostObject gObject;
    protected boolean locationDirty = false;
    //TEMP VARIABLES
    protected final Quaternion tmp_inverseWorldRotation = new Quaternion();
    protected Transform tempTrans = new Transform(Converter.convert(new Matrix3f()));
    private com.clockwork.math.Transform physicsLocation = new com.clockwork.math.Transform();
    protected javax.vecmath.Quat4f tempRot = new javax.vecmath.Quat4f();
    private List<PhysicsCollisionObject> overlappingObjects = new LinkedList<PhysicsCollisionObject>();

    public PhysicsGhostObject() {
    }

    public PhysicsGhostObject(CollisionShape shape) {
        collisionShape = shape;
        buildObject();
    }

    public PhysicsGhostObject(Spatial child, CollisionShape shape) {
        collisionShape = shape;
        buildObject();
    }

    protected void buildObject() {
        if (gObject == null) {
            gObject = new PairCachingGhostObject();
            gObject.setCollisionFlags(gObject.getCollisionFlags() | CollisionFlags.NO_CONTACT_RESPONSE);
        }
        gObject.setCollisionShape(collisionShape.getCShape());
        gObject.setUserPointer(this);
    }

    @Override
    public void setCollisionShape(CollisionShape collisionShape) {
        super.setCollisionShape(collisionShape);
        if (gObject == null) {
            buildObject();
        }else{
            gObject.setCollisionShape(collisionShape.getCShape());
        }
    }

    /**
     * Sets the physics object location
     * @param location the location of the actual physics object
     */
    public void setPhysicsLocation(Vector3f location) {
        gObject.getWorldTransform(tempTrans);
        Converter.convert(location, tempTrans.origin);
        gObject.setWorldTransform(tempTrans);
    }

    /**
     * Sets the physics object rotation
     * @param rotation the rotation of the actual physics object
     */
    public void setPhysicsRotation(Matrix3f rotation) {
        gObject.getWorldTransform(tempTrans);
        Converter.convert(rotation, tempTrans.basis);
        gObject.setWorldTransform(tempTrans);
    }

    /**
     * Sets the physics object rotation
     * @param rotation the rotation of the actual physics object
     */
    public void setPhysicsRotation(Quaternion rotation) {
        gObject.getWorldTransform(tempTrans);
        Converter.convert(rotation, tempTrans.basis);
        gObject.setWorldTransform(tempTrans);
    }

    /**
     * @return the physicsLocation
     */
    public com.clockwork.math.Transform getPhysicsTransform() {
        return physicsLocation;
    }

    /**
     * @return the physicsLocation
     */
    public Vector3f getPhysicsLocation(Vector3f trans) {
        if (trans == null) {
            trans = new Vector3f();
        }
        gObject.getWorldTransform(tempTrans);
        Converter.convert(tempTrans.origin, physicsLocation.getTranslation());
        return trans.set(physicsLocation.getTranslation());
    }

    /**
     * @return the physicsLocation
     */
    public Quaternion getPhysicsRotation(Quaternion rot) {
        if (rot == null) {
            rot = new Quaternion();
        }
        gObject.getWorldTransform(tempTrans);
        Converter.convert(tempTrans.getRotation(tempRot), physicsLocation.getRotation());
        return rot.set(physicsLocation.getRotation());
    }

    /**
     * @return the physicsLocation
     */
    public Matrix3f getPhysicsRotationMatrix(Matrix3f rot) {
        if (rot == null) {
            rot = new Matrix3f();
        }
        gObject.getWorldTransform(tempTrans);
        Converter.convert(tempTrans.getRotation(tempRot), physicsLocation.getRotation());
        return rot.set(physicsLocation.getRotation());
    }

    /**
     * @return the physicsLocation
     */
    public Vector3f getPhysicsLocation() {
        gObject.getWorldTransform(tempTrans);
        Converter.convert(tempTrans.origin, physicsLocation.getTranslation());
        return physicsLocation.getTranslation();
    }

    /**
     * @return the physicsLocation
     */
    public Quaternion getPhysicsRotation() {
        gObject.getWorldTransform(tempTrans);
        Converter.convert(tempTrans.getRotation(tempRot), physicsLocation.getRotation());
        return physicsLocation.getRotation();
    }

    public Matrix3f getPhysicsRotationMatrix() {
        gObject.getWorldTransform(tempTrans);
        Converter.convert(tempTrans.getRotation(tempRot), physicsLocation.getRotation());
        return physicsLocation.getRotation().toRotationMatrix();
    }

    /**
     * used internally
     */
    public PairCachingGhostObject getObjectId() {
        return gObject;
    }

    /**
     * destroys this PhysicsGhostNode and removes it from memory
     */
    public void destroy() {
    }

    /**
     * Another Object is overlapping with this GhostNode,
     * if and if only there CollisionShapes overlaps.
     * They could be both regular PhysicsRigidBodys or PhysicsGhostObjects.
     * @return All CollisionObjects overlapping with this GhostNode.
     */
    public List<PhysicsCollisionObject> getOverlappingObjects() {
        overlappingObjects.clear();
        for (com.bulletphysics.collision.dispatch.CollisionObject collObj : gObject.getOverlappingPairs()) {
            overlappingObjects.add((PhysicsCollisionObject) collObj.getUserPointer());
        }
        return overlappingObjects;
    }

    /**
     *
     * @return With how many other CollisionObjects this GhostNode is currently overlapping.
     */
    public int getOverlappingCount() {
        return gObject.getNumOverlappingObjects();
    }

    /**
     *
     * @param index The index of the overlapping Node to retrieve.
     * @return The Overlapping CollisionObject at the given index.
     */
    public PhysicsCollisionObject getOverlapping(int index) {
        return overlappingObjects.get(index);
    }

    public void setCcdSweptSphereRadius(float radius) {
        gObject.setCcdSweptSphereRadius(radius);
    }

    public void setCcdMotionThreshold(float threshold) {
        gObject.setCcdMotionThreshold(threshold);
    }

    public float getCcdSweptSphereRadius() {
        return gObject.getCcdSweptSphereRadius();
    }

    public float getCcdMotionThreshold() {
        return gObject.getCcdMotionThreshold();
    }

    public float getCcdSquareMotionThreshold() {
        return gObject.getCcdSquareMotionThreshold();
    }

    @Override
    public void write(JmeExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(getPhysicsLocation(new Vector3f()), "physicsLocation", new Vector3f());
        capsule.write(getPhysicsRotationMatrix(new Matrix3f()), "physicsRotation", new Matrix3f());
        capsule.write(getCcdMotionThreshold(), "ccdMotionThreshold", 0);
        capsule.write(getCcdSweptSphereRadius(), "ccdSweptSphereRadius", 0);
    }

    @Override
    public void read(JmeImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        buildObject();
        setPhysicsLocation((Vector3f) capsule.readSavable("physicsLocation", new Vector3f()));
        setPhysicsRotation(((Matrix3f) capsule.readSavable("physicsRotation", new Matrix3f())));
        setCcdMotionThreshold(capsule.readFloat("ccdMotionThreshold", 0));
        setCcdSweptSphereRadius(capsule.readFloat("ccdSweptSphereRadius", 0));
    }
}
