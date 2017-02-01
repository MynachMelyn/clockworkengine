

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_collision_shapes_CompoundCollisionShape.h"
#include "jmeBulletUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

/*
     * Class:     com_jme3_bullet_collision_shapes_CompoundCollisionShape
     * Method:    createShape
     * Signature: ()J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_collision_shapes_CompoundCollisionShape_createShape
    (JNIEnv *env, jobject object) {
        jmeClasses::initJavaClasses(env);
        btCompoundShape* shape = new btCompoundShape();
        return reinterpret_cast<jlong>(shape);
    }

    /*
     * Class:     com_jme3_bullet_collision_shapes_CompoundCollisionShape
     * Method:    addChildShape
     * Signature: (JJLcom/jme3/math/Vector3f;Lcom/jme3/math/Matrix3f;)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_collision_shapes_CompoundCollisionShape_addChildShape
    (JNIEnv *env, jobject object, jlong compoundId, jlong childId, jobject childLocation, jobject childRotation) {
        btCompoundShape* shape = reinterpret_cast<btCompoundShape*>(compoundId);
        if (shape == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        btCollisionShape* child = reinterpret_cast<btCollisionShape*>(childId);
        if (shape == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        btMatrix3x3 mtx = btMatrix3x3();
        btTransform trans = btTransform(mtx);
        jmeBulletUtil::convert(env, childLocation, &trans.getOrigin());
        jmeBulletUtil::convert(env, childRotation, &trans.getBasis());
        shape->addChildShape(trans, child);
        return 0;
    }

    /*
     * Class:     com_jme3_bullet_collision_shapes_CompoundCollisionShape
     * Method:    removeChildShape
     * Signature: (JJ)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_collision_shapes_CompoundCollisionShape_removeChildShape
    (JNIEnv * env, jobject object, jlong compoundId, jlong childId) {
        btCompoundShape* shape = reinterpret_cast<btCompoundShape*>(compoundId);
        if (shape == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        btCollisionShape* child = reinterpret_cast<btCollisionShape*>(childId);
        if (shape == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        shape->removeChildShape(child);
        return 0;
    }

#ifdef __cplusplus
}
#endif
