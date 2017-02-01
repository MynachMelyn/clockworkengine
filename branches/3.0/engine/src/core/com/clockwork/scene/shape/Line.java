
package com.clockwork.scene.shape;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.VertexBuffer;
import com.clockwork.scene.VertexBuffer.Type;
import java.io.IOException;
import java.nio.FloatBuffer;

/**
 * A simple line implementation with a start and an end.
 * 
 */
public class Line extends Mesh {

    private Vector3f start;
    private Vector3f end;
    
    public Line() {
    }

    public Line(Vector3f start, Vector3f end) {
        setMode(Mode.Lines);
        updateGeometry(start, end);
    }

    protected void updateGeometry(Vector3f start, Vector3f end) {
        this.start = start;
        this.end = end;
        setBuffer(Type.Position, 3, new float[]{start.x,    start.y,    start.z,
                                                end.x,      end.y,      end.z,});


        setBuffer(Type.TexCoord, 2, new float[]{0, 0,
                                                1, 1});

        setBuffer(Type.Normal, 3, new float[]{0, 0, 1,
                                              0, 0, 1});

        setBuffer(Type.Index, 2, new short[]{0, 1});

        updateBound();
    }

    /**
     * Update the start and end points of the line.
     */
    public void updatePoints(Vector3f start, Vector3f end) {
        VertexBuffer posBuf = getBuffer(Type.Position);
        
        FloatBuffer fb = (FloatBuffer) posBuf.getData();
        fb.rewind();
        fb.put(start.x).put(start.y).put(start.z);
        fb.put(end.x).put(end.y).put(end.z);
        
        posBuf.updateData(fb);
        
        updateBound();
    }

    public Vector3f getEnd() {
        return end;
    }

    public Vector3f getStart() {
        return start;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);

        out.write(start, "startVertex", null);
        out.write(end, "endVertex", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);

        start = (Vector3f) in.readSavable("startVertex", null);
        end = (Vector3f) in.readSavable("endVertex", null);
    }
}
