

package com.clockwork.system.jogl;

import com.clockwork.input.KeyInput;
import com.clockwork.input.MouseInput;
import com.clockwork.input.TouchInput;
import com.clockwork.input.awt.AwtKeyInput;
import com.clockwork.input.awt.AwtMouseInput;
import com.clockwork.renderer.jogl.JoglRenderer;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import javax.media.opengl.DebugGL2;
import javax.media.opengl.DebugGL3;
import javax.media.opengl.DebugGL3bc;
import javax.media.opengl.DebugGL4;
import javax.media.opengl.DebugGL4bc;
import javax.media.opengl.DebugGLES1;
import javax.media.opengl.DebugGLES2;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.GLRunnable;
import javax.media.opengl.awt.GLCanvas;

public abstract class JoglAbstractDisplay extends JoglContext implements GLEventListener {

    private static final Logger logger = Logger.getLogger(JoglAbstractDisplay.class.getName());

    protected GraphicsDevice device;

    protected GLCanvas canvas;

    protected AnimatorBase animator;

    protected AtomicBoolean active = new AtomicBoolean(false);

    protected boolean wasActive = false;

    protected int frameRate;

    protected boolean useAwt = true;

    protected AtomicBoolean autoFlush = new AtomicBoolean(true);

    protected boolean wasAnimating = false;

    protected void initGLCanvas() {
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        //FIXME use the settings to know whether to use the max programmable profile
        //then call GLProfile.getMaxProgrammable(true);
        GLCapabilities caps = new GLCapabilities(GLProfile.getMaxFixedFunc(true));
        caps.setHardwareAccelerated(true);
        caps.setDoubleBuffered(true);
        caps.setStencilBits(settings.getStencilBits());
        caps.setDepthBits(settings.getDepthBits());

        if (settings.getSamples() > 1) {
            caps.setSampleBuffers(true);
            caps.setNumSamples(settings.getSamples());
        }

        canvas = new GLCanvas(caps) {
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
        canvas.invoke(false, new GLRunnable() {
            public boolean run(GLAutoDrawable glad) {
                canvas.getGL().setSwapInterval(settings.isVSync() ? 1 : 0);
                return true;
            }
        });
        canvas.setFocusable(true);
        canvas.requestFocus();
        canvas.setSize(settings.getWidth(), settings.getHeight());
        canvas.setIgnoreRepaint(true);
        canvas.addGLEventListener(this);

        if (settings.getBoolean("GraphicsDebug")) {
            canvas.invoke(false, new GLRunnable() {
                public boolean run(GLAutoDrawable glad) {
                    GL gl = glad.getGL();
                    if (gl.isGLES()) {
                        if (gl.isGLES1()) {
                            glad.setGL(new DebugGLES1(gl.getGLES1()));
                        } else {
                            if (gl.isGLES2()) {
                                glad.setGL(new DebugGLES2(gl.getGLES2()));
                            } else {
                                // TODO ES3
                            }
                        }
                    } else {
                        if (gl.isGL4bc()) {
                            glad.setGL(new DebugGL4bc(gl.getGL4bc()));
                        } else {
                            if (gl.isGL4()) {
                                glad.setGL(new DebugGL4(gl.getGL4()));
                            } else {
                                if (gl.isGL3bc()) {
                                    glad.setGL(new DebugGL3bc(gl.getGL3bc()));
                                } else {
                                    if (gl.isGL3()) {
                                        glad.setGL(new DebugGL3(gl.getGL3()));
                                    } else {
                                        if (gl.isGL2()) {
                                            glad.setGL(new DebugGL2(gl.getGL2()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return true;
                }
            });
        }
        
        renderer = new JoglRenderer();
    }

    protected void startGLCanvas() {
        if (frameRate > 0) {
            animator = new FPSAnimator(canvas, frameRate);
        }
        else {
            animator = new Animator();
            animator.add(canvas);
            ((Animator) animator).setRunAsFastAsPossible(true);
        }

        animator.start();
        wasAnimating = true;
        
        //FIXME not sure it is the best place to do that
        renderable.set(true);
    }

    protected void onCanvasAdded() {
    }

    protected void onCanvasRemoved() {
    }

    @Override
    public KeyInput getKeyInput() {
        if (keyInput == null) {
            keyInput = new AwtKeyInput();
            ((AwtKeyInput)keyInput).setInputSource(canvas);
        }
        return keyInput;
    }

    @Override
    public MouseInput getMouseInput() {
        if (mouseInput == null) {
            mouseInput = new AwtMouseInput();
            ((AwtMouseInput)mouseInput).setInputSource(canvas);
        }
        return mouseInput;
    }
    
    public TouchInput getTouchInput() {
        return null;
    }

    public void setAutoFlushFrames(boolean enabled) {
        autoFlush.set(enabled);
    }

    /**
     * Callback.
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        listener.reshape(width, height);
    }

    /**
     * Callback.
     */
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    /**
     * Callback
     */
    public void dispose(GLAutoDrawable drawable) {

    }
}
