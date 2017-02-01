
package com.clockwork.material;

/**
 * Fixed function binding is used to specify a binding for a {@link MatParam}
 * in case that shaders are not supported on the system.
 * 
 */
public enum FixedFuncBinding {
    /**
     * Specifies the material ambient color.
     * Same as GL_AMBIENT for OpenGL.
     */
    MaterialAmbient,
    
    /**
     * Specifies the material diffuse color.
     * Same as GL_DIFFUSE for OpenGL.
     */
    MaterialDiffuse,
    
    /**
     * Specifies the material specular color.
     * Same as GL_SPECULAR for OpenGL
     */
    MaterialSpecular,
    
    /**
     * Specifies the color of the object.
     * <p>
     * Used only for non-lit materials.
     */
    Color,
    
    /**
     * Specifies the material shininess value.
     * 
     * Same as GL_SHININESS for OpenGL.
     */
    MaterialShininess,
    
    /**
     * Use vertex color as an additional diffuse color, if lighting is enabled.
     * If lighting is disabled, vertex color is modulated with
     * {@link #Color material color}.
     */
    UseVertexColor,
    
    /**
     * Set the alpha threshold to discard pixels.
     * @see RenderState#setAlphaFallOff
     */
    AlphaTestFallOff
}
