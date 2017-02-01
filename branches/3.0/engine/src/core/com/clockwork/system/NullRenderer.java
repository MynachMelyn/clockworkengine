
package com.clockwork.system;

import java.nio.ByteBuffer;
import java.util.EnumSet;

import com.clockwork.light.LightList;
import com.clockwork.material.RenderState;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Matrix4f;
import com.clockwork.renderer.Caps;
import com.clockwork.renderer.Renderer;
import com.clockwork.renderer.Statistics;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.VertexBuffer;
import com.clockwork.shader.Shader;
import com.clockwork.shader.Shader.ShaderSource;
import com.clockwork.texture.FrameBuffer;
import com.clockwork.texture.Image;
import com.clockwork.texture.Texture;

public class NullRenderer implements Renderer {

    private static final EnumSet<Caps> caps = EnumSet.noneOf(Caps.class);
    private static final Statistics stats = new Statistics();

    public EnumSet<Caps> getCaps() {
        return caps;
    }

    public Statistics getStatistics() {
        return stats;
    }

    public void invalidateState(){
    }

    public void clearBuffers(boolean color, boolean depth, boolean stencil) {
    }

    public void setBackgroundColor(ColorRGBA color) {
    }

    public void applyRenderState(RenderState state) {
    }

    public void setDepthRange(float start, float end) {
    }

    public void onFrame() {
    }

    public void setWorldMatrix(Matrix4f worldMatrix) {
    }

    public void setViewProjectionMatrices(Matrix4f viewMatrix, Matrix4f projMatrix) {
    }

    public void setViewPort(int x, int y, int width, int height) {
    }

    public void setClipRect(int x, int y, int width, int height) {
    }

    public void clearClipRect() {
    }

    public void setLighting(LightList lights) {
    }

    public void setShader(Shader shader) {
    }

    public void deleteShader(Shader shader) {
    }

    public void deleteShaderSource(ShaderSource source) {
    }

    public void copyFrameBuffer(FrameBuffer src, FrameBuffer dst) {
    }

    public void copyFrameBuffer(FrameBuffer src, FrameBuffer dst, boolean copyDepth) {
    }
    
    public void setMainFrameBufferOverride(FrameBuffer fb) {
    }
    
    public void setFrameBuffer(FrameBuffer fb) {
    }

    public void readFrameBuffer(FrameBuffer fb, ByteBuffer byteBuf) {
    }

    public void deleteFrameBuffer(FrameBuffer fb) {
    }

    public void setTexture(int unit, Texture tex) {
    }

    public void modifyTexture(Texture tex, Image pixels, int x, int y) {
    }

    public void updateBufferData(VertexBuffer vb) {
    }

    public void deleteBuffer(VertexBuffer vb) {
    }

    public void renderMesh(Mesh mesh, int lod, int count) {
    }

    public void resetGLObjects() {
    }

    public void cleanup() {
    }

    public void deleteImage(Image image) {
    }

    public void setAlphaToCoverage(boolean value) {
    }

}
