
package com.clockwork.scene.debug;

import com.clockwork.animation.Bone;
import com.clockwork.animation.Skeleton;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.VertexBuffer;
import com.clockwork.scene.VertexBuffer.Format;
import com.clockwork.scene.VertexBuffer.Type;
import com.clockwork.scene.VertexBuffer.Usage;
import com.clockwork.util.BufferUtils;
import java.nio.FloatBuffer;

public class SkeletonPoints extends Mesh {

    private Skeleton skeleton;

    public SkeletonPoints(Skeleton skeleton){
        this.skeleton = skeleton;

        setMode(Mode.Points);

        VertexBuffer pb = new VertexBuffer(Type.Position);
        FloatBuffer fpb = BufferUtils.createFloatBuffer(skeleton.getBoneCount() * 3);
        pb.setupData(Usage.Stream, 3, Format.Float, fpb);
        setBuffer(pb);

        setPointSize(7);

        updateCounts();
    }

    public void updateGeometry(){
        VertexBuffer vb = getBuffer(Type.Position);
        FloatBuffer posBuf = getFloatBuffer(Type.Position);
        posBuf.clear();
        for (int i = 0; i < skeleton.getBoneCount(); i++){
            Bone bone = skeleton.getBone(i);
            Vector3f bonePos = bone.getModelSpacePosition();

            posBuf.put(bonePos.getX()).put(bonePos.getY()).put(bonePos.getZ());
        }
        posBuf.flip();
        vb.updateData(posBuf);

        updateBound();
    }
}
