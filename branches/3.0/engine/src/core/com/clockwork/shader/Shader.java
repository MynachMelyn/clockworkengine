
package com.clockwork.shader;

import com.clockwork.renderer.Renderer;
import com.clockwork.scene.VertexBuffer;
import com.clockwork.util.IntMap;
import com.clockwork.util.IntMap.Entry;
import com.clockwork.util.ListMap;
import com.clockwork.util.NativeObject;
import java.util.ArrayList;
import java.util.Collection;

public final class Shader extends NativeObject {
    
    /**
     * A list of all shader sources currently attached.
     */
    private ArrayList<ShaderSource> shaderSourceList;

    /**
     * Maps uniform name to the uniform variable.
     */
    private ListMap<String, Uniform> uniforms;

    /**
     * Maps attribute name to the location of the attribute in the shader.
     */
    private IntMap<Attribute> attribs;

    /**
     * Type of shader. The shader will control the pipeline of it's type.
     */
    public static enum ShaderType {
        /**
         * Control fragment rasterization. (e.g color of pixel).
         */
        Fragment,

        /**
         * Control vertex processing. (e.g transform of model to clip space)
         */
        Vertex,

        /**
         * Control geometry assembly. (e.g compile a triangle list from input data)
         */
        Geometry;
    }

    /**
     * Shader source describes a shader object in OpenGL. Each shader source
     * is assigned a certain pipeline which it controls (described by it's type).
     */
    public static class ShaderSource extends NativeObject {

        ShaderType sourceType;
        String language;
        String name;
        String source;
        String defines;

        public ShaderSource(ShaderType type){
            super();
            this.sourceType = type;
            if (type == null) {
                throw new IllegalArgumentException("The shader type must be specified");
            }
        }
        
        protected ShaderSource(ShaderSource ss){
            super(ss.id);
            // No data needs to be copied.
            // (This is a destructable clone)
        }

        public ShaderSource(){
            super();
        }

        public void setName(String name){
            this.name = name;
        }

        public String getName(){
            return name;
        }

        public ShaderType getType() {
            return sourceType;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            if (language == null) {
                throw new IllegalArgumentException("Shader language cannot be null");
            }
            this.language = language;
            setUpdateNeeded();
        }

        public void setSource(String source){
            if (source == null) {
                throw new IllegalArgumentException("Shader source cannot be null");
            }
            this.source = source;
            setUpdateNeeded();
        }

        public void setDefines(String defines){
            if (defines == null) {
                throw new IllegalArgumentException("Shader defines cannot be null");
            }
            this.defines = defines;
            setUpdateNeeded();
        }

        public String getSource(){
            return source;
        }

        public String getDefines(){
            return defines;
        }
        
        @Override
        public long getUniqueId() {
            return ((long)OBJTYPE_SHADERSOURCE << 32) | ((long)id);
        }
        
        @Override
        public String toString(){
            String nameTxt = "";
            if (name != null)
                nameTxt = "name="+name+", ";
            if (defines != null)
                nameTxt += "defines, ";
            

            return getClass().getSimpleName() + "["+nameTxt+"type="
                                              + sourceType.name()+", language=" + language + "]";
        }

        public void resetObject(){
            id = -1;
            setUpdateNeeded();
        }

        public void deleteObject(Object rendererObject){
            ((Renderer)rendererObject).deleteShaderSource(ShaderSource.this);
        }

        public NativeObject createDestructableClone(){
            return new ShaderSource(ShaderSource.this);
        }
    }

    /**
     * Initializes the shader for use, must be called after the 
     * constructor without arguments is used.
     */
    public void initialize() {
        shaderSourceList = new ArrayList<ShaderSource>();
        uniforms = new ListMap<String, Uniform>();
        attribs = new IntMap<Attribute>();
    }
    
    /**
     * Creates a new shader, #initialize() } must be called
     * after this constructor for the shader to be usable.
     */
    public Shader(){
        super();
    }

    /**
     * Do not use this constructor. Used for destructable clones only.
     */
    protected Shader(Shader s){
        super(s.id);
        
        // Shader sources cannot be shared, therefore they must
        // be destroyed together with the parent shader.
        shaderSourceList = new ArrayList<ShaderSource>();
        for (ShaderSource source : s.shaderSourceList){
            shaderSourceList.add( (ShaderSource)source.createDestructableClone() );
        }
    }

    /**
     * Adds source code to a certain pipeline.
     *
     * @param type The pipeline to control
     * @param source The shader source code (in GLSL).
     * @param defines Preprocessor defines (placed at the beginning of the shader)
     * @param language The shader source language, currently accepted is GLSL###
     * where ### is the version, e.g. GLSL100 = GLSL 1.0, GLSL330 = GLSL 3.3, etc.
     */
    public void addSource(ShaderType type, String name, String source, String defines, String language){
        ShaderSource shaderSource = new ShaderSource(type);
        shaderSource.setSource(source);
        shaderSource.setName(name);
        shaderSource.setLanguage(language);
        if (defines != null) {
            shaderSource.setDefines(defines);
        }
        shaderSourceList.add(shaderSource);
        setUpdateNeeded();
    }

    public Uniform getUniform(String name){
        Uniform uniform = uniforms.get(name);
        if (uniform == null){
            uniform = new Uniform();
            uniform.name = name;
            uniforms.put(name, uniform);
        }
        return uniform;
    }

    public void removeUniform(String name){
        uniforms.remove(name);
    }

    public Attribute getAttribute(VertexBuffer.Type attribType){
        int ordinal = attribType.ordinal();
        Attribute attrib = attribs.get(ordinal);
        if (attrib == null){
            attrib = new Attribute();
            attrib.name = attribType.name();
            attribs.put(ordinal, attrib);
        }
        return attrib;
    }

    public ListMap<String, Uniform> getUniformMap(){
        return uniforms;
    }

    public Collection<ShaderSource> getSources(){
        return shaderSourceList;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + 
                "[numSources=" + shaderSourceList.size() +
                ", numUniforms=" + uniforms.size() +
                ", shaderSources=" + getSources() + "]";
    }

    /**
     * Usually called when the shader itself changes or during any
     * time when the variable locations need to be refreshed.
     */
    public void resetLocations() {
        if (uniforms != null) {
            // NOTE: Shader sources will be reset seperately from the shader itself.
            for (Uniform uniform : uniforms.values()) {
                uniform.reset(); // fixes issue with re-initialization
            }
        }
        if (attribs != null) {
            for (Entry<Attribute> entry : attribs) {
                entry.getValue().location = ShaderVariable.LOC_UNKNOWN;
            }
        }
    }

    @Override
    public void setUpdateNeeded(){
        super.setUpdateNeeded();
        resetLocations();
    }

    /**
     * Called by the object manager to reset all object IDs. This causes
     * the shader to be reuploaded to the GPU incase the display was restarted.
     */
    @Override
    public void resetObject() {
        this.id = -1;
        for (ShaderSource source : shaderSourceList){
            source.resetObject();
        }
        setUpdateNeeded();
    }

    @Override
    public void deleteObject(Object rendererObject) {
        ((Renderer)rendererObject).deleteShader(this);
    }

    public NativeObject createDestructableClone(){
        return new Shader(this);
    }

    @Override
    public long getUniqueId() {
        return ((long)OBJTYPE_SHADER << 32) | ((long)id);
    }
}
