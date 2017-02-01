

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_collision_shapes_HeightfieldCollisionShape.h"
#include "jmeBulletUtil.h"
#include "BulletCollision/CollisionShapes/btHeightfieldTerrainShape.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_collision_shapes_HeightfieldCollisionShape
     * Method:    createShape
     * Signature: (II[FFFFIZ)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_collision_shapes_HeightfieldCollisionShape_createShape
    (JNIEnv * env, jobject object, jint heightStickWidth, jint heightStickLength, jobject heightfieldData, jfloat heightScale, jfloat minHeight, jfloat maxHeight, jint upAxis, jboolean flipQuadEdges) {
        jmeClasses::initJavaClasses(env);
        void* data = env->GetDirectBufferAddress(heightfieldData);
        btHeightfieldTerrainShape* shape=new btHeightfieldTerrainShape(heightStickWidth, heightStickLength, data, heightScale, minHeight, maxHeight, upAxis, PHY_FLOAT, flipQuadEdges);
        return reinterpret_cast<jlong>(shape);
    }

#ifdef __cplusplus
}
#endif
