
package com.clockwork.renderer;

import com.clockwork.scene.Mesh;
import com.clockwork.shader.Shader;
import com.clockwork.texture.FrameBuffer;
import com.clockwork.texture.Image;
import java.util.HashSet;

/**
 * The statistics class allows tracking of real-time rendering statistics.
 * 
 * The Statistics can be retrieved by using Renderer#getStatistics() }.
 * 
 */
public class Statistics {

    protected boolean enabled = false;

    protected int numObjects;
    protected int numTriangles;
    protected int numVertices;
    protected int numShaderSwitches;
    protected int numTextureBinds;
    protected int numFboSwitches;
    protected int numUniformsSet;

    protected int memoryShaders;
    protected int memoryFrameBuffers;
    protected int memoryTextures;

    protected HashSet<Integer> shadersUsed = new HashSet<Integer>();
    protected HashSet<Integer> texturesUsed = new HashSet<Integer>();
    protected HashSet<Integer> fbosUsed = new HashSet<Integer>();

    /**
     * Returns a list of labels corresponding to each statistic.
     * 
     * @return a list of labels corresponding to each statistic.
     * 
     * see #getData(int[]) 
     */
    public String[] getLabels(){
        return new String[]{ "Vertices",
                             "Triangles",
                             "Uniforms",

                             "Objects",

                             "Shaders (S)",
                             "Shaders (F)",
                             "Shaders (M)",

                             "Textures (S)",
                             "Textures (F)",
                             "Textures (M)",

                             "FrameBuffers (S)",
                             "FrameBuffers (F)",
                             "FrameBuffers (M)" };

    }

    /**
     * Retrieves the statistics data into the given array.
     * The array should be as large as the array given in 
     * #getLabels() }.
     * 
     * @param data The data array to write to
     */
    public void getData(int[] data){
        data[0] = numVertices;
        data[1] = numTriangles;
        data[2] = numUniformsSet;
        data[3] = numObjects;

        data[4] = numShaderSwitches;
        data[5] = shadersUsed.size();
        data[6] = memoryShaders;

        data[7] = numTextureBinds;
        data[8] = texturesUsed.size();
        data[9] = memoryTextures;
        
        data[10] = numFboSwitches;
        data[11] = fbosUsed.size();
        data[12] = memoryFrameBuffers;
    }

    /**
     * Called by the Renderer when a mesh has been drawn.
     * 
     */
    public void onMeshDrawn(Mesh mesh, int lod){
        if( !enabled )
            return;
            
        numObjects ++;
        numTriangles += mesh.getTriangleCount(lod);
        numVertices += mesh.getVertexCount();
    }

    /**
     * Called by the Renderer when a shader has been utilized.
     * 
     * @param shader The shader that was used
     * @param wasSwitched If true, the shader has required a state switch
     */
    public void onShaderUse(Shader shader, boolean wasSwitched){
        assert shader.getId() >= 1;

        if( !enabled )
            return;
            
        if (!shadersUsed.contains(shader.getId()))
            shadersUsed.add(shader.getId());

        if (wasSwitched)
            numShaderSwitches++;
    }

    /**
     * Called by the Renderer when a uniform was set.
     */
    public void onUniformSet(){
        if( !enabled )
            return;
        numUniformsSet ++;
    }

    /**
     * Called by the Renderer when a texture has been set.
     * 
     * @param image The image that was set
     * @param wasSwitched If true, the texture has required a state switch
     */
    public void onTextureUse(Image image, boolean wasSwitched){
        assert image.getId() >= 1;

        if( !enabled )
            return;
            
        if (!texturesUsed.contains(image.getId()))
            texturesUsed.add(image.getId());

        if (wasSwitched)
            numTextureBinds ++;
    }

    /**
     * Called by the Renderer when a framebuffer has been set.
     * 
     * @param fb The framebuffer that was set
     * @param wasSwitched If true, the framebuffer required a state switch
     */
    public void onFrameBufferUse(FrameBuffer fb, boolean wasSwitched){
        if( !enabled )
            return;
            
        if (fb != null){
            assert fb.getId() >= 1;

            if (!fbosUsed.contains(fb.getId()))
                fbosUsed.add(fb.getId());
        }

        if (wasSwitched)
            numFboSwitches ++;
    }
    
    /**
     * Clears all frame-specific statistics such as objects used per frame.
     */
    public void clearFrame(){
        shadersUsed.clear();
        texturesUsed.clear();
        fbosUsed.clear();

        numObjects = 0;
        numTriangles = 0;
        numVertices = 0;
        numShaderSwitches = 0;
        numTextureBinds = 0;
        numFboSwitches = 0;
        numUniformsSet = 0;
    }

    /**
     * Called by the Renderer when it creates a new shader
     */
    public void onNewShader(){
        if( !enabled )
            return;
        memoryShaders ++;
    }

    /**
     * Called by the Renderer when it creates a new texture
     */
    public void onNewTexture(){
        if( !enabled )
            return;
        memoryTextures ++;
    }

    /**
     * Called by the Renderer when it creates a new framebuffer
     */
    public void onNewFrameBuffer(){
        if( !enabled )
            return;
        memoryFrameBuffers ++;
    }

    /**
     * Called by the Renderer when it deletes a shader
     */
    public void onDeleteShader(){
        if( !enabled )
            return;
        memoryShaders --;
    }

    /**
     * Called by the Renderer when it deletes a texture
     */
    public void onDeleteTexture(){
        if( !enabled )
            return;
        memoryTextures --;
    }

    /**
     * Called by the Renderer when it deletes a framebuffer
     */
    public void onDeleteFrameBuffer(){
        if( !enabled )
            return;
        memoryFrameBuffers --;
    }

    /**
     * Called when video memory is cleared.
     */
    public void clearMemory(){
        memoryFrameBuffers = 0;
        memoryShaders = 0;
        memoryTextures = 0;
    }

    public void setEnabled( boolean f ) {
        this.enabled = f;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
}
