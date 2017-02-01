
package com.clockwork.cinematic.events;

import com.clockwork.animation.LoopMode;
import com.clockwork.cinematic.MotionPath;
import com.clockwork.scene.Spatial;

/** 
 *
 * @deprecated use MotionEvent instead
 */
@Deprecated
public class MotionTrack extends MotionEvent {

      /**
     * Create MotionTrack,
     * when using this constructor don't forget to assign spatial and path
     */
    public MotionTrack() {
        super();
    }

    /**
     * Creates a MotionPath for the given spatial on the given motion path
     * @param spatial
     * @param path
     */
    public MotionTrack(Spatial spatial, MotionPath path) {
        super(spatial, path);
    }

    /**
     * Creates a MotionPath for the given spatial on the given motion path
     * @param spatial
     * @param path
     */
    public MotionTrack(Spatial spatial, MotionPath path, float initialDuration) {
        super(spatial, path, initialDuration);
    }

    /**
     * Creates a MotionPath for the given spatial on the given motion path
     * @param spatial
     * @param path
     */
    public MotionTrack(Spatial spatial, MotionPath path, LoopMode loopMode) {
        super(spatial, path, loopMode);
        
    }

    /**
     * Creates a MotionPath for the given spatial on the given motion path
     * @param spatial
     * @param path
     */
    public MotionTrack(Spatial spatial, MotionPath path, float initialDuration, LoopMode loopMode) {
        super(spatial, path, initialDuration, loopMode);
    }
}
