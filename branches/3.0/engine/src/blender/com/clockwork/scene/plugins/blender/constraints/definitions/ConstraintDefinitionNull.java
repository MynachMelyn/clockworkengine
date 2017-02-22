package com.clockwork.scene.plugins.blender.constraints.definitions;

import com.clockwork.math.Transform;
import com.clockwork.scene.plugins.blender.BlenderContext;
import com.clockwork.scene.plugins.blender.file.Structure;

/**
 * This class represents 'Null' constraint type in blender.
 * 
 * 
 */
/* package */class ConstraintDefinitionNull extends ConstraintDefinition {

    public ConstraintDefinitionNull(Structure constraintData, Long ownerOMA, BlenderContext blenderContext) {
        super(constraintData, ownerOMA, blenderContext);
    }

    @Override
    public void bake(Transform ownerTransform, Transform targetTransform, float influence) {
        // null constraint does nothing so no need to implement this one
    }

    @Override
    public String getConstraintTypeName() {
        return "Null";
    }
}
