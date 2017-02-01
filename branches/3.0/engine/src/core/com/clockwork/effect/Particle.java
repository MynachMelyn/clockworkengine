
package com.clockwork.effect;

import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector3f;

/**
 * Represents a single particle in a {@link ParticleEmitter}.
 * 
 */
public class Particle {
    
    /**
     * Particle velocity.
     */
    public final Vector3f velocity = new Vector3f();
    
    /**
     * Current particle position
     */
    public final Vector3f position = new Vector3f();
    
    /**
     * Particle color
     */
    public final ColorRGBA color = new ColorRGBA(0,0,0,0);
    
    /**
     * Particle size or radius.
     */
    public float size;
    
    /**
     * Particle remaining life, in seconds.
     */
    public float life;
    
    /**
     * The initial particle life
     */
    public float startlife;
    
    /**
     * Particle rotation angle (in radians).
     */
    public float angle;
    
    /**
     * Particle rotation angle speed (in radians).
     */
    public float rotateSpeed;
    
    /**
     * Particle image index. 
     */
    public int imageIndex = 0;
    
    /**
     * Distance to camera. Only used for sorted particles.
     */
    //public float distToCam;
}
