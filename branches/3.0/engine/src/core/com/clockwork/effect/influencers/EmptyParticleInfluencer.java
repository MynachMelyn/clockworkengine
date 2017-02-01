
package com.clockwork.effect.influencers;

import com.clockwork.effect.Particle;
import com.clockwork.effect.shapes.EmitterShape;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.math.Vector3f;
import java.io.IOException;

/**
 * This influencer does not influence particle at all.
 * It makes particles not to move.
 */
public class EmptyParticleInfluencer implements ParticleInfluencer {

    @Override
    public void write(JmeExporter ex) throws IOException {
    }

    @Override
    public void read(JmeImporter im) throws IOException {
    }

    @Override
    public void influenceParticle(Particle particle, EmitterShape emitterShape) {
    }

    @Override
    public void setInitialVelocity(Vector3f initialVelocity) {
    }

    @Override
    public Vector3f getInitialVelocity() {
        return null;
    }

    @Override
    public void setVelocityVariation(float variation) {
    }

    @Override
    public float getVelocityVariation() {
        return 0;
    }

    @Override
    public ParticleInfluencer clone() {
        try {
            return (ParticleInfluencer) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
