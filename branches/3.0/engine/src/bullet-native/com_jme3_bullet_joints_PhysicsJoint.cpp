

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_joints_PhysicsJoint.h"
#include "jmeBulletUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_joints_PhysicsJoint
     * Method:    getAppliedImpulse
     * Signature: (J)F
     */
    JNIEXPORT jfloat JNICALL Java_com_jme3_bullet_joints_PhysicsJoint_getAppliedImpulse
    (JNIEnv * env, jobject object, jlong jointId) {
        btTypedConstraint* joint = reinterpret_cast<btTypedConstraint*>(jointId);
        if (joint == NULL) {
            jclass newExc = env->FindClass("java/lang/NullPointerException");
            env->ThrowNew(newExc, "The native object does not exist.");
            return 0;
        }
        return joint->getAppliedImpulse();
    }

#ifdef __cplusplus
}
#endif
