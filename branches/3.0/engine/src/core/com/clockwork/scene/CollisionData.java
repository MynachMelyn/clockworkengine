
package com.clockwork.scene;

import com.clockwork.bounding.BoundingVolume;
import com.clockwork.collision.Collidable;
import com.clockwork.collision.CollisionResults;
import com.clockwork.export.Savable;
import com.clockwork.math.Matrix4f;

/**
 * CollisionData is an interface that can be used to 
 * do triangle-accurate collision with bounding volumes and rays.
 *
 */
public interface CollisionData extends Savable, Cloneable {
    public int collideWith(Collidable other,
                           Matrix4f worldMatrix,
                           BoundingVolume worldBound,
                           CollisionResults results);
}
