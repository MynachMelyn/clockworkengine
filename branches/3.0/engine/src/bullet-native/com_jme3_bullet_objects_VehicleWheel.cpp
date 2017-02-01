

/**
 * Author: Normen Hansen
 */

#include "com_jme3_bullet_objects_VehicleWheel.h"
#include "jmeBulletUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_objects_VehicleWheel
     * Method:    getWheelLocation
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_VehicleWheel_getWheelLocation
    (JNIEnv *env, jobject object, jlong vehicleId, jint wheelIndex, jobject out) {
        btRaycastVehicle* vehicle = reinterpret_cast<btRaycastVehicle*>(vehicleId);
        if (vehicle == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convert(env, &vehicle->getWheelInfo(wheelIndex).m_worldTransform.getOrigin(), out);
    }

    /*
     * Class:     com_jme3_bullet_objects_VehicleWheel
     * Method:    getWheelRotation
     * Signature: (JLcom/jme3/math/Matrix3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_VehicleWheel_getWheelRotation
    (JNIEnv *env, jobject object, jlong vehicleId, jint wheelIndex, jobject out) {
        btRaycastVehicle* vehicle = reinterpret_cast<btRaycastVehicle*>(vehicleId);
        if (vehicle == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convert(env, &vehicle->getWheelInfo(wheelIndex).m_worldTransform.getBasis(), out);
    }

    /*
     * Class:     com_jme3_bullet_objects_VehicleWheel
     * Method:    applyInfo
     * Signature: (JFFFFFFFFZF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_VehicleWheel_applyInfo
    (JNIEnv *env, jobject object, jlong vehicleId, jint wheelIndex, jfloat suspensionStiffness, jfloat wheelsDampingRelaxation, jfloat wheelsDampingCompression, jfloat frictionSlip, jfloat rollInfluence, jfloat maxSuspensionTravelCm, jfloat maxSuspensionForce, jfloat radius, jboolean frontWheel, jfloat restLength) {
        btRaycastVehicle* vehicle = reinterpret_cast<btRaycastVehicle*>(vehicleId);
        vehicle->getWheelInfo(wheelIndex).m_suspensionStiffness = suspensionStiffness;
        vehicle->getWheelInfo(wheelIndex).m_wheelsDampingRelaxation = wheelsDampingRelaxation;
        vehicle->getWheelInfo(wheelIndex).m_wheelsDampingCompression = wheelsDampingCompression;
        vehicle->getWheelInfo(wheelIndex).m_frictionSlip = frictionSlip;
        vehicle->getWheelInfo(wheelIndex).m_rollInfluence = rollInfluence;
        vehicle->getWheelInfo(wheelIndex).m_maxSuspensionTravelCm = maxSuspensionTravelCm;
        vehicle->getWheelInfo(wheelIndex).m_maxSuspensionForce = maxSuspensionForce;
        vehicle->getWheelInfo(wheelIndex).m_wheelsRadius = radius;
        vehicle->getWheelInfo(wheelIndex).m_bIsFrontWheel = frontWheel;
        vehicle->getWheelInfo(wheelIndex).m_suspensionRestLength1 = restLength;

    }

    /*
     * Class:     com_jme3_bullet_objects_VehicleWheel
     * Method:    getCollisionLocation
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_VehicleWheel_getCollisionLocation
    (JNIEnv *env, jobject object, jlong vehicleId, jint wheelIndex, jobject out) {
        btRaycastVehicle* vehicle = reinterpret_cast<btRaycastVehicle*>(vehicleId);
        if (vehicle == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convert(env, &vehicle->getWheelInfo(wheelIndex).m_raycastInfo.m_contactPointWS, out);
    }

    /*
     * Class:     com_jme3_bullet_objects_VehicleWheel
     * Method:    getCollisionNormal
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_VehicleWheel_getCollisionNormal
    (JNIEnv *env, jobject object, jlong vehicleId, jint wheelIndex, jobject out) {
        btRaycastVehicle* vehicle = reinterpret_cast<btRaycastVehicle*>(vehicleId);
        if (vehicle == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        jmeBulletUtil::convert(env, &vehicle->getWheelInfo(wheelIndex).m_raycastInfo.m_contactNormalWS, out);
    }

    /*
     * Class:     com_jme3_bullet_objects_VehicleWheel
     * Method:    getSkidInfo
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_objects_VehicleWheel_getSkidInfo
    (JNIEnv *env, jobject object, jlong vehicleId, jint wheelIndex) {
        btRaycastVehicle* vehicle = reinterpret_cast<btRaycastVehicle*>(vehicleId);
        if (vehicle == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return vehicle->getWheelInfo(wheelIndex).m_skidInfo;
    }

    /*
     * Class:     com_jme3_bullet_objects_VehicleWheel
     * Method:    getDeltaRotation
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_objects_VehicleWheel_getDeltaRotation
    (JNIEnv *env, jobject object, jlong vehicleId, jint wheelIndex) {
        btRaycastVehicle* vehicle = reinterpret_cast<btRaycastVehicle*>(vehicleId);
        if (vehicle == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return vehicle->getWheelInfo(wheelIndex).m_deltaRotation;
    }

#ifdef __cplusplus
}
#endif
