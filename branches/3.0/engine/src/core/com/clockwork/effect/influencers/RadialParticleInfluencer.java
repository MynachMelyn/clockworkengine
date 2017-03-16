
package com.clockwork.effect.influencers;

import com.clockwork.effect.Particle;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.FastMath;
import com.clockwork.math.Vector3f;
import java.io.IOException;

/**
 * an influencer to make blasts expanding on the ground. can be used for various other things
 */
public class RadialParticleInfluencer extends DefaultParticleInfluencer {

    private float radialVelocity = 0f;
    private Vector3f origin = new Vector3f(0, 0, 0);
    private boolean horizontal = false;

    /**
     * This method applies the variation to the particle with already set velocity.
     * @param particle
     *        the particle to be affected
     */
    @Override
    protected void applyVelocityVariation(Particle particle) {
        particle.velocity.set(initialVelocity);
        temp.set(particle.position).subtractLocal(origin).normalizeLocal().multLocal(radialVelocity);
        if (horizontal) {
            temp.y = 0;
        }
        particle.velocity.addLocal(temp);

        temp.set(FastMath.nextRandomFloat(), FastMath.nextRandomFloat(), FastMath.nextRandomFloat());
        temp.multLocal(2f);
        temp.subtractLocal(1f, 1f, 1f);
        temp.multLocal(initialVelocity.length());
        particle.velocity.interpolate(temp, velocityVariation);
    }

    /**
     * the origin used for computing the radial velocity direction
     * @return the origin
     */
    public Vector3f getOrigin() {
        return origin;
    }

    /**
     * the origin used for computing the radial velocity direction
     * @param origin 
     */
    public void setOrigin(Vector3f origin) {
        this.origin = origin;
    }

    /**
     * the radial velocity
     * @return radialVelocity
     */
    public float getRadialVelocity() {
        return radialVelocity;
    }

    /**
     * the radial velocity
     * @param radialVelocity 
     */
    public void setRadialVelocity(float radialVelocity) {
        this.radialVelocity = radialVelocity;
    }

    /**
     * nullify y component of particle velocity to make the effect expand only on x and z axis
     * @return 
     */
    public boolean isHorizontal() {
        return horizontal;
    }

    /**
     * nullify y component of particle velocity to make the effect expand only on x and z axis
     * @param horizontal 
     */
    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    @Override
    public void write(CWExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(radialVelocity, "radialVelocity", 0f);
        oc.write(origin, "origin", new Vector3f());
        oc.write(horizontal, "horizontal", false);
    }

    @Override
    public void read(CWImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        radialVelocity = ic.readFloat("radialVelocity", 0f);
        origin = (Vector3f) ic.readSavable("origin", new Vector3f());
        horizontal = ic.readBoolean("horizontal", false);
    }
}
