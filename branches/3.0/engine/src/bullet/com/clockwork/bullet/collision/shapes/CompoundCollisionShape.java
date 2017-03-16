
package com.clockwork.bullet.collision.shapes;

import com.clockwork.bullet.collision.shapes.infos.ChildCollisionShape;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Matrix3f;
import com.clockwork.math.Vector3f;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A CompoundCollisionShape allows combining multiple base shapes
 * to generate a more sophisticated shape.
 */
public class CompoundCollisionShape extends CollisionShape {

    protected ArrayList<ChildCollisionShape> children = new ArrayList<ChildCollisionShape>();

    public CompoundCollisionShape() {
        objectId = createShape();//new CompoundShape();
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Created Shape {0}", Long.toHexString(objectId));
    }

    /**
     * adds a child shape at the given local translation
     * @param shape the child shape to add
     * @param location the local location of the child shape
     */
    public void addChildShape(CollisionShape shape, Vector3f location) {
//        Transform transA = new Transform(Converter.convert(new Matrix3f()));
//        Converter.convert(location, transA.origin);
//        children.add(new ChildCollisionShape(location.clone(), new Matrix3f(), shape));
//        ((CompoundShape) objectId).addChildShape(transA, shape.getObjectId());
        addChildShape(shape, location, new Matrix3f());
    }

    /**
     * adds a child shape at the given local translation
     * @param shape the child shape to add
     * @param location the local location of the child shape
     */
    public void addChildShape(CollisionShape shape, Vector3f location, Matrix3f rotation) {
        if(shape instanceof CompoundCollisionShape){
            throw new IllegalStateException("CompoundCollisionShapes cannot have CompoundCollisionShapes as children!");
        }
//        Transform transA = new Transform(Converter.convert(rotation));
//        Converter.convert(location, transA.origin);
//        Converter.convert(rotation, transA.basis);
        children.add(new ChildCollisionShape(location.clone(), rotation.clone(), shape));
        addChildShape(objectId, shape.getObjectId(), location, rotation);
//        ((CompoundShape) objectId).addChildShape(transA, shape.getObjectId());
    }

    private void addChildShapeDirect(CollisionShape shape, Vector3f location, Matrix3f rotation) {
        if(shape instanceof CompoundCollisionShape){
            throw new IllegalStateException("CompoundCollisionShapes cannot have CompoundCollisionShapes as children!");
        }
//        Transform transA = new Transform(Converter.convert(rotation));
//        Converter.convert(location, transA.origin);
//        Converter.convert(rotation, transA.basis);
        addChildShape(objectId, shape.getObjectId(), location, rotation);
//        ((CompoundShape) objectId).addChildShape(transA, shape.getObjectId());
    }

    /**
     * removes a child shape
     * @param shape the child shape to remove
     */
    public void removeChildShape(CollisionShape shape) {
        removeChildShape(objectId, shape.getObjectId());
//        ((CompoundShape) objectId).removeChildShape(shape.getObjectId());
        for (Iterator<ChildCollisionShape> it = children.iterator(); it.hasNext();) {
            ChildCollisionShape childCollisionShape = it.next();
            if (childCollisionShape.shape == shape) {
                it.remove();
            }
        }
    }

    public List<ChildCollisionShape> getChildren() {
        return children;
    }

    /**
     * WARNING - CompoundCollisionShape scaling has no effect.
     */
    @Override
    public void setScale(Vector3f scale) {
        Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "CompoundCollisionShape cannot be scaled");
    }

    private native long createShape();
    
    private native long addChildShape(long objectId, long childId, Vector3f location, Matrix3f rotation);
    
    private native long removeChildShape(long objectId, long childId);
    
    public void write(CWExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.writeSavableArrayList(children, "children", new ArrayList<ChildCollisionShape>());
    }

    public void read(CWImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);
        children = capsule.readSavableArrayList("children", new ArrayList<ChildCollisionShape>());
        setScale(scale);
        setMargin(margin);
        loadChildren();
    }

    private void loadChildren() {
        for (Iterator<ChildCollisionShape> it = children.iterator(); it.hasNext();) {
            ChildCollisionShape child = it.next();
            addChildShapeDirect(child.shape, child.location, child.rotation);
        }
    }

}
