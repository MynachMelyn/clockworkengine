
package com.clockwork.bullet.collision;

import com.clockwork.animation.Bone;

/**
 *
 */
public interface RagdollCollisionListener {
    
    public void collide(Bone bone, PhysicsCollisionObject object, PhysicsCollisionEvent event);
    
}
