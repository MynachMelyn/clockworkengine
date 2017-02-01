
#include "jmeClasses.h"
#include "btBulletDynamicsCommon.h"
#include "btBulletCollisionCommon.h"
#include "LinearMath/btVector3.h"

/**
 * Author: Normen Hansen
 */
class jmeBulletUtil{
public:
    static void convert(JNIEnv* env, jobject in, btVector3* out);
    static void convert(JNIEnv* env, const btVector3* in, jobject out);
    static void convert(JNIEnv* env, jobject in, btMatrix3x3* out);
    static void convert(JNIEnv* env, const btMatrix3x3* in, jobject out);
    static void convertQuat(JNIEnv* env, jobject in, btMatrix3x3* out);
    static void convertQuat(JNIEnv* env, const btMatrix3x3* in, jobject out);
    static void addResult(JNIEnv* env, jobject resultlist, const btVector3 hitnormal,const btVector3 m_hitPointWorld,const btScalar  m_hitFraction,const btCollisionObject* hitobject);
private:
    jmeBulletUtil(){};
    ~jmeBulletUtil(){};
    
};

class jmeUserPointer {
public:
    jobject javaCollisionObject;
    jint group;
    jint groups;
    void *space;
};