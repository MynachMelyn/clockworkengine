

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_joints_ConeJoint.h"
#include "jmeBulletUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_joints_ConeJoint
     * Method:    setLimit
     * Signature: (JFFF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_ConeJoint_setLimit
    (JNIEnv * env, jobject object, jlong jointId, jfloat swingSpan1, jfloat swingSpan2, jfloat twistSpan) {
        btConeTwistConstraint* joint = reinterpret_cast<btConeTwistConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        //TODO: extended setLimit!
        joint->setLimit(swingSpan1, swingSpan2, twistSpan);
    }

    /*
     * Class:     com_jme3_bullet_joints_ConeJoint
     * Method:    setAngularOnly
     * Signature: (JZ)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_ConeJoint_setAngularOnly
    (JNIEnv * env, jobject object, jlong jointId, jboolean angularOnly) {
        btConeTwistConstraint* joint = reinterpret_cast<btConeTwistConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        joint->setAngularOnly(angularOnly);
    }

    /*
     * Class:     com_jme3_bullet_joints_ConeJoint
     * Method:    createJoint
     * Signature: (JJLcom/jme3/math/Vector3f;Lcom/jme3/math/Matrix3f;Lcom/jme3/math/Vector3f;Lcom/jme3/math/Matrix3f;)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_joints_ConeJoint_createJoint
    (JNIEnv * env, jobject object, jlong bodyIdA, jlong bodyIdB, jobject pivotA, jobject rotA, jobject pivotB, jobject rotB) {
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
        btConeTwistConstraint* joint = new btConeTwistConstraint(*bodyA, *bodyB, transA, transB);
        return reinterpret_cast<jlong>(joint);
    }

#ifdef __cplusplus
}
#endif
