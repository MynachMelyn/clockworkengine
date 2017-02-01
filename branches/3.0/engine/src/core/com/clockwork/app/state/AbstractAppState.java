
package com.clockwork.app.state;

import com.clockwork.app.Application;
import com.clockwork.renderer.RenderManager;

/**
 * <code>AbstractAppState</code> implements some common methods
 * that make creation of AppStates easier.
 */
public class AbstractAppState implements AppState {

    /**
     * <code>initialized</code> is set to true when the method
     * {@link AbstractAppState#initialize(com.clockwork.app.state.AppStateManager, com.clockwork.app.Application) }
     * is called. When {@link AbstractAppState#cleanup() } is called, <code>initialized</code>
     * is set back to false.
     */
    protected boolean initialized = false;
    private boolean enabled = true;

    public void initialize(AppStateManager stateManager, Application app) {
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isEnabled() {
        return enabled;
    }

    public void stateAttached(AppStateManager stateManager) {
    }

    public void stateDetached(AppStateManager stateManager) {
    }

    public void update(float tpf) {
    }

    public void render(RenderManager rm) {
    }

    public void postRender(){
    }

    public void cleanup() {
        initialized = false;
    }

}
