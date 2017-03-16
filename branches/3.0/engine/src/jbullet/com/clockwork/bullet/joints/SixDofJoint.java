
package com.clockwork.bullet.joints;

import com.bulletphysics.dynamics.constraintsolver.Generic6DofConstraint;
import com.bulletphysics.linearmath.Transform;
import com.clockwork.bullet.joints.motors.RotationalLimitMotor;
import com.clockwork.bullet.joints.motors.TranslationalLimitMotor;
import com.clockwork.bullet.objects.PhysicsRigidBody;
import com.clockwork.bullet.util.Converter;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Matrix3f;
import com.clockwork.math.Vector3f;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * <i>From bullet manual:</i>
 * This generic constraint can emulate a variety of standard constraints,
 * by configuring each of the 6 degrees of freedom (dof).
 * The first 3 dof axis are linear axis, which represent translation of rigidbodies,
 * and the latter 3 dof axis represent the angular motion. Each axis can be either locked,
 * free or limited. On construction of a new btGeneric6DofConstraint, all axis are locked.
 * Afterwards the axis can be reconfigured. Note that several combinations that
 * include free and/or limited angular degrees of freedom are undefined.
 */
public class SixDofJoint extends PhysicsJoint {

    private boolean useLinearReferenceFrameA = true;
    private LinkedList<RotationalLimitMotor> rotationalMotors = new LinkedList<RotationalLimitMotor>();
    private TranslationalLimitMotor translationalMotor;
    private Vector3f angularUpperLimit = new Vector3f(Vector3f.POSITIVE_INFINITY);
    private Vector3f angularLowerLimit = new Vector3f(Vector3f.NEGATIVE_INFINITY);
    private Vector3f linearUpperLimit = new Vector3f(Vector3f.POSITIVE_INFINITY);
    private Vector3f linearLowerLimit = new Vector3f(Vector3f.NEGATIVE_INFINITY);

    public SixDofJoint() {
    }

    /**
     * @param pivotA local translation of the joint connection point in node A
     * @param pivotB local translation of the joint connection point in node B
     */
    public SixDofJoint(PhysicsRigidBody nodeA, PhysicsRigidBody nodeB, Vector3f pivotA, Vector3f pivotB, Matrix3f rotA, Matrix3f rotB, boolean useLinearReferenceFrameA) {
        super(nodeA, nodeB, pivotA, pivotB);
        this.useLinearReferenceFrameA = useLinearReferenceFrameA;

        Transform transA = new Transform(Converter.convert(rotA));
        Converter.convert(pivotA, transA.origin);
        Converter.convert(rotA, transA.basis);

        Transform transB = new Transform(Converter.convert(rotB));
        Converter.convert(pivotB, transB.origin);
        Converter.convert(rotB, transB.basis);

        constraint = new Generic6DofConstraint(nodeA.getObjectId(), nodeB.getObjectId(), transA, transB, useLinearReferenceFrameA);
        gatherMotors();
    }

    /**
     * @param pivotA local translation of the joint connection point in node A
     * @param pivotB local translation of the joint connection point in node B
     */
    public SixDofJoint(PhysicsRigidBody nodeA, PhysicsRigidBody nodeB, Vector3f pivotA, Vector3f pivotB, boolean useLinearReferenceFrameA) {
        super(nodeA, nodeB, pivotA, pivotB);
        this.useLinearReferenceFrameA = useLinearReferenceFrameA;

        Transform transA = new Transform(Converter.convert(new Matrix3f()));
        Converter.convert(pivotA, transA.origin);

        Transform transB = new Transform(Converter.convert(new Matrix3f()));
        Converter.convert(pivotB, transB.origin);

        constraint = new Generic6DofConstraint(nodeA.getObjectId(), nodeB.getObjectId(), transA, transB, useLinearReferenceFrameA);
        gatherMotors();
    }

    private void gatherMotors() {
        for (int i = 0; i < 3; i++) {
            RotationalLimitMotor rmot = new RotationalLimitMotor(((Generic6DofConstraint) constraint).getRotationalLimitMotor(i));
            rotationalMotors.add(rmot);
        }
        translationalMotor = new TranslationalLimitMotor(((Generic6DofConstraint) constraint).getTranslationalLimitMotor());
    }

    /**
     * returns the TranslationalLimitMotor of this 6DofJoint which allows
     * manipulating the translational axis
     * @return the TranslationalLimitMotor
     */
    public TranslationalLimitMotor getTranslationalLimitMotor() {
        return translationalMotor;
    }

    /**
     * returns one of the three RotationalLimitMotors of this 6DofJoint which
     * allow manipulating the rotational axes
     * @param index the index of the RotationalLimitMotor
     * @return the RotationalLimitMotor at the given index
     */
    public RotationalLimitMotor getRotationalLimitMotor(int index) {
        return rotationalMotors.get(index);
    }

    public void setLinearUpperLimit(Vector3f vector) {
        linearUpperLimit.set(vector);
        ((Generic6DofConstraint) constraint).setLinearUpperLimit(Converter.convert(vector));
    }

    public void setLinearLowerLimit(Vector3f vector) {
        linearLowerLimit.set(vector);
        ((Generic6DofConstraint) constraint).setLinearLowerLimit(Converter.convert(vector));
    }

    public void setAngularUpperLimit(Vector3f vector) {
        angularUpperLimit.set(vector);
        ((Generic6DofConstraint) constraint).setAngularUpperLimit(Converter.convert(vector));
    }

    public void setAngularLowerLimit(Vector3f vector) {
        angularLowerLimit.set(vector);
        ((Generic6DofConstraint) constraint).setAngularLowerLimit(Converter.convert(vector));
    }

    @Override
    public void read(CWImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);

        Transform transA = new Transform(Converter.convert(new Matrix3f()));
        Converter.convert(pivotA, transA.origin);

        Transform transB = new Transform(Converter.convert(new Matrix3f()));
        Converter.convert(pivotB, transB.origin);
        constraint = new Generic6DofConstraint(nodeA.getObjectId(), nodeB.getObjectId(), transA, transB, useLinearReferenceFrameA);
        gatherMotors();

        setAngularUpperLimit((Vector3f) capsule.readSavable("angularUpperLimit", new Vector3f(Vector3f.POSITIVE_INFINITY)));
        setAngularLowerLimit((Vector3f) capsule.readSavable("angularLowerLimit", new Vector3f(Vector3f.NEGATIVE_INFINITY)));
        setLinearUpperLimit((Vector3f) capsule.readSavable("linearUpperLimit", new Vector3f(Vector3f.POSITIVE_INFINITY)));
        setLinearLowerLimit((Vector3f) capsule.readSavable("linearLowerLimit", new Vector3f(Vector3f.NEGATIVE_INFINITY)));

        for (int i = 0; i < 3; i++) {
            RotationalLimitMotor rotationalLimitMotor = getRotationalLimitMotor(i);
            rotationalLimitMotor.setBounce(capsule.readFloat("rotMotor" + i + "_Bounce", 0.0f));
            rotationalLimitMotor.setDamping(capsule.readFloat("rotMotor" + i + "_Damping", 1.0f));
            rotationalLimitMotor.setERP(capsule.readFloat("rotMotor" + i + "_ERP", 0.5f));
            rotationalLimitMotor.setHiLimit(capsule.readFloat("rotMotor" + i + "_HiLimit", Float.POSITIVE_INFINITY));
            rotationalLimitMotor.setLimitSoftness(capsule.readFloat("rotMotor" + i + "_LimitSoftness", 0.5f));
            rotationalLimitMotor.setLoLimit(capsule.readFloat("rotMotor" + i + "_LoLimit", Float.NEGATIVE_INFINITY));
            rotationalLimitMotor.setMaxLimitForce(capsule.readFloat("rotMotor" + i + "_MaxLimitForce", 300.0f));
            rotationalLimitMotor.setMaxMotorForce(capsule.readFloat("rotMotor" + i + "_MaxMotorForce", 0.1f));
            rotationalLimitMotor.setTargetVelocity(capsule.readFloat("rotMotor" + i + "_TargetVelocity", 0));
            rotationalLimitMotor.setEnableMotor(capsule.readBoolean("rotMotor" + i + "_EnableMotor", false));
        }
        getTranslationalLimitMotor().setAccumulatedImpulse((Vector3f) capsule.readSavable("transMotor_AccumulatedImpulse", Vector3f.ZERO));
        getTranslationalLimitMotor().setDamping(capsule.readFloat("transMotor_Damping", 1.0f));
        getTranslationalLimitMotor().setLimitSoftness(capsule.readFloat("transMotor_LimitSoftness", 0.7f));
        getTranslationalLimitMotor().setLowerLimit((Vector3f) capsule.readSavable("transMotor_LowerLimit", Vector3f.ZERO));
        getTranslationalLimitMotor().setRestitution(capsule.readFloat("transMotor_Restitution", 0.5f));
        getTranslationalLimitMotor().setUpperLimit((Vector3f) capsule.readSavable("transMotor_UpperLimit", Vector3f.ZERO));
    }

    @Override
    public void write(CWExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(angularUpperLimit, "angularUpperLimit", new Vector3f(Vector3f.POSITIVE_INFINITY));
        capsule.write(angularLowerLimit, "angularLowerLimit", new Vector3f(Vector3f.NEGATIVE_INFINITY));
        capsule.write(linearUpperLimit, "linearUpperLimit", new Vector3f(Vector3f.POSITIVE_INFINITY));
        capsule.write(linearLowerLimit, "linearLowerLimit", new Vector3f(Vector3f.NEGATIVE_INFINITY));
        int i = 0;
        for (Iterator<RotationalLimitMotor> it = rotationalMotors.iterator(); it.hasNext();) {
            RotationalLimitMotor rotationalLimitMotor = it.next();
            capsule.write(rotationalLimitMotor.getBounce(), "rotMotor" + i + "_Bounce", 0.0f);
            capsule.write(rotationalLimitMotor.getDamping(), "rotMotor" + i + "_Damping", 1.0f);
            capsule.write(rotationalLimitMotor.getERP(), "rotMotor" + i + "_ERP", 0.5f);
            capsule.write(rotationalLimitMotor.getHiLimit(), "rotMotor" + i + "_HiLimit", Float.POSITIVE_INFINITY);
            capsule.write(rotationalLimitMotor.getLimitSoftness(), "rotMotor" + i + "_LimitSoftness", 0.5f);
            capsule.write(rotationalLimitMotor.getLoLimit(), "rotMotor" + i + "_LoLimit", Float.NEGATIVE_INFINITY);
            capsule.write(rotationalLimitMotor.getMaxLimitForce(), "rotMotor" + i + "_MaxLimitForce", 300.0f);
            capsule.write(rotationalLimitMotor.getMaxMotorForce(), "rotMotor" + i + "_MaxMotorForce", 0.1f);
            capsule.write(rotationalLimitMotor.getTargetVelocity(), "rotMotor" + i + "_TargetVelocity", 0);
            capsule.write(rotationalLimitMotor.isEnableMotor(), "rotMotor" + i + "_EnableMotor", false);
            i++;
        }
        capsule.write(getTranslationalLimitMotor().getAccumulatedImpulse(), "transMotor_AccumulatedImpulse", Vector3f.ZERO);
        capsule.write(getTranslationalLimitMotor().getDamping(), "transMotor_Damping", 1.0f);
        capsule.write(getTranslationalLimitMotor().getLimitSoftness(), "transMotor_LimitSoftness", 0.7f);
        capsule.write(getTranslationalLimitMotor().getLowerLimit(), "transMotor_LowerLimit", Vector3f.ZERO);
        capsule.write(getTranslationalLimitMotor().getRestitution(), "transMotor_Restitution", 0.5f);
        capsule.write(getTranslationalLimitMotor().getUpperLimit(), "transMotor_UpperLimit", Vector3f.ZERO);
    }
}
