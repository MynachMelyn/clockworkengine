

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_collision_shapes_HullCollisionShape.h"
#include "jmeBulletUtil.h"
#include "BulletCollision/CollisionShapes/btConvexHullShape.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_collision_shapes_HullCollisionShape
     * Method:    createShape
     * Signature: ([F)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_collision_shapes_HullCollisionShape_createShape
    (JNIEnv *env, jobject object, jobject array) {
        jmeClasses::initJavaClasses(env);
        float* data = (float*) env->GetDirectBufferAddress(array);
        //TODO: capacity will not always be length!
        int length = env->GetDirectBufferCapacity(array)/4;
        btConvexHullShape* shape = new btConvexHullShape();
        for (int i = 0; i < length; i+=3) {
            btVector3 vect = btVector3(data[i],
                    data[i + 1],
                    data[i + 2]);
            
            shape->addPoint(vect);
        }

        return reinterpret_cast<jlong>(shape);
    }

#ifdef __cplusplus
}
#endif
