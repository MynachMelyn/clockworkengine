
package com.clockwork.math;

import com.clockwork.export.*;
import com.clockwork.util.BufferUtils;
import com.clockwork.util.TempVars;
import java.io.IOException;
import java.nio.FloatBuffer;

/**
 * Line defines a line. Where a line is defined as infinite along
 * two points. The two points of the line are defined as the origin and direction.
 * 
 * 
 * 
 */
public class Line implements Savable, Cloneable, java.io.Serializable {

    static final long serialVersionUID = 1;

    private Vector3f origin;
    private Vector3f direction;

    /**
     * Constructor instantiates a new Line object. The origin and
     * direction are set to defaults (0,0,0).
     *
     */
    public Line() {
        origin = new Vector3f();
        direction = new Vector3f();
    }

    /**
     * Constructor instantiates a new Line object. The origin
     * and direction are set via the parameters.
     * @param origin the origin of the line.
     * @param direction the direction of the line.
     */
    public Line(Vector3f origin, Vector3f direction) {
        this.origin = origin;
        this.direction = direction;
    }

    /**
     *
     * getOrigin returns the origin of the line.
     * @return the origin of the line.
     */
    public Vector3f getOrigin() {
        return origin;
    }

    /**
     *
     * setOrigin sets the origin of the line.
     * @param origin the origin of the line.
     */
    public void setOrigin(Vector3f origin) {
        this.origin = origin;
    }

    /**
     *
     * getDirection returns the direction of the line.
     * @return the direction of the line.
     */
    public Vector3f getDirection() {
        return direction;
    }

    /**
     *
     * setDirection sets the direction of the line.
     * @param direction the direction of the line.
     */
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public float distanceSquared(Vector3f point) {
        TempVars vars = TempVars.get();

        Vector3f compVec1 = vars.vect1;
        Vector3f compVec2 = vars.vect2;

        point.subtract(origin, compVec1);
        float lineParameter = direction.dot(compVec1);
        origin.add(direction.mult(lineParameter, compVec2), compVec2);
        compVec2.subtract(point, compVec1);
        float len = compVec1.lengthSquared();
        vars.release();
        return len;
    }

    public float distance(Vector3f point) {
        return FastMath.sqrt(distanceSquared(point));
    }

    public void orthogonalLineFit(FloatBuffer points) {
        if (points == null) {
            return;
        }

        TempVars vars = TempVars.get();

        Vector3f compVec1 = vars.vect1;
        Vector3f compVec2 = vars.vect2;
        Matrix3f compMat1 = vars.tempMat3;
        Eigen3f compEigen1 = vars.eigen;

        points.rewind();

        // compute average of points
        int length = points.remaining() / 3;

        BufferUtils.populateFromBuffer(origin, points, 0);
        for (int i = 1; i < length; i++) {
            BufferUtils.populateFromBuffer(compVec1, points, i);
            origin.addLocal(compVec1);
        }

        origin.multLocal(1f / (float) length);

        // compute sums of products
        float sumXX = 0.0f, sumXY = 0.0f, sumXZ = 0.0f;
        float sumYY = 0.0f, sumYZ = 0.0f, sumZZ = 0.0f;

        points.rewind();
        for (int i = 0; i < length; i++) {
            BufferUtils.populateFromBuffer(compVec1, points, i);
            compVec1.subtract(origin, compVec2);
            sumXX += compVec2.x * compVec2.x;
            sumXY += compVec2.x * compVec2.y;
            sumXZ += compVec2.x * compVec2.z;
            sumYY += compVec2.y * compVec2.y;
            sumYZ += compVec2.y * compVec2.z;
            sumZZ += compVec2.z * compVec2.z;
        }

        //find the smallest eigen vector for the direction vector
        compMat1.m00 = sumYY + sumZZ;
        compMat1.m01 = -sumXY;
        compMat1.m02 = -sumXZ;
        compMat1.m10 = -sumXY;
        compMat1.m11 = sumXX + sumZZ;
        compMat1.m12 = -sumYZ;
        compMat1.m20 = -sumXZ;
        compMat1.m21 = -sumYZ;
        compMat1.m22 = sumXX + sumYY;

        compEigen1.calculateEigen(compMat1);
        direction = compEigen1.getEigenVector(0);

        vars.release();
    }

    /**
     *
     * random determines a random point along the line.
     * @return a random point on the line.
     */
    public Vector3f random() {
        return random(null);
    }

    /**
     * random determines a random point along the line.
     * 
     * @param result Vector to store result in
     * @return a random point on the line.
     */
    public Vector3f random(Vector3f result) {
        if (result == null) {
            result = new Vector3f();
        }
        float rand = (float) Math.random();

        result.x = (origin.x * (1 - rand)) + (direction.x * rand);
        result.y = (origin.y * (1 - rand)) + (direction.y * rand);
        result.z = (origin.z * (1 - rand)) + (direction.z * rand);

        return result;
    }

    public void write(CWExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(origin, "origin", Vector3f.ZERO);
        capsule.write(direction, "direction", Vector3f.ZERO);
    }

    public void read(CWImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        origin = (Vector3f) capsule.readSavable("origin", Vector3f.ZERO.clone());
        direction = (Vector3f) capsule.readSavable("direction", Vector3f.ZERO.clone());
    }

    @Override
    public Line clone() {
        try {
            Line line = (Line) super.clone();
            line.direction = direction.clone();
            line.origin = origin.clone();
            return line;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
