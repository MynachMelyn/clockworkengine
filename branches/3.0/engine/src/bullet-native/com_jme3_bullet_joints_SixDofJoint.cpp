

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_joints_SixDofJoint.h"
#include "jmeBulletUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_joints_SixDofJoint
     * Method:    getRotationalLimitMotor
     * Signature: (JI)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_joints_SixDofJoint_getRotationalLimitMotor
    (JNIEnv * env, jobject object, jlong jointId, jint index) {
        btGeneric6DofConstraint* joint = reinterpret_cast<btGeneric6DofConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return reinterpret_cast<jlong>(joint->getRotationalLimitMotor(index));
    }

    /*
     * Class:     com_jme3_bullet_joints_SixDofJoint
     * Method:    getTranslationalLimitMotor
     * Signature: (J)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_joints_SixDofJoint_getTranslationalLimitMotor
    (JNIEnv * env, jobject object, jlong jointId) {
        btGeneric6DofConstraint* joint = reinterpret_cast<btGeneric6DofConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return reinterpret_cast<jlong>(joint->getTranslationalLimitMotor());
    }

    /*
     * Class:     com_jme3_bullet_joints_SixDofJoint
     * Method:    setLinearUpperLimit
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_SixDofJoint_setLinearUpperLimit
    (JNIEnv * env, jobject object, jlong jointId, jobject vector) {
        btGeneric6DofConstraint* joint = reinterpret_cast<btGeneric6DofConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        btVector3 vec = btVector3();
        jmeBulletUtil::convert(env, vector, &vec);
        joint->setLinearUpperLimit(vec);
    }

    /*
     * Class:     com_jme3_bullet_joints_SixDofJoint
     * Method:    setLinearLowerLimit
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_SixDofJoint_setLinearLowerLimit
    (JNIEnv * env, jobject object, jlong jointId, jobject vector) {
        btGeneric6DofConstraint* joint = reinterpret_cast<btGeneric6DofConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        btVector3 vec = btVector3();
        jmeBulletUtil::convert(env, vector, &vec);
        joint->setLinearLowerLimit(vec);
    }

    /*
     * Class:     com_jme3_bullet_joints_SixDofJoint
     * Method:    setAngularUpperLimit
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_SixDofJoint_setAngularUpperLimit
    (JNIEnv * env, jobject object, jlong jointId, jobject vector) {
        btGeneric6DofConstraint* joint = reinterpret_cast<btGeneric6DofConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        btVector3 vec = btVector3();
        jmeBulletUtil::convert(env, vector, &vec);
        joint->setAngularUpperLimit(vec);
    }

    /*
     * Class:     com_jme3_bullet_joints_SixDofJoint
     * Method:    setAngularLowerLimit
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_SixDofJoint_setAngularLowerLimit
    (JNIEnv * env, jobject object, jlong jointId, jobject vector) {
        btGeneric6DofConstraint* joint = reinterpret_cast<btGeneric6DofConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        btVector3 vec = btVector3();
        jmeBulletUtil::convert(env, vector, &vec);
        joint->setAngularLowerLimit(vec);
    }

    /*
     * Class:     com_jme3_bullet_joints_SixDofJoint
     * Method:    createJoint
     * Signature: (JJLcom/jme3/math/Vector3f;Lcom/jme3/math/Matrix3f;Lcom/jme3/math/Vector3f;Lcom/jme3/math/Matrix3f;Z)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_joints_SixDofJoint_createJoint
    (JNIEnv * env, jobject object, jlong bodyIdA, jlong bodyIdB, jobject pivotA, jobject rotA, jobject pivotB, jobject rotB, jboolean useLinearReferenceFrameA) {
        jmeClasses::initJavaClasses(env);
        btRigidBody* bodyA = reinterpret_cast<btRigidBody*>(bodyIdA);
        btRigidBody* bodyB = reinterpret_cast<btRigidBody*>(bodyIdB);
        btMatrix3x3 mtx1 = btMatrix3x3();
        btMatrix3x3 mtx2 = btMatrix3x3();
        btTransform transA = btTransform(mtx1);
        jmeBulletUtil::convert(env, pivotA, &transA.getOrigin());
        jmeBulletUtil::convert(env, rotA, &transA.getBasis());
        btTransform transB = btTransform(mtx2);
        jmeBulletUtil::convert(env, pivotB, &transB.getOrigin());
        jmeBulletUtil::convert(env, rotB, &transB.getBasis());
        btGeneric6DofConstraint* joint = new btGeneric6DofConstraint(*bodyA, *bodyB, transA, transB, useLinearReferenceFrameA);
        return reinterpret_cast<jlong>(joint);
    }
#ifdef __cplusplus
}
#endif
