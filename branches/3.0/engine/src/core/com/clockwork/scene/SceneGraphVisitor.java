
package com.clockwork.scene;

/**
 * SceneGraphVisitorAdapter is used to traverse the scene
 * graph tree. 
 * Use by calling Spatial#depthFirstTraversal(com.clockwork.scene.SceneGraphVisitor) }
 * or Spatial#breadthFirstTraversal(com.clockwork.scene.SceneGraphVisitor)}.
 */
public interface SceneGraphVisitor {
    /**
     * Called when a spatial is visited in the scene graph.
     * 
     * @param spatial The visited spatial
     */
    public void visit(Spatial spatial);
}
