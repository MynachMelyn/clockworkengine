
package com.clockwork.renderer;

import com.clockwork.light.LightList;
import com.clockwork.material.RenderState;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Matrix4f;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.VertexBuffer;
import com.clockwork.shader.Shader;
import com.clockwork.shader.Shader.ShaderSource;
import com.clockwork.texture.FrameBuffer;
import com.clockwork.texture.Image;
import com.clockwork.texture.Texture;
import com.clockwork.util.NativeObject;
import java.nio.ByteBuffer;
import java.util.EnumSet;

/**
 * The Renderer is responsible for taking rendering commands and
 * executing them on the underlying video hardware.
 * 
 */
public interface Renderer {

    /**
     * Get the capabilities of the renderer.
     * @return The capabilities of the renderer.
     */
    public EnumSet<Caps> getCaps();

    /**
     * The statistics allow tracking of how data
     * per frame, such as number of objects rendered, number of triangles, etc.
     * These are updated when the Renderer's methods are used, make sure
     * to call Statistics#clearFrame() } at the appropriate time
     * to get accurate info per frame.
     */
    public Statistics getStatistics();

    /**
     * Invalidates the current rendering state. Should be called after
     * the GL state was changed manually or through an external library.
     */
    public void invalidateState();

    /**
     * Clears certain channels of the currently bound framebuffer.
     *
     * @param color True if to clear colors (RGBA)
     * @param depth True if to clear depth/z
     * @param stencil True if to clear stencil buffer (if available, otherwise
     * ignored)
     */
    public void clearBuffers(boolean color, boolean depth, boolean stencil);

    /**
     * Sets the background (aka clear) color.
     * 
     * @param color The background color to set
     */
    public void setBackgroundColor(ColorRGBA color);

    /**
     * Applies the given RenderState}, making the necessary
     * GL calls so that the state is applied.
     */
    public void applyRenderState(RenderState state);

    /**
     * Set the range of the depth values for objects. All rendered
     * objects will have their depth clamped to this range.
     * 
     * @param start The range start
     * @param end The range end
     */
    public void setDepthRange(float start, float end);

    /**
     * Called when a new frame has been rendered.
     */
    public void onFrame();

    /**
     * Set the world matrix to use. Does nothing if the Renderer is 
     * shader based.
     * 
     * @param worldMatrix World matrix to use.
     */
    public void setWorldMatrix(Matrix4f worldMatrix);

    /**
     * Sets the view and projection matrices to use. Does nothing if the Renderer 
     * is shader based.
     * 
     * @param viewMatrix The view matrix to use.
     * @param projMatrix The projection matrix to use.
     */
    public void setViewProjectionMatrices(Matrix4f viewMatrix, Matrix4f projMatrix);

    /**
     * Set the viewport location and resolution on the screen.
     * 
     * @param x The x coordinate of the viewport
     * @param y The y coordinate of the viewport
     * @param width Width of the viewport
     * @param height Height of the viewport
     */
    public void setViewPort(int x, int y, int width, int height);

    /**
     * Specifies a clipping rectangle.
     * For all future rendering commands, no pixels will be allowed
     * to be rendered outside of the clip rectangle.
     * 
     * @param x The x coordinate of the clip rect
     * @param y The y coordinate of the clip rect
     * @param width Width of the clip rect
     * @param height Height of the clip rect
     */
    public void setClipRect(int x, int y, int width, int height);

    /**
     * Clears the clipping rectangle set with 
     * #setClipRect(int, int, int, int) }.
     */
    public void clearClipRect();

    /**
     * Set lighting state.
     * Does nothing if the renderer is shader based.
     * The lights should be provided in world space. 
     * Specify null to disable lighting.
     * 
     * @param lights The light list to set.
     */
    public void setLighting(LightList lights);

    /**
     * Sets the shader to use for rendering.
     * If the shader has not been uploaded yet, it is compiled
     * and linked. If it has been uploaded, then the 
     * uniform data is updated and the shader is set.
     * 
     * @param shader The shader to use for rendering.
     */
    public void setShader(Shader shader);

    /**
     * Deletes a shader. This method also deletes
     * the attached shader sources.
     * 
     * @param shader Shader to delete.
     */
    public void deleteShader(Shader shader);

    /**
     * Deletes the provided shader source.
     * 
     * @param source The ShaderSource to delete.
     */
    public void deleteShaderSource(ShaderSource source);

    /**
     * Copies contents from src to dst, scaling if necessary.
     */
    public void copyFrameBuffer(FrameBuffer src, FrameBuffer dst);

    /**
     * Copies contents from src to dst, scaling if necessary.
     * set copyDepth to false to only copy the color buffers.
     */
    public void copyFrameBuffer(FrameBuffer src, FrameBuffer dst, boolean copyDepth);

    /**
     * Sets the framebuffer that will be drawn to.
     */
    public void setFrameBuffer(FrameBuffer fb);
    
    /**
     * Set the framebuffer that will be set instead of the main framebuffer
     * when a call to setFrameBuffer(null) is made.
     * 
     * @param fb 
     */
    public void setMainFrameBufferOverride(FrameBuffer fb);

    /**
     * Reads the pixels currently stored in the specified framebuffer
     * into the given ByteBuffer object. 
     * Only color pixels are transferred, the format is BGRA with 8 bits 
     * per component. The given byte buffer should have at least
     * fb.getWidth() * fb.getHeight() * 4 bytes remaining.
     * 
     * @param fb The framebuffer to read from
     * @param byteBuf The bytebuffer to transfer color data to
     */
    public void readFrameBuffer(FrameBuffer fb, ByteBuffer byteBuf);

    /**
     * Deletes a framebuffer and all attached renderbuffers
     */
    public void deleteFrameBuffer(FrameBuffer fb);

    /**
     * Sets the texture to use for the given texture unit.
     */
    public void setTexture(int unit, Texture tex);

    /**
     * Modify the given Texture tex with the given Image. The image will be put at x and y into the texture.
     *
     * @param tex the Texture that will be modified
     * @param pixels the source Image data to copy data from
     * @param x the x position to put the image into the texture
     * @param y the y position to put the image into the texture
     */
    public void modifyTexture(Texture tex, Image pixels, int x, int y);

    /**
     * Deletes a texture from the GPU.
     */
    public void deleteImage(Image image);

    /**
     * Uploads a vertex buffer to the GPU.
     * 
     * @param vb The vertex buffer to upload
     */
    public void updateBufferData(VertexBuffer vb);

    /**
     * Deletes a vertex buffer from the GPU.
     * @param vb The vertex buffer to delete
     */
    public void deleteBuffer(VertexBuffer vb);

    /**
     * Renders count meshes, with the geometry data supplied.
     * The shader which is currently set with setShader is
     * responsible for transforming the input verticies into clip space
     * and shading it based on the given vertex attributes.
     * The int variable gl_InstanceID can be used to access the current
     * instance of the mesh being rendered inside the vertex shader.
     *
     * @param mesh The mesh to render
     * @param lod The LOD level to use, see Mesh#setLodLevels(com.clockwork.scene.VertexBuffer[]) }.
     * @param count Number of mesh instances to render
     */
    public void renderMesh(Mesh mesh, int lod, int count);

    /**
     * Resets all previously used NativeObject Native Objects} on this Renderer.
     * The state of the native objects is reset in such way, that using
     * them again will cause the renderer to reupload them.
     * Call this method when you know the GL context is going to shutdown.
     * 
     * see NativeObject#resetObject() 
     */
    public void resetGLObjects();

    /**
     * Deletes all previously used NativeObject Native Objects} on this Renderer, and
     * then resets the native objects.
     * 
     * see #resetGLObjects() 
     * see NativeObject#deleteObject(java.lang.Object) 
     */
    public void cleanup();
    
    /**
     * Sets the alpha to coverage state.
     * 
     * When alpha coverage and multi-sampling is enabled, 
     * each pixel will contain alpha coverage in all
     * of its subsamples, which is then combined when
     * other future alpha-blended objects are rendered.
     * 
     * 
     * Alpha-to-coverage is useful for rendering transparent objects
     * without having to worry about sorting them.
     * 
     */
    public void setAlphaToCoverage(boolean value);
}
