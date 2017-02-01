
package com.clockwork.scene;

/**
 * <code>SceneGraphVisitorAdapter</code> is used to traverse the scene
 * graph tree. 
 * Use by calling {@link Spatial#depthFirstTraversal(com.clockwork.scene.SceneGraphVisitor) }
 * or {@link Spatial#breadthFirstTraversal(com.clockwork.scene.SceneGraphVisitor)}.
 */
public interface SceneGraphVisitor {
    /**
     * Called when a spatial is visited in the scene graph.
     * 
     * @param spatial The visited spatial
     */
    public void visit(Spatial spatial);
}
