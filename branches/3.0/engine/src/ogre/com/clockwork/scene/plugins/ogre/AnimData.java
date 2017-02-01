
package com.clockwork.scene.plugins.ogre;

import com.clockwork.animation.Animation;
import com.clockwork.animation.Skeleton;
import java.util.ArrayList;

public class AnimData {

    public final Skeleton skeleton;
    public final ArrayList<Animation> anims;

    public AnimData(Skeleton skeleton, ArrayList<Animation> anims) {
        this.skeleton = skeleton;
        this.anims = anims;
    }
}
