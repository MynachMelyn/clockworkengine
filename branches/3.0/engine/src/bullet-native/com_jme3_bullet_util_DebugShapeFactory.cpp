

/**
 * Author: Normen Hansen, CJ Hare
 */
#include "com_jme3_bullet_util_DebugShapeFactory.h"
#include "jmeBulletUtil.h"
#include "BulletCollision/CollisionShapes/btShapeHull.h"

class DebugCallback : public btTriangleCallback, public btInternalTriangleIndexCallback {
public:
    JNIEnv* env;
    jobject callback;

    DebugCallback(JNIEnv* env, jobject object) {
        this->env = env;
        this->callback = object;
    }

    virtual void internalProcessTriangleIndex(btVector3* triangle, int partId, int triangleIndex) {
        processTriangle(triangle, partId, triangleIndex);
    }

    virtual void processTriangle(btVector3* triangle, int partId, int triangleIndex) {
        btVector3 vertexA, vertexB, vertexC;
        vertexA = triangle[0];
        vertexB = triangle[1];
        vertexC = triangle[2];
        env->CallVoidMethod(callback, jmeClasses::DebugMeshCallback_addVector, vertexA.getX(), vertexA.getY(), vertexA.getZ(), partId, triangleIndex);
        if (env->ExceptionCheck()) {
            env->Throw(env->ExceptionOccurred());
            return;
        }
//        triangle = 
        env->CallVoidMethod(callback, jmeClasses::DebugMeshCallback_addVector, vertexB.getX(), vertexB.getY(), vertexB.getZ(), partId, triangleIndex);
        if (env->ExceptionCheck()) {
            env->Throw(env->ExceptionOccurred());
            return;
        }
        env->CallVoidMethod(callback, jmeClasses::DebugMeshCallback_addVector, vertexC.getX(), vertexC.getY(), vertexC.getZ(), partId, triangleIndex);
        if (env->ExceptionCheck()) {
            env->Throw(env->ExceptionOccurred());
            return;
        }
    }
};

#ifdef __cplusplus
extern "C" {
#endif

    /* Inaccessible static: _00024assertionsDisabled */

    /*
     * Class:     com_jme3_bullet_util_DebugShapeFactory
     * Method:    getVertices
     * Signature: (JLcom/jme3/bullet/util/DebugMeshCallback;)V
     */
    JNIEXPORT void JNICALL Java_com_jme3_bullet_util_DebugShapeFactory_getVertices
    (JNIEnv *env, jclass clazz, jlong shapeId, jobject callback) {
        btCollisionShape* shape = reinterpret_cast<btCollisionShape*>(shapeId);
        if (shape->isConcave()) {
            btConcaveShape* concave = reinterpret_cast<btConcaveShape*>(shape);
            DebugCallback* clb = new DebugCallback(env, callback);
            btVector3 min = btVector3(-1e30, -1e30, -1e30);
            btVector3 max = btVector3(1e30, 1e30, 1e30);
            concave->processAllTriangles(clb, min, max);
            delete(clb);
        } else if (shape->isConvex()) {
            btConvexShape* convexShape = reinterpret_cast<btConvexShape*>(shape);
            // Check there is a hull shape to render
            if (convexShape->getUserPointer() == NULL) {
                // create a hull approximation
                btShapeHull* hull = new btShapeHull(convexShape);
                float margin = convexShape->getMargin();
                hull->buildHull(margin);
                convexShape->setUserPointer(hull);
            }

            btShapeHull* hull = (btShapeHull*) convexShape->getUserPointer();

            int numberOfTriangles = hull->numTriangles();
            int numberOfFloats = 3 * 3 * numberOfTriangles;
            int byteBufferSize = numberOfFloats * 4;
            
            // Loop variables
            const unsigned int* hullIndices = hull->getIndexPointer();
            const btVector3* hullVertices = hull->getVertexPointer();
            btVector3 vertexA, vertexB, vertexC;
            int index = 0;

            for (int i = 0; i < numberOfTriangles; i++) {
                // Grab the data for this triangle from the hull
                vertexA = hullVertices[hullIndices[index++]];
                vertexB = hullVertices[hullIndices[index++]];
                vertexC = hullVertices[hullIndices[index++]];

                // Put the verticies into the vertex buffer
                env->CallVoidMethod(callback, jmeClasses::DebugMeshCallback_addVector, vertexA.getX(), vertexA.getY(), vertexA.getZ());
                if (env->ExceptionCheck()) {
                    env->Throw(env->ExceptionOccurred());
                    return;
                }
                env->CallVoidMethod(callback, jmeClasses::DebugMeshCallback_addVector, vertexB.getX(), vertexB.getY(), vertexB.getZ());
                if (env->ExceptionCheck()) {
                    env->Throw(env->ExceptionOccurred());
                    return;
                }
                env->CallVoidMethod(callback, jmeClasses::DebugMeshCallback_addVector, vertexC.getX(), vertexC.getY(), vertexC.getZ());
                if (env->ExceptionCheck()) {
                    env->Throw(env->ExceptionOccurred());
                    return;
                }
            }
            delete hull;
            convexShape->setUserPointer(NULL);
        }
    }

#ifdef __cplusplus
}
#endif
