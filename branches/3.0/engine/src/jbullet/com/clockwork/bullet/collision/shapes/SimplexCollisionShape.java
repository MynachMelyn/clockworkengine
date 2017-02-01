
package com.clockwork.bullet.collision.shapes;

import com.bulletphysics.collision.shapes.BU_Simplex1to4;
import com.clockwork.bullet.util.Converter;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Vector3f;
import java.io.IOException;

/**
 * A simple point, line, triangle or quad collisionShape based on one to four points-
 */
public class SimplexCollisionShape extends CollisionShape {

    private Vector3f vector1, vector2, vector3, vector4;

    public SimplexCollisionShape() {
    }

    public SimplexCollisionShape(Vector3f point1, Vector3f point2, Vector3f point3, Vector3f point4) {
        vector1 = point1;
        vector2 = point2;
        vector3 = point3;
        vector4 = point4;
        createShape();
    }

    public SimplexCollisionShape(Vector3f point1, Vector3f point2, Vector3f point3) {
        vector1 = point1;
        vector2 = point2;
        vector3 = point3;
        createShape();
    }

    public SimplexCollisionShape(Vector3f point1, Vector3f point2) {
        vector1 = point1;
        vector2 = point2;
        createShape();
    }

    public SimplexCollisionShape(Vector3f point1) {
        vector1 = point1;
        createShape();
    }

    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(vector1, "simplexPoint1", null);
        capsule.write(vector2, "simplexPoint2", null);
        capsule.write(vector3, "simplexPoint3", null);
        capsule.write(vector4, "simplexPoint4", null);
    }

    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);
        vector1 = (Vector3f) capsule.readSavable("simplexPoint1", null);
        vector2 = (Vector3f) capsule.readSavable("simplexPoint2", null);
        vector3 = (Vector3f) capsule.readSavable("simplexPoint3", null);
        vector4 = (Vector3f) capsule.readSavable("simplexPoint4", null);
        createShape();
    }

    protected void createShape() {
        if (vector4 != null) {
            cShape = new BU_Simplex1to4(Converter.convert(vector1), Converter.convert(vector2), Converter.convert(vector3), Converter.convert(vector4));
        } else if (vector3 != null) {
            cShape = new BU_Simplex1to4(Converter.convert(vector1), Converter.convert(vector2), Converter.convert(vector3));
        } else if (vector2 != null) {
            cShape = new BU_Simplex1to4(Converter.convert(vector1), Converter.convert(vector2));
        } else {
            cShape = new BU_Simplex1to4(Converter.convert(vector1));
        }
        cShape.setLocalScaling(Converter.convert(getScale()));
        cShape.setMargin(margin);
    }
}
