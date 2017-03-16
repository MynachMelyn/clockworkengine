
package com.clockwork.app.state;

import com.clockwork.app.Application;
import com.clockwork.renderer.RenderManager;

/**
 * AppState represents continously executing code inside the main loop.
 * 
 * An AppState can track when it is attached to the 
 * AppStateManager or when it is detached. 
 * 
 * AppStates are initialised in the render thread, upon a call to 
 * AppState#initialise(com.clockwork.app.state.AppStateManager, com.clockwork.app.Application)
 * and are de-initialised upon a call to AppState#cleanup(). 
 * Implementations should return the correct value with a call to 
 * AppState#isInitialized() as specified above.
 * 
 * 
 * If a detached AppState is attached then initialise() will be called
 * on the following render pass.
 * 
 * If an attached AppState is detached then cleanup() will be called
 * on the following render pass.
 * 
 * If you attach an already-attached AppState then the second attach
 * is a no-op and will return false.
 * 
 * If you both attach and detach an AppState within one frame then
 * neither initialise() or cleanup() will be called,
 * although if either is called both will be.
 * 
 * If you both detach and then re-attach an AppState within one frame
 * then on the next update pass its cleanup() and initialise()
 * methods will be called in that order.
 * 
 * 
 */
public interface AppState {

    /**
     * Called by AppStateManager} when transitioning this AppState}
     * from <i>initialising</i> to <i>running</i>.
     * This will happen on the next iteration through the update loop after
     * AppStateManager#attach()} was called.
     * 
     * AppStateManager will call this only from the update loop
     * inside the rendering thread. This means is it safe to modify the scene 
     * graph from this method.
     *
     * @param stateManager The state manager
     * @param app The application
     */
    public void initialize(AppStateManager stateManager, Application app);

    /**
     * @return True if initialise() was called on the state,
     * false otherwise.
     */
    public boolean isInitialized();

    /**
     * Enable or disable the functionality of the AppState.
     * The effect of this call depends on implementation. An 
     * AppState starts as being enabled by default.
     * A disabled AppStates does not get calls to
     * #update(float)}, #render(RenderManager)}, or
     * #postRender()} from its AppStateManager}.
     * 
     * @param active activate the AppState or not.
     */
    public void setEnabled(boolean active);
    
    /**
     * @return True if the AppState is enabled, false otherwise.
     * 
     * see AppState#setEnabled(boolean)
     */
    public boolean isEnabled();

    /**
     * Called by AppStateManager#attach()} when transitioning this
     * AppState from <i>detached</i> to <i>initialising</i>.
     * 
     * There is no assumption about the thread from which this function is
     * called, therefore it is unsafe to modify the scene graph
     * from this method. Please use 
     * #initialise(com.clockwork.app.state.AppStateManager, com.clockwork.app.Application) }
     * instead.
     *
     * @param stateManager State manager to which the state was attached to.
     */
    public void stateAttached(AppStateManager stateManager);

   /**
    * Called by AppStateManager#detach()} when transitioning this
    * AppState from <i>running</i> to <i>terminating</i>.
    * 
    * There is no assumption about the thread from which this function is
    * called, therefore it is unsafe to modify the scene graph
    * from this method. Please use 
    * #cleanup() }
    * instead.
    * 
    * @param stateManager The state manager from which the state was detached from.
    */
    public void stateDetached(AppStateManager stateManager);

    /**
     * Called to update the AppState. This method will be called 
     * every render pass if the AppState is both attached and enabled.
     *
     * @param tpf Time since the last call to update(), in seconds.
     */
    public void update(float tpf);

    /**
     * Render the state. This method will be called 
     * every render pass if the AppState is both attached and enabled.
     *
     * @param rm RenderManager
     */
    public void render(RenderManager rm);

    /**
     * Called after all rendering commands are flushed. This method will be called 
     * every render pass if the AppState is both attached and enabled.
     */
    public void postRender();

    /**
     * Called by AppStateManager} when transitioning this
     * AppState from <i>terminating</i> to <i>detached</i>. This
     * method is called the following render pass after the AppState has 
     * been detached and is always called once and only once for each time
     * initialise() is called. Either when the AppState
     * is detached or when the application terminates (if it terminates normally).
     */
    public void cleanup();

}
