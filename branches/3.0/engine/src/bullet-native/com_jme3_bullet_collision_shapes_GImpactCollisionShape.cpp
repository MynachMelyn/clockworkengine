

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_collision_shapes_GImpactCollisionShape.h"
#include "jmeBulletUtil.h"
#include <BulletCollision/Gimpact/btGImpactShape.h>

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_collision_shapes_GImpactCollisionShape
     * Method:    createShape
     * Signature: (J)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_collision_shapes_GImpactCollisionShape_createShape
    (JNIEnv * env, jobject object, jlong meshId) {
        jmeClasses::initJavaClasses(env);
        btTriangleIndexVertexArray* array = reinterpret_cast<btTriangleIndexVertexArray*>(meshId);
        btGImpactMeshShape* shape = new btGImpactMeshShape(array);
        return reinterpret_cast<jlong>(shape);
    }

    /*
     * Class:     com_jme3_bullet_collision_shapes_GImpactCollisionShape
     * Method:    finalizeNative
     * Signature: (J)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_collision_shapes_GImpactCollisionShape_finalizeNative
    (JNIEnv * env, jobject object, jlong meshId) {
        btTriangleIndexVertexArray* array = reinterpret_cast<btTriangleIndexVertexArray*> (meshId);
        delete(array);
    }
    
#ifdef __cplusplus
}
#endif
