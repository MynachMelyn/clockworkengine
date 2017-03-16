
package com.clockwork.math;

import com.clockwork.export.*;
import java.io.IOException;


/**
 * 
 * Rectangle defines a finite plane within three dimensional space
 * that is specified via three points (A, B, C). These three points define a
 * triangle with the fourth point defining the rectangle ((B + C) - A.
 * 
 * 
 * 
 */

public final class Rectangle implements Savable, Cloneable, java.io.Serializable {

    static final long serialVersionUID = 1;

    private Vector3f a, b, c;

    /**
     * Constructor creates a new Rectangle with no defined corners.
     * A, B, and C must be set to define a valid rectangle.
     * 
     */
    public Rectangle() {
        a = new Vector3f();
        b = new Vector3f();
        c = new Vector3f();
    }

    /**
     * Constructor creates a new Rectangle with defined A, B, and C
     * points that define the area of the rectangle.
     * 
     * @param a
     *            the first corner of the rectangle.
     * @param b
     *            the second corner of the rectangle.
     * @param c
     *            the third corner of the rectangle.
     */
    public Rectangle(Vector3f a, Vector3f b, Vector3f c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    /**
     * getA returns the first point of the rectangle.
     * 
     * @return the first point of the rectangle.
     */
    public Vector3f getA() {
        return a;
    }

    /**
     * setA sets the first point of the rectangle.
     * 
     * @param a
     *            the first point of the rectangle.
     */
    public void setA(Vector3f a) {
        this.a = a;
    }

    /**
     * getB returns the second point of the rectangle.
     * 
     * @return the second point of the rectangle.
     */
    public Vector3f getB() {
        return b;
    }

    /**
     * setB sets the second point of the rectangle.
     * 
     * @param b
     *            the second point of the rectangle.
     */
    public void setB(Vector3f b) {
        this.b = b;
    }

    /**
     * getC returns the third point of the rectangle.
     * 
     * @return the third point of the rectangle.
     */
    public Vector3f getC() {
        return c;
    }

    /**
     * setC sets the third point of the rectangle.
     * 
     * @param c
     *            the third point of the rectangle.
     */
    public void setC(Vector3f c) {
        this.c = c;
    }

    /**
     * random returns a random point within the plane defined by:
     * A, B, C, and (B + C) - A.
     * 
     * @return a random point within the rectangle.
     */
    public Vector3f random() {
        return random(null);
    }

    /**
     * random returns a random point within the plane defined by:
     * A, B, C, and (B + C) - A.
     * 
     * @param result
     *            Vector to store result in
     * @return a random point within the rectangle.
     */
    public Vector3f random(Vector3f result) {
        if (result == null) {
            result = new Vector3f();
        }

        float s = FastMath.nextRandomFloat();
        float t = FastMath.nextRandomFloat();

        float aMod = 1.0f - s - t;
        result.set(a.mult(aMod).addLocal(b.mult(s).addLocal(c.mult(t))));
        return result;
    }

    public void write(CWExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(a, "a", Vector3f.ZERO);
        capsule.write(b, "b", Vector3f.ZERO);
        capsule.write(c, "c", Vector3f.ZERO);
    }

    public void read(CWImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        a = (Vector3f) capsule.readSavable("a", Vector3f.ZERO.clone());
        b = (Vector3f) capsule.readSavable("b", Vector3f.ZERO.clone());
        c = (Vector3f) capsule.readSavable("c", Vector3f.ZERO.clone());
    }

    @Override
    public Rectangle clone() {
        try {
            Rectangle r = (Rectangle) super.clone();
            r.a = a.clone();
            r.b = b.clone();
            r.c = c.clone();
            return r;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
