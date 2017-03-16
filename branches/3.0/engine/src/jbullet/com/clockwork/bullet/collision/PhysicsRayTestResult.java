
package com.clockwork.bullet.collision;

import com.clockwork.math.Vector3f;

/**
 * Contains the results of a PhysicsSpace rayTest.
 * Read data only in callback method, object is reused
 */
public class PhysicsRayTestResult {

    private PhysicsCollisionObject collisionObject;
    private Vector3f hitNormalLocal;
    private float hitFraction;
    private boolean normalInWorldSpace;

    public PhysicsRayTestResult() {
    }

    public PhysicsRayTestResult(PhysicsCollisionObject collisionObject, Vector3f hitNormalLocal, float hitFraction, boolean normalInWorldSpace) {
        this.collisionObject = collisionObject;
        this.hitNormalLocal = hitNormalLocal;
        this.hitFraction = hitFraction;
        this.normalInWorldSpace = normalInWorldSpace;
    }

    /**
     * @return the PhysicsObject the ray collided with
     */
    public PhysicsCollisionObject getCollisionObject() {
        return collisionObject;
    }

    /**
     * @return the normal of the collision in the objects local space
     */
    public Vector3f getHitNormalLocal() {
        return hitNormalLocal;
    }

    /**
     * The hitFraction is the fraction of the ray length (yeah, I know) at which the collision occurred.
     * If e.g. the raytest was from 0,0,0 to 0,6,0 and the hitFraction is 0.5 then the collision occurred at 0,3,0
     * @return the hitFraction
     */
    public float getHitFraction() {
        return hitFraction;
    }

    /**
     * @return the normalInWorldSpace
     */
    public boolean isNormalInWorldSpace() {
        return normalInWorldSpace;
    }

    public void fill(PhysicsCollisionObject collisionObject, Vector3f hitNormalLocal, float hitFraction, boolean normalInWorldSpace) {
        this.collisionObject = collisionObject;
        this.hitNormalLocal = hitNormalLocal;
        this.hitFraction = hitFraction;
        this.normalInWorldSpace = normalInWorldSpace;
    }
}
