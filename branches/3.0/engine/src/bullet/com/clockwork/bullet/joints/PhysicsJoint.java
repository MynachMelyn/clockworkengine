
package com.clockwork.bullet.joints;

import com.clockwork.bullet.objects.PhysicsRigidBody;
import com.clockwork.export.*;
import com.clockwork.math.Vector3f;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>PhysicsJoint - Basic Phyiscs Joint</p>
 */
public abstract class PhysicsJoint implements Savable {

    protected long objectId = 0;
    protected PhysicsRigidBody nodeA;
    protected PhysicsRigidBody nodeB;
    protected Vector3f pivotA;
    protected Vector3f pivotB;
    protected boolean collisionBetweenLinkedBodys = true;

    public PhysicsJoint() {
    }

    /**
     * @param pivotA local translation of the joint connection point in node A
     * @param pivotB local translation of the joint connection point in node B
     */
    public PhysicsJoint(PhysicsRigidBody nodeA, PhysicsRigidBody nodeB, Vector3f pivotA, Vector3f pivotB) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.pivotA = pivotA;
        this.pivotB = pivotB;
        nodeA.addJoint(this);
        nodeB.addJoint(this);
    }

    public float getAppliedImpulse() {
        return getAppliedImpulse(objectId);
    }

    private native float getAppliedImpulse(long objectId);

    /**
     * @return the constraint
     */
    public long getObjectId() {
        return objectId;
    }

    /**
     * @return the collisionBetweenLinkedBodys
     */
    public boolean isCollisionBetweenLinkedBodys() {
        return collisionBetweenLinkedBodys;
    }

    /**
     * toggles collisions between linked bodys<br>
     * joint has to be removed from and added to PhyiscsSpace to apply this.
     * @param collisionBetweenLinkedBodys set to false to have no collisions between linked bodys
     */
    public void setCollisionBetweenLinkedBodys(boolean collisionBetweenLinkedBodys) {
        this.collisionBetweenLinkedBodys = collisionBetweenLinkedBodys;
    }

    public PhysicsRigidBody getBodyA() {
        return nodeA;
    }

    public PhysicsRigidBody getBodyB() {
        return nodeB;
    }

    public Vector3f getPivotA() {
        return pivotA;
    }

    public Vector3f getPivotB() {
        return pivotB;
    }

    /**
     * destroys this joint and removes it from its connected PhysicsRigidBodys joint lists
     */
    public void destroy() {
        getBodyA().removeJoint(this);
        getBodyB().removeJoint(this);
    }

    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(nodeA, "nodeA", null);
        capsule.write(nodeB, "nodeB", null);
        capsule.write(pivotA, "pivotA", null);
        capsule.write(pivotB, "pivotB", null);
    }

    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        this.nodeA = ((PhysicsRigidBody) capsule.readSavable("nodeA", new PhysicsRigidBody()));
        this.nodeB = (PhysicsRigidBody) capsule.readSavable("nodeB", new PhysicsRigidBody());
        this.pivotA = (Vector3f) capsule.readSavable("pivotA", new Vector3f());
        this.pivotB = (Vector3f) capsule.readSavable("pivotB", new Vector3f());
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Finalizing Joint {0}", Long.toHexString(objectId));
        finalizeNative(objectId);
    }

    private native void finalizeNative(long objectId);
}
