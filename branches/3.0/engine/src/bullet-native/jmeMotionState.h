
#include <jni.h>

/**
 * Author: Normen Hansen
 */

#include "btBulletDynamicsCommon.h"
//#include "btBulletCollisionCommon.h"

class jmeMotionState : public btMotionState {
private:
    bool dirty;
    btTransform* trans;
public:
    jmeMotionState();
    virtual ~jmeMotionState();

    btTransform worldTransform;
    virtual void getWorldTransform(btTransform& worldTrans) const;
    virtual void setWorldTransform(const btTransform& worldTrans);
    void setKinematicTransform(const btTransform& worldTrans);
    void setKinematicLocation(JNIEnv*, jobject);
    void setKinematicRotation(JNIEnv*, jobject);
    void setKinematicRotationQuat(JNIEnv*, jobject);
    bool applyTransform(JNIEnv* env, jobject location, jobject rotation);
};
