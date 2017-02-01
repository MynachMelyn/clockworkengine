
#include <jni.h>
#include "btBulletDynamicsCommon.h"
#include "btBulletCollisionCommon.h"
#include "BulletCollision/CollisionDispatch/btCollisionDispatcher.h"
#include "BulletCollision/CollisionDispatch/btCollisionObject.h"
#include "BulletCollision/CollisionDispatch/btGhostObject.h"
#include "BulletDynamics/Character/btKinematicCharacterController.h"
#ifdef _WIN32
#include "BulletMultiThreaded/Win32ThreadSupport.h"
#else
#include "BulletMultiThreaded/PosixThreadSupport.h"
#endif
#include "BulletMultiThreaded/btParallelConstraintSolver.h"
#include "BulletMultiThreaded/SpuGatheringCollisionDispatcher.h"
#include "BulletMultiThreaded/SpuCollisionTaskProcess.h"
#include "BulletMultiThreaded/SequentialThreadSupport.h"
#include "BulletCollision/CollisionDispatch/btSimulationIslandManager.h"
#include "BulletCollision/NarrowPhaseCollision/btManifoldPoint.h"
#include "BulletCollision/NarrowPhaseCollision/btPersistentManifold.h"

/**
 * Author: Normen Hansen
 */
class jmePhysicsSpace {
private:
	JNIEnv* env;
	JavaVM* vm;
	btDynamicsWorld* dynamicsWorld;
	jobject javaPhysicsSpace;
        btThreadSupportInterface* createSolverThreadSupport(int);
        btThreadSupportInterface* createDispatchThreadSupport(int);
        void attachThread();
public:
	jmePhysicsSpace(){};
	~jmePhysicsSpace();
        jmePhysicsSpace(JNIEnv*, jobject);
	void stepSimulation(jfloat, jint, jfloat);
        void createPhysicsSpace(jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jint, jboolean);
        btDynamicsWorld* getDynamicsWorld();
        jobject getJavaPhysicsSpace();
        JNIEnv* getEnv();
        static void preTickCallback(btDynamicsWorld*, btScalar);
        static void postTickCallback(btDynamicsWorld*, btScalar);
        static bool contactProcessedCallback(btManifoldPoint &, void *, void *);
};