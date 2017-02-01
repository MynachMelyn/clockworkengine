

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_collision_shapes_CollisionShape.h"
#include "jmeBulletUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_collision_shapes_CollisionShape
     * Method:    getMargin
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_collision_shapes_CollisionShape_getMargin
    (JNIEnv * env, jobject object, jlong shapeId) {
        btCollisionShape* shape = reinterpret_cast<btCollisionShape*>(shapeId);
        if (shape == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return shape->getMargin();
    }

    /*
     * Class:     com_jme3_bullet_collision_shapes_CollisionShape
     * Method:    setLocalScaling
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_collision_shapes_CollisionShape_setLocalScaling
    (JNIEnv * env, jobject object, jlong shapeId, jobject scale) {
        btCollisionShape* shape = reinterpret_cast<btCollisionShape*>(shapeId);
        if (shape == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        btVector3 scl = btVector3();
        jmeBulletUtil::convert(env, scale, &scl);
        shape->setLocalScaling(scl);
    }

    /*
     * Class:     com_jme3_bullet_collision_shapes_CollisionShape
     * Method:    setMargin
     * Signature: (JF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_collision_shapes_CollisionShape_setMargin
    (JNIEnv * env, jobject object, jlong shapeId, jfloat newMargin) {
        btCollisionShape* shape = reinterpret_cast<btCollisionShape*>(shapeId);
        if (shape == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        shape->setMargin(newMargin);
    }

    /*
     * Class:     com_jme3_bullet_collision_shapes_CollisionShape
     * Method:    finalizeNative
     * Signature: (J)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_collision_shapes_CollisionShape_finalizeNative
    (JNIEnv * env, jobject object, jlong shapeId) {
        btCollisionShape* shape = reinterpret_cast<btCollisionShape*>(shapeId);
        if (shape == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        delete(shape);
    }
#ifdef __cplusplus
}
#endif
