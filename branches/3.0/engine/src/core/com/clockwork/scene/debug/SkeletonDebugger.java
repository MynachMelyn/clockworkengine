
package com.clockwork.scene.debug;

import com.clockwork.animation.Skeleton;
import com.clockwork.renderer.queue.RenderQueue.Bucket;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;

public class SkeletonDebugger extends Node {

    private SkeletonWire wires;
    private SkeletonPoints points;
    private Skeleton skeleton;

    public SkeletonDebugger(String name, Skeleton skeleton){
        super(name);

        this.skeleton = skeleton;
        wires = new SkeletonWire(skeleton);
        points = new SkeletonPoints(skeleton);

        attachChild(new Geometry(name+"_wires", wires));
        attachChild(new Geometry(name+"_points", points));

        setQueueBucket(Bucket.Transparent);
    }

    public SkeletonDebugger(){
    }

    @Override
    public void updateLogicalState(float tpf){
        super.updateLogicalState(tpf);

//        skeleton.resetAndUpdate();
        wires.updateGeometry();
        points.updateGeometry();
    }

    public SkeletonPoints getPoints() {
        return points;
    }

    public SkeletonWire getWires() {
        return wires;
    }
    
    
}
