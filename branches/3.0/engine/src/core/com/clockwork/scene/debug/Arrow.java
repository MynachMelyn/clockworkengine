
package com.clockwork.scene.debug;

import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.VertexBuffer;
import com.clockwork.scene.VertexBuffer.Type;
import java.nio.FloatBuffer;

/**
 * The <code>Arrow</code> debug shape represents an arrow.
 * An arrow is simply a line going from the original toward an extent
 * and at the tip there will be triangle-like shape.
 * 
 */
public class Arrow extends Mesh {
    
    private Quaternion tempQuat = new Quaternion();
    private Vector3f tempVec = new Vector3f();

    private static final float[] positions = new float[]{
        0, 0, 0,
        0, 0, 1, // tip
        0.05f, 0, 0.9f, // tip right
        -0.05f, 0, 0.9f, // tip left
        0, 0.05f, 0.9f, // tip top
        0, -0.05f, 0.9f, // tip buttom
    };

    /**
     * Serialisation only. Do not use.
     */
    public Arrow() {
    }

    /**
     * Creates an arrow mesh with the given extent.
     * The arrow will start at the origin (0,0,0) and finish
     * at the given extent.
     * 
     * @param extent Extent of the arrow from origin
     */
    public Arrow(Vector3f extent) {
        float len = extent.length();
        Vector3f dir = extent.normalize();

        tempQuat.lookAt(dir, Vector3f.UNIT_Y);
        tempQuat.normalizeLocal();

        float[] newPositions = new float[positions.length];
        for (int i = 0; i < positions.length; i += 3) {
            Vector3f vec = tempVec.set(positions[i],
                    positions[i + 1],
                    positions[i + 2]);
            vec.multLocal(len);
            tempQuat.mult(vec, vec);

            newPositions[i] = vec.getX();
            newPositions[i + 1] = vec.getY();
            newPositions[i + 2] = vec.getZ();
        }

        setBuffer(Type.Position, 3, newPositions);
        setBuffer(Type.Index, 2,
                new short[]{
                    0, 1,
                    1, 2,
                    1, 3,
                    1, 4,
                    1, 5,});
        setMode(Mode.Lines);

        updateBound();
        updateCounts();
    }

    /**
     * Sets the arrow's extent.
     * This will modify the buffers on the mesh.
     * 
     * @param extent the arrow's extent.
     */
    public void setArrowExtent(Vector3f extent) {
        float len = extent.length();
//        Vector3f dir = extent.normalize();

        tempQuat.lookAt(extent, Vector3f.UNIT_Y);
        tempQuat.normalizeLocal();

        VertexBuffer pvb = getBuffer(Type.Position);
        FloatBuffer buffer = (FloatBuffer)pvb.getData(); 
        buffer.rewind();
        for (int i = 0; i < positions.length; i += 3) {
            Vector3f vec = tempVec.set(positions[i],
                    positions[i + 1],
                    positions[i + 2]);
            vec.multLocal(len);
            tempQuat.mult(vec, vec);

            buffer.put(vec.x);
            buffer.put(vec.y);
            buffer.put(vec.z);
        }
        
        pvb.updateData(buffer);

        updateBound();
        updateCounts();
    }
}
