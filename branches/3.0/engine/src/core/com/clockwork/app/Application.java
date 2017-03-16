
package com.clockwork.app;

import com.clockwork.app.state.AppStateManager;
import com.clockwork.asset.AssetManager;
import com.clockwork.audio.AudioContext;
import com.clockwork.audio.AudioRenderer;
import com.clockwork.audio.Listener;
import com.clockwork.input.*;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.Camera;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.Renderer;
import com.clockwork.renderer.ViewPort;
import com.clockwork.system.*;
import com.clockwork.system.CWContext.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Application class represents an instance of a
 * real-time 3D rendering application.
 *
 * An Application provides all the tools that are commonly used in CW
 * applications.
 *
 * Applications *SHOULD NOT EXTEND* this class but extend com.clockwork.app.SimpleApplication instead.
 *
 */
public class Application implements SystemListener {

    private static final Logger logger = Logger.getLogger(Application.class.getName());

    protected AssetManager assetManager;

    protected AudioRenderer audioRenderer;
    protected Renderer renderer;
    protected RenderManager renderManager;
    protected ViewPort viewPort;
    protected ViewPort guiViewPort;

    protected CWContext context;
    protected AppSettings settings;
    protected Timer timer = new NanoTimer();
    protected Camera cam;
    protected Listener listener;

    protected boolean inputEnabled = true;
    protected boolean pauseOnFocus = true;
    protected float speed = 1f;
    protected boolean paused = false;
    protected MouseInput mouseInput;
    protected KeyInput keyInput;
    protected JoyInput joyInput;
    protected TouchInput touchInput;
    protected InputManager inputManager;
    protected AppStateManager stateManager;

    private final ConcurrentLinkedQueue<AppTask<?>> taskQueue = new ConcurrentLinkedQueue<AppTask<?>>();

    /**
     * Create a new instance of Application.
     */
    public Application(){
        initStateManager();
    }

    /**
     * Returns true if pause on lost focus is enabled, false otherwise.
     *
     * @return true if pause on lost focus is enabled
     *
     * see #setPauseOnLostFocus(boolean)
     */
    public boolean isPauseOnLostFocus() {
        return pauseOnFocus;
    }

    /**
     * Enable or disable pause on lost focus.
     * 
     * By default, pause on lost focus is enabled.
     * If enabled, the application will stop updating
     * when it loses focus or becomes inactive (e.g. alt-tab).
     * For online or real-time applications, this might not be preferable,
     * so this feature should be set to disabled. For other applications,
     * it is best to keep it on so that CPU usage is not used when
     * not necessary.
     *
     * @param pauseOnLostFocus True to enable pause on lost focus, false
     * otherwise.
     */
    public void setPauseOnLostFocus(boolean pauseOnLostFocus) {
        this.pauseOnFocus = pauseOnLostFocus;
    }

    @Deprecated
    public void setAssetManager(AssetManager assetManager){
        if (this.assetManager != null)
            throw new IllegalStateException("Can only set asset manager"
                                          + " before initialization.");

        this.assetManager = assetManager;
    }

    private void initAssetManager(){
        if (settings != null){
            String assetCfg = settings.getString("AssetConfigURL");
            if (assetCfg != null){
                URL url = null;
                try {
                    url = new URL(assetCfg);
                } catch (MalformedURLException ex) {
                }
                if (url == null) {
                    url = Application.class.getClassLoader().getResource(assetCfg);
                    if (url == null) {
                        logger.log(Level.SEVERE, "Unable to access AssetConfigURL in asset config:{0}", assetCfg);
                        return;
                    }
                }
                assetManager = CWSystem.newAssetManager(url);
            }
        }
        if (assetManager == null){
            assetManager = CWSystem.newAssetManager(
                    Thread.currentThread().getContextClassLoader()
                    .getResource("com/clockwork/asset/Desktop.cfg"));
        }
    }

    /**
     * Set the display settings to define the display created.
     * 
     * Examples of display parameters include display pixel width and height,
     * color bit depth, z-buffer bits, anti-aliasing samples, and update frequency.
     * If this method is called while the application is already running, then
     * #restart() } must be called to apply the settings to the display.
     *
     * @param settings The settings to set.
     */
    public void setSettings(AppSettings settings){
        this.settings = settings;
        if (context != null && settings.useInput() != inputEnabled){
            // may need to create or destroy input based
            // on settings change
            inputEnabled = !inputEnabled;
            if (inputEnabled){
                initInput();
            }else{
                destroyInput();
            }
        }else{
            inputEnabled = settings.useInput();
        }
    }

    /**
     * Sets the Timer implementation that will be used for calculating
     * frame times.  By default, Application will use the Timer as returned
     * by the current CWContext implementation.
     */
    public void setTimer(Timer timer){
        this.timer = timer;

        if (timer != null) {
            timer.reset();
        }

        if (renderManager != null) {
            renderManager.setTimer(timer);
        }
    }

    public Timer getTimer(){
        return timer;
    }

    private void initDisplay(){
        // aquire important objects
        // from the context
        settings = context.getSettings();

        // Only reset the timer if a user has not already provided one
        if (timer == null) {
            timer = context.getTimer();
        }

        renderer = context.getRenderer();
    }

    private void initAudio(){
        if (settings.getAudioRenderer() != null && context.getType() != Type.Headless){
            audioRenderer = CWSystem.newAudioRenderer(settings);
            audioRenderer.initialize();
            AudioContext.setAudioRenderer(audioRenderer);

            listener = new Listener();
            audioRenderer.setListener(listener);
        }
    }

    /**
     * Creates the camera to use for rendering. Default values are perspective
     * projection with 45° field of view, with near and far values 1 and 1000
     * units respectively.
     */
    private void initCamera(){
        cam = new Camera(settings.getWidth(), settings.getHeight());

        cam.setFrustumPerspective(45f, (float)cam.getWidth() / cam.getHeight(), 1f, 1000f);
        cam.setLocation(new Vector3f(0f, 0f, 10f));
        cam.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);

        renderManager = new RenderManager(renderer);
        //Remy - 09/14/2010 setted the timer in the renderManager
        renderManager.setTimer(timer);
        viewPort = renderManager.createMainView("Default", cam);
        viewPort.setClearFlags(true, true, true);

        // Create a new cam for the gui
        Camera guiCam = new Camera(settings.getWidth(), settings.getHeight());
        guiViewPort = renderManager.createPostView("Gui Default", guiCam);
        guiViewPort.setClearFlags(false, false, false);
    }

    /**
     * Initializes mouse and keyboard input. Also
     * initializes joystick input if joysticks are enabled in the
     * AppSettings.
     */
    private void initInput(){
        mouseInput = context.getMouseInput();
        if (mouseInput != null)
            mouseInput.initialize();

        keyInput = context.getKeyInput();
        if (keyInput != null)
            keyInput.initialize();

        touchInput = context.getTouchInput();
        if (touchInput != null)
            touchInput.initialize();

        if (!settings.getBoolean("DisableJoysticks")){
            joyInput = context.getJoyInput();
            if (joyInput != null)
                joyInput.initialize();
        }

        inputManager = new InputManager(mouseInput, keyInput, joyInput, touchInput);
    }

    private void initStateManager(){
        stateManager = new AppStateManager(this);

        // Always register a ResetStatsState to make sure
        // that the stats are cleared every frame
        stateManager.attach(new ResetStatsState());
    }

    /**
     * @return The AssetManager asset manager} for this application.
     */
    public AssetManager getAssetManager(){
        return assetManager;
    }

    /**
     * @return the InputManager input manager}.
     */
    public InputManager getInputManager(){
        return inputManager;
    }

    /**
     * @return the AppStateManager app state manager}
     */
    public AppStateManager getStateManager() {
        return stateManager;
    }

    /**
     * @return the RenderManager render manager}
     */
    public RenderManager getRenderManager() {
        return renderManager;
    }

    /**
     * @return The Renderer renderer} for the application
     */
    public Renderer getRenderer(){
        return renderer;
    }

    /**
     * @return The AudioRenderer audio renderer} for the application
     */
    public AudioRenderer getAudioRenderer() {
        return audioRenderer;
    }

    /**
     * @return The Listener listener} object for audio
     */
    public Listener getListener() {
        return listener;
    }

    /**
     * @return The CWContext display context} for the application
     */
    public CWContext getContext(){
        return context;
    }

    /**
     * @return The Camera camera} for the application
     */
    public Camera getCamera(){
        return cam;
    }

    /**
     * Starts the application in Type#Display display} mode.
     *
     * see #start(com.clockwork.system.CWContext.Type)
     */
    public void start(){
        start(CWContext.Type.Display);
    }

    /**
     * Starts the application.
     * Creating a rendering context and executing
     * the main loop in a separate thread.
     */
    public void start(CWContext.Type contextType){
        if (context != null && context.isCreated()){
            logger.warning("start() called when application already created!");
            return;
        }

        if (settings == null){
            settings = new AppSettings(true);
        }

        logger.log(Level.FINE, "Starting application: {0}", getClass().getName());
        context = CWSystem.newContext(settings, contextType);
        context.setSystemListener(this);
        context.create(false);
    }

    /**
     * Initializes the application's canvas for use.
     * 
     * After calling this method, cast the #getContext() context} to
     * CWCanvasContext},
     * then acquire the canvas with CWCanvasContext#getCanvas() }
     * and attach it to an AWT/Swing Frame.
     * The rendering thread will start when the canvas becomes visible on
     * screen, however if you wish to start the context immediately you
     * may call #startCanvas() } to force the rendering thread
     * to start.
     *
     * see CWCanvasContext
     * see Type#Canvas
     */
    public void createCanvas(){
        if (context != null && context.isCreated()){
            logger.warning("createCanvas() called when application already created!");
            return;
        }

        if (settings == null){
            settings = new AppSettings(true);
        }

        logger.log(Level.FINE, "Starting application: {0}", getClass().getName());
        context = CWSystem.newContext(settings, CWContext.Type.Canvas);
        context.setSystemListener(this);
    }

    /**
     * Starts the rendering thread after createCanvas() has been called.
     * 
     * Same as calling startCanvas(false)
     *
     * see #startCanvas(boolean)
     */
    public void startCanvas(){
        startCanvas(false);
    }

    /**
     * Starts the rendering thread after createCanvas() has been called.
     * 
     * Calling this method is optional, the canvas will start automatically
     * when it becomes visible.
     *
     * @param waitFor If true, the current thread will block until the
     * rendering thread is running
     */
    public void startCanvas(boolean waitFor){
        context.create(waitFor);
    }

    /**
     * Internal use only.
     */
    public void reshape(int w, int h){
        renderManager.notifyReshape(w, h);
    }

    /**
     * Restarts the context, applying any changed settings.
     * 
     * Changes to the AppSettings} of this Application are not
     * applied immediately; calling this method forces the context
     * to restart, applying the new settings.
     */
    public void restart(){
        context.setSettings(settings);
        context.restart();
    }

    /**
     * Requests the context to close, shutting down the main loop
     * and making necessary cleanup operations.
     *
     * Same as calling stop(false)
     *
     * see #stop(boolean)
     */
    public void stop(){
        stop(false);
    }

    /**
     * Requests the context to close, shutting down the main loop
     * and making necessary cleanup operations.
     * After the application has stopped, it cannot be used anymore.
     */
    public void stop(boolean waitFor){
        logger.log(Level.FINE, "Closing application: {0}", getClass().getName());
        context.destroy(waitFor);
    }

    /**
     * Do not call manually.
     * Callback from ContextListener.
     * 
     * Initializes the Application, by creating a display and
     * default camera. If display settings are not specified, a default
     * 640x480 display is created. Default values are used for the camera;
     * perspective projection with 45° field of view, with near
     * and far values 1 and 1000 units respectively.
     */
    public void initialize(){
        if (assetManager == null){
            initAssetManager();
        }

        initDisplay();
        initCamera();

        if (inputEnabled){
            initInput();
        }
        initAudio();

        // update timer so that the next delta is not too large
//        timer.update();
        timer.reset();

        // user code here..
    }

    /**
     * Internal use only.
     */
    public void handleError(String errMsg, Throwable t){
        // Print error to log.
        logger.log(Level.SEVERE, errMsg, t);
        // Display error message on screen
        if (t != null) {
            CWSystem.showErrorDialog(errMsg + "\n" + t.getClass().getSimpleName() +
                    (t.getMessage() != null ? ": " +  t.getMessage() : ""));
        } else {
            CWSystem.showErrorDialog(errMsg);
        }

        stop(); // stop the application
    }

    /**
     * Internal use only.
     */
    public void gainFocus(){
        if (pauseOnFocus) {
            paused = false;
            context.setAutoFlushFrames(true);
            if (inputManager != null) {
                inputManager.reset();
            }
        }
    }

    /**
     * Internal use only.
     */
    public void loseFocus(){
        if (pauseOnFocus){
            paused = true;
            context.setAutoFlushFrames(false);
        }
    }

    /**
     * Internal use only.
     */
    public void requestClose(boolean esc){
        context.destroy(false);
    }

    /**
     * Enqueues a task/callable object to execute in the CW
     * rendering thread.
     * 
     * Callables are executed right at the beginning of the main loop.
     * They are executed even if the application is currently paused
     * or out of focus.
     */
    public <V> Future<V> enqueue(Callable<V> callable) {
        AppTask<V> task = new AppTask<V>(callable);
        taskQueue.add(task);
        return task;
    }

    /**
     * Runs tasks enqueued via #enqueue(Callable)}
     */
    protected void runQueuedTasks() {
	  AppTask<?> task;
        while( (task = taskQueue.poll()) != null ) {
            if (!task.isCancelled()) {
                task.invoke();
            }
        }
    }

    /**
     * Do not call manually.
     * Callback from ContextListener.
     */
    public void update(){
        // Make sure the audio renderer is available to callables
        AudioContext.setAudioRenderer(audioRenderer);

        runQueuedTasks();

        if (speed == 0 || paused)
            return;

        timer.update();

        if (inputEnabled){
            inputManager.update(timer.getTimePerFrame());
        }

        if (audioRenderer != null){
            audioRenderer.update(timer.getTimePerFrame());
        }

        // user code here..
    }

    protected void destroyInput(){
        if (mouseInput != null)
            mouseInput.destroy();

        if (keyInput != null)
            keyInput.destroy();

        if (joyInput != null)
            joyInput.destroy();

        if (touchInput != null)
            touchInput.destroy();

        inputManager = null;
    }

    /**
     * Do not call manually.
     * Callback from ContextListener.
     */
    public void destroy(){
        stateManager.cleanup();

        destroyInput();
        if (audioRenderer != null)
            audioRenderer.cleanup();

        timer.reset();
    }

    /**
     * @return The GUI viewport. Which is used for the on screen
     * statistics and FPS.
     */
    public ViewPort getGuiViewPort() {
        return guiViewPort;
    }

    public ViewPort getViewPort() {
        return viewPort;
    }

}
