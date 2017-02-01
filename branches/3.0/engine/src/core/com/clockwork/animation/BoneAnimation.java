
package com.clockwork.animation;

/**
 * @deprecated use Animation instead with tracks of selected type (ie. BoneTrack, SpatialTrack, MeshTrack)
 */
@Deprecated
public final class BoneAnimation extends Animation {

    @Deprecated
    public BoneAnimation(String name, float length) {
        super(name, length);
    }
}
