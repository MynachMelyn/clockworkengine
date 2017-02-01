
package com.clockwork.bullet.collision;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 */
public class PhysicsCollisionEventFactory {

    private ConcurrentLinkedQueue<PhysicsCollisionEvent> eventBuffer = new ConcurrentLinkedQueue<PhysicsCollisionEvent>();

    public PhysicsCollisionEvent getEvent(int type, PhysicsCollisionObject source, PhysicsCollisionObject nodeB, long manifoldPointObjectId) {
        PhysicsCollisionEvent event = eventBuffer.poll();
        if (event == null) {
            event = new PhysicsCollisionEvent(type, source, nodeB, manifoldPointObjectId);
        }else{
            event.refactor(type, source, nodeB, manifoldPointObjectId);
        }
        return event;
    }

    public void recycle(PhysicsCollisionEvent event) {
        event.clean();
        eventBuffer.add(event);
    }
}
