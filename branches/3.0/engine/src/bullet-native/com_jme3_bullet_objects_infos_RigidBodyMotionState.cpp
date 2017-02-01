

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_objects_infos_RigidBodyMotionState.h"
#include "jmeBulletUtil.h"
#include "jmeMotionState.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_objects_infos_RigidBodyMotionState
     * Method:    createMotionState
     * Signature: ()J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_objects_infos_RigidBodyMotionState_createMotionState
    (JNIEnv *env, jobject object) {
        jmeClasses::initJavaClasses(env);
        jmeMotionState* motionState = new jmeMotionState();
        return reinterpret_cast<jlong>(motionState);
    }

    /*
     * Class:     com_jme3_bullet_objects_infos_RigidBodyMotionState
     * Method:    applyTransform
     * Signature: (JLcom/jme3/math/Vector3f;Lcom/jme3/math/Quaternion;)Z
     */
    JNIEXPORT jboolean JNICALL Java_com_jme3_bullet_objects_infos_RigidBodyMotionState_applyTransform
    (JNIEnv *env, jobject object, jlong stateId, jobject location, jobject rotation) {
        jmeMotionState* motionState = reinterpret_cast<jmeMotionState*>(stateId);
        if (motionState == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return false;
        }
        return motionState->applyTransform(env, location, rotation);
    }

    /*
     * Class:     com_jme3_bullet_objects_infos_RigidBodyMotionState
     * Method:    getWorldLocation
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_infos_RigidBodyMotionState_getWorldLocation
    (JNIEnv *env, jobject object, jlong stateId, jobject value) {
        jmeMotionState* motionState = reinterpret_cast<jmeMotionState*>(stateId);
        if (motionState == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convert(env, &motionState->worldTransform.getOrigin(), value);
    }

    /*
     * Class:     com_jme3_bullet_objects_infos_RigidBodyMotionState
     * Method:    getWorldRotation
     * Signature: (JLcom/jme3/math/Matrix3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_infos_RigidBodyMotionState_getWorldRotation
    (JNIEnv *env, jobject object, jlong stateId, jobject value) {
        jmeMotionState* motionState = reinterpret_cast<jmeMotionState*>(stateId);
        if (motionState == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convert(env, &motionState->worldTransform.getBasis(), value);
    }

    /*
     * Class:     com_jme3_bullet_objects_infos_RigidBodyMotionState
     * Method:    getWorldRotationQuat
     * Signature: (JLcom/jme3/math/Quaternion;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_infos_RigidBodyMotionState_getWorldRotationQuat
    (JNIEnv *env, jobject object, jlong stateId, jobject value) {
        jmeMotionState* motionState = reinterpret_cast<jmeMotionState*>(stateId);
        if (motionState == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convertQuat(env, &motionState->worldTransform.getBasis(), value);
    }

    /*
     * Class:     com_jme3_bullet_objects_infos_RigidBodyMotionState
     * Method:    finalizeNative
     * Signature: (J)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_infos_RigidBodyMotionState_finalizeNative
    (JNIEnv *env, jobject object, jlong stateId) {
        jmeMotionState* motionState = reinterpret_cast<jmeMotionState*>(stateId);
        if (motionState == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        delete(motionState);
    }

#ifdef __cplusplus
}
#endif
