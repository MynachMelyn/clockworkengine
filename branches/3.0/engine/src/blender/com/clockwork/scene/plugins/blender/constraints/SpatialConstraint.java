package com.clockwork.scene.plugins.blender.constraints;

import com.clockwork.math.Transform;
import com.clockwork.scene.plugins.blender.BlenderContext;
import com.clockwork.scene.plugins.blender.BlenderContext.LoadedFeatureDataType;
import com.clockwork.scene.plugins.blender.animations.Ipo;
import com.clockwork.scene.plugins.blender.exceptions.BlenderFileException;
import com.clockwork.scene.plugins.blender.file.Structure;

/**
 * Constraint applied on the spatial objects. This includes: nodes, cameras
 * nodes and light nodes.
 * 
 * 
 */
/* package */class SpatialConstraint extends Constraint {
    public SpatialConstraint(Structure constraintStructure, Long ownerOMA, Ipo influenceIpo, BlenderContext blenderContext) throws BlenderFileException {
        super(constraintStructure, ownerOMA, influenceIpo, blenderContext);
    }

    @Override
    public boolean validate() {
        if (targetOMA != null) {
            return blenderContext.getLoadedFeature(targetOMA, LoadedFeatureDataType.LOADED_FEATURE) != null;
        }
        return true;
    }

    @Override
    public void apply(int frame) {
        Transform ownerTransform = constraintHelper.getTransform(ownerOMA, null, ownerSpace);
        Transform targetTransform = targetOMA != null ? constraintHelper.getTransform(targetOMA, subtargetName, targetSpace) : null;
        constraintDefinition.bake(ownerTransform, targetTransform, this.ipo.calculateValue(frame));
        constraintHelper.applyTransform(ownerOMA, subtargetName, ownerSpace, ownerTransform);
    }
}
