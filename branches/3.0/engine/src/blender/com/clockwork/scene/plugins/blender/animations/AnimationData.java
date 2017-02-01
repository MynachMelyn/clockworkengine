package com.clockwork.scene.plugins.blender.animations;

import java.util.List;

import com.clockwork.animation.Animation;
import com.clockwork.animation.Skeleton;

/**
 * A simple class that sotres animation data.
 * If skeleton is null then we deal with object animation.
 * 
 */
public class AnimationData {
    /** The skeleton. */
    public final Skeleton skeleton;
    /** The animations list. */
    public final List<Animation> anims;

    public AnimationData(List<Animation> anims) {
        this.anims = anims;
        skeleton = null;
    }
    
    public AnimationData(Skeleton skeleton, List<Animation> anims) {
        this.skeleton = skeleton;
        this.anims = anims;
    }
}
