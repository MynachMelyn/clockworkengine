
package com.clockwork.scene;

/**
 * SceneGraphVisitorAdapter is used to traverse the scene
 * graph tree. The adapter version of the interface simply separates 
 * between the Geometry geometries} and the Node nodes} by
 * supplying visit methods that take them.
 * Use by calling Spatial#depthFirstTraversal(com.clockwork.scene.SceneGraphVisitor) }
 * or Spatial#breadthFirstTraversal(com.clockwork.scene.SceneGraphVisitor)}.
 */
public class SceneGraphVisitorAdapter implements SceneGraphVisitor {
    
    /**
     * Called when a Geometry} is visited.
     * 
     * @param geom The visited geometry
     */
    public void visit(Geometry geom) {}
    
    /**
     * Called when a Node} is visited.
     * 
     * @param geom The visited node
     */
    public void visit(Node geom) {}

    @Override
    public final void visit(Spatial spatial) {
        if (spatial instanceof Geometry) {
            visit((Geometry)spatial);
        } else {
            visit((Node)spatial);
        }
    }
}
