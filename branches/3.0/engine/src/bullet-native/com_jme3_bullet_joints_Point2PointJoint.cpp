

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_joints_Point2PointJoint.h"
#include "jmeBulletUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_joints_Point2PointJoint
     * Method:    setDamping
     * Signature: (JF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_Point2PointJoint_setDamping
    (JNIEnv * env, jobject object, jlong jointId, jfloat damping) {
        btPoint2PointConstraint* joint = reinterpret_cast<btPoint2PointConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        joint->m_setting.m_damping = damping;
    }

    /*
     * Class:     com_jme3_bullet_joints_Point2PointJoint
     * Method:    setImpulseClamp
     * Signature: (JF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_Point2PointJoint_setImpulseClamp
    (JNIEnv * env, jobject object, jlong jointId, jfloat clamp) {
        btPoint2PointConstraint* joint = reinterpret_cast<btPoint2PointConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        joint->m_setting.m_impulseClamp = clamp;
    }

    /*
     * Class:     com_jme3_bullet_joints_Point2PointJoint
     * Method:    setTau
     * Signature: (JF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_Point2PointJoint_setTau
    (JNIEnv * env, jobject object, jlong jointId, jfloat tau) {
        btPoint2PointConstraint* joint = reinterpret_cast<btPoint2PointConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        joint->m_setting.m_tau = tau;
    }

    /*
     * Class:     com_jme3_bullet_joints_Point2PointJoint
     * Method:    getDamping
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_joints_Point2PointJoint_getDamping
    (JNIEnv * env, jobject object, jlong jointId) {
        btPoint2PointConstraint* joint = reinterpret_cast<btPoint2PointConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return joint->m_setting.m_damping;
    }

    /*
     * Class:     com_jme3_bullet_joints_Point2PointJoint
     * Method:    getImpulseClamp
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_joints_Point2PointJoint_getImpulseClamp
    (JNIEnv * env, jobject object, jlong jointId) {
        btPoint2PointConstraint* joint = reinterpret_cast<btPoint2PointConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return joint->m_setting.m_damping;
    }

    /*
     * Class:     com_jme3_bullet_joints_Point2PointJoint
     * Method:    getTau
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_joints_Point2PointJoint_getTau
    (JNIEnv * env, jobject object, jlong jointId) {
        btPoint2PointConstraint* joint = reinterpret_cast<btPoint2PointConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return joint->m_setting.m_damping;
    }

    /*
     * Class:     com_jme3_bullet_joints_Point2PointJoint
     * Method:    createJoint
     * Signature: (JJLcom/jme3/math/Vector3f;Lcom/jme3/math/Vector3f;)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_joints_Point2PointJoint_createJoint
    (JNIEnv * env, jobject object, jlong bodyIdA, jlong bodyIdB, jobject pivotA, jobject pivotB) {
        jmeClasses::initJavaClasses(env);
        btRigidBody* bodyA = reinterpret_cast<btRigidBody*>(bodyIdA);
        btRigidBody* bodyB = reinterpret_cast<btRigidBody*>(bodyIdB);
        //TODO: matrix not needed?
        btMatrix3x3 mtx1 = btMatrix3x3();
        btMatrix3x3 mtx2 = btMatrix3x3();
        btTransform transA = btTransform(mtx1);
        jmeBulletUtil::convert(env, pivotA, &transA.getOrigin());
        btTransform transB = btTransform(mtx2);
        jmeBulletUtil::convert(env, pivotB, &transB.getOrigin());
        btHingeConstraint* joint = new btHingeConstraint(*bodyA, *bodyB, transA, transB);
        return reinterpret_cast<jlong>(joint);
    }

#ifdef __cplusplus
}
#endif
