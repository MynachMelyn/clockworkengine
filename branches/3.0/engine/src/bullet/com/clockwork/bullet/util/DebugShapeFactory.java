
package com.clockwork.bullet.util;

import com.clockwork.bullet.collision.shapes.CollisionShape;
import com.clockwork.bullet.collision.shapes.CompoundCollisionShape;
import com.clockwork.bullet.collision.shapes.infos.ChildCollisionShape;
import com.clockwork.math.Matrix3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.VertexBuffer.Type;
import com.clockwork.util.TempVars;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class DebugShapeFactory {

    /** The maximum corner for the aabb used for triangles to include in ConcaveShape processing.*/
//    private static final Vector3f aabbMax = new Vector3f(1e30f, 1e30f, 1e30f);
    /** The minimum corner for the aabb used for triangles to include in ConcaveShape processing.*/
//    private static final Vector3f aabbMin = new Vector3f(-1e30f, -1e30f, -1e30f);

    /**
     * Creates a debug shape from the given collision shape. This is mostly used internally.
     * To attach a debug shape to a physics object, call attachDebugShape(AssetManager manager); on it.
     * @param collisionShape
     * @return
     */
    public static Spatial getDebugShape(CollisionShape collisionShape) {
        if (collisionShape == null) {
            return null;
        }
        Spatial debugShape;
        if (collisionShape instanceof CompoundCollisionShape) {
            CompoundCollisionShape shape = (CompoundCollisionShape) collisionShape;
            List<ChildCollisionShape> children = shape.getChildren();
            Node node = new Node("DebugShapeNode");
            for (Iterator<ChildCollisionShape> it = children.iterator(); it.hasNext();) {
                ChildCollisionShape childCollisionShape = it.next();
                CollisionShape ccollisionShape = childCollisionShape.shape;
                Geometry geometry = createDebugShape(ccollisionShape);

                // apply translation
                geometry.setLocalTranslation(childCollisionShape.location);

                // apply rotation
                TempVars vars = TempVars.get();                
                Matrix3f tempRot = vars.tempMat3;

                tempRot.set(geometry.getLocalRotation());
                childCollisionShape.rotation.mult(tempRot, tempRot);
                geometry.setLocalRotation(tempRot);

                vars.release();

                node.attachChild(geometry);
            }
            debugShape = node;
        } else {
            debugShape = createDebugShape(collisionShape);
        }
        if (debugShape == null) {
            return null;
        }
        debugShape.updateGeometricState();
        return debugShape;
    }

    private static Geometry createDebugShape(CollisionShape shape) {
        Geometry geom = new Geometry();
        geom.setMesh(DebugShapeFactory.getDebugMesh(shape));
//        geom.setLocalScale(shape.getScale());
        geom.updateModelBound();
        return geom;
    }

    public static Mesh getDebugMesh(CollisionShape shape) {
        Mesh mesh = new Mesh();
        mesh = new Mesh();
        DebugMeshCallback callback = new DebugMeshCallback();
        getVertices(shape.getObjectId(), callback);
        mesh.setBuffer(Type.Position, 3, callback.getVertices());
        mesh.getFloatBuffer(Type.Position).clear();
        return mesh;
    }

    private static native void getVertices(long shapeId, DebugMeshCallback buffer);
}
