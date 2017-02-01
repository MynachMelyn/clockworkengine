
package com.clockwork.cinematic;

import com.clockwork.cinematic.events.MotionEvent;

/**
 * Trigger the events appening on an motion path
 */
public interface MotionPathListener {

    /**
     * Triggers every time the target reach a waypoint on the path
     * @param motionControl the MotionEvent objects that reached the waypoint
     * @param wayPointIndex the index of the way point reached
     */
    public void onWayPointReach(MotionEvent motionControl,int wayPointIndex);

}
