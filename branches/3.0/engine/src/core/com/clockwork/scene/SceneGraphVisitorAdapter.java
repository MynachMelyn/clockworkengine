
package com.clockwork.scene;

/**
 * <code>SceneGraphVisitorAdapter</code> is used to traverse the scene
 * graph tree. The adapter version of the interface simply separates 
 * between the {@link Geometry geometries} and the {@link Node nodes} by
 * supplying visit methods that take them.
 * Use by calling {@link Spatial#depthFirstTraversal(com.clockwork.scene.SceneGraphVisitor) }
 * or {@link Spatial#breadthFirstTraversal(com.clockwork.scene.SceneGraphVisitor)}.
 */
public class SceneGraphVisitorAdapter implements SceneGraphVisitor {
    
    /**
     * Called when a {@link Geometry} is visited.
     * 
     * @param geom The visited geometry
     */
    public void visit(Geometry geom) {}
    
    /**
     * Called when a {@link Node} is visited.
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
