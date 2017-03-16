
package com.clockwork.scene;

import com.clockwork.math.Matrix4f;
import com.clockwork.math.Transform;
import com.clockwork.util.TempVars;

/**
 * 
 * SimpleBatchNode  comes with some restrictions, but can yield better performances.
 * Geometries to be batched has to be attached directly to the BatchNode
 * You can't attach a Node to a SimpleBatchNode
 * SimpleBatchNode is recommended when you have a large number of geometries using the same material that does not require a complex scene graph structure.
 * see BatchNode
 */
public class SimpleBatchNode extends BatchNode {

    public SimpleBatchNode() {
        super();
    }

    public SimpleBatchNode(String name) {
        super(name);
    }

    @Override
    public int attachChild(Spatial child) {

        if (!(child instanceof Geometry)) {
            throw new UnsupportedOperationException("BatchNode is BatchMode.Simple only support child of type Geometry, use BatchMode.Complex to use a complex structure");
        }

        return super.attachChild(child);
    }

    @Override
    protected void setTransformRefresh() {
        refreshFlags |= RF_TRANSFORM;
        setBoundRefresh();
        for (Batch batch : batches.getArray()) {
            batch.geometry.setTransformRefresh();
        }
    }
    private Matrix4f cachedLocalMat = new Matrix4f();

    @Override
    protected Matrix4f getTransformMatrix(Geometry g){
        // Compute the Local matrix for the geometry
        cachedLocalMat.loadIdentity();
        cachedLocalMat.setRotationQuaternion(g.localTransform.getRotation());
        cachedLocalMat.setTranslation(g.localTransform.getTranslation());

        TempVars vars = TempVars.get();
        Matrix4f scaleMat = vars.tempMat4;
        scaleMat.loadIdentity();
        scaleMat.scale(g.localTransform.getScale());
        cachedLocalMat.multLocal(scaleMat);
        vars.release();
        return cachedLocalMat;
    }
    

    @Override
    public void batch() {
        doBatch();
    }
}
