
package com.clockwork.input.ios;

import com.clockwork.input.event.TouchEvent;
import com.clockwork.util.RingBuffer;
import java.util.logging.Logger;

/**
 * TouchEventPool provides a RingBuffer of jME TouchEvents to help with garbage
 * collection on Android.  Each TouchEvent is stored in the RingBuffer and is 
 * reused if the TouchEvent has been consumed.
 * 
 * If a TouchEvent has not been consumed, it is placed back into the pool at the 
 * end for later use.  If a TouchEvent has been consumed, it is reused to avoid
 * creating lots of little objects.
 * 
 * If the pool is full of unconsumed events, then a new event is created and provided.
 * 
 * 
 * 
 */
public class TouchEventPool {
    private static final Logger logger = Logger.getLogger(TouchEventPool.class.getName());
    private final RingBuffer<TouchEvent> eventPool;
    private final int maxEvents;
    
    public TouchEventPool (int maxEvents) {
        eventPool = new RingBuffer<TouchEvent>(maxEvents);
        this.maxEvents = maxEvents;
    } 

    public void initialize() {
        TouchEvent newEvent;
        while (!eventPool.isEmpty()) {
            eventPool.pop();
        }
        for (int i = 0; i < maxEvents; i++) {
            newEvent = new TouchEvent();
            newEvent.setConsumed();
            eventPool.push(newEvent);
        }
    }
    
    public void destroy() {
        // Clean up queues
        while (!eventPool.isEmpty()) {
            eventPool.pop();
        }
    }

    /**
     * Fetches a touch event from the reuse pool
     *
     * @return a usable TouchEvent
     */
    public TouchEvent getNextFreeEvent() {
        TouchEvent evt = null;
        int curSize = eventPool.size();
        while (curSize > 0) {
            evt = (TouchEvent)eventPool.pop();
            if (evt.isConsumed()) {
                break;
            } else {
                eventPool.push(evt);
                evt = null;
            }
            curSize--;
        }

        if (evt == null) {
            logger.warning("eventPool full of unconsumed events");
            evt = new TouchEvent();
        }
        return evt;
    }
    
    /**
     * Stores the TouchEvent back in the pool for later reuse.  It is only reused
     * if the TouchEvent has been consumed.
     * 
     * @param event TouchEvent to store for later use if consumed.
     */
    public void storeEvent(TouchEvent event) {
        if (eventPool.size() < maxEvents) {
            eventPool.push(event);
        } else {
            logger.warning("eventPool full");
        }
    }    
    
}
