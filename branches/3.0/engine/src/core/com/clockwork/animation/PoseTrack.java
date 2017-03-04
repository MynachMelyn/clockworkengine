
package com.clockwork.animation;

import com.clockwork.export.*;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.VertexBuffer;
import com.clockwork.scene.VertexBuffer.Type;
import com.clockwork.util.TempVars;
import java.io.IOException;
import java.nio.FloatBuffer;

/**
 * A single track of pose animation associated with a certain mesh.
 */
@Deprecated
public final class PoseTrack implements Track {
    
    private int targetMeshIndex;
    private PoseFrame[] frames;
    private float[] times;

    public static class PoseFrame implements Savable, Cloneable {

        Pose[] poses;
        float[] weights;

        public PoseFrame(Pose[] poses, float[] weights) {
            this.poses = poses;
            this.weights = weights;
        }
        
        /**
         * Serialisation-only. Do not use.
         */
        public PoseFrame()
        {
        }
        
        /**
         * This method creates a clone of the current object.
         * @return a clone of the current object
         */
        @Override
        public PoseFrame clone() {
            try {
                PoseFrame result = (PoseFrame) super.clone();
                result.weights = this.weights.clone();
                if (this.poses != null) {
                    result.poses = new Pose[this.poses.length];
                    for (int i = 0; i < this.poses.length; ++i) {
                        result.poses[i] = this.poses[i].clone();
                    }
                }
                return result;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }

        public void write(JmeExporter e) throws IOException {
            OutputCapsule out = e.getCapsule(this);
            out.write(poses, "poses", null);
            out.write(weights, "weights", null);
        }

        public void read(JmeImporter i) throws IOException {
            InputCapsule in = i.getCapsule(this);
            weights = in.readFloatArray("weights", null);
            
            Savable[] readSavableArray = in.readSavableArray("poses", null);
            if (readSavableArray != null) {
                poses = new Pose[readSavableArray.length];
                System.arraycopy(readSavableArray, 0, poses, 0, readSavableArray.length);
            }
        }
    }

    public PoseTrack(int targetMeshIndex, float[] times, PoseFrame[] frames){
        this.targetMeshIndex = targetMeshIndex;
        this.times = times;
        this.frames = frames;
    }
    
    /**
     * Serialisation-only. Do not use.
     */
    public PoseTrack()
    {
    }
    
    private void applyFrame(Mesh target, int frameIndex, float weight){
        PoseFrame frame = frames[frameIndex];
        VertexBuffer pb = target.getBuffer(Type.Position);
        for (int i = 0; i < frame.poses.length; i++){
            Pose pose = frame.poses[i];
            float poseWeight = frame.weights[i] * weight;

            pose.apply(poseWeight, (FloatBuffer) pb.getData());
        }

        // force to re-upload data to gpu
        pb.updateData(pb.getData());
    }

    public void setTime(float time, float weight, AnimControl control, AnimChannel channel, TempVars vars) {
        // TODO: When MeshControl is created, it will gather targets
        // list automatically which is then retrieved here.
        
        /*
        Mesh target = targets[targetMeshIndex];
        if (time < times[0]) {
            applyFrame(target, 0, weight);
        } else if (time > times[times.length - 1]) {
            applyFrame(target, times.length - 1, weight);
        } else {
            int startFrame = 0;
            for (int i = 0; i < times.length; i++) {
                if (times[i] < time) {
                    startFrame = i;
                }
            }

            int endFrame = startFrame + 1;
            float blend = (time - times[startFrame]) / (times[endFrame] - times[startFrame]);
            applyFrame(target, startFrame, blend * weight);
            applyFrame(target, endFrame, (1f - blend) * weight);
        }
        */
    }

    /**
     * @return the length of the track
     */
    public float getLength() {
        return times == null ? 0 : times[times.length - 1] - times[0];
    }
    
    /**
     * This method creates a clone of the current object.
     * @return a clone of the current object
     */
    @Override
    public PoseTrack clone() {
        try {
            PoseTrack result = (PoseTrack) super.clone();
            result.times = this.times.clone();
            if (this.frames != null) {
                result.frames = new PoseFrame[this.frames.length];
                for (int i = 0; i < this.frames.length; ++i) {
                    result.frames[i] = this.frames[i].clone();
                }
            }
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
    @Override
    public void write(JmeExporter e) throws IOException {
        OutputCapsule out = e.getCapsule(this);
        out.write(targetMeshIndex, "meshIndex", 0);
        out.write(frames, "frames", null);
        out.write(times, "times", null);
    }

    @Override
    public void read(JmeImporter i) throws IOException {
        InputCapsule in = i.getCapsule(this);
        targetMeshIndex = in.readInt("meshIndex", 0);
        times = in.readFloatArray("times", null);
        
        Savable[] readSavableArray = in.readSavableArray("frames", null);
        if (readSavableArray != null) {
            frames = new PoseFrame[readSavableArray.length];
            System.arraycopy(readSavableArray, 0, frames, 0, readSavableArray.length);
        }
    }
}
