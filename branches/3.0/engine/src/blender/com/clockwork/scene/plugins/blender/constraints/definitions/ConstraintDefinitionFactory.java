
package com.clockwork.scene.plugins.blender.constraints.definitions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.clockwork.scene.plugins.blender.BlenderContext;
import com.clockwork.scene.plugins.blender.exceptions.BlenderFileException;
import com.clockwork.scene.plugins.blender.file.Structure;

public class ConstraintDefinitionFactory {
    private static final Map<String, Class<? extends ConstraintDefinition>> CONSTRAINT_CLASSES      = new HashMap<String, Class<? extends ConstraintDefinition>>();
    static {
        CONSTRAINT_CLASSES.put("bDistLimitConstraint", ConstraintDefinitionDistLimit.class);
        CONSTRAINT_CLASSES.put("bLocateLikeConstraint", ConstraintDefinitionLocLike.class);
        CONSTRAINT_CLASSES.put("bLocLimitConstraint", ConstraintDefinitionLocLimit.class);
        CONSTRAINT_CLASSES.put("bNullConstraint", ConstraintDefinitionNull.class);
        CONSTRAINT_CLASSES.put("bRotateLikeConstraint", ConstraintDefinitionRotLike.class);
        CONSTRAINT_CLASSES.put("bRotLimitConstraint", ConstraintDefinitionRotLimit.class);
        CONSTRAINT_CLASSES.put("bSizeLikeConstraint", ConstraintDefinitionSizeLike.class);
        CONSTRAINT_CLASSES.put("bSizeLimitConstraint", ConstraintDefinitionSizeLimit.class);
    }

    private static final Map<String, String>                                UNSUPPORTED_CONSTRAINTS = new HashMap<String, String>();
    static {
        UNSUPPORTED_CONSTRAINTS.put("bActionConstraint", "Action");
        UNSUPPORTED_CONSTRAINTS.put("bChildOfConstraint", "Child of");
        UNSUPPORTED_CONSTRAINTS.put("bClampToConstraint", "Clamp to");
        UNSUPPORTED_CONSTRAINTS.put("bFollowPathConstraint", "Follow path");
        UNSUPPORTED_CONSTRAINTS.put("bKinematicConstraint", "Inverse kinematic");
        UNSUPPORTED_CONSTRAINTS.put("bLockTrackConstraint", "Lock track");
        UNSUPPORTED_CONSTRAINTS.put("bMinMaxConstraint", "Min max");
        UNSUPPORTED_CONSTRAINTS.put("bPythonConstraint", "Python/Script");
        UNSUPPORTED_CONSTRAINTS.put("bRigidBodyJointConstraint", "Rigid body joint");
        UNSUPPORTED_CONSTRAINTS.put("bShrinkWrapConstraint", "Shrinkwrap");
        UNSUPPORTED_CONSTRAINTS.put("bStretchToConstraint", "Stretch to");
        UNSUPPORTED_CONSTRAINTS.put("bTransformConstraint", "Transform");
        // Blender 2.50+
        UNSUPPORTED_CONSTRAINTS.put("bSplineIKConstraint", "Spline inverse kinematics");
        UNSUPPORTED_CONSTRAINTS.put("bDampTrackConstraint", "Damp track");
        UNSUPPORTED_CONSTRAINTS.put("bPivotConstraint", "Pivot");
        // Blender 2.56+
        UNSUPPORTED_CONSTRAINTS.put("bTrackToConstraint", "Track to");
        UNSUPPORTED_CONSTRAINTS.put("bSameVolumeConstraint", "Same volume");
        UNSUPPORTED_CONSTRAINTS.put("bTransLikeConstraint", "Trans like");
        // Blender 2.62+
        UNSUPPORTED_CONSTRAINTS.put("bCameraSolverConstraint", "Camera solver");
        UNSUPPORTED_CONSTRAINTS.put("bObjectSolverConstraint", "Object solver");
        UNSUPPORTED_CONSTRAINTS.put("bFollowTrackConstraint", "Follow track");
    }

    /**
     * This method creates the constraint instance.
     * 
     * @param constraintStructure
     *            the constraint's structure (bConstraint clss in blender 2.49).
     *            If the value is null the NullConstraint is created.
     * @param blenderContext
     *            the blender context
     * @throws BlenderFileException
     *             this exception is thrown when the blender file is somehow
     *             corrupted
     */
    public static ConstraintDefinition createConstraintDefinition(Structure constraintStructure, Long ownerOMA, BlenderContext blenderContext) throws BlenderFileException {
        if (constraintStructure == null) {
            return new ConstraintDefinitionNull(null, ownerOMA, blenderContext);
        }
        String constraintClassName = constraintStructure.getType();
        Class<? extends ConstraintDefinition> constraintDefinitionClass = CONSTRAINT_CLASSES.get(constraintClassName);
        if (constraintDefinitionClass != null) {
            try {
                return (ConstraintDefinition) constraintDefinitionClass.getDeclaredConstructors()[0].newInstance(constraintStructure, ownerOMA, blenderContext);
            } catch (IllegalArgumentException e) {
                throw new BlenderFileException(e.getLocalizedMessage(), e);
            } catch (SecurityException e) {
                throw new BlenderFileException(e.getLocalizedMessage(), e);
            } catch (InstantiationException e) {
                throw new BlenderFileException(e.getLocalizedMessage(), e);
            } catch (IllegalAccessException e) {
                throw new BlenderFileException(e.getLocalizedMessage(), e);
            } catch (InvocationTargetException e) {
                throw new BlenderFileException(e.getLocalizedMessage(), e);
            }
        } else {
            String constraintName = UNSUPPORTED_CONSTRAINTS.get(constraintClassName);
            if (constraintName != null) {
                return new UnsupportedConstraintDefinition(constraintName);
            } else {
                throw new BlenderFileException("Unknown constraint type: " + constraintClassName);
            }
        }
    }
}
