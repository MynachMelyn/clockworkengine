
#include "jmeMotionState.h"
#include "jmeBulletUtil.h"

/**
 * Author: Normen Hansen
 */

jmeMotionState::jmeMotionState() {
    trans = new btTransform();
    trans -> setIdentity();
    worldTransform = *trans;
    dirty = true;
}

void jmeMotionState::getWorldTransform(btTransform& worldTrans) const {
    worldTrans = worldTransform;
}

void jmeMotionState::setWorldTransform(const btTransform& worldTrans) {
    worldTransform = worldTrans;
    dirty = true;
}

void jmeMotionState::setKinematicTransform(const btTransform& worldTrans) {
    worldTransform = worldTrans;
    dirty = true;
}

void jmeMotionState::setKinematicLocation(JNIEnv* env, jobject location) {
    jmeBulletUtil::convert(env, location, &worldTransform.getOrigin());
    dirty = true;
}

void jmeMotionState::setKinematicRotation(JNIEnv* env, jobject rotation) {
    jmeBulletUtil::convert(env, rotation, &worldTransform.getBasis());
    dirty = true;
}

void jmeMotionState::setKinematicRotationQuat(JNIEnv* env, jobject rotation) {
    jmeBulletUtil::convertQuat(env, rotation, &worldTransform.getBasis());
    dirty = true;
}

bool jmeMotionState::applyTransform(JNIEnv* env, jobject location, jobject rotation) {
    if (dirty) {
        //        fprintf(stdout, "Apply world translation\n");
        //        fflush(stdout);
        jmeBulletUtil::convert(env, &worldTransform.getOrigin(), location);
        jmeBulletUtil::convertQuat(env, &worldTransform.getBasis(), rotation);
        dirty = false;
        return true;
    }
    return false;
}

jmeMotionState::~jmeMotionState() {
    free(trans);
}
