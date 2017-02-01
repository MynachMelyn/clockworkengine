

/**
 * Author: Normen Hansen
 */

#include "com_jme3_bullet_objects_PhysicsCharacter.h"
#include "jmeBulletUtil.h"
#include "BulletCollision/CollisionDispatch/btGhostObject.h"
#include "BulletDynamics/Character/btKinematicCharacterController.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    createGhostObject
     * Signature: ()J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_createGhostObject
    (JNIEnv * env, jobject object) {
        jmeClasses::initJavaClasses(env);
        btPairCachingGhostObject* ghost = new btPairCachingGhostObject();
        return reinterpret_cast<jlong>(ghost);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    setCharacterFlags
     * Signature: (J)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_setCharacterFlags
    (JNIEnv *env, jobject object, jlong ghostId) {
        btPairCachingGhostObject* ghost = reinterpret_cast<btPairCachingGhostObject*>(ghostId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        ghost->setCollisionFlags(/*ghost->getCollisionFlags() |*/ btCollisionObject::CF_CHARACTER_OBJECT);
        ghost->setCollisionFlags(ghost->getCollisionFlags() & ~btCollisionObject::CF_NO_CONTACT_RESPONSE);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    createCharacterObject
     * Signature: (JJF)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_createCharacterObject
    (JNIEnv *env, jobject object, jlong objectId, jlong shapeId, jfloat stepHeight) {
        btPairCachingGhostObject* ghost = reinterpret_cast<btPairCachingGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        //TODO: check convexshape!
        btConvexShape* shape = reinterpret_cast<btConvexShape*>(shapeId);
        btKinematicCharacterController* character = new btKinematicCharacterController(ghost, shape, stepHeight);
        return reinterpret_cast<jlong>(character);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    warp
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_warp
    (JNIEnv *env, jobject object, jlong objectId, jobject vector) {
        btKinematicCharacterController* character = reinterpret_cast<btKinematicCharacterController*>(objectId);
        if (character == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        btVector3 vec = btVector3();
        jmeBulletUtil::convert(env, vector, &vec);
        character->warp(vec);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    setWalkDirection
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_setWalkDirection
    (JNIEnv *env, jobject object, jlong objectId, jobject vector) {
        btKinematicCharacterController* character = reinterpret_cast<btKinematicCharacterController*>(objectId);
        if (character == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        btVector3 vec = btVector3();
        jmeBulletUtil::convert(env, vector, &vec);
        character->setWalkDirection(vec);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    setUpAxis
     * Signature: (JI)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_setUpAxis
    (JNIEnv *env, jobject object, jlong objectId, jint value) {
        btKinematicCharacterController* character = reinterpret_cast<btKinematicCharacterController*>(objectId);
        if (character == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        character->setUpAxis(value);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    setFallSpeed
     * Signature: (JF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_setFallSpeed
    (JNIEnv *env, jobject object, jlong objectId, jfloat value) {
        btKinematicCharacterController* character = reinterpret_cast<btKinematicCharacterController*>(objectId);
        if (character == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        character->setFallSpeed(value);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    setJumpSpeed
     * Signature: (JF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_setJumpSpeed
    (JNIEnv *env, jobject object, jlong objectId, jfloat value) {
        btKinematicCharacterController* character = reinterpret_cast<btKinematicCharacterController*>(objectId);
        if (character == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        character->setJumpSpeed(value);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    setGravity
     * Signature: (JF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_setGravity
    (JNIEnv *env, jobject object, jlong objectId, jfloat value) {
        btKinematicCharacterController* character = reinterpret_cast<btKinematicCharacterController*>(objectId);
        if (character == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        character->setGravity(value);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    getGravity
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_getGravity
    (JNIEnv *env, jobject object, jlong objectId) {
        btKinematicCharacterController* character = reinterpret_cast<btKinematicCharacterController*>(objectId);
        if (character == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return character->getGravity();
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    setMaxSlope
     * Signature: (JF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_setMaxSlope
    (JNIEnv *env, jobject object, jlong objectId, jfloat value) {
        btKinematicCharacterController* character = reinterpret_cast<btKinematicCharacterController*>(objectId);
        if (character == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        character->setMaxSlope(value);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    getMaxSlope
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_getMaxSlope
    (JNIEnv *env, jobject object, jlong objectId) {
        btKinematicCharacterController* character = reinterpret_cast<btKinematicCharacterController*>(objectId);
        if (character == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return character->getMaxSlope();
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    onGround
     * Signature: (J)Z
     */
    JNIEXPORT jboolean JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_onGround
    (JNIEnv *env, jobject object, jlong objectId) {
        btKinematicCharacterController* character = reinterpret_cast<btKinematicCharacterController*>(objectId);
        if (character == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return false;
        }
        return character->onGround();
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    jump
     * Signature: (J)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_jump
    (JNIEnv *env, jobject object, jlong objectId) {
        btKinematicCharacterController* character = reinterpret_cast<btKinematicCharacterController*>(objectId);
        if (character == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        character->jump();
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    getPhysicsLocation
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_getPhysicsLocation
    (JNIEnv *env, jobject object, jlong objectId, jobject value) {
        btGhostObject* ghost = reinterpret_cast<btGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convert(env, &ghost->getWorldTransform().getOrigin(), value);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    setCcdSweptSphereRadius
     * Signature: (JF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_setCcdSweptSphereRadius
    (JNIEnv *env, jobject object, jlong objectId, jfloat value) {
        btGhostObject* ghost = reinterpret_cast<btGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        ghost->setCcdSweptSphereRadius(value);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    setCcdMotionThreshold
     * Signature: (JF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_setCcdMotionThreshold
    (JNIEnv *env, jobject object, jlong objectId, jfloat value) {
        btGhostObject* ghost = reinterpret_cast<btGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        ghost->setCcdMotionThreshold(value);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    getCcdSweptSphereRadius
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_getCcdSweptSphereRadius
    (JNIEnv *env, jobject object, jlong objectId) {
        btGhostObject* ghost = reinterpret_cast<btGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return ghost->getCcdSweptSphereRadius();
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    getCcdMotionThreshold
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_getCcdMotionThreshold
    (JNIEnv *env, jobject object, jlong objectId) {
        btGhostObject* ghost = reinterpret_cast<btGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return ghost->getCcdMotionThreshold();
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    getCcdSquareMotionThreshold
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_getCcdSquareMotionThreshold
    (JNIEnv *env, jobject object, jlong objectId) {
        btGhostObject* ghost = reinterpret_cast<btGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return ghost->getCcdSquareMotionThreshold();
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsCharacter
     * Method:    finalizeNativeCharacter
     * Signature: (J)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsCharacter_finalizeNativeCharacter
    (JNIEnv *env, jobject object, jlong objectId) {
        btKinematicCharacterController* character = reinterpret_cast<btKinematicCharacterController*>(objectId);
        if (character == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        delete(character);
    }

#ifdef __cplusplus
}
#endif
