
package com.clockwork.bullet.collision.shapes;

import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.util.ObjectArrayList;
import com.clockwork.bullet.util.Converter;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.VertexBuffer.Type;
import java.io.IOException;
import java.nio.FloatBuffer;
import javax.vecmath.Vector3f;

public class HullCollisionShape extends CollisionShape {

    private float[] points;

    public HullCollisionShape() {
    }

    public HullCollisionShape(Mesh mesh) {
        this.points = getPoints(mesh);
        createShape(this.points);
    }

    public HullCollisionShape(float[] points) {
        this.points = points;
        createShape(this.points);
    }

    @Override
    public void write(CWExporter ex) throws IOException {
        super.write(ex);

        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(points, "points", null);
    }

    @Override
    public void read(CWImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);

        // for backwards compatability
        Mesh mesh = (Mesh) capsule.readSavable("hullMesh", null);
        if (mesh != null) {
            this.points = getPoints(mesh);
        } else {
            this.points = capsule.readFloatArray("points", null);

        }
        createShape(this.points);
    }

    protected void createShape(float[] points) {
        ObjectArrayList<Vector3f> pointList = new ObjectArrayList<Vector3f>();
        for (int i = 0; i < points.length; i += 3) {
            pointList.add(new Vector3f(points[i], points[i + 1], points[i + 2]));
        }
        cShape = new ConvexHullShape(pointList);
        cShape.setLocalScaling(Converter.convert(getScale()));
        cShape.setMargin(margin);
    }

    protected float[] getPoints(Mesh mesh) {
        FloatBuffer vertices = mesh.getFloatBuffer(Type.Position);
        vertices.rewind();
        int components = mesh.getVertexCount() * 3;
        float[] pointsArray = new float[components];
        for (int i = 0; i < components; i += 3) {
            pointsArray[i] = vertices.get();
            pointsArray[i + 1] = vertices.get();
            pointsArray[i + 2] = vertices.get();
        }
        return pointsArray;
    }
}
