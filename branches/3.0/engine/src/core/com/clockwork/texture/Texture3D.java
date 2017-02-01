
package com.clockwork.texture;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import java.io.IOException;

/**
 */
public class Texture3D extends Texture {

    private WrapMode wrapS = WrapMode.EdgeClamp;
    private WrapMode wrapT = WrapMode.EdgeClamp;
    private WrapMode wrapR = WrapMode.EdgeClamp;

    /**
     * Creates a new two-dimensional texture with default attributes.
     */
    public Texture3D() {
        super();
    }

    /**
     * Creates a new three-dimensional texture using the given image.
     * @param img The image to use.
     */
    public Texture3D(Image img) {
        super();
        setImage(img);
        if (img.getFormat().isDepthFormat()) {
            setMagFilter(MagFilter.Nearest);
            setMinFilter(MinFilter.NearestNoMipMaps);
        }
    }

    /**
     * Creates a new three-dimensional texture for the purpose of offscreen
     * rendering.
     *
     * @see com.clockwork.texture.FrameBuffer
     *
     * @param width
     * @param height
     * @param depth
     * @param format
     */
    public Texture3D(int width, int height, int depth, Image.Format format) {
        this(new Image(format, width, height, depth, null));
    }

    /**
     * Creates a new three-dimensional texture for the purpose of offscreen
     * rendering.
     *
     * @see com.clockwork.texture.FrameBuffer
     *
     * @param width
     * @param height
     * @param format
     * @param numSamples
     */
    public Texture3D(int width, int height, int depth, int numSamples, Image.Format format) {
        this(new Image(format, width, height, depth, null));
        getImage().setMultiSamples(numSamples);
    }

    @Override
    public Texture createSimpleClone() {
        Texture3D clone = new Texture3D();
        createSimpleClone(clone);
        return clone;
    }

    @Override
    public Texture createSimpleClone(Texture rVal) {
        rVal.setWrap(WrapAxis.S, wrapS);
        rVal.setWrap(WrapAxis.T, wrapT);
        rVal.setWrap(WrapAxis.R, wrapR);
        return super.createSimpleClone(rVal);
    }

    /**
     * <code>setWrap</code> sets the wrap mode of this texture for a
     * particular axis.
     *
     * @param axis
     *            the texture axis to define a wrapmode on.
     * @param mode
     *            the wrap mode for the given axis of the texture.
     * @throws IllegalArgumentException
     *             if axis or mode are null
     */
    public void setWrap(WrapAxis axis, WrapMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("mode can not be null.");
        } else if (axis == null) {
            throw new IllegalArgumentException("axis can not be null.");
        }
        switch (axis) {
            case S:
                this.wrapS = mode;
                break;
            case T:
                this.wrapT = mode;
                break;
            case R:
                this.wrapR = mode;
                break;
        }
    }

    /**
     * <code>setWrap</code> sets the wrap mode of this texture for all axis.
     *
     * @param mode
     *            the wrap mode for the given axis of the texture.
     * @throws IllegalArgumentException
     *             if mode is null
     */
    public void setWrap(WrapMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("mode can not be null.");
        }
        this.wrapS = mode;
        this.wrapT = mode;
        this.wrapR = mode;
    }

    /**
     * <code>getWrap</code> returns the wrap mode for a given coordinate axis
     * on this texture.
     *
     * @param axis
     *            the axis to return for
     * @return the wrap mode of the texture.
     * @throws IllegalArgumentException
     *             if axis is null
     */
    public WrapMode getWrap(WrapAxis axis) {
        switch (axis) {
            case S:
                return wrapS;
            case T:
                return wrapT;
            case R:
                return wrapR;
        }
        throw new IllegalArgumentException("invalid WrapAxis: " + axis);
    }

    @Override
    public Type getType() {
        return Type.ThreeDimensional;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Texture3D)) {
            return false;
        }
        Texture3D that = (Texture3D) other;
        if (this.getWrap(WrapAxis.S) != that.getWrap(WrapAxis.S)) {
            return false;
        }
        if (this.getWrap(WrapAxis.T) != that.getWrap(WrapAxis.T)) {
            return false;
        }
        if (this.getWrap(WrapAxis.R) != that.getWrap(WrapAxis.R)) {
            return false;
        }
        return super.equals(other);
    }

    @Override
    public void write(JmeExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(wrapS, "wrapS", WrapMode.EdgeClamp);
        capsule.write(wrapT, "wrapT", WrapMode.EdgeClamp);
        capsule.write(wrapR, "wrapR", WrapMode.EdgeClamp);
    }

    @Override
    public void read(JmeImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        wrapS = capsule.readEnum("wrapS", WrapMode.class, WrapMode.EdgeClamp);
        wrapT = capsule.readEnum("wrapT", WrapMode.class, WrapMode.EdgeClamp);
        wrapR = capsule.readEnum("wrapR", WrapMode.class, WrapMode.EdgeClamp);
    }
}