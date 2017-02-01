

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_collision_shapes_BoxCollisionShape.h"
#include "jmeBulletUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_collision_shapes_BoxCollisionShape
     * Method:    createShape
     * Signature: (Lcom/jme3/math/Vector3f;)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_collision_shapes_BoxCollisionShape_createShape
    (JNIEnv *env, jobject object, jobject halfExtents) {
        jmeClasses::initJavaClasses(env);
        btVector3 extents =  btVector3();
        jmeBulletUtil::convert(env, halfExtents, &extents);
        btBoxShape* shape = new btBoxShape(extents);
        return reinterpret_cast<jlong>(shape);
    }

#ifdef __cplusplus
}
#endif
