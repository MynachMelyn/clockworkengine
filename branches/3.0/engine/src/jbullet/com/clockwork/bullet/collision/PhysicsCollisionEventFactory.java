
package com.clockwork.bullet.collision;

import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 */
public class PhysicsCollisionEventFactory {

    private ConcurrentLinkedQueue<PhysicsCollisionEvent> eventBuffer = new ConcurrentLinkedQueue<PhysicsCollisionEvent>();

    public PhysicsCollisionEvent getEvent(int type, PhysicsCollisionObject source, PhysicsCollisionObject nodeB, ManifoldPoint cp) {
        PhysicsCollisionEvent event = eventBuffer.poll();
        if (event == null) {
            event = new PhysicsCollisionEvent(type, source, nodeB, cp);
        }else{
            event.refactor(type, source, nodeB, cp);
        }
        return event;
    }

    public void recycle(PhysicsCollisionEvent event) {
        event.clean();
        eventBuffer.add(event);
    }
}
