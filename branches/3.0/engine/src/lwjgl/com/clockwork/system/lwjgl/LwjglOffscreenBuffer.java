

package com.clockwork.system.lwjgl;

import com.clockwork.input.JoyInput;
import com.clockwork.input.KeyInput;
import com.clockwork.input.MouseInput;
import com.clockwork.input.TouchInput;
import com.clockwork.input.dummy.DummyKeyInput;
import com.clockwork.input.dummy.DummyMouseInput;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

public class LwjglOffscreenBuffer extends LwjglContext implements Runnable {

    private static final Logger logger = Logger.getLogger(LwjglOffscreenBuffer.class.getName());
    private Pbuffer pbuffer;
    protected AtomicBoolean needClose = new AtomicBoolean(false);
    private int width;
    private int height;
    private PixelFormat pixelFormat;

    protected void initInThread(){
        if ((Pbuffer.getCapabilities() & Pbuffer.PBUFFER_SUPPORTED) == 0){
            logger.severe("Offscreen surfaces are not supported.");
            return;
        }

        int samples = getNumSamplesToUse();
        pixelFormat = new PixelFormat(settings.getBitsPerPixel(),
                                      0,
                                      settings.getDepthBits(),
                                      settings.getStencilBits(),
                                      samples);

        width = settings.getWidth();
        height = settings.getHeight();
        try{
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                public void uncaughtException(Thread thread, Throwable thrown) {
                    listener.handleError("Uncaught exception thrown in "+thread.toString(), thrown);
                }
            });

            pbuffer = new Pbuffer(width, height, pixelFormat, null, null, createContextAttribs());
            pbuffer.makeCurrent();

            renderable.set(true);

            logger.fine("Offscreen buffer created.");
            printContextInitInfo();
        } catch (LWJGLException ex){
            listener.handleError("Failed to create display", ex);
        } finally {
            // TODO: It is possible to avoid "Failed to find pixel format"
            // error here by creating a default display.
        }
        super.internalCreate();
        listener.initialize();
    }

    protected boolean checkGLError(){
        try {
            Util.checkGLError();
        } catch (OpenGLException ex){
            listener.handleError("An OpenGL error has occured!", ex);
        }
        // NOTE: Always return true since this is used in an "assert" statement
        return true;
    }

    protected void runLoop(){
        if (!created.get()) {
            throw new IllegalStateException();
        }

        if (pbuffer.isBufferLost()) {
            pbuffer.destroy();

            try {
                pbuffer = new Pbuffer(width, height, pixelFormat, null);
                pbuffer.makeCurrent();
                
                // Context MUST be reset here to avoid invalid objects!
                renderer.invalidateState();
            } catch (LWJGLException ex) {
                listener.handleError("Failed to restore pbuffer content", ex);
            }
        }

        listener.update();
        checkGLError();

        renderer.onFrame();

        int frameRate = settings.getFrameRate();
        if (frameRate >= 1) {
            Display.sync(frameRate);
        }
    }

    protected void deinitInThread(){
        renderable.set(false);

        listener.destroy();
        renderer.cleanup();
        pbuffer.destroy();
        logger.fine("Offscreen buffer destroyed.");
        
        super.internalDestroy();
    }

    public void run(){
        logger.log(Level.FINE, "Using LWJGL {0}", Sys.getVersion());
        initInThread();
        while (!needClose.get()){
            runLoop();
        }
        deinitInThread();
    }

    public void destroy(boolean waitFor){
        needClose.set(true);
        if (waitFor)
            waitFor(false);
    }

    public void create(boolean waitFor){
        if (created.get()){
            logger.warning("create() called when pbuffer is already created!");
            return;
        }

        new Thread(this, "LWJGL Renderer Thread").start();
        if (waitFor)
            waitFor(true);
    }

    public void restart() {
    }

    public void setAutoFlushFrames(boolean enabled){
    }

    public Type getType() {
        return Type.OffscreenSurface;
    }

    public MouseInput getMouseInput() {
        return new DummyMouseInput();
    }

    public KeyInput getKeyInput() {
        return new DummyKeyInput();
    }

    public JoyInput getJoyInput() {
        return null;
    }

    public TouchInput getTouchInput() {
        return null;
    }

    public void setTitle(String title) {
    }

}
