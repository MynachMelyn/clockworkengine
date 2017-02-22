
package com.clockwork.bullet.util;

import com.clockwork.scene.Mesh;
import com.clockwork.scene.VertexBuffer.Type;
import com.clockwork.scene.mesh.IndexBuffer;
import com.clockwork.util.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 *
 * 
 */
public class NativeMeshUtil {
    
    public static long getTriangleIndexVertexArray(Mesh mesh){
        ByteBuffer triangleIndexBase = BufferUtils.createByteBuffer(mesh.getTriangleCount() * 3 * 4);
        ByteBuffer vertexBase = BufferUtils.createByteBuffer(mesh.getVertexCount() * 3 * 4);
        int numVertices = mesh.getVertexCount();
        int vertexStride = 12; //3 verts * 4 bytes per.
        int numTriangles = mesh.getTriangleCount();
        int triangleIndexStride = 12; //3 index entries * 4 bytes each.

        IndexBuffer indices = mesh.getIndicesAsList();
        FloatBuffer vertices = mesh.getFloatBuffer(Type.Position);
        vertices.rewind();

        int verticesLength = mesh.getVertexCount() * 3;
        for (int i = 0; i < verticesLength; i++) {
            float tempFloat = vertices.get();
            vertexBase.putFloat(tempFloat);
        }

        int indicesLength = mesh.getTriangleCount() * 3;
        for (int i = 0; i < indicesLength; i++) {
            triangleIndexBase.putInt(indices.get(i));
        }
        vertices.rewind();
        vertices.clear();

        return createTriangleIndexVertexArray(triangleIndexBase, vertexBase, numTriangles, numVertices, vertexStride, triangleIndexStride);
    }
    
    public static native long createTriangleIndexVertexArray(ByteBuffer triangleIndexBase, ByteBuffer vertexBase, int numTraingles, int numVertices, int vertextStride, int triangleIndexStride);
    
}
