

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_collision_shapes_CapsuleCollisionShape.h"
#include "jmeBulletUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_collision_shapes_CapsuleCollisionShape
     * Method:    createShape
     * Signature: (IFF)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_collision_shapes_CapsuleCollisionShape_createShape
    (JNIEnv * env, jobject object, jint axis, jfloat radius, jfloat height) {
        jmeClasses::initJavaClasses(env);
        btCollisionShape* shape;
        switch(axis){
            case 0:
                shape = new btCapsuleShapeX(radius, height);
                break;
            case 1:
                shape = new btCapsuleShape(radius, height);
                break;
            case 2:
                shape = new btCapsuleShapeZ(radius, height);
                break;
        }
        return reinterpret_cast<jlong>(shape);
    }

#ifdef __cplusplus
}
#endif
