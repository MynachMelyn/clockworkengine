

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_joints_HingeJoint.h"
#include "jmeBulletUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_joints_HingeJoint
     * Method:    enableMotor
     * Signature: (JZFF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_HingeJoint_enableMotor
    (JNIEnv * env, jobject object, jlong jointId, jboolean enable, jfloat targetVelocity, jfloat maxMotorImpulse) {
        btHingeConstraint* joint = reinterpret_cast<btHingeConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        joint->enableAngularMotor(enable, targetVelocity, maxMotorImpulse);
    }

    /*
     * Class:     com_jme3_bullet_joints_HingeJoint
     * Method:    getEnableAngularMotor
     * Signature: (J)Z
     */
    JNIEXPORT jboolean JNICALL Java_com_jme3_bullet_joints_HingeJoint_getEnableAngularMotor
    (JNIEnv * env, jobject object, jlong jointId) {
        btHingeConstraint* joint = reinterpret_cast<btHingeConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return false;
        }
        return joint->getEnableAngularMotor();
    }

    /*
     * Class:     com_jme3_bullet_joints_HingeJoint
     * Method:    getMotorTargetVelocity
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_joints_HingeJoint_getMotorTargetVelocity
    (JNIEnv * env, jobject object, jlong jointId) {
        btHingeConstraint* joint = reinterpret_cast<btHingeConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return joint->getMotorTargetVelosity();
    }

    /*
     * Class:     com_jme3_bullet_joints_HingeJoint
     * Method:    getMaxMotorImpulse
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_joints_HingeJoint_getMaxMotorImpulse
    (JNIEnv * env, jobject object, jlong jointId) {
        btHingeConstraint* joint = reinterpret_cast<btHingeConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return joint->getMaxMotorImpulse();
    }

    /*
     * Class:     com_jme3_bullet_joints_HingeJoint
     * Method:    setLimit
     * Signature: (JFF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_HingeJoint_setLimit__JFF
    (JNIEnv * env, jobject object, jlong jointId, jfloat low, jfloat high) {
        btHingeConstraint* joint = reinterpret_cast<btHingeConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        return joint->setLimit(low, high);
    }

    /*
     * Class:     com_jme3_bullet_joints_HingeJoint
     * Method:    setLimit
     * Signature: (JFFFFF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_HingeJoint_setLimit__JFFFFF
    (JNIEnv * env, jobject object, jlong jointId, jfloat low, jfloat high, jfloat softness, jfloat biasFactor, jfloat relaxationFactor) {
        btHingeConstraint* joint = reinterpret_cast<btHingeConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        return joint->setLimit(low, high, softness, biasFactor, relaxationFactor);
    }

    /*
     * Class:     com_jme3_bullet_joints_HingeJoint
     * Method:    getUpperLimit
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_joints_HingeJoint_getUpperLimit
    (JNIEnv * env, jobject object, jlong jointId) {
        btHingeConstraint* joint = reinterpret_cast<btHingeConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return joint->getUpperLimit();
    }

    /*
     * Class:     com_jme3_bullet_joints_HingeJoint
     * Method:    getLowerLimit
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_joints_HingeJoint_getLowerLimit
    (JNIEnv * env, jobject object, jlong jointId) {
        btHingeConstraint* joint = reinterpret_cast<btHingeConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return joint->getLowerLimit();
    }

    /*
     * Class:     com_jme3_bullet_joints_HingeJoint
     * Method:    setAngularOnly
     * Signature: (JZ)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_HingeJoint_setAngularOnly
    (JNIEnv * env, jobject object, jlong jointId, jboolean angular) {
        btHingeConstraint* joint = reinterpret_cast<btHingeConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        joint->setAngularOnly(angular);
    }

    /*
     * Class:     com_jme3_bullet_joints_HingeJoint
     * Method:    getHingeAngle
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_joints_HingeJoint_getHingeAngle
    (JNIEnv * env, jobject object, jlong jointId) {
        btHingeConstraint* joint = reinterpret_cast<btHingeConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return joint->getHingeAngle();
    }

    /*
     * Class:     com_jme3_bullet_joints_HingeJoint
     * Method:    createJoint
     * Signature: (JJLcom/jme3/math/Vector3f;Lcom/jme3/math/Vector3f;Lcom/jme3/math/Vector3f;Lcom/jme3/math/Vector3f;)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_joints_HingeJoint_createJoint
    (JNIEnv * env, jobject object, jlong bodyIdA, jlong bodyIdB, jobject pivotA, jobject axisA, jobject pivotB, jobject axisB) {
        jmeClasses::initJavaClasses(env);
        btRigidBody* bodyA = reinterpret_cast<btRigidBody*>(bodyIdA);
        btRigidBody* bodyB = reinterpret_cast<btRigidBody*>(bodyIdB);
        btVector3 vec1 = btVector3();
        btVector3 vec2 = btVector3();
        btVector3 vec3 = btVector3();
        btVector3 vec4 = btVector3();
        jmeBulletUtil::convert(env, pivotA, &vec1);
        jmeBulletUtil::convert(env, pivotB, &vec2);
        jmeBulletUtil::convert(env, axisA, &vec3);
        jmeBulletUtil::convert(env, axisB, &vec4);
        btHingeConstraint* joint = new btHingeConstraint(*bodyA, *bodyB, vec1, vec2, vec3, vec4);
        return reinterpret_cast<jlong>(joint);
    }
#ifdef __cplusplus
}
#endif
