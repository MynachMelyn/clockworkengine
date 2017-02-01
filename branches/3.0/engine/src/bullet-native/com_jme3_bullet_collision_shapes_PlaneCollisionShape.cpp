

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_collision_shapes_PlaneCollisionShape.h"
#include "jmeBulletUtil.h"
#include "BulletCollision/CollisionShapes/btStaticPlaneShape.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_collision_shapes_PlaneCollisionShape
     * Method:    createShape
     * Signature: (Lcom/jme3/math/Vector3f;F)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_collision_shapes_PlaneCollisionShape_createShape
    (JNIEnv * env, jobject object, jobject normal, jfloat constant) {
        jmeClasses::initJavaClasses(env);
        btVector3 norm = btVector3();
        jmeBulletUtil::convert(env, normal, &norm);
        btStaticPlaneShape* shape = new btStaticPlaneShape(norm, constant);
        return reinterpret_cast<jlong>(shape);
    }

#ifdef __cplusplus
}
#endif
