
package com.clockwork.bullet.joints;

import com.bulletphysics.dynamics.constraintsolver.Point2PointConstraint;
import com.clockwork.bullet.objects.PhysicsRigidBody;
import com.clockwork.bullet.util.Converter;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Vector3f;
import java.io.IOException;

/**
 * <i>From bullet manual:</i>
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
        ((Point2PointConstraint) constraint).setting.damping = value;
    }

    public void setImpulseClamp(float value) {
        ((Point2PointConstraint) constraint).setting.impulseClamp = value;
    }

    public void setTau(float value) {
        ((Point2PointConstraint) constraint).setting.tau = value;
    }

    public float getDamping() {
        return ((Point2PointConstraint) constraint).setting.damping;
    }

    public float getImpulseClamp() {
        return ((Point2PointConstraint) constraint).setting.impulseClamp;
    }

    public float getTau() {
        return ((Point2PointConstraint) constraint).setting.tau;
    }

    @Override
    public void write(CWExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule cap = ex.getCapsule(this);
        cap.write(getDamping(), "damping", 1.0f);
        cap.write(getTau(), "tau", 0.3f);
        cap.write(getImpulseClamp(), "impulseClamp", 0f);
    }

    @Override
    public void read(CWImporter im) throws IOException {
        super.read(im);
        createJoint();
        InputCapsule cap=im.getCapsule(this);
        setDamping(cap.readFloat("damping", 1.0f));
        setDamping(cap.readFloat("tau", 0.3f));
        setDamping(cap.readFloat("impulseClamp", 0f));
    }

    protected void createJoint() {
        constraint = new Point2PointConstraint(nodeA.getObjectId(), nodeB.getObjectId(), Converter.convert(pivotA), Converter.convert(pivotB));
    }
}
