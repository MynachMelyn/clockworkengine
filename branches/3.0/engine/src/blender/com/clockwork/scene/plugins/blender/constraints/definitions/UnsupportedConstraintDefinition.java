package com.clockwork.scene.plugins.blender.constraints.definitions;

import com.clockwork.math.Transform;

/**
 * This class represents a constraint that is defined by blender but not
 * supported by either importer ot CW. It only wirtes down a warning when
 * baking is called.
 * 
 * 
 */
/* package */class UnsupportedConstraintDefinition extends ConstraintDefinition {
    private String typeName;

    public UnsupportedConstraintDefinition(String typeName) {
        super(null, null, null);
        this.typeName = typeName;
    }

    @Override
    public void bake(Transform ownerTransform, Transform targetTransform, float influence) {
    }

    @Override
    public boolean isImplemented() {
        return false;
    }

    @Override
    public String getConstraintTypeName() {
        return typeName;
    }
}
