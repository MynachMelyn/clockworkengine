
package com.clockwork.bullet.joints;

import com.clockwork.bullet.objects.PhysicsRigidBody;
import com.clockwork.math.Matrix3f;
import com.clockwork.math.Vector3f;

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
public class SixDofSpringJoint extends SixDofJoint {

   final boolean       springEnabled[] = new boolean[6];
   final float equilibriumPoint[] = new float[6];
   final float springStiffness[] = new float[6];
   final float springDamping[] = new float[6]; // between 0 and 1 (1 == no damping)

    public SixDofSpringJoint() {
    }

    /**
     * @param pivotA local translation of the joint connection point in node A
     * @param pivotB local translation of the joint connection point in node B
     */
    public SixDofSpringJoint(PhysicsRigidBody nodeA, PhysicsRigidBody nodeB, Vector3f pivotA, Vector3f pivotB, Matrix3f rotA, Matrix3f rotB, boolean useLinearReferenceFrameA) {
        super(nodeA, nodeB, pivotA, pivotB, rotA, rotB, useLinearReferenceFrameA);
    }
    public void enableSpring(int index, boolean onOff) {
        enableSpring(objectId, index, onOff);
    }
    native void enableSpring(long objctId, int index, boolean onOff);

    public void setStiffness(int index, float stiffness) {
        setStiffness(objectId, index, stiffness);
    }
    native void setStiffness(long objctId, int index, float stiffness);

    public void setDamping(int index, float damping) {
        setDamping(objectId, index, damping);

    }
    native void setDamping(long objctId, int index, float damping);
    public void setEquilibriumPoint() { // set the current constraint position/orientation as an equilibrium point for all DOF
        setEquilibriumPoint(objectId);
    }
    native void setEquilibriumPoint(long objctId);
    public void setEquilibriumPoint(int index){ // set the current constraint position/orientation as an equilibrium point for given DOF
        setEquilibriumPoint(objectId, index);
    }
    native void setEquilibriumPoint(long objctId, int index);
    @Override
    native long createJoint(long objectIdA, long objectIdB, Vector3f pivotA, Matrix3f rotA, Vector3f pivotB, Matrix3f rotB, boolean useLinearReferenceFrameA);

}
