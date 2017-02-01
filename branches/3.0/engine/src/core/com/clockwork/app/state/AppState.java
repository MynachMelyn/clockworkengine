
package com.clockwork.app.state;

import com.clockwork.app.Application;
import com.clockwork.renderer.RenderManager;

/**
 * AppState represents continously executing code inside the main loop.
 * 
 * An <code>AppState</code> can track when it is attached to the 
 * {@link AppStateManager} or when it is detached. 
 * 
 * <br/><code>AppState</code>s are initialized in the render thread, upon a call to 
 * {@link AppState#initialize(com.clockwork.app.state.AppStateManager, com.clockwork.app.Application) }
 * and are de-initialized upon a call to {@link AppState#cleanup()}. 
 * Implementations should return the correct value with a call to 
 * {@link AppState#isInitialized() } as specified above.<br/>
 * 
 * <ul>
 * <li>If a detached AppState is attached then <code>initialize()</code> will be called
 * on the following render pass.
 * </li>
 * <li>If an attached AppState is detached then <code>cleanup()</code> will be called
 * on the following render pass.
 * </li>
 * <li>If you attach an already-attached <code>AppState</code> then the second attach
 * is a no-op and will return false.
 * </li>
 * <li>If you both attach and detach an <code>AppState</code> within one frame then
 * neither <code>initialize()</code> or <code>cleanup()</code> will be called,
 * although if either is called both will be.
 * </li>
 * <li>If you both detach and then re-attach an <code>AppState</code> within one frame
 * then on the next update pass its <code>cleanup()</code> and <code>initialize()</code>
 * methods will be called in that order.
 * </li>
 * </ul>
 */
public interface AppState {

    /**
     * Called by {@link AppStateManager} when transitioning this {@code AppState}
     * from <i>initializing</i> to <i>running</i>.<br>
     * This will happen on the next iteration through the update loop after
     * {@link AppStateManager#attach()} was called.
     * <p>
     * <code>AppStateManager</code> will call this only from the update loop
     * inside the rendering thread. This means is it safe to modify the scene 
     * graph from this method.
     *
     * @param stateManager The state manager
     * @param app The application
     */
    public void initialize(AppStateManager stateManager, Application app);

    /**
     * @return True if <code>initialize()</code> was called on the state,
     * false otherwise.
     */
    public boolean isInitialized();

    /**
     * Enable or disable the functionality of the <code>AppState</code>.
     * The effect of this call depends on implementation. An 
     * <code>AppState</code> starts as being enabled by default.
     * A disabled <code>AppState</code>s does not get calls to
     * {@link #update(float)}, {@link #render(RenderManager)}, or
     * {@link #postRender()} from its {@link AppStateManager}.
     * 
     * @param active activate the AppState or not.
     */
    public void setEnabled(boolean active);
    
    /**
     * @return True if the <code>AppState</code> is enabled, false otherwise.
     * 
     * @see AppState#setEnabled(boolean)
     */
    public boolean isEnabled();

    /**
     * Called by {@link AppStateManager#attach()} when transitioning this
     * <code>AppState</code> from <i>detached</i> to <i>initializing</i>.
     * <p>
     * There is no assumption about the thread from which this function is
     * called, therefore it is <b>unsafe</b> to modify the scene graph
     * from this method. Please use 
     * {@link #initialize(com.clockwork.app.state.AppStateManager, com.clockwork.app.Application) }
     * instead.
     *
     * @param stateManager State manager to which the state was attached to.
     */
    public void stateAttached(AppStateManager stateManager);

   /**
    * Called by {@link AppStateManager#detach()} when transitioning this
    * <code>AppState</code> from <i>running</i> to <i>terminating</i>.
    * <p>
    * There is no assumption about the thread from which this function is
    * called, therefore it is <b>unsafe</b> to modify the scene graph
    * from this method. Please use 
    * {@link #cleanup() }
    * instead.
    * 
    * @param stateManager The state manager from which the state was detached from.
    */
    public void stateDetached(AppStateManager stateManager);

    /**
     * Called to update the <code>AppState</code>. This method will be called 
     * every render pass if the <code>AppState</code> is both attached and enabled.
     *
     * @param tpf Time since the last call to update(), in seconds.
     */
    public void update(float tpf);

    /**
     * Render the state. This method will be called 
     * every render pass if the <code>AppState</code> is both attached and enabled.
     *
     * @param rm RenderManager
     */
    public void render(RenderManager rm);

    /**
     * Called after all rendering commands are flushed. This method will be called 
     * every render pass if the <code>AppState</code> is both attached and enabled.
     */
    public void postRender();

    /**
     * Called by {@link AppStateManager} when transitioning this
     * <code>AppState</code> from <i>terminating</i> to <i>detached</i>. This
     * method is called the following render pass after the <code>AppState</code> has 
     * been detached and is always called once and only once for each time
     * <code>initialize()</code> is called. Either when the <code>AppState</code>
     * is detached or when the application terminates (if it terminates normally).
     */
    public void cleanup();

}
