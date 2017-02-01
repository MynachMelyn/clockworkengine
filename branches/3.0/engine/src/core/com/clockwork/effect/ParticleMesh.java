
package com.clockwork.effect;

import com.clockwork.material.RenderState;
import com.clockwork.math.Matrix3f;
import com.clockwork.renderer.Camera;
import com.clockwork.scene.Mesh;

/**
 * The <code>ParticleMesh</code> is the underlying visual implementation of a 
 * {@link ParticleEmitter particle emitter}.
 * 
 */
public abstract class ParticleMesh extends Mesh {

    /**
     * Type of particle mesh
     */
    public enum Type {
        /**
         * The particle mesh is composed of points. Each particle is a point.
         * This can be used in conjuction with {@link RenderState#setPointSprite(boolean) point sprites}
         * to render particles the usual way.
         */
        Point,
        
        /**
         * The particle mesh is composed of triangles. Each particle is 
         * two triangles making a single quad.
         */
        Triangle;
    }

    /**
     * Initialize mesh data.
     * 
     * @param emitter The emitter which will use this <code>ParticleMesh</code>.
     * @param numParticles The maxmimum number of particles to simulate
     */
    public abstract void initParticleData(ParticleEmitter emitter, int numParticles);
    
    /**
     * Set the images on the X and Y coordinates
     * @param imagesX Images on the X coordinate
     * @param imagesY Images on the Y coordinate
     */
    public abstract void setImagesXY(int imagesX, int imagesY);
    
    /**
     * Update the particle visual data. Typically called every frame.
     */
    public abstract void updateParticleData(Particle[] particles, Camera cam, Matrix3f inverseRotation);

}
