

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_collision_PhysicsCollisionObject.h"
#include "jmeBulletUtil.h"
#include "jmePhysicsSpace.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_collision_PhysicsCollisionObject
     * Method:    attachCollisionShape
     * Signature: (JJ)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_collision_PhysicsCollisionObject_attachCollisionShape
    (JNIEnv * env, jobject object, jlong objectId, jlong shapeId) {
        btCollisionObject* collisionObject = reinterpret_cast<btCollisionObject*>(objectId);
        if (collisionObject == NULL) {
            jclass newExc = env->FindClass("java/lang/IllegalStateException");
            env->ThrowNew(newExc, "The collision object does not exist.");
            return;
        }
        btCollisionShape* collisionShape = reinterpret_cast<btCollisionShape*>(shapeId);
        if (collisionShape == NULL) {
            jclass newExc = env->FindClass("java/lang/IllegalStateException");
            env->ThrowNew(newExc, "The collision shape does not exist.");
            return;
        }
        collisionObject->setCollisionShape(collisionShape);
    }

    /*
     * Class:     com_jme3_bullet_collision_PhysicsCollisionObject
     * Method:    finalizeNative
     * Signature: (J)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_collision_PhysicsCollisionObject_finalizeNative
    (JNIEnv * env, jobject object, jlong objectId) {
        btCollisionObject* collisionObject = reinterpret_cast<btCollisionObject*>(objectId);
        if (collisionObject == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        if (collisionObject -> getUserPointer() != NULL){
            jmeUserPointer *userPointer = (jmeUserPointer*)collisionObject->getUserPointer();
            delete(userPointer);
        }
        delete(collisionObject);
    }
    /*
     * Class:     com_jme3_bullet_collision_PhysicsCollisionObject
     * Method:    initUserPointer
     * Signature: (JII)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_collision_PhysicsCollisionObject_initUserPointer
      (JNIEnv *env, jobject object, jlong objectId, jint group, jint groups) {
        btCollisionObject* collisionObject = reinterpret_cast<btCollisionObject*>(objectId);
        if (collisionObject == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeUserPointer *userPointer = (jmeUserPointer*)collisionObject->getUserPointer();
        if (userPointer != NULL) {
//            delete(userPointer);
        }
        userPointer = new jmeUserPointer();
        userPointer -> javaCollisionObject = env->NewWeakGlobalRef(object);
        userPointer -> group = group;
        userPointer -> groups = groups;
        userPointer -> space = NULL;
        collisionObject -> setUserPointer(userPointer);
    }
    /*
     * Class:     com_jme3_bullet_collision_PhysicsCollisionObject
     * Method:    setCollisionGroup
     * Signature: (JI)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_collision_PhysicsCollisionObject_setCollisionGroup
      (JNIEnv *env, jobject object, jlong objectId, jint group) {
        btCollisionObject* collisionObject = reinterpret_cast<btCollisionObject*>(objectId);
        if (collisionObject == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeUserPointer *userPointer = (jmeUserPointer*)collisionObject->getUserPointer();
        if (userPointer != NULL){
            userPointer -> group = group;
        }
    }
    /*
     * Class:     com_jme3_bullet_collision_PhysicsCollisionObject
     * Method:    setCollideWithGroups
     * Signature: (JI)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_collision_PhysicsCollisionObject_setCollideWithGroups
      (JNIEnv *env, jobject object, jlong objectId, jint groups) {
        btCollisionObject* collisionObject = reinterpret_cast<btCollisionObject*>(objectId);
        if (collisionObject == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeUserPointer *userPointer = (jmeUserPointer*)collisionObject->getUserPointer();
        if (userPointer != NULL){
            userPointer -> groups = groups;
        }
    }

#ifdef __cplusplus
}
#endif
