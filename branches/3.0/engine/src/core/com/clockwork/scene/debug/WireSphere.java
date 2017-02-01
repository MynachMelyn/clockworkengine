
package com.clockwork.scene.debug;

import com.clockwork.bounding.BoundingSphere;
import com.clockwork.math.FastMath;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.Mesh.Mode;
import com.clockwork.scene.VertexBuffer;
import com.clockwork.scene.VertexBuffer.Format;
import com.clockwork.scene.VertexBuffer.Type;
import com.clockwork.scene.VertexBuffer.Usage;
import com.clockwork.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class WireSphere extends Mesh {

    private static final int samples = 30;
    private static final int zSamples = 10;

    public WireSphere() {
        this(1);
    }

    public WireSphere(float radius) {
        updatePositions(radius);
        ShortBuffer ib = BufferUtils.createShortBuffer(samples * 2 * 2 + zSamples * samples * 2 /*+ 3 * 2*/);
        setBuffer(Type.Index, 2, ib);

//        ib.put(new byte[]{
//            (byte) 0, (byte) 1,
//            (byte) 2, (byte) 3,
//            (byte) 4, (byte) 5,
//        });

//        int curNum = 3 * 2;
        int curNum = 0;
        for (int j = 0; j < 2 + zSamples; j++) {
            for (int i = curNum; i < curNum + samples - 1; i++) {
                ib.put((short) i).put((short) (i + 1));
            }
            ib.put((short) (curNum + samples - 1)).put((short) curNum);
            curNum += samples;
        }

        setMode(Mode.Lines);

        updateBound();
        updateCounts();
    }

    public void updatePositions(float radius) {
        VertexBuffer pvb = getBuffer(Type.Position);
        FloatBuffer pb;

        if (pvb == null) {
            pvb = new VertexBuffer(Type.Position);
            pb = BufferUtils.createVector3Buffer(samples * 2 + samples * zSamples /*+ 6 * 3*/);
            pvb.setupData(Usage.Dynamic, 3, Format.Float, pb);
            setBuffer(pvb);
        } else {
            pb = (FloatBuffer) pvb.getData();
        }

        pb.rewind();

        // X axis
//        pb.put(radius).put(0).put(0);
//        pb.put(-radius).put(0).put(0);
//
//        // Y axis
//        pb.put(0).put(radius).put(0);
//        pb.put(0).put(-radius).put(0);
//
//        // Z axis
//        pb.put(0).put(0).put(radius);
//        pb.put(0).put(0).put(-radius);

        float rate = FastMath.TWO_PI / (float) samples;
        float angle = 0;
        for (int i = 0; i < samples; i++) {
            float x = radius * FastMath.cos(angle);
            float y = radius * FastMath.sin(angle);
            pb.put(x).put(y).put(0);
            angle += rate;
        }

        angle = 0;
        for (int i = 0; i < samples; i++) {
            float x = radius * FastMath.cos(angle);
            float y = radius * FastMath.sin(angle);
            pb.put(0).put(x).put(y);
            angle += rate;
        }

        float zRate = (radius * 2) / (float) (zSamples);
        float zHeight = -radius + (zRate / 2f);


        float rb = 1f / zSamples;
        float b = rb / 2f;

        for (int k = 0; k < zSamples; k++) {
            angle = 0;
            float scale = FastMath.sin(b * FastMath.PI);
            for (int i = 0; i < samples; i++) {
                float x = radius * FastMath.cos(angle);
                float y = radius * FastMath.sin(angle);

                pb.put(x * scale).put(zHeight).put(y * scale);

                angle += rate;
            }
            zHeight += zRate;
            b += rb;
        }
    }

    /**
     * Create a WireSphere from a BoundingSphere
     *
     * @param bsph
     *     BoundingSphere used to create the WireSphere
     *
     */
    public void fromBoundingSphere(BoundingSphere bsph) {
        updatePositions(bsph.getRadius());
    }
}
