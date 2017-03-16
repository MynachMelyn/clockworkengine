
package com.clockwork.math;

import com.clockwork.export.*;
import java.io.IOException;

public final class ColorRGBA implements Savable, Cloneable, java.io.Serializable {

    static final long serialVersionUID = 1;
    /**
     * The color black (0,0,0).
     */
    public static final ColorRGBA Black = new ColorRGBA(0f, 0f, 0f, 1f);
    /**
     * The color white (1,1,1).
     */
    public static final ColorRGBA White = new ColorRGBA(1f, 1f, 1f, 1f);
    /**
     * The color gray (.2,.2,.2).
     */
    public static final ColorRGBA DarkGray = new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f);
    /**
     * The color gray (.5,.5,.5).
     */
    public static final ColorRGBA Gray = new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f);
    /**
     * The color gray (.8,.8,.8).
     */
    public static final ColorRGBA LightGray = new ColorRGBA(0.8f, 0.8f, 0.8f, 1.0f);
    /**
     * The color red (1,0,0).
     */
    public static final ColorRGBA Red = new ColorRGBA(1f, 0f, 0f, 1f);
    /**
     * The color green (0,1,0).
     */
    public static final ColorRGBA Green = new ColorRGBA(0f, 1f, 0f, 1f);
    /**
     * The color blue (0,0,1).
     */
    public static final ColorRGBA Blue = new ColorRGBA(0f, 0f, 1f, 1f);
    /**
     * The color yellow (1,1,0).
     */
    public static final ColorRGBA Yellow = new ColorRGBA(1f, 1f, 0f, 1f);
    /**
     * The color magenta (1,0,1).
     */
    public static final ColorRGBA Magenta = new ColorRGBA(1f, 0f, 1f, 1f);
    /**
     * The color cyan (0,1,1).
     */
    public static final ColorRGBA Cyan = new ColorRGBA(0f, 1f, 1f, 1f);
    /**
     * The color orange (251/255, 130/255,0).
     */
    public static final ColorRGBA Orange = new ColorRGBA(251f / 255f, 130f / 255f, 0f, 1f);
    /**
     * The color brown (65/255, 40/255, 25/255).
     */
    public static final ColorRGBA Brown = new ColorRGBA(65f / 255f, 40f / 255f, 25f / 255f, 1f);
    /**
     * The color pink (1, 0.68, 0.68).
     */
    public static final ColorRGBA Pink = new ColorRGBA(1f, 0.68f, 0.68f, 1f);
    /**
     * The black color with no alpha (0, 0, 0, 0).
     */
    public static final ColorRGBA BlackNoAlpha = new ColorRGBA(0f, 0f, 0f, 0f);
    /**
     * The red component of the color. 0 is none and 1 is maximum red.
     */
    public float r;
    /**
     * The green component of the color. 0 is none and 1 is maximum green.
     */
    public float g;
    /**
     * The blue component of the color. 0 is none and 1 is maximum blue.
     */
    public float b;
    /**
     * The alpha component of the color. 0 is transparent and 1 is opaque.
     */
    public float a;

    /**
     * Constructor instantiates a new ColorRGBA object. This
     * color is the default "white" with all values 1.
     */
    public ColorRGBA() {
        r = g = b = a = 1.0f;
    }

    /**
     * Constructor instantiates a new ColorRGBA object. The
     * values are defined as passed parameters. These values are then clamped
     * to insure that they are between 0 and 1.
     * @param r The red component of this color.
     * @param g The green component of this ColorRGBA.
     * @param b The blue component of this ColorRGBA.
     * @param a The alpha component of this ColorRGBA.
     */
    public ColorRGBA(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    /**
     * Copy constructor creates a new ColorRGBA object, based on
     * a provided color.
     * @param rgba The ColorRGBA object to copy.
     */
    public ColorRGBA(ColorRGBA rgba) {
        this.a = rgba.a;
        this.r = rgba.r;
        this.g = rgba.g;
        this.b = rgba.b;
    }

    /**
     * set sets the RGBA values of this ColorRGBA. The 
     * values are then clamped to insure that they are between 0 and 1.
     *
     * @param r The red component of this color.
     * @param g The green component of this color.
     * @param b The blue component of this color.
     * @param a The alpha component of this color.
     * @return this
     */
    public ColorRGBA set(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this;
    }

    /**
     * set sets the values of this ColorRGBA to those 
     * set by a parameter color.
     *
     * @param rgba The color to set this ColorRGBA to.
     * @return this
     */
    public ColorRGBA set(ColorRGBA rgba) {
        if (rgba == null) {
            r = 0;
            g = 0;
            b = 0;
            a = 0;
        } else {
            r = rgba.r;
            g = rgba.g;
            b = rgba.b;
            a = rgba.a;
        }
        return this;
    }

    /**
     * clamp insures that all values are between 0 and 1. If any
     * are less than 0 they are set to zero. If any are more than 1 they are
     * set to one.
     */
    public void clamp() {
        if (r < 0) {
            r = 0;
        } else if (r > 1) {
            r = 1;
        }

        if (g < 0) {
            g = 0;
        } else if (g > 1) {
            g = 1;
        }

        if (b < 0) {
            b = 0;
        } else if (b > 1) {
            b = 1;
        }

        if (a < 0) {
            a = 0;
        } else if (a > 1) {
            a = 1;
        }
    }

    /**
     * getColorArray retrieves the color values of this 
     * ColorRGBA as a four element float array in the 
     * order: r,g,b,a.
     * @return The float array that contains the color components.
     */
    public float[] getColorArray() {
        return new float[]{r, g, b, a};
    }

    /**
     * Stores the current r,g,b,a values into the given array.  The given array must have a
     * length of 4 or greater, or an array index out of bounds exception will be thrown.
     * @param store The float array to store the values into.
     * @return The float array after storage.
     */
    public float[] getColorArray(float[] store) {
        store[0] = r;
        store[1] = g;
        store[2] = b;
        store[3] = a;
        return store;
    }

    /**
     * Retrieves the alpha component value of this ColorRGBA.
     * @return The alpha component value.
     */
    public float getAlpha() {
        return a;
    }

    /**
     * Retrieves the red component value of this ColorRGBA.
     * @return The red component value.
     */
    public float getRed() {
        return r;
    }

    /**
     * Retrieves the blue component value of this ColorRGBA.
     * @return The blue component value.
     */
    public float getBlue() {
        return b;
    }

    /**
     * Retrieves the green component value of this ColorRGBA.
     * @return The green component value.
     */
    public float getGreen() {
        return g;
    }

    /**
     * Sets this ColorRGBA to the interpolation by changeAmnt from 
     * this to the finalColor:
     * this=(1-changeAmnt)*this + changeAmnt * finalColor
     * @param finalColor The final color to interpolate towards.
     * @param changeAmnt An amount between 0.0 - 1.0 representing a percentage
     *  change from this towards finalColor.
     */
    public void interpolate(ColorRGBA finalColor, float changeAmnt) {
        this.r = (1 - changeAmnt) * this.r + changeAmnt * finalColor.r;
        this.g = (1 - changeAmnt) * this.g + changeAmnt * finalColor.g;
        this.b = (1 - changeAmnt) * this.b + changeAmnt * finalColor.b;
        this.a = (1 - changeAmnt) * this.a + changeAmnt * finalColor.a;
    }

    /**
     * Sets this ColorRGBA to the interpolation by changeAmnt from 
     * beginColor to finalColor:
     * this=(1-changeAmnt)*beginColor + changeAmnt * finalColor
     * @param beginColor The begining color (changeAmnt=0).
     * @param finalColor The final color to interpolate towards (changeAmnt=1).
     * @param changeAmnt An amount between 0.0 - 1.0 representing a precentage
     *  change from beginColor towards finalColor.
     */
    public void interpolate(ColorRGBA beginColor, ColorRGBA finalColor, float changeAmnt) {
        this.r = (1 - changeAmnt) * beginColor.r + changeAmnt * finalColor.r;
        this.g = (1 - changeAmnt) * beginColor.g + changeAmnt * finalColor.g;
        this.b = (1 - changeAmnt) * beginColor.b + changeAmnt * finalColor.b;
        this.a = (1 - changeAmnt) * beginColor.a + changeAmnt * finalColor.a;
    }

    /**
     * randomColor is a utility method that generates a random
     * opaque color.
     * @return a random ColorRGBA with an alpha set to 1.
     */
    public static ColorRGBA randomColor() {
        ColorRGBA rVal = new ColorRGBA(0, 0, 0, 1);
        rVal.r = FastMath.nextRandomFloat();
        rVal.g = FastMath.nextRandomFloat();
        rVal.b = FastMath.nextRandomFloat();
        return rVal;
    }

    /**
     * Multiplies each r,g,b,a of this ColorRGBA by the corresponding 
     * r,g,b,a of the given color and returns the result as a new ColorRGBA.  
     * Used as a way of combining colors and lights.
     * @param c The color to multiply by.
     * @return The new ColorRGBA.  this*c
     */
    public ColorRGBA mult(ColorRGBA c) {
        return new ColorRGBA(c.r * r, c.g * g, c.b * b, c.a * a);
    }

    /**
     * Multiplies each r,g,b,a of this ColorRGBA by the given scalar and
     * returns the result as a new ColorRGBA.  
     * Used as a way of making colors dimmer or brighter.
     * @param scalar The scalar to multiply by.
     * @return The new ColorRGBA.  this*scalar
     */
    public ColorRGBA mult(float scalar) {
        return new ColorRGBA(scalar * r, scalar * g, scalar * b, scalar * a);
    }

    /**
     * Multiplies each r,g,b,a of this ColorRGBA by the given scalar and
     * returns the result (this).  
     * Used as a way of making colors dimmer or brighter.
     * @param scalar The scalar to multiply by.
     * @return this*c
     */
    public ColorRGBA multLocal(float scalar) {
        this.r *= scalar;
        this.g *= scalar;
        this.b *= scalar;
        this.a *= scalar;
        return this;
    }

    /**
     * Adds each r,g,b,a of this ColorRGBA by the corresponding 
     * r,g,b,a of the given color and returns the result as a new ColorRGBA.
     * Used as a way of combining colors and lights.
     * @param c The color to add.
     * @return The new ColorRGBA.  this+c
     */
    public ColorRGBA add(ColorRGBA c) {
        return new ColorRGBA(c.r + r, c.g + g, c.b + b, c.a + a);
    }

    /**
     * Adds each r,g,b,a of this ColorRGBA by the r,g,b,a the given 
     * color and returns the result (this).  
     * Used as a way of combining colors and lights.
     * @param c The color to add.
     * @return this+c
     */
    public ColorRGBA addLocal(ColorRGBA c) {
        set(c.r + r, c.g + g, c.b + b, c.a + a);
        return this;
    }

    /**
     * toString returns the string representation of this ColorRGBA.
     * The format of the string is:
     * <Class Name>: [R=RR.RRRR, G=GG.GGGG, B=BB.BBBB, A=AA.AAAA]
     * @return The string representation of this ColorRGBA.
     */
    @Override
    public String toString() {
        return "Color[" + r + ", " + g + ", " + b + ", " + a + "]";
    }

    @Override
    public ColorRGBA clone() {
        try {
            return (ColorRGBA) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // can not happen
        }
    }

    /**
     * Saves this ColorRGBA into the given float array.
     * @param floats The float array to take this ColorRGBA. 
     * If null, a new float[4] is created.
     * @return The array, with r,g,b,a float values in that order.
     */
    public float[] toArray(float[] floats) {
        if (floats == null) {
            floats = new float[4];
        }
        floats[0] = r;
        floats[1] = g;
        floats[2] = b;
        floats[3] = a;
        return floats;
    }

    /**
     * equals returns true if this ColorRGBA is logically equivalent
     * to a given color. That is, if all the components of the two colors are the same.
     * False is returned otherwise.
     * @param o The object to compare against.
     * @return true if the colors are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColorRGBA)) {
            return false;
        }

        if (this == o) {
            return true;
        }

        ColorRGBA comp = (ColorRGBA) o;
        if (Float.compare(r, comp.r) != 0) {
            return false;
        }
        if (Float.compare(g, comp.g) != 0) {
            return false;
        }
        if (Float.compare(b, comp.b) != 0) {
            return false;
        }
        if (Float.compare(a, comp.a) != 0) {
            return false;
        }
        return true;
    }

    /**
     * hashCode returns a unique code for this ColorRGBA based
     * on its values. If two colors are logically equivalent, they will return
     * the same hash code value.
     * @return The hash code value of this ColorRGBA.
     */
    @Override
    public int hashCode() {
        int hash = 37;
        hash += 37 * hash + Float.floatToIntBits(r);
        hash += 37 * hash + Float.floatToIntBits(g);
        hash += 37 * hash + Float.floatToIntBits(b);
        hash += 37 * hash + Float.floatToIntBits(a);
        return hash;
    }

    public void write(JmeExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(r, "r", 0);
        capsule.write(g, "g", 0);
        capsule.write(b, "b", 0);
        capsule.write(a, "a", 0);
    }

    public void read(JmeImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        r = capsule.readFloat("r", 0);
        g = capsule.readFloat("g", 0);
        b = capsule.readFloat("b", 0);
        a = capsule.readFloat("a", 0);
    }
    /**
     * Retrieves the component values of this ColorRGBA as
     * a four element byte array in the order: r,g,b,a.
     * @return the byte array that contains the color components.
     */
    public byte[] asBytesRGBA() {
        byte[] store = new byte[4];
        store[0] = (byte) ((int) (r * 255) & 0xFF);
        store[1] = (byte) ((int) (g * 255) & 0xFF);
        store[2] = (byte) ((int) (b * 255) & 0xFF);
        store[3] = (byte) ((int) (a * 255) & 0xFF);
        return store;
    }

    /**
     * Retrieves the component values of this ColorRGBA as an 
     * int in a,r,g,b order. 
     * Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue.
     * @return The integer representation of this ColorRGBA in a,r,g,b order.
     */
    public int asIntARGB() {
        int argb = (((int) (a * 255) & 0xFF) << 24)
                | (((int) (r * 255) & 0xFF) << 16)
                | (((int) (g * 255) & 0xFF) << 8)
                | (((int) (b * 255) & 0xFF));
        return argb;
    }

    /**
     * Retrieves the component values of this ColorRGBA as an 
     * int in r,g,b,a order.
     * Bits 24-31 are red, 16-23 are green, 8-15 are blue, 0-7 are alpha.
     * @return The integer representation of this ColorRGBA in r,g,b,a order.
     */
    public int asIntRGBA() {
        int rgba = (((int) (r * 255) & 0xFF) << 24)
                | (((int) (g * 255) & 0xFF) << 16)
                | (((int) (b * 255) & 0xFF) << 8)
                | (((int) (a * 255) & 0xFF));
        return rgba;
    }
    /**
     * Retrieves the component values of this ColorRGBA as an 
     * int in a,b,g,r order.
     * Bits 24-31 are alpha, 16-23 are blue, 8-15 are green, 0-7 are red.
     * @return The integer representation of this ColorRGBA in a,b,g,r order.
     */
    public int asIntABGR() {
        int abgr = (((int) (a * 255) & 0xFF) << 24)
                | (((int) (b * 255) & 0xFF) << 16)
                | (((int) (g * 255) & 0xFF) << 8)
                | (((int) (r * 255) & 0xFF));
        return abgr;
    }
    /**
     * Sets the component values of this ColorRGBA with the given  
     * combined ARGB int.
     * Bits 24-31 are alpha, bits 16-23 are red, bits 8-15 are green, bits 0-7 are blue.
     * @param color The integer ARGB value used to set this ColorRGBA.
     */
    public void fromIntARGB(int color) {
        a = ((byte) (color >> 24) & 0xFF) / 255f;
        r = ((byte) (color >> 16) & 0xFF) / 255f;
        g = ((byte) (color >> 8) & 0xFF) / 255f;
        b = ((byte) (color) & 0xFF) / 255f;
    }
    /**
     * Sets the RGBA values of this ColorRGBA with the given combined RGBA value 
     * Bits 24-31 are red, bits 16-23 are green, bits 8-15 are blue, bits 0-7 are alpha.
     * @param color The integer RGBA value used to set this object.
     */
    public void fromIntRGBA(int color) {
        r = ((byte) (color >> 24) & 0xFF) / 255f;
        g = ((byte) (color >> 16) & 0xFF) / 255f;
        b = ((byte) (color >> 8) & 0xFF) / 255f;
        a = ((byte) (color) & 0xFF) / 255f;
    }

    /**
     * Transform this ColorRGBA to a Vector3f using
     * x = r, y = g, z = b. The Alpha value is not used.
     * This method is useful to use for shaders assignment.
     * @return A Vector3f containing the RGB value of this ColorRGBA.
     */
    public Vector3f toVector3f() {
        return new Vector3f(r, g, b);
    }

    /**
     * Transform this ColorRGBA to a Vector4f using
     * x = r, y = g, z = b, w = a.
     * This method is useful to use for shaders assignment.
     * @return A Vector4f containing the RGBA value of this ColorRGBA.
     */
    public Vector4f toVector4f() {
        return new Vector4f(r, g, b, a);
    }
}
