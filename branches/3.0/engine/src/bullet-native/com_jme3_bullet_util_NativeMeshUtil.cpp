

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_util_NativeMeshUtil.h"
#include "jmeBulletUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_util_NativeMeshUtil
     * Method:    createTriangleIndexVertexArray
     * Signature: (Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;IIII)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_util_NativeMeshUtil_createTriangleIndexVertexArray
    (JNIEnv * env, jclass cls, jobject triangleIndexBase, jobject vertexIndexBase, jint numTriangles, jint numVertices, jint vertexStride, jint triangleIndexStride) {
        jmeClasses::initJavaClasses(env);
        int* triangles = (int*) env->GetDirectBufferAddress(triangleIndexBase);
        float* vertices = (float*) env->GetDirectBufferAddress(vertexIndexBase);
        btTriangleIndexVertexArray* array = new btTriangleIndexVertexArray(numTriangles, triangles, triangleIndexStride, numVertices, vertices, vertexStride);
        return reinterpret_cast<jlong>(array);
    }

#ifdef __cplusplus
}
#endif
