
package com.clockwork.app.state;

import com.clockwork.app.Application;
import com.clockwork.input.InputManager;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.post.SceneProcessor;
import com.clockwork.renderer.Camera;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.Renderer;
import com.clockwork.renderer.ViewPort;
import com.clockwork.renderer.queue.RenderQueue;
import com.clockwork.system.CWSystem;
import com.clockwork.texture.FrameBuffer;
import com.clockwork.util.BufferUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScreenshotAppState extends AbstractAppState implements ActionListener, SceneProcessor {

    private static final Logger logger = Logger.getLogger(ScreenshotAppState.class.getName());
    private String filePath = null;
    private boolean capture = false;
    private Renderer renderer;
    private RenderManager rm;
    private ByteBuffer outBuf;
    private String appName;
    private int shotIndex = 0;
    private int width, height;

    /**
     * Using this constructor, the screenshot files will be written sequentially to the system
     * default storage folder.
     */
    public ScreenshotAppState() {
        this(null);
    }

    /**
     * This constructor allows you to specify the output file path of the screenshot.
     * Include the seperator at the end of the path.
     * Use an emptry string to use the application folder. Use NULL to use the system
     * default storage folder.
     * @param file The screenshot file path to use. Include the seperator at the end of the path.
     */
    public ScreenshotAppState(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Set the file path to store the screenshot.
     * Include the seperator at the end of the path.
     * Use an emptry string to use the application folder. Use NULL to use the system
     * default storage folder.
     * @param file File path to use to store the screenshot. Include the seperator at the end of the path.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        if (!super.isInitialized()){
            InputManager inputManager = app.getInputManager();
            inputManager.addMapping("ScreenShot", new KeyTrigger(KeyInput.KEY_SYSRQ));
            inputManager.addListener(this, "ScreenShot");

            List<ViewPort> vps = app.getRenderManager().getPostViews();
            ViewPort last = vps.get(vps.size()-1);
            last.addProcessor(this);

            appName = app.getClass().getSimpleName();
        }

        super.initialize(stateManager, app);
    }

    public void onAction(String name, boolean value, float tpf) {
        if (value){
            capture = true;
        }
    }

    public void takeScreenshot() {
        capture = true;
    }

    public void initialize(RenderManager rm, ViewPort vp) {
        renderer = rm.getRenderer();
        this.rm = rm;
        reshape(vp, vp.getCamera().getWidth(), vp.getCamera().getHeight());
    }

    @Override
    public boolean isInitialized() {
        return super.isInitialized() && renderer != null;
    }

    public void reshape(ViewPort vp, int w, int h) {
        outBuf = BufferUtils.createByteBuffer(w * h * 4);
        width = w;
        height = h;
    }

    public void preFrame(float tpf) {
    }

    public void postQueue(RenderQueue rq) {
    }

    public void postFrame(FrameBuffer out) {
        if (capture){
            capture = false;
            shotIndex++;

            Camera curCamera = rm.getCurrentCamera();
            int viewX = (int) (curCamera.getViewPortLeft() * curCamera.getWidth());
            int viewY = (int) (curCamera.getViewPortBottom() * curCamera.getHeight());
            int viewWidth = (int) ((curCamera.getViewPortRight() - curCamera.getViewPortLeft()) * curCamera.getWidth());
            int viewHeight = (int) ((curCamera.getViewPortTop() - curCamera.getViewPortBottom()) * curCamera.getHeight());

            renderer.setViewPort(0, 0, width, height);
            renderer.readFrameBuffer(out, outBuf);
            renderer.setViewPort(viewX, viewY, viewWidth, viewHeight);

            File file;
            if (filePath == null) {
                file = new File(CWSystem.getStorageFolder() + File.separator + appName + shotIndex + ".png").getAbsoluteFile();
            } else {
                file = new File(filePath + appName + shotIndex + ".png").getAbsoluteFile();
            }
            logger.log(Level.FINE, "Saving ScreenShot to: {0}", file.getAbsolutePath());

            OutputStream outStream = null;
            try {
                outStream = new FileOutputStream(file);
                CWSystem.writeImageFile(outStream, "png", outBuf, width, height);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error while saving screenshot", ex);
            } finally {
                if (outStream != null){
                    try {
                        outStream.close();
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, "Error while saving screenshot", ex);
                    }
                }
            }
        }
    }
}
