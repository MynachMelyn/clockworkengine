
package com.clockwork.renderer;

import com.clockwork.material.RenderState;
import com.clockwork.math.ColorRGBA;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.VertexBuffer;
import com.clockwork.texture.FrameBuffer;
import com.clockwork.texture.Image;

/**
 * Represents the current state of the graphics library. This class is used
 * internally to reduce state changes. NOTE: This class is specific to OpenGL.
 */
public class RenderContext {

    /**
     * see RenderState#setFaceCullMode(com.clockwork.material.RenderState.FaceCullMode)
     */
    public RenderState.FaceCullMode cullMode = RenderState.FaceCullMode.Off;

    /**
     * see RenderState#setDepthTest(boolean) 
     */
    public boolean depthTestEnabled = false;

    /**
     * see RenderState#setAlphaFallOff(float) 
     */
    public float alphaTestFallOff = 0f;

    /**
     * see RenderState#setAlphaTest(boolean) 
     */
    public boolean alphaTestEnabled = false;

    /**
     * see RenderState#setDepthWrite(boolean) 
     */
    public boolean depthWriteEnabled = true;

    /**
     * see RenderState#setColorWrite(boolean) 
     */
    public boolean colorWriteEnabled = true;

    /**
     * see Renderer#setClipRect(int, int, int, int) 
     */
    public boolean clipRectEnabled = false;

    /**
     * see RenderState#setPolyOffset(float, float) 
     */
    public boolean polyOffsetEnabled = false;
    
    /**
     * see RenderState#setPolyOffset(float, float) 
     */
    public float polyOffsetFactor = 0;
    
    /**
     * see RenderState#setPolyOffset(float, float) 
     */
    public float polyOffsetUnits = 0;

    /**
     * For normals only. Uses GL_NORMALIZE.
     * 
     * see VertexBuffer#setNormalized(boolean) 
     */
    public boolean normalizeEnabled = false;

    /**
     * For glMatrixMode.
     * 
     * see Renderer#setWorldMatrix(com.clockwork.math.Matrix4f) 
     * see Renderer#setViewProjectionMatrices(com.clockwork.math.Matrix4f, com.clockwork.math.Matrix4f) 
     */
    public int matrixMode = -1;

    /**
     * see Mesh#setPointSize(float) 
     */
    public float pointSize = 1;
    
    /**
     * see Mesh#setLineWidth(float) 
     */
    public float lineWidth = 1;

    /**
     * see RenderState#setBlendMode(com.clockwork.material.RenderState.BlendMode) 
     */
    public RenderState.BlendMode blendMode = RenderState.BlendMode.Off;

    /**
     * see RenderState#setWireframe(boolean) 
     */
    public boolean wireframe = false;

    /**
     * see RenderState#setPointSprite(boolean) 
     */
    public boolean pointSprite = false;

    /**
     * see Renderer#setShader(com.clockwork.shader.Shader) 
     */
    public int boundShaderProgram;

    /**
     * see Renderer#setFrameBuffer(com.clockwork.texture.FrameBuffer) 
     */
    public int boundFBO = 0;

    /**
     * Currently bound Renderbuffer
     * 
     * see Renderer#setFrameBuffer(com.clockwork.texture.FrameBuffer) 
     */
    public int boundRB = 0;

    /**
     * Currently bound draw buffer
     * -2 = GL_NONE
     * -1 = GL_BACK
     *  0 = GL_COLOR_ATTACHMENT0
     *  n = GL_COLOR_ATTACHMENTn
     *  where n is an integer greater than 1
     * 
     * see Renderer#setFrameBuffer(com.clockwork.texture.FrameBuffer) 
     * see FrameBuffer#setTargetIndex(int) 
     */
    public int boundDrawBuf = -1;

    /**
     * Currently bound read buffer
     *
     * see RenderContext#boundDrawBuf
     * see Renderer#setFrameBuffer(com.clockwork.texture.FrameBuffer) 
     * see FrameBuffer#setTargetIndex(int) 
     */
    public int boundReadBuf = -1;

    /**
     * Currently bound element array vertex buffer.
     * 
     * see Renderer#renderMesh(com.clockwork.scene.Mesh, int, int) 
     */
    public int boundElementArrayVBO;

    /**
     * see Renderer#renderMesh(com.clockwork.scene.Mesh, int, int) 
     */
    public int boundVertexArray;

    /**
     * Currently bound array vertex buffer.
     * 
     * see Renderer#renderMesh(com.clockwork.scene.Mesh, int, int) 
     */
    public int boundArrayVBO;

    public int numTexturesSet = 0;

    /**
     * Current bound texture IDs for each texture unit.
     * 
     * see Renderer#setTexture(int, com.clockwork.texture.Texture) 
     */
    public Image[] boundTextures = new Image[16];

    /**
     * IDList for texture units
     * 
     * see Renderer#setTexture(int, com.clockwork.texture.Texture) 
     */
    public IDList textureIndexList = new IDList();

    /**
     * Currently bound texture unit
     * 
     * see Renderer#setTexture(int, com.clockwork.texture.Texture) 
     */
    public int boundTextureUnit = 0;

    /**
     * Stencil Buffer state
     */
    public boolean stencilTest = false;
    public RenderState.StencilOperation frontStencilStencilFailOperation = RenderState.StencilOperation.Keep;
    public RenderState.StencilOperation frontStencilDepthFailOperation = RenderState.StencilOperation.Keep;
    public RenderState.StencilOperation frontStencilDepthPassOperation = RenderState.StencilOperation.Keep;
    public RenderState.StencilOperation backStencilStencilFailOperation = RenderState.StencilOperation.Keep;
    public RenderState.StencilOperation backStencilDepthFailOperation = RenderState.StencilOperation.Keep;
    public RenderState.StencilOperation backStencilDepthPassOperation = RenderState.StencilOperation.Keep;
    public RenderState.TestFunction frontStencilFunction = RenderState.TestFunction.Always;
    public RenderState.TestFunction backStencilFunction = RenderState.TestFunction.Always;

    /**
     * Vertex attribs currently bound and enabled. If a slot is null, then
     * it is disabled.
     */
    public VertexBuffer[] boundAttribs = new VertexBuffer[16];

    /**
     * IDList for vertex attributes
     */
    public IDList attribIndexList = new IDList();
    
    /**
     * Ambient color (GL1 only)
     */
    public ColorRGBA ambient;
    
    /**
     * Diffuse color (GL1 only)
     */
    public ColorRGBA diffuse;
    
    /**
     * Specular color (GL1 only)
     */
    public ColorRGBA specular;
    
    /**
     * Material color (GL1 only)
     */
    public ColorRGBA color;
    
    /**
     * Shininess (GL1 only)
     */
    public float shininess;
    
    /**
     * Use vertex color (GL1 only)
     */
    public boolean useVertexColor;

    /**
     * Reset the RenderContext to default GL state
     */
    public void reset(){
        cullMode = RenderState.FaceCullMode.Off;
        depthTestEnabled = false;
        alphaTestFallOff = 0f;
        depthWriteEnabled = false;
        colorWriteEnabled = false;
        clipRectEnabled = false;
        polyOffsetEnabled = false;
        polyOffsetFactor = 0;
        polyOffsetUnits = 0;
        normalizeEnabled = false;
        matrixMode = -1;
        pointSize = 1;
        blendMode = RenderState.BlendMode.Off;
        wireframe = false;
        boundShaderProgram = 0;
        boundFBO = 0;
        boundRB = 0;
        boundDrawBuf = -1; 
        boundReadBuf = -1;
        boundElementArrayVBO = 0;
        boundVertexArray = 0;
        boundArrayVBO = 0;
        numTexturesSet = 0;
        for (int i = 0; i < boundTextures.length; i++)
            boundTextures[i] = null;

        textureIndexList.reset();
        boundTextureUnit = 0;
        for (int i = 0; i < boundAttribs.length; i++)
            boundAttribs[i] = null;

        attribIndexList.reset();
        
        stencilTest = false;
        frontStencilStencilFailOperation = RenderState.StencilOperation.Keep;
        frontStencilDepthFailOperation = RenderState.StencilOperation.Keep;
        frontStencilDepthPassOperation = RenderState.StencilOperation.Keep;
        backStencilStencilFailOperation = RenderState.StencilOperation.Keep;
        backStencilDepthFailOperation = RenderState.StencilOperation.Keep;
        backStencilDepthPassOperation = RenderState.StencilOperation.Keep;
        frontStencilFunction = RenderState.TestFunction.Always;
        backStencilFunction = RenderState.TestFunction.Always;
        
        ambient = diffuse = specular = color = null;
        shininess = 0;
        useVertexColor = false;
    }
}
