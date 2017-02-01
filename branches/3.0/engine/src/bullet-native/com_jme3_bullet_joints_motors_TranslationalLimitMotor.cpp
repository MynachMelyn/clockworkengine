

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_joints_motors_TranslationalLimitMotor.h"
#include "jmeBulletUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_joints_motors_TranslationalLimitMotor
     * Method:    getLowerLimit
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_motors_TranslationalLimitMotor_getLowerLimit
    (JNIEnv *env, jobject object, jlong motorId, jobject vector) {
        btTranslationalLimitMotor* motor = reinterpret_cast<btTranslationalLimitMotor*>(motorId);
        if (motor == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convert(env, &motor->m_lowerLimit, vector);
    }

    /*
     * Class:     com_jme3_bullet_joints_motors_TranslationalLimitMotor
     * Method:    setLowerLimit
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_motors_TranslationalLimitMotor_setLowerLimit
    (JNIEnv *env, jobject object, jlong motorId, jobject vector) {
        btTranslationalLimitMotor* motor = reinterpret_cast<btTranslationalLimitMotor*>(motorId);
        if (motor == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convert(env, vector, &motor->m_lowerLimit);
    }

    /*
     * Class:     com_jme3_bullet_joints_motors_TranslationalLimitMotor
     * Method:    getUpperLimit
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_motors_TranslationalLimitMotor_getUpperLimit
    (JNIEnv *env, jobject object, jlong motorId, jobject vector) {
        btTranslationalLimitMotor* motor = reinterpret_cast<btTranslationalLimitMotor*>(motorId);
        if (motor == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convert(env, &motor->m_upperLimit, vector);
    }

    /*
     * Class:     com_jme3_bullet_joints_motors_TranslationalLimitMotor
     * Method:    setUpperLimit
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_motors_TranslationalLimitMotor_setUpperLimit
    (JNIEnv *env, jobject object, jlong motorId, jobject vector) {
        btTranslationalLimitMotor* motor = reinterpret_cast<btTranslationalLimitMotor*>(motorId);
        if (motor == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convert(env, vector, &motor->m_upperLimit);
    }

    /*
     * Class:     com_jme3_bullet_joints_motors_TranslationalLimitMotor
     * Method:    getAccumulatedImpulse
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_motors_TranslationalLimitMotor_getAccumulatedImpulse
    (JNIEnv *env, jobject object, jlong motorId, jobject vector) {
        btTranslationalLimitMotor* motor = reinterpret_cast<btTranslationalLimitMotor*>(motorId);
        if (motor == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convert(env, &motor->m_accumulatedImpulse, vector);
    }

    /*
     * Class:     com_jme3_bullet_joints_motors_TranslationalLimitMotor
     * Method:    setAccumulatedImpulse
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_motors_TranslationalLimitMotor_setAccumulatedImpulse
    (JNIEnv *env, jobject object, jlong motorId, jobject vector) {
        btTranslationalLimitMotor* motor = reinterpret_cast<btTranslationalLimitMotor*>(motorId);
        if (motor == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convert(env, vector, &motor->m_accumulatedImpulse);
    }

    /*
     * Class:     com_jme3_bullet_joints_motors_TranslationalLimitMotor
     * Method:    getLimitSoftness
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_joints_motors_TranslationalLimitMotor_getLetLimitSoftness
    (JNIEnv *env, jobject object, jlong motorId) {
        btTranslationalLimitMotor* motor = reinterpret_cast<btTranslationalLimitMotor*>(motorId);
        if (motor == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return motor->m_limitSoftness;
    }

    /*
     * Class:     com_jme3_bullet_joints_motors_TranslationalLimitMotor
     * Method:    setLimitSoftness
     * Signature: (JF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_motors_TranslationalLimitMotor_setLimitSoftness
    (JNIEnv *env, jobject object, jlong motorId, jfloat value) {
        btTranslationalLimitMotor* motor = reinterpret_cast<btTranslationalLimitMotor*>(motorId);
        if (motor == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        motor->m_limitSoftness = value;
    }

    /*
     * Class:     com_jme3_bullet_joints_motors_TranslationalLimitMotor
     * Method:    getDamping
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_joints_motors_TranslationalLimitMotor_getDamping
    (JNIEnv *env, jobject object, jlong motorId) {
        btTranslationalLimitMotor* motor = reinterpret_cast<btTranslationalLimitMotor*>(motorId);
        if (motor == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return motor->m_damping;
    }

    /*
     * Class:     com_jme3_bullet_joints_motors_TranslationalLimitMotor
     * Method:    setDamping
     * Signature: (JF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_motors_TranslationalLimitMotor_setDamping
    (JNIEnv *env, jobject object, jlong motorId, jfloat value) {
        btTranslationalLimitMotor* motor = reinterpret_cast<btTranslationalLimitMotor*>(motorId);
        if (motor == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        motor->m_damping = value;
    }

    /*
     * Class:     com_jme3_bullet_joints_motors_TranslationalLimitMotor
     * Method:    getRestitution
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_joints_motors_TranslationalLimitMotor_getRestitution
    (JNIEnv *env, jobject object, jlong motorId) {
        btTranslationalLimitMotor* motor = reinterpret_cast<btTranslationalLimitMotor*>(motorId);
        if (motor == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return motor->m_restitution;
    }

    /*
     * Class:     com_jme3_bullet_joints_motors_TranslationalLimitMotor
     * Method:    setRestitution
     * Signature: (JF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_joints_motors_TranslationalLimitMotor_setRestitution
    (JNIEnv *env, jobject object, jlong motorId, jfloat value) {
        btTranslationalLimitMotor* motor = reinterpret_cast<btTranslationalLimitMotor*>(motorId);
        if (motor == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        motor->m_restitution = value;
    }

#ifdef __cplusplus
}
#endif
