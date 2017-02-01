

package com.clockwork.system.jogl;

import com.clockwork.system.JmeCanvasContext;
import com.jogamp.newt.awt.NewtCanvasAWT;
import java.util.logging.Logger;
import javax.media.opengl.GLAutoDrawable;

public class JoglNewtCanvas extends JoglNewtAbstractDisplay implements JmeCanvasContext {
    
    private static final Logger logger = Logger.getLogger(JoglNewtCanvas.class.getName());
    private int width, height;
    
    private NewtCanvasAWT newtAwtCanvas;

    public JoglNewtCanvas(){
        super();
        initGLCanvas();
    }
    
    @Override
    protected final void initGLCanvas() {
        super.initGLCanvas();
        newtAwtCanvas = new NewtCanvasAWT(canvas) {
            @Override
            public void addNotify() {
                super.addNotify();
                onCanvasAdded();
            }

            @Override
            public void removeNotify() {
                onCanvasRemoved();
                super.removeNotify();
            }
        };
    }

    public Type getType() {
        return Type.Canvas;
    }

    public void setTitle(String title) {
    }

    public void restart() {
    }

    public void create(boolean waitFor){
        if (waitFor)
            waitFor(true);
    }

    public void destroy(boolean waitFor){
        if (waitFor)
            waitFor(false);
    }

    @Override
    protected void onCanvasRemoved(){
        super.onCanvasRemoved();
        created.set(false);
        waitFor(false);
    }

    @Override
    protected void onCanvasAdded(){
        startGLCanvas();
    }

    public void init(GLAutoDrawable drawable) {
        canvas.requestFocus();

        super.internalCreate();
        logger.fine("Display created.");

        renderer.initialize();
        listener.initialize();
    }

    public void display(GLAutoDrawable glad) {
        if (!created.get() && renderer != null){
            listener.destroy();
            logger.fine("Canvas destroyed.");
            super.internalDestroy();
            return;
        }

        int newWidth = Math.max(canvas.getWidth(), 1);
        int newHeight = Math.max(canvas.getHeight(), 1);
        if (width != newWidth || height != newHeight) {
            width = newWidth;
            height = newHeight;
            if (listener != null) {
                listener.reshape(width, height);
            }
        }

        boolean flush = autoFlush.get();
        if (flush && !wasAnimating){
            animator.start();
            wasAnimating = true;
        }else if (!flush && wasAnimating){
            animator.stop();
            wasAnimating = false;
        }
            
        listener.update();
        renderer.onFrame();

    }

    @Override
    public NewtCanvasAWT getCanvas() {
        return newtAwtCanvas;
    }

    @Override
    public void dispose(GLAutoDrawable arg0) {       
    }

}
