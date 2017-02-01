
package com.clockwork.bullet.collision.shapes;

import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.extras.gimpact.GImpactMeshShape;
import com.clockwork.bullet.util.Converter;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Mesh;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Basic mesh collision shape
 */
public class GImpactCollisionShape extends CollisionShape{

    protected Vector3f worldScale;
    protected int numVertices, numTriangles, vertexStride, triangleIndexStride;
    protected ByteBuffer triangleIndexBase, vertexBase;
    protected IndexedMesh bulletMesh;

    public GImpactCollisionShape() {
    }

    /**
     * creates a collision shape from the given Mesh
     * @param mesh the Mesh to use
     */
    public GImpactCollisionShape(Mesh mesh) {
        createCollisionMesh(mesh, new Vector3f(1,1,1));
    }


    private void createCollisionMesh(Mesh mesh, Vector3f worldScale) {
        this.worldScale = worldScale;
        bulletMesh = Converter.convert(mesh);
        this.numVertices = bulletMesh.numVertices;
        this.numTriangles = bulletMesh.numTriangles;
        this.vertexStride = bulletMesh.vertexStride;
        this.triangleIndexStride = bulletMesh.triangleIndexStride;
        this.triangleIndexBase = bulletMesh.triangleIndexBase;
        this.vertexBase = bulletMesh.vertexBase;
        createShape();
    }

    /**
     * creates a jme mesh from the collision shape, only needed for debugging
     */
    public Mesh createJmeMesh(){
        return Converter.convert(bulletMesh);
    }

    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(worldScale, "worldScale", new Vector3f(1, 1, 1));
        capsule.write(numVertices, "numVertices", 0);
        capsule.write(numTriangles, "numTriangles", 0);
        capsule.write(vertexStride, "vertexStride", 0);
        capsule.write(triangleIndexStride, "triangleIndexStride", 0);

        capsule.write(triangleIndexBase.array(), "triangleIndexBase", new byte[0]);
        capsule.write(vertexBase.array(), "vertexBase", new byte[0]);
    }

    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);
        worldScale = (Vector3f) capsule.readSavable("worldScale", new Vector3f(1, 1, 1));
        numVertices = capsule.readInt("numVertices", 0);
        numTriangles = capsule.readInt("numTriangles", 0);
        vertexStride = capsule.readInt("vertexStride", 0);
        triangleIndexStride = capsule.readInt("triangleIndexStride", 0);

        triangleIndexBase = ByteBuffer.wrap(capsule.readByteArray("triangleIndexBase", new byte[0]));
        vertexBase = ByteBuffer.wrap(capsule.readByteArray("vertexBase", new byte[0]));
        createShape();
    }

    protected void createShape() {
        bulletMesh = new IndexedMesh();
        bulletMesh.numVertices = numVertices;
        bulletMesh.numTriangles = numTriangles;
        bulletMesh.vertexStride = vertexStride;
        bulletMesh.triangleIndexStride = triangleIndexStride;
        bulletMesh.triangleIndexBase = triangleIndexBase;
        bulletMesh.vertexBase = vertexBase;
        bulletMesh.triangleIndexBase = triangleIndexBase;
        TriangleIndexVertexArray tiv = new TriangleIndexVertexArray(numTriangles, triangleIndexBase, triangleIndexStride, numVertices, vertexBase, vertexStride);
        cShape = new GImpactMeshShape(tiv);
        cShape.setLocalScaling(Converter.convert(worldScale));
        ((GImpactMeshShape)cShape).updateBound();
        cShape.setLocalScaling(Converter.convert(getScale()));
        cShape.setMargin(margin);
    }

}
