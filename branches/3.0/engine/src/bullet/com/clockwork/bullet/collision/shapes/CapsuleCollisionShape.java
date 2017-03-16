
package com.clockwork.bullet.collision.shapes;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Vector3f;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basic capsule collision shape
 */
public class CapsuleCollisionShape extends CollisionShape{
    protected float radius,height;
    protected int axis;

    public CapsuleCollisionShape() {
    }

    /**
     * creates a new CapsuleCollisionShape with the given radius and height
     * @param radius the radius of the capsule
     * @param height the height of the capsule
     */
    public CapsuleCollisionShape(float radius, float height) {
        this.radius=radius;
        this.height=height;
        this.axis=1;
        createShape();
    }

    /**
     * creates a capsule shape around the given axis (0=X,1=Y,2=Z)
     * @param radius
     * @param height
     * @param axis
     */
    public CapsuleCollisionShape(float radius, float height, int axis) {
        this.radius=radius;
        this.height=height;
        this.axis=axis;
        createShape();
    }

    public float getRadius() {
        return radius;
    }

    public float getHeight() {
        return height;
    }

    public int getAxis() {
        return axis;
    }

    /**
     * WARNING - CompoundCollisionShape scaling has no effect.
     */
    @Override
    public void setScale(Vector3f scale) {
        Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "CapsuleCollisionShape cannot be scaled");
    }

    public void write(CWExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(radius, "radius", 0.5f);
        capsule.write(height, "height", 1);
        capsule.write(axis, "axis", 1);
    }

    public void read(CWImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);
        radius = capsule.readFloat("radius", 0.5f);
        height = capsule.readFloat("height", 0.5f);
        axis = capsule.readInt("axis", 1);
        createShape();
    }

    protected void createShape(){
        objectId = createShape(axis, radius, height);
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Created Shape {0}", Long.toHexString(objectId));
        setScale(scale);
        setMargin(margin);
//        switch(axis){
//            case 0:
//                objectId=new CapsuleShapeX(radius,height);
//            break;
//            case 1:
//                objectId=new CapsuleShape(radius,height);
//            break;
//            case 2:
//                objectId=new CapsuleShapeZ(radius,height);
//            break;
//        }
//        objectId.setLocalScaling(Converter.convert(getScale()));
//        objectId.setMargin(margin);
    }
    
    private native long createShape(int axis, float radius, float height);

}
