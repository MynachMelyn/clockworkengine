

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_collision_shapes_ConeCollisionShape.h"
#include "jmeBulletUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_collision_shapes_ConeCollisionShape
     * Method:    createShape
     * Signature: (IFF)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_collision_shapes_ConeCollisionShape_createShape
    (JNIEnv * env, jobject object, jint axis, jfloat radius, jfloat height) {
        jmeClasses::initJavaClasses(env);
        btCollisionShape* shape;
        switch (axis) {
            case 0:
                shape = new btConeShapeX(radius, height);
                break;
            case 1:
                shape = new btConeShape(radius, height);
                break;
            case 2:
                shape = new btConeShapeZ(radius, height);
                break;
        }
        return reinterpret_cast<jlong>(shape);
    }

#ifdef __cplusplus
}
#endif
