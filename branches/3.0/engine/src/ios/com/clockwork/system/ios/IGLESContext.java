
package com.clockwork.system.ios;

import com.clockwork.input.*;
import com.clockwork.input.controls.SoftTextDialogInputListener;
import com.clockwork.input.dummy.DummyKeyInput;
import com.clockwork.input.dummy.DummyMouseInput;
import com.clockwork.renderer.ios.IGLESShaderRenderer;
import com.clockwork.system.*;
import com.clockwork.input.ios.IosInputHandler;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IGLESContext implements CWContext {

    private static final Logger logger = Logger.getLogger(IGLESContext.class.getName());
    protected final AtomicBoolean created = new AtomicBoolean(false);
    protected final AtomicBoolean renderable = new AtomicBoolean(false);
    protected final AtomicBoolean needClose = new AtomicBoolean(false);
    protected AppSettings settings = new AppSettings(true);
    protected boolean autoFlush = true;

    /*
     * >= OpenGL ES 2.0 (iOS)
     */
    protected IGLESShaderRenderer renderer;
    protected Timer timer;
    protected SystemListener listener;
    protected IosInputHandler input;
    protected int minFrameDuration = 0;                   // No FPS cap

    public IGLESContext() {
           logger.log(Level.FINE, "IGLESContext constructor");
    }

    @Override
    public CWContext.Type getType() {
        return CWContext.Type.Display;
    }

    @Override
    public void setSettings(AppSettings settings) {
        logger.log(Level.FINE, "IGLESContext setSettings");
        this.settings.copyFrom(settings);
        if (input != null) {
            input.loadSettings(settings);
        }
    }

    @Override
    public void setSystemListener(SystemListener listener) {
        logger.log(Level.FINE, "IGLESContext setSystemListener");
        this.listener = listener;
    }

    @Override
    public AppSettings getSettings() {
        return settings;
    }

    @Override
    public com.clockwork.renderer.Renderer getRenderer() {
        logger.log(Level.FINE, "IGLESContext getRenderer");
        return renderer;
    }

    @Override
    public MouseInput getMouseInput() {
        return new DummyMouseInput();
    }

    @Override
    public KeyInput getKeyInput() {
        return new DummyKeyInput();
    }

    @Override
    public JoyInput getJoyInput() {
    /*
        if (androidSensorJoyInput == null) {
            androidSensorJoyInput = new AndroidSensorJoyInput();
        }
        return androidSensorJoyInput;
        */
        return null;//  new DummySensorJoyInput();
    }

    @Override
    public TouchInput getTouchInput() {
        return input;
    }

    @Override
    public Timer getTimer() {
        return timer;
    }

    @Override
    public void setTitle(String title) {
    }

    @Override
    public boolean isCreated() {
        logger.log(Level.FINE, "IGLESContext isCreated");
		return created.get();
    }

    @Override
    public void setAutoFlushFrames(boolean enabled) {
        this.autoFlush = enabled;
    }

    @Override
    public boolean isRenderable() {
        logger.log(Level.FINE, "IGLESContext isRenderable");
        return true;// renderable.get();
    }

    @Override
    public void create(boolean waitFor) {
        logger.log(Level.FINE, "IGLESContext create");
        renderer = new IGLESShaderRenderer();
        input = new IosInputHandler();
        timer = new NanoTimer();

//synchronized (createdLock){
            created.set(true);
            //createdLock.notifyAll();
        //}

        listener.initialize();

        if (waitFor) {
            //waitFor(true);
        }
        logger.log(Level.FINE, "IGLESContext created");
    }

    public void create() {
        create(false);
    }

    @Override
    public void restart() {
    }

    @Override
    public void destroy(boolean waitFor) {
        logger.log(Level.FINE, "IGLESContext destroy");
		listener.destroy();
		needClose.set(true);
        if (waitFor) {
            //waitFor(false);
        }
    }

    public void destroy() {
        destroy(true);
    }

    protected void waitFor(boolean createdVal) {
        while (renderable.get() != createdVal) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }
}