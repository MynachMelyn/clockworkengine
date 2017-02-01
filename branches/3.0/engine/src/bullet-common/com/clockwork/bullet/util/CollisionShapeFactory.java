
package com.clockwork.bullet.util;

import com.clockwork.bounding.BoundingBox;
import com.clockwork.bullet.collision.shapes.*;
import com.clockwork.bullet.collision.shapes.infos.ChildCollisionShape;
import com.clockwork.math.Matrix3f;
import com.clockwork.math.Transform;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.*;
import com.clockwork.terrain.geomipmap.TerrainPatch;
import com.clockwork.terrain.geomipmap.TerrainQuad;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 */
public class CollisionShapeFactory {

    /**
     * returns the correct transform for a collisionshape in relation
     * to the ancestor for which the collisionshape is generated
     * @param spat
     * @param parent
     * @return
     */
    private static Transform getTransform(Spatial spat, Spatial parent) {
        Transform shapeTransform = new Transform();
        Spatial parentNode = spat.getParent() != null ? spat.getParent() : spat;
        Spatial currentSpatial = spat;
        //if we have parents combine their transforms
        while (parentNode != null) {
            if (parent == currentSpatial) {
                //real parent -> only apply scale, not transform
                Transform trans = new Transform();
                trans.setScale(currentSpatial.getLocalScale());
                shapeTransform.combineWithParent(trans);
                parentNode = null;
            } else {
                shapeTransform.combineWithParent(currentSpatial.getLocalTransform());
                parentNode = currentSpatial.getParent();
                currentSpatial = parentNode;
            }
        }
        return shapeTransform;
    }

    private static CompoundCollisionShape createCompoundShape(Node realRootNode,
            Node rootNode, CompoundCollisionShape shape, boolean meshAccurate, boolean dynamic) {
        for (Spatial spatial : rootNode.getChildren()) {
            if (spatial instanceof TerrainQuad) {
                Boolean bool = spatial.getUserData(UserData.JME_PHYSICSIGNORE);
                if (bool != null && bool.booleanValue()) {
                    continue; // go to the next child in the loop
                }
                TerrainQuad terrain = (TerrainQuad) spatial;
                Transform trans = getTransform(spatial, realRootNode);
                shape.addChildShape(new HeightfieldCollisionShape(terrain.getHeightMap(), trans.getScale()),
                        trans.getTranslation(),
                        trans.getRotation().toRotationMatrix());
            } else if (spatial instanceof Node) {
                createCompoundShape(realRootNode, (Node) spatial, shape, meshAccurate, dynamic);
            } else if (spatial instanceof TerrainPatch) {
                Boolean bool = spatial.getUserData(UserData.JME_PHYSICSIGNORE);
                if (bool != null && bool.booleanValue()) {
                    continue; // go to the next child in the loop
                }
                TerrainPatch terrain = (TerrainPatch) spatial;
                Transform trans = getTransform(spatial, realRootNode);
                shape.addChildShape(new HeightfieldCollisionShape(terrain.getHeightMap(), terrain.getLocalScale()),
                        trans.getTranslation(),
                        trans.getRotation().toRotationMatrix());
            } else if (spatial instanceof Geometry) {
                Boolean bool = spatial.getUserData(UserData.JME_PHYSICSIGNORE);
                if (bool != null && bool.booleanValue()) {
                    continue; // go to the next child in the loop
                }

                if (meshAccurate) {
                    CollisionShape childShape = dynamic
                            ? createSingleDynamicMeshShape((Geometry) spatial, realRootNode)
                            : createSingleMeshShape((Geometry) spatial, realRootNode);
                    if (childShape != null) {
                        Transform trans = getTransform(spatial, realRootNode);
                        shape.addChildShape(childShape,
                                trans.getTranslation(),
                                trans.getRotation().toRotationMatrix());
                    }
                } else {
                    Transform trans = getTransform(spatial, realRootNode);
                    shape.addChildShape(createSingleBoxShape(spatial, realRootNode),
                            trans.getTranslation(),
                            trans.getRotation().toRotationMatrix());
                }
            }
        }
        return shape;
    }

    private static CompoundCollisionShape createCompoundShape(
            Node rootNode, CompoundCollisionShape shape, boolean meshAccurate) {
        return createCompoundShape(rootNode, rootNode, shape, meshAccurate, false);
    }

    /**
     * This type of collision shape is mesh-accurate and meant for immovable "world objects".
     * Examples include terrain, houses or whole shooter levels.<br>
     * Objects with "mesh" type collision shape will not collide with each other.
     */
    private static CompoundCollisionShape createMeshCompoundShape(Node rootNode) {
        return createCompoundShape(rootNode, new CompoundCollisionShape(), true);
    }

    /**
     * This type of collision shape creates a CompoundShape made out of boxes that
     * are based on the bounds of the Geometries  in the tree.
     * @param rootNode
     * @return
     */
    private static CompoundCollisionShape createBoxCompoundShape(Node rootNode) {
        return createCompoundShape(rootNode, new CompoundCollisionShape(), false);
    }

    /**
     * This type of collision shape is mesh-accurate and meant for immovable "world objects".
     * Examples include terrain, houses or whole shooter levels.<br/>
     * Objects with "mesh" type collision shape will not collide with each other.<br/>
     * Creates a HeightfieldCollisionShape if the supplied spatial is a TerrainQuad.
     * @return A MeshCollisionShape or a CompoundCollisionShape with MeshCollisionShapes as children if the supplied spatial is a Node. A HeightieldCollisionShape if a TerrainQuad was supplied.
     */
    public static CollisionShape createMeshShape(Spatial spatial) {
        if (spatial instanceof TerrainQuad) {
            TerrainQuad terrain = (TerrainQuad) spatial;
            return new HeightfieldCollisionShape(terrain.getHeightMap(), terrain.getLocalScale());
        } else if (spatial instanceof TerrainPatch) {
            TerrainPatch terrain = (TerrainPatch) spatial;
            return new HeightfieldCollisionShape(terrain.getHeightMap(), terrain.getLocalScale());
        } else if (spatial instanceof Geometry) {
            return createSingleMeshShape((Geometry) spatial, spatial);
        } else if (spatial instanceof Node) {
            return createMeshCompoundShape((Node) spatial);
        } else {
            throw new IllegalArgumentException("Supplied spatial must either be Node or Geometry!");
        }
    }

    /**
     * This method creates a hull shape for the given Spatial.<br>
     * If you want to have mesh-accurate dynamic shapes (CPU intense!!!) use GImpact shapes, its probably best to do so with a low-poly version of your model.
     * @return A HullCollisionShape or a CompoundCollisionShape with HullCollisionShapes as children if the supplied spatial is a Node.
     */
    public static CollisionShape createDynamicMeshShape(Spatial spatial) {
        if (spatial instanceof Geometry) {
            return createSingleDynamicMeshShape((Geometry) spatial, spatial);
        } else if (spatial instanceof Node) {
            return createCompoundShape((Node) spatial, (Node) spatial, new CompoundCollisionShape(), true, true);
        } else {
            throw new IllegalArgumentException("Supplied spatial must either be Node or Geometry!");
        }

    }

    public static CollisionShape createBoxShape(Spatial spatial) {
        if (spatial instanceof Geometry) {
            return createSingleBoxShape((Geometry) spatial, spatial);
        } else if (spatial instanceof Node) {
            return createBoxCompoundShape((Node) spatial);
        } else {
            throw new IllegalArgumentException("Supplied spatial must either be Node or Geometry!");
        }
    }

    /**
     * This type of collision shape is mesh-accurate and meant for immovable "world objects".
     * Examples include terrain, houses or whole shooter levels.<br>
     * Objects with "mesh" type collision shape will not collide with each other.
     */
    private static MeshCollisionShape createSingleMeshShape(Geometry geom, Spatial parent) {
        Mesh mesh = geom.getMesh();
        Transform trans = getTransform(geom, parent);
        if (mesh != null && mesh.getMode() == Mesh.Mode.Triangles) {
            MeshCollisionShape mColl = new MeshCollisionShape(mesh);
            mColl.setScale(trans.getScale());
            return mColl;
        } else {
            return null;
        }
    }

    /**
     * Uses the bounding box of the supplied spatial to create a BoxCollisionShape
     * @param spatial
     * @return BoxCollisionShape with the size of the spatials BoundingBox
     */
    private static BoxCollisionShape createSingleBoxShape(Spatial spatial, Spatial parent) {
        //TODO: using world bound here instead of "local world" bound...
        BoxCollisionShape shape = new BoxCollisionShape(
                ((BoundingBox) spatial.getWorldBound()).getExtent(new Vector3f()));
        return shape;
    }

    /**
     * This method creates a hull collision shape for the given mesh.<br>
     */
    private static HullCollisionShape createSingleDynamicMeshShape(Geometry geom, Spatial parent) {
        Mesh mesh = geom.getMesh();
        Transform trans = getTransform(geom, parent);
        if (mesh != null) {
            HullCollisionShape dynamicShape = new HullCollisionShape(mesh);
            dynamicShape.setScale(trans.getScale());
            return dynamicShape;
        } else {
            return null;
        }
    }

    /**
     * This method moves each child shape of a compound shape by the given vector
     * @param vector
     */
    public static void shiftCompoundShapeContents(CompoundCollisionShape compoundShape, Vector3f vector) {
        for (Iterator<ChildCollisionShape> it = new LinkedList(compoundShape.getChildren()).iterator(); it.hasNext();) {
            ChildCollisionShape childCollisionShape = it.next();
            CollisionShape child = childCollisionShape.shape;
            Vector3f location = childCollisionShape.location;
            Matrix3f rotation = childCollisionShape.rotation;
            compoundShape.removeChildShape(child);
            compoundShape.addChildShape(child, location.add(vector), rotation);
        }
    }
}
