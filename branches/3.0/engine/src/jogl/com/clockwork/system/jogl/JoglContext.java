

package com.clockwork.system.jogl;

import com.clockwork.input.JoyInput;
import com.clockwork.input.KeyInput;
import com.clockwork.input.MouseInput;
import com.clockwork.renderer.Renderer;
import com.clockwork.renderer.jogl.JoglRenderer;
import com.clockwork.system.AppSettings;
import com.clockwork.system.JmeContext;
import com.clockwork.system.NanoTimer;
import com.clockwork.system.SystemListener;
import com.clockwork.system.Timer;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.media.opengl.GL;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLContext;

public abstract class JoglContext implements JmeContext {

    protected AtomicBoolean created = new AtomicBoolean(false);
    protected AtomicBoolean renderable = new AtomicBoolean(false);
    protected final Object createdLock = new Object();

    protected AppSettings settings = new AppSettings(true);
    protected JoglRenderer renderer;
    protected Timer timer;
    protected SystemListener listener;

    protected KeyInput keyInput;
    protected MouseInput mouseInput;
    protected JoyInput joyInput;

    public void setSystemListener(SystemListener listener){
        this.listener = listener;
    }

    public void setSettings(AppSettings settings) {
        this.settings.copyFrom(settings);
    }
    
    public boolean isRenderable(){
        return renderable.get();
    }

    public AppSettings getSettings() {
        return settings;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public MouseInput getMouseInput() {
        return mouseInput;
    }

    public KeyInput getKeyInput() {
        return keyInput;
    }

    public JoyInput getJoyInput() {
        return joyInput;
    }

    public Timer getTimer() {
        return timer;
    }

    public boolean isCreated() {
        return created.get();
    }

    public void create(){
        create(false);
    }

    public void destroy(){
        destroy(false);
    }

    protected void waitFor(boolean createdVal){
        synchronized (createdLock){
            while (created.get() != createdVal){
                try {
                    createdLock.wait();
                } catch (InterruptedException ex) {
                }
            }
        }
    }

     public void internalCreate() {
        timer = new NanoTimer();
        synchronized (createdLock){
            created.set(true);
            createdLock.notifyAll();
        }
    }

    protected void internalDestroy() {
        renderer = null;
        timer = null;
        renderable.set(false);
        synchronized (createdLock){
            created.set(false);
            createdLock.notifyAll();
        }
    }
    
    protected int determineMaxSamples(int requestedSamples) {
        GL gl = GLContext.getCurrentGL();
        if (gl.hasFullFBOSupport()) {
            return gl.getMaxRenderbufferSamples();
        } else {
            if (gl.isExtensionAvailable("GL_ARB_framebuffer_object")
                    || gl.isExtensionAvailable("GL_EXT_framebuffer_multisample")) {
                IntBuffer intBuf1 = IntBuffer.allocate(1);
                gl.glGetIntegerv(GL2GL3.GL_MAX_SAMPLES, intBuf1);
                return intBuf1.get(0);
            } else {
                return Integer.MAX_VALUE;
            }
        }
    }
    
    protected int getNumSamplesToUse() {
        int samples = 0;
        if (settings.getSamples() > 1){
            samples = settings.getSamples();
            int supportedSamples = determineMaxSamples(samples);
            if (supportedSamples < samples) {
                samples = supportedSamples;
            }
        }
        return samples;
    }
}
