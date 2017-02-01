
#include <jni.h>

/**
 * Author: Normen Hansen
 */

class jmeClasses {
public:
    static void initJavaClasses(JNIEnv* env);
//    static JNIEnv* env;
    static JavaVM* vm;
    static jclass PhysicsSpace;
    static jmethodID PhysicsSpace_preTick;
    static jmethodID PhysicsSpace_postTick;
    static jmethodID PhysicsSpace_addCollisionEvent;
    static jclass PhysicsGhostObject;
    static jmethodID PhysicsGhostObject_addOverlappingObject;

    static jclass Vector3f;
    static jmethodID Vector3f_set;
    static jmethodID Vector3f_getX;
    static jmethodID Vector3f_getY;
    static jmethodID Vector3f_getZ;
    static jmethodID Vector3f_toArray;
    static jfieldID Vector3f_x;
    static jfieldID Vector3f_y;
    static jfieldID Vector3f_z;
    
    static jclass Quaternion;
    static jmethodID Quaternion_set;
    static jmethodID Quaternion_getX;
    static jmethodID Quaternion_getY;
    static jmethodID Quaternion_getZ;
    static jmethodID Quaternion_getW;
    static jfieldID Quaternion_x;
    static jfieldID Quaternion_y;
    static jfieldID Quaternion_z;
    static jfieldID Quaternion_w;

    static jclass Matrix3f;
    static jmethodID Matrix3f_get;
    static jmethodID Matrix3f_set;
    static jfieldID Matrix3f_m00;
    static jfieldID Matrix3f_m01;
    static jfieldID Matrix3f_m02;
    static jfieldID Matrix3f_m10;
    static jfieldID Matrix3f_m11;
    static jfieldID Matrix3f_m12;
    static jfieldID Matrix3f_m20;
    static jfieldID Matrix3f_m21;
    static jfieldID Matrix3f_m22;

    static jclass PhysicsRay_Class;
    static jmethodID PhysicsRay_newSingleResult;
    static jfieldID PhysicsRay_normalInWorldSpace;
    static jfieldID PhysicsRay_hitfraction;
    static jfieldID PhysicsRay_collisionObject;
    static jclass PhysicsRay_listresult;
    static jmethodID PhysicsRay_addmethod;

    static jclass DebugMeshCallback;
    static jmethodID DebugMeshCallback_addVector;

    static void throwNPE(JNIEnv* env);
private:
    jmeClasses(){};
    ~jmeClasses(){};
};