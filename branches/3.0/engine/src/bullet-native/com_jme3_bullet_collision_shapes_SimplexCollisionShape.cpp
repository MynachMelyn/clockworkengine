

/**
 * Author: Normen Hansen
 */
#include "com_jme3_bullet_collision_shapes_SimplexCollisionShape.h"
#include "jmeBulletUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     com_jme3_bullet_collision_shapes_SimplexCollisionShape
     * Method:    createShape
     * Signature: (Lcom/jme3/math/Vector3f;)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_collision_shapes_SimplexCollisionShape_createShape__Lcom_jme3_math_Vector3f_2
    (JNIEnv *env, jobject object, jobject vector1) {
        jmeClasses::initJavaClasses(env);
        btVector3 vec1 = btVector3();
        jmeBulletUtil::convert(env, vector1, &vec1);
        btBU_Simplex1to4* simplexShape = new btBU_Simplex1to4(vec1);
        return reinterpret_cast<jlong>(simplexShape);
    }

    /*
     * Class:     com_jme3_bullet_collision_shapes_SimplexCollisionShape
     * Method:    createShape
     * Signature: (Lcom/jme3/math/Vector3f;Lcom/jme3/math/Vector3f;)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_collision_shapes_SimplexCollisionShape_createShape__Lcom_jme3_math_Vector3f_2Lcom_jme3_math_Vector3f_2
    (JNIEnv *env, jobject object, jobject vector1, jobject vector2) {
        jmeClasses::initJavaClasses(env);
        btVector3 vec1 = btVector3();
        jmeBulletUtil::convert(env, vector1, &vec1);
        btVector3 vec2 = btVector3();
        jmeBulletUtil::convert(env, vector2, &vec2);
        btBU_Simplex1to4* simplexShape = new btBU_Simplex1to4(vec1, vec2);
        return reinterpret_cast<jlong>(simplexShape);
    }
    /*
     * Class:     com_jme3_bullet_collision_shapes_SimplexCollisionShape
     * Method:    createShape
     * Signature: (Lcom/jme3/math/Vector3f;Lcom/jme3/math/Vector3f;Lcom/jme3/math/Vector3f;)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_collision_shapes_SimplexCollisionShape_createShape__Lcom_jme3_math_Vector3f_2Lcom_jme3_math_Vector3f_2Lcom_jme3_math_Vector3f_2
    (JNIEnv * env, jobject object, jobject vector1, jobject vector2, jobject vector3) {
        jmeClasses::initJavaClasses(env);
        btVector3 vec1 = btVector3();
        jmeBulletUtil::convert(env, vector1, &vec1);
        btVector3 vec2 = btVector3();
        jmeBulletUtil::convert(env, vector2, &vec2);
        btVector3 vec3 = btVector3();
        jmeBulletUtil::convert(env, vector3, &vec3);
        btBU_Simplex1to4* simplexShape = new btBU_Simplex1to4(vec1, vec2, vec3);
        return reinterpret_cast<jlong>(simplexShape);
    }
    /*
     * Class:     com_jme3_bullet_collision_shapes_SimplexCollisionShape
     * Method:    createShape
     * Signature: (Lcom/jme3/math/Vector3f;Lcom/jme3/math/Vector3f;Lcom/jme3/math/Vector3f;Lcom/jme3/math/Vector3f;)J
     */
    JNIEXPORT jlong JNICALL Java_com_jme3_bullet_collision_shapes_SimplexCollisionShape_createShape__Lcom_jme3_math_Vector3f_2Lcom_jme3_math_Vector3f_2Lcom_jme3_math_Vector3f_2Lcom_jme3_math_Vector3f_2
    (JNIEnv * env, jobject object, jobject vector1, jobject vector2, jobject vector3, jobject vector4) {
        jmeClasses::initJavaClasses(env);
        btVector3 vec1 = btVector3();
        jmeBulletUtil::convert(env, vector1, &vec1);
        btVector3 vec2 = btVector3();
        jmeBulletUtil::convert(env, vector2, &vec2);
        btVector3 vec3 = btVector3();
        jmeBulletUtil::convert(env, vector3, &vec3);
        btVector3 vec4 = btVector3();
        jmeBulletUtil::convert(env, vector4, &vec4);
        btBU_Simplex1to4* simplexShape = new btBU_Simplex1to4(vec1, vec2, vec3, vec4);
        return reinterpret_cast<jlong>(simplexShape);
    }
#ifdef __cplusplus
}
#endif
