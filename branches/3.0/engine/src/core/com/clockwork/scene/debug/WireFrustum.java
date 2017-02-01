
package com.clockwork.scene.debug;

import com.clockwork.math.Vector3f;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.VertexBuffer;
import com.clockwork.scene.VertexBuffer.Type;
import com.clockwork.util.BufferUtils;
import java.nio.FloatBuffer;

public class WireFrustum extends Mesh {

    public WireFrustum(Vector3f[] points){
        if (points != null)
            setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(points));

        setBuffer(Type.Index, 2,
                new short[]{
                     0, 1,
                     1, 2,
                     2, 3,
                     3, 0,

                     4, 5,
                     5, 6,
                     6, 7,
                     7, 4,

                     0, 4,
                     1, 5,
                     2, 6,
                     3, 7,
                }
        );
        setMode(Mode.Lines);
    }

    public void update(Vector3f[] points){
        VertexBuffer vb = getBuffer(Type.Position);
        if (vb == null){
            setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(points));
            return;
        }

        FloatBuffer b = BufferUtils.createFloatBuffer(points);
        FloatBuffer a = (FloatBuffer) vb.getData();
        b.rewind();
        a.rewind();
        a.put(b);
        a.rewind();

        vb.updateData(a);
        
        updateBound();
    }

}
