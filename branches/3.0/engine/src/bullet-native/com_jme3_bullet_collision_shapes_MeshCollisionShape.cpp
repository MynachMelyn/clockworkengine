

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_collision_shapes_MeshCollisionShape.h"
#include "jmeBulletUtil.h"
#include "BulletCollision/CollisionShapes/btBvhTriangleMeshShape.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_collision_shapes_MeshCollisionShape
     * Method:    createShape
     * Signature: (J)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_collision_shapes_MeshCollisionShape_createShape
    (JNIEnv * env, jobject object, jlong arrayId) {
        jmeClasses::initJavaClasses(env);
        btTriangleIndexVertexArray* array = reinterpret_cast<btTriangleIndexVertexArray*>(arrayId);
        btBvhTriangleMeshShape* shape = new btBvhTriangleMeshShape(array, true, true);
        return reinterpret_cast<jlong>(shape);
    }
    
    /*
     * Class:     com_jme3_bullet_collision_shapes_MeshCollisionShape
     * Method:    finalizeNative
     * Signature: (J)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_collision_shapes_MeshCollisionShape_finalizeNative
    (JNIEnv * env, jobject object, jlong arrayId){
        btTriangleIndexVertexArray* array = reinterpret_cast<btTriangleIndexVertexArray*>(arrayId);
        delete(array);
    }

#ifdef __cplusplus
}
#endif
