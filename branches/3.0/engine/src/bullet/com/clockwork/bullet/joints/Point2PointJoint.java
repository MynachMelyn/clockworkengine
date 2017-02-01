
package com.clockwork.bullet.joints;

import com.clockwork.bullet.objects.PhysicsRigidBody;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Vector3f;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <i>From bullet manual:</i><br>
 * Point to point constraint, also known as ball socket joint limits the translation
 * so that the local pivot points of 2 rigidbodies match in worldspace.
 * A chain of rigidbodies can be connected using this constraint.
 */
public class Point2PointJoint extends PhysicsJoint {

    public Point2PointJoint() {
    }

    /**
     * @param pivotA local translation of the joint connection point in node A
     * @param pivotB local translation of the joint connection point in node B
     */
    public Point2PointJoint(PhysicsRigidBody nodeA, PhysicsRigidBody nodeB, Vector3f pivotA, Vector3f pivotB) {
        super(nodeA, nodeB, pivotA, pivotB);
        createJoint();
    }

    public void setDamping(float value) {
        setDamping(objectId, value);
    }

    private native void setDamping(long objectId, float value);

    public void setImpulseClamp(float value) {
        setImpulseClamp(objectId, value);
    }

    private native void setImpulseClamp(long objectId, float value);

    public void setTau(float value) {
        setTau(objectId, value);
    }

    private native void setTau(long objectId, float value);

    public float getDamping() {
        return getDamping(objectId);
    }

    private native float getDamping(long objectId);

    public float getImpulseClamp() {
        return getImpulseClamp(objectId);
    }

    private native float getImpulseClamp(long objectId);

    public float getTau() {
        return getTau(objectId);
    }

    private native float getTau(long objectId);

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule cap = ex.getCapsule(this);
        cap.write(getDamping(), "damping", 1.0f);
        cap.write(getTau(), "tau", 0.3f);
        cap.write(getImpulseClamp(), "impulseClamp", 0f);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        createJoint();
        InputCapsule cap = im.getCapsule(this);
        setDamping(cap.readFloat("damping", 1.0f));
        setDamping(cap.readFloat("tau", 0.3f));
        setDamping(cap.readFloat("impulseClamp", 0f));
    }

    protected void createJoint() {
        objectId = createJoint(nodeA.getObjectId(), nodeB.getObjectId(), pivotA, pivotB);
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Created Joint {0}", Long.toHexString(objectId));
    }

    private native long createJoint(long objectIdA, long objectIdB, Vector3f pivotA, Vector3f pivotB);
}
