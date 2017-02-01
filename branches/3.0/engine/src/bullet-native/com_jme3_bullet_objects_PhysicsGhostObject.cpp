

/**
 * Author: Normen Hansen
 */

#include <BulletCollision/CollisionDispatch/btGhostObject.h>

#include "com_jme3_bullet_objects_PhysicsGhostObject.h"
#include "BulletCollision/BroadphaseCollision/btOverlappingPairCache.h"
#include "jmeBulletUtil.h"
#include "jmePhysicsSpace.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_objects_PhysicsGhostObject
     * Method:    createGhostObject
     * Signature: ()J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_objects_PhysicsGhostObject_createGhostObject
    (JNIEnv * env, jobject object) {
        jmeClasses::initJavaClasses(env);
        btPairCachingGhostObject* ghost = new btPairCachingGhostObject();
        return reinterpret_cast<jlong>(ghost);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsGhostObject
     * Method:    setGhostFlags
     * Signature: (J)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsGhostObject_setGhostFlags
    (JNIEnv *env, jobject object, jlong objectId) {
        btPairCachingGhostObject* ghost = reinterpret_cast<btPairCachingGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        ghost->setCollisionFlags(ghost->getCollisionFlags() | btCollisionObject::CF_NO_CONTACT_RESPONSE);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsGhostObject
     * Method:    setPhysicsLocation
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsGhostObject_setPhysicsLocation
    (JNIEnv *env, jobject object, jlong objectId, jobject value) {
        btPairCachingGhostObject* ghost = reinterpret_cast<btPairCachingGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convert(env, value, &ghost->getWorldTransform().getOrigin());
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsGhostObject
     * Method:    setPhysicsRotation
     * Signature: (JLcom/jme3/math/Matrix3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsGhostObject_setPhysicsRotation__JLcom_jme3_math_Matrix3f_2
    (JNIEnv *env, jobject object, jlong objectId, jobject value) {
        btPairCachingGhostObject* ghost = reinterpret_cast<btPairCachingGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convert(env, value, &ghost->getWorldTransform().getBasis());
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsGhostObject
     * Method:    setPhysicsRotation
     * Signature: (JLcom/jme3/math/Quaternion;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsGhostObject_setPhysicsRotation__JLcom_jme3_math_Quaternion_2
    (JNIEnv *env, jobject object, jlong objectId, jobject value) {
        btPairCachingGhostObject* ghost = reinterpret_cast<btPairCachingGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convertQuat(env, value, &ghost->getWorldTransform().getBasis());
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsGhostObject
     * Method:    getPhysicsLocation
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsGhostObject_getPhysicsLocation
    (JNIEnv *env, jobject object, jlong objectId, jobject value) {
        btPairCachingGhostObject* ghost = reinterpret_cast<btPairCachingGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convert(env, &ghost->getWorldTransform().getOrigin(), value);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsGhostObject
     * Method:    getPhysicsRotation
     * Signature: (JLcom/jme3/math/Quaternion;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsGhostObject_getPhysicsRotation
    (JNIEnv *env, jobject object, jlong objectId, jobject value) {
        btPairCachingGhostObject* ghost = reinterpret_cast<btPairCachingGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convertQuat(env, &ghost->getWorldTransform().getBasis(), value);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsGhostObject
     * Method:    getPhysicsRotationMatrix
     * Signature: (JLcom/jme3/math/Matrix3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsGhostObject_getPhysicsRotationMatrix
    (JNIEnv *env, jobject object, jlong objectId, jobject value) {
        btPairCachingGhostObject* ghost = reinterpret_cast<btPairCachingGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convert(env, &ghost->getWorldTransform().getBasis(), value);
    }

    class jmeGhostOverlapCallback : public btOverlapCallback {
        JNIEnv* m_env;
        jobject m_object;
        btCollisionObject *m_ghost;
    public:
        jmeGhostOverlapCallback(JNIEnv *env, jobject object, btCollisionObject *ghost)
                :m_env(env),
                 m_object(object),
                 m_ghost(ghost)
        {
        }
        virtual ~jmeGhostOverlapCallback() {}
        virtual bool    processOverlap(btBroadphasePair& pair)
        {
            btCollisionObject *other;
            if(pair.m_pProxy1->m_clientObject == m_ghost){
                other = (btCollisionObject *)pair.m_pProxy0->m_clientObject;
            }else{
                other = (btCollisionObject *)pair.m_pProxy1->m_clientObject;
            }
            jmeUserPointer *up1 = (jmeUserPointer*)other -> getUserPointer();
            jobject javaCollisionObject1 = m_env->NewLocalRef(up1->javaCollisionObject);
            m_env->CallVoidMethod(m_object, jmeClasses::PhysicsGhostObject_addOverlappingObject, javaCollisionObject1);
            m_env->DeleteLocalRef(javaCollisionObject1);
            if (m_env->ExceptionCheck()) {
                m_env->Throw(m_env->ExceptionOccurred());
                return false;
            }

            return false;
        }
    };

    /*
     * Class:     com_jme3_bullet_objects_PhysicsGhostObject
     * Method:    getOverlappingObjects
     * Signature: (J)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsGhostObject_getOverlappingObjects
      (JNIEnv *env, jobject object, jlong objectId) {
        btPairCachingGhostObject* ghost = reinterpret_cast<btPairCachingGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        btHashedOverlappingPairCache * pc = ghost->getOverlappingPairCache();
        jmeGhostOverlapCallback cb(env, object, ghost);
        pc -> processAllOverlappingPairs(&cb, NULL);
    }
    /*
     * Class:     com_jme3_bullet_objects_PhysicsGhostObject
     * Method:    getOverlappingCount
     * Signature: (J)I
     */
    JNIEXPORT jint JNICALL Java_com_jme3_bullet_objects_PhysicsGhostObject_getOverlappingCount
    (JNIEnv *env, jobject object, jlong objectId) {
        btPairCachingGhostObject* ghost = reinterpret_cast<btPairCachingGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return ghost->getNumOverlappingObjects();
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsGhostObject
     * Method:    setCcdSweptSphereRadius
     * Signature: (JF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsGhostObject_setCcdSweptSphereRadius
    (JNIEnv *env, jobject object, jlong objectId, jfloat value) {
        btPairCachingGhostObject* ghost = reinterpret_cast<btPairCachingGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        ghost->setCcdSweptSphereRadius(value);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsGhostObject
     * Method:    setCcdMotionThreshold
     * Signature: (JF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsGhostObject_setCcdMotionThreshold
    (JNIEnv *env, jobject object, jlong objectId, jfloat value) {
        btPairCachingGhostObject* ghost = reinterpret_cast<btPairCachingGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        ghost->setCcdMotionThreshold(value);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsGhostObject
     * Method:    getCcdSweptSphereRadius
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_objects_PhysicsGhostObject_getCcdSweptSphereRadius
    (JNIEnv *env, jobject object, jlong objectId) {
        btPairCachingGhostObject* ghost = reinterpret_cast<btPairCachingGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return ghost->getCcdSweptSphereRadius();
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsGhostObject
     * Method:    getCcdMotionThreshold
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_objects_PhysicsGhostObject_getCcdMotionThreshold
    (JNIEnv *env, jobject object, jlong objectId) {
        btPairCachingGhostObject* ghost = reinterpret_cast<btPairCachingGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return ghost->getCcdMotionThreshold();
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsGhostObject
     * Method:    getCcdSquareMotionThreshold
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_objects_PhysicsGhostObject_getCcdSquareMotionThreshold
    (JNIEnv *env, jobject object, jlong objectId) {
        btPairCachingGhostObject* ghost = reinterpret_cast<btPairCachingGhostObject*>(objectId);
        if (ghost == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return ghost->getCcdSquareMotionThreshold();
    }

#ifdef __cplusplus
}
#endif
