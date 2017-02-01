

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_collision_shapes_CylinderCollisionShape.h"
#include "jmeBulletUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_collision_shapes_CylinderCollisionShape
     * Method:    createShape
     * Signature: (ILcom/jme3/math/Vector3f;)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_collision_shapes_CylinderCollisionShape_createShape
    (JNIEnv * env, jobject object, jint axis, jobject halfExtents) {
        jmeClasses::initJavaClasses(env);
        btVector3 extents = btVector3();
        jmeBulletUtil::convert(env, halfExtents, &extents);
        btCollisionShape* shape;
        switch (axis) {
            case 0:
                shape = new btCylinderShapeX(extents);
                break;
            case 1:
                shape = new btCylinderShape(extents);
                break;
            case 2:
                shape = new btCylinderShapeZ(extents);
                break;
        }
        return reinterpret_cast<jlong>(shape);
    }

#ifdef __cplusplus
}
#endif
