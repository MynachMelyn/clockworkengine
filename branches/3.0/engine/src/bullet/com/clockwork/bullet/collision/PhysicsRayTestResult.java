
package com.clockwork.bullet.collision;

import com.clockwork.math.Vector3f;

/**
 * Contains the results of a PhysicsSpace rayTest
 *  bulletAppState.getPhysicsSpace().rayTest(new Vector3f(0,1000,0),new Vector3f(0,-1000,0));
    javap -s java.util.List
 */
public class PhysicsRayTestResult {

    private PhysicsCollisionObject collisionObject;
    private Vector3f hitNormalLocal;
    private float hitFraction;
    private boolean normalInWorldSpace = true;

    /**
     * allocated by native code only
     */
    private PhysicsRayTestResult() {
    }

    /**
     * @return the collisionObject
     */
    public PhysicsCollisionObject getCollisionObject() {
        return collisionObject;
    }

    /**
     * @return the hitNormalLocal
     */
    public Vector3f getHitNormalLocal() {
        return hitNormalLocal;
    }

    /**
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
}
