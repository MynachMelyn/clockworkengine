
package com.clockwork.bullet.collision;

import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.clockwork.bullet.util.Converter;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Spatial;
import java.util.EventObject;

/**
 * A CollisionEvent stores all information about a collision in the PhysicsWorld.
 * Do not store this Object, as it will be reused after the collision() method has been called.
 * Get/reference all data you need in the collide method.
 */
public class PhysicsCollisionEvent extends EventObject {

    public static final int TYPE_ADDED = 0;
    public static final int TYPE_PROCESSED = 1;
    public static final int TYPE_DESTROYED = 2;
    private int type;
    private PhysicsCollisionObject nodeA;
    private PhysicsCollisionObject nodeB;
    private ManifoldPoint cp;

    public PhysicsCollisionEvent(int type, PhysicsCollisionObject source, PhysicsCollisionObject nodeB, ManifoldPoint cp) {
        super(source);
        this.type = type;
        this.nodeA = source;
        this.nodeB = nodeB;
        this.cp = cp;
    }

    /**
     * used by event factory, called when event is destroyed
     */
    public void clean() {
        source = null;
        type = 0;
        nodeA = null;
        nodeB = null;
        cp = null;
    }

    /**
     * used by event factory, called when event reused
     */
    public void refactor(int type, PhysicsCollisionObject source, PhysicsCollisionObject nodeB, ManifoldPoint cp) {
        this.source = source;
        this.type = type;
        this.nodeA = source;
        this.nodeB = nodeB;
        this.cp = cp;
    }

    public int getType() {
        return type;
    }

    /**
     * @return A Spatial if the UserObject of the PhysicsCollisionObject is a Spatial
     */
    public Spatial getNodeA() {
        if (nodeA.getUserObject() instanceof Spatial) {
            return (Spatial) nodeA.getUserObject();
        }
        return null;
    }

    /**
     * @return A Spatial if the UserObject of the PhysicsCollisionObject is a Spatial
     */
    public Spatial getNodeB() {
        if (nodeB.getUserObject() instanceof Spatial) {
            return (Spatial) nodeB.getUserObject();
        }
        return null;
    }

    public PhysicsCollisionObject getObjectA() {
        return nodeA;
    }

    public PhysicsCollisionObject getObjectB() {
        return nodeB;
    }

    public float getAppliedImpulse() {
        return cp.appliedImpulse;
    }

    public float getAppliedImpulseLateral1() {
        return cp.appliedImpulseLateral1;
    }

    public float getAppliedImpulseLateral2() {
        return cp.appliedImpulseLateral2;
    }

    public float getCombinedFriction() {
        return cp.combinedFriction;
    }

    public float getCombinedRestitution() {
        return cp.combinedRestitution;
    }

    public float getDistance1() {
        return cp.distance1;
    }

    public int getIndex0() {
        return cp.index0;
    }

    public int getIndex1() {
        return cp.index1;
    }

    public Vector3f getLateralFrictionDir1() {
        return Converter.convert(cp.lateralFrictionDir1);
    }

    public Vector3f getLateralFrictionDir2() {
        return Converter.convert(cp.lateralFrictionDir2);
    }

    public boolean isLateralFrictionInitialized() {
        return cp.lateralFrictionInitialized;
    }

    public int getLifeTime() {
        return cp.lifeTime;
    }

    public Vector3f getLocalPointA() {
        return Converter.convert(cp.localPointA);
    }

    public Vector3f getLocalPointB() {
        return Converter.convert(cp.localPointB);
    }

    public Vector3f getNormalWorldOnB() {
        return Converter.convert(cp.normalWorldOnB);
    }

    public int getPartId0() {
        return cp.partId0;
    }

    public int getPartId1() {
        return cp.partId1;
    }

    public Vector3f getPositionWorldOnA() {
        return Converter.convert(cp.positionWorldOnA);
    }

    public Vector3f getPositionWorldOnB() {
        return Converter.convert(cp.positionWorldOnB);
    }

    public Object getUserPersistentData() {
        return cp.userPersistentData;
    }
}
