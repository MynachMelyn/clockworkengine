

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_collision_shapes_SphereCollisionShape.h"
#include "jmeBulletUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_collision_shapes_SphereCollisionShape
     * Method:    createShape
     * Signature: (F)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_collision_shapes_SphereCollisionShape_createShape
    (JNIEnv *env, jobject object, jfloat radius) {
        jmeClasses::initJavaClasses(env);
        btSphereShape* shape=new btSphereShape(radius);
        return reinterpret_cast<jlong>(shape);
    }

#ifdef __cplusplus
}
#endif
