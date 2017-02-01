

/**
 * Author: Normen Hansen
 */

#include "com_jme3_bullet_objects_PhysicsVehicle.h"
#include "jmeBulletUtil.h"
#include "jmePhysicsSpace.h"
#include "BulletDynamics/Vehicle/btRaycastVehicle.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_objects_PhysicsVehicle
     * Method:    updateWheelTransform
     * Signature: (JIZ)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsVehicle_updateWheelTransform
    (JNIEnv *env, jobject object, jlong vehicleId, jint wheel, jboolean interpolated) {
        btRaycastVehicle* vehicle = reinterpret_cast<btRaycastVehicle*>(vehicleId);
        if (vehicle == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        vehicle->updateWheelTransform(wheel, interpolated);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsVehicle
     * Method:    createVehicleRaycaster
     * Signature: (JJ)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_objects_PhysicsVehicle_createVehicleRaycaster
    (JNIEnv *env, jobject object, jlong bodyId, jlong spaceId) {
        //btRigidBody* body = reinterpret_cast<btRigidBody*> bodyId;
        jmeClasses::initJavaClasses(env);
        jmePhysicsSpace *space = reinterpret_cast<jmePhysicsSpace*>(spaceId);
        if (space == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        btDefaultVehicleRaycaster* caster = new btDefaultVehicleRaycaster(space->getDynamicsWorld());
        return reinterpret_cast<jlong>(caster);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsVehicle
     * Method:    createRaycastVehicle
     * Signature: (JJ)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_objects_PhysicsVehicle_createRaycastVehicle
    (JNIEnv *env, jobject object, jlong objectId, jlong casterId) {
        jmeClasses::initJavaClasses(env);
        btRigidBody* body = reinterpret_cast<btRigidBody*>(objectId);
        if (body == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        body->setActivationState(DISABLE_DEACTIVATION);
        btVehicleRaycaster* caster = reinterpret_cast<btDefaultVehicleRaycaster*>(casterId);
        if (caster == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        btRaycastVehicle::btVehicleTuning tuning;
        btRaycastVehicle* vehicle = new btRaycastVehicle(tuning, body, caster);
        return reinterpret_cast<jlong>(vehicle);

    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsVehicle
     * Method:    setCoordinateSystem
     * Signature: (JIII)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsVehicle_setCoordinateSystem
    (JNIEnv *env, jobject object, jlong vehicleId, jint right, jint up, jint forward) {
        btRaycastVehicle* vehicle = reinterpret_cast<btRaycastVehicle*>(vehicleId);
        if (vehicle == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        vehicle->setCoordinateSystem(right, up, forward);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsVehicle
     * Method:    addWheel
     * Signature: (JLcom/jme3/math/Vector3f;Lcom/jme3/math/Vector3f;Lcom/jme3/math/Vector3f;FFLcom/jme3/bullet/objects/infos/VehicleTuning;Z)J
     */
    JNIEXPORT jint JNICALL Java_com_jme3_bullet_objects_PhysicsVehicle_addWheel
    (JNIEnv *env, jobject object, jlong vehicleId, jobject location, jobject direction, jobject axle, jfloat restLength, jfloat radius, jobject tuning, jboolean frontWheel) {
        btRaycastVehicle* vehicle = reinterpret_cast<btRaycastVehicle*>(vehicleId);
        if (vehicle == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        btVector3 vec1 = btVector3();
        btVector3 vec2 = btVector3();
        btVector3 vec3 = btVector3();
        jmeBulletUtil::convert(env, location, &vec1);
        jmeBulletUtil::convert(env, direction, &vec2);
        jmeBulletUtil::convert(env, axle, &vec3);
        btRaycastVehicle::btVehicleTuning tune;
        btWheelInfo* info = &vehicle->addWheel(vec1, vec2, vec3, restLength, radius, tune, frontWheel);
        int idx = vehicle->getNumWheels();
        return idx-1;
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsVehicle
     * Method:    resetSuspension
     * Signature: (J)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsVehicle_resetSuspension
    (JNIEnv *env, jobject object, jlong vehicleId) {
        btRaycastVehicle* vehicle = reinterpret_cast<btRaycastVehicle*>(vehicleId);
        if (vehicle == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        vehicle->resetSuspension();
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsVehicle
     * Method:    applyEngineForce
     * Signature: (JIF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsVehicle_applyEngineForce
    (JNIEnv *env, jobject object, jlong vehicleId, jint wheel, jfloat force) {
        btRaycastVehicle* vehicle = reinterpret_cast<btRaycastVehicle*>(vehicleId);
        if (vehicle == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        vehicle->applyEngineForce(force, wheel);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsVehicle
     * Method:    steer
     * Signature: (JIF)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsVehicle_steer
    (JNIEnv *env, jobject object, jlong vehicleId, jint wheel, jfloat value) {
        btRaycastVehicle* vehicle = reinterpret_cast<btRaycastVehicle*>(vehicleId);
        if (vehicle == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        vehicle->setSteeringValue(value, wheel);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsVehicle
     * Method:    brake
     * Signature: (JIF)F
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsVehicle_brake
    (JNIEnv *env, jobject object, jlong vehicleId, jint wheel, jfloat value) {
        btRaycastVehicle* vehicle = reinterpret_cast<btRaycastVehicle*>(vehicleId);
        if (vehicle == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        vehicle->setBrake(value, wheel);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsVehicle
     * Method:    getCurrentVehicleSpeedKmHour
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_objects_PhysicsVehicle_getCurrentVehicleSpeedKmHour
    (JNIEnv *env, jobject object, jlong vehicleId) {
        btRaycastVehicle* vehicle = reinterpret_cast<btRaycastVehicle*>(vehicleId);
        if (vehicle == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return vehicle->getCurrentSpeedKmHour();
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsVehicle
     * Method:    getForwardVector
     * Signature: (JLcom/jme3/math/Vector3f;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsVehicle_getForwardVector
    (JNIEnv *env, jobject object, jlong vehicleId, jobject out) {
        btRaycastVehicle* vehicle = reinterpret_cast<btRaycastVehicle*>(vehicleId);
        if (vehicle == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        btVector3 forwardVector = vehicle->getForwardVector();
        jmeBulletUtil::convert(env, &forwardVector, out);
    }

    /*
     * Class:     com_jme3_bullet_objects_PhysicsVehicle
     * Method:    finalizeNative
     * Signature: (JJ)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_objects_PhysicsVehicle_finalizeNative
    (JNIEnv *env, jobject object, jlong casterId, jlong vehicleId) {
        btVehicleRaycaster* rayCaster = reinterpret_cast<btVehicleRaycaster*>(casterId);
        btRaycastVehicle* vehicle = reinterpret_cast<btRaycastVehicle*>(vehicleId);
        if (vehicle == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        delete(vehicle);
        if (rayCaster == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return;
        }
        delete(rayCaster);
    }

#ifdef __cplusplus
}
#endif

