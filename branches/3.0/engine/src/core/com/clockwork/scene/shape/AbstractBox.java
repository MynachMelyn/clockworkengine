
package com.clockwork.scene.shape;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Mesh;
import java.io.IOException;

/**
 * An eight sided box.
 * 
 * A Box} is defined by a minimal point and a maximal point. The eight
 * vertices that make the box are then computed, they are computed in such
 * a way as to generate an axis-aligned box.
 * 
 * This class does not control how the geometry data is generated, see Box}
 * for that.
 *
 * @version $Revision: 4131 $, $Date: 2009-03-19 16:15:28 -0400 (Thu, 19 Mar 2009) $
 */
public abstract class AbstractBox extends Mesh {

    public final Vector3f center = new Vector3f(0f, 0f, 0f);

    public float xExtent, yExtent, zExtent;
    
    public AbstractBox() {
        super();
    }

    /**
     * Gets the array or vectors representing the 8 vertices of the box.
     *
     * @return a newly created array of vertex vectors.
     */
    protected final Vector3f[] computeVertices() {
        Vector3f[] axes = {
                Vector3f.UNIT_X.mult(xExtent),
                Vector3f.UNIT_Y.mult(yExtent),
                Vector3f.UNIT_Z.mult(zExtent)
        };
        return new Vector3f[] {
                center.subtract(axes[0]).subtractLocal(axes[1]).subtractLocal(axes[2]),
                center.add(axes[0]).subtractLocal(axes[1]).subtractLocal(axes[2]),
                center.add(axes[0]).addLocal(axes[1]).subtractLocal(axes[2]),
                center.subtract(axes[0]).addLocal(axes[1]).subtractLocal(axes[2]),
                center.add(axes[0]).subtractLocal(axes[1]).addLocal(axes[2]),
                center.subtract(axes[0]).subtractLocal(axes[1]).addLocal(axes[2]),
                center.add(axes[0]).addLocal(axes[1]).addLocal(axes[2]),
                center.subtract(axes[0]).addLocal(axes[1]).addLocal(axes[2])
        };
    }

    /**
     * Convert the indices into the list of vertices that define the box's geometry.
     */
    protected abstract void duUpdateGeometryIndices();
    
    /**
     * Update the normals of each of the box's planes.
     */
    protected abstract void duUpdateGeometryNormals();

    /**
     * Update the points that define the texture of the box.
     * 
     * It's a one-to-one ratio, where each plane of the box has it's own copy
     * of the texture. That is, the texture is repeated one time for each face.
     */
    protected abstract void duUpdateGeometryTextures();

    /**
     * Update the position of the vertices that define the box.
     * 
     * These eight points are determined from the minimum and maximum point.
     */
    protected abstract void duUpdateGeometryVertices();

    /** 
     * Get the center point of this box. 
     */
    public final Vector3f getCenter() {
        return center;
    }

    /** 
     * Get the x-axis size (extent) of this box. 
     */
    public final float getXExtent() {
        return xExtent;
    }

    /** 
     * Get the y-axis size (extent) of this box. 
     */
    public final float getYExtent() {
        return yExtent;
    }

    /** 
     * Get the z-axis size (extent) of this box.
     */
    public final float getZExtent() {
        return zExtent;
    }
    
    /**
     * Rebuilds the box after a property has been directly altered.
     * 
     * For example, if you call getXExtent().x = 5.0f} then you will
     * need to call this method afterwards in order to update the box.
     */
    public final void updateGeometry() {
        duUpdateGeometryVertices();
        duUpdateGeometryNormals();
        duUpdateGeometryTextures();
        duUpdateGeometryIndices();
    }

    /**
     * Rebuilds this box based on a new set of parameters.
     * 
     * Note that the actual sides will be twice the given extent values because
     * the box extends in both directions from the center for each extent.
     * 
     * @param center the center of the box.
     * @param x the x extent of the box, in each directions.
     * @param y the y extent of the box, in each directions.
     * @param z the z extent of the box, in each directions.
     */
    public final void updateGeometry(Vector3f center, float x, float y, float z) {
        if (center != null) {this.center.set(center); }
        this.xExtent = x;
        this.yExtent = y;
        this.zExtent = z;
        updateGeometry();
    }

    /**
     * Rebuilds this box based on a new set of parameters.
     * 
     * The box is updated so that the two opposite corners are minPoint}
     * and maxPoint}, the other corners are created from those two positions.
     * 
     * @param minPoint the new minimum point of the box.
     * @param maxPoint the new maximum point of the box.
     */
    public final void updateGeometry(Vector3f minPoint, Vector3f maxPoint) {
        center.set(maxPoint).addLocal(minPoint).multLocal(0.5f);
        float x = maxPoint.x - center.x;
        float y = maxPoint.y - center.y;
        float z = maxPoint.z - center.z;
        updateGeometry(center, x, y, z);
    }

    @Override
    public void read(JmeImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        xExtent = capsule.readFloat("xExtent", 0);
        yExtent = capsule.readFloat("yExtent", 0);
        zExtent = capsule.readFloat("zExtent", 0);
        center.set((Vector3f) capsule.readSavable("center", Vector3f.ZERO.clone()));
    }

    @Override
    public void write(JmeExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(xExtent, "xExtent", 0);
        capsule.write(yExtent, "yExtent", 0);
        capsule.write(zExtent, "zExtent", 0);
        capsule.write(center, "center", Vector3f.ZERO);
    }

}
