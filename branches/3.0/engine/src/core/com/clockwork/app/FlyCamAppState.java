
package com.clockwork.app;

import com.clockwork.app.state.AbstractAppState;
import com.clockwork.app.state.AppStateManager;
import com.clockwork.input.FlyByCamera;


/**
 *  Manages a FlyByCamera.  
 *
 */
public class FlyCamAppState extends AbstractAppState {

    private Application app;
    private FlyByCamera flyCam;

    public FlyCamAppState() {
    }    

    /**
     *  This is called by SimpleApplication during initialize().
     */
    void setCamera( FlyByCamera cam ) {
        this.flyCam = cam;
    }
    
    public FlyByCamera getCamera() {
        return flyCam;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.app = app;

        if (app.getInputManager() != null) {
        
            if (flyCam == null) {
                flyCam = new FlyByCamera(app.getCamera());
            }
            
            flyCam.registerWithInput(app.getInputManager());            
        }               
    }
            
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        flyCam.setEnabled(enabled);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();

        if (app.getInputManager() != null) {        
            flyCam.unregisterInput();
        }        
    }


}
