
package com.clockwork.shader;

public enum VarType {

    Float("float"),
    Vector2("vec2"),
    Vector3("vec3"),
    Vector4("vec4"),

    IntArray(true,false,"int"),
    FloatArray(true,false,"float"),
    Vector2Array(true,false,"vec2"),
    Vector3Array(true,false,"vec3"),
    Vector4Array(true,false,"vec4"),

    Boolean("bool"),

    Matrix3(true,false,"mat3"),
    Matrix4(true,false,"mat4"),

    Matrix3Array(true,false,"mat3"),
    Matrix4Array(true,false,"mat4"),
    
    TextureBuffer(false,true,"sampler1D|sampler1DShadow"),
    Texture2D(false,true,"sampler2D|sampler2DShadow"),
    Texture3D(false,true,"sampler3D"),
    TextureArray(false,true,"sampler2DArray"),
    TextureCubeMap(false,true,"samplerCube"),
    Int("int");

    private boolean usesMultiData = false;
    private boolean textureType = false;
    private String glslType;

    
    VarType(String glslType){
        this.glslType = glslType;
    }

    VarType(boolean multiData, boolean textureType,String glslType){
        usesMultiData = multiData;
        this.textureType = textureType;
        this.glslType = glslType;
    }

    public boolean isTextureType() {
        return textureType;
    }

    public boolean usesMultiData() {
        return usesMultiData;
    }

    public String getGlslType() {
        return glslType;
    }    

}
