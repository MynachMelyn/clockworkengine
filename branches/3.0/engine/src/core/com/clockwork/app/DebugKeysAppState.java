
package com.clockwork.app;

import com.clockwork.app.state.AbstractAppState;
import com.clockwork.app.state.AppStateManager;
import com.clockwork.input.InputManager;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.Camera;
import com.clockwork.util.BufferUtils;


/**
 *  Registers a few keys that will dump debug information
 *  to the console.
 *
 */
public class DebugKeysAppState extends AbstractAppState {

    public static final String INPUT_MAPPING_CAMERA_POS = "SIMPLEAPP_CameraPos";
    public static final String INPUT_MAPPING_MEMORY = "SIMPLEAPP_Memory";
    
    private Application app;
    private DebugKeyListener keyListener = new DebugKeyListener();
    private InputManager inputManager;

    public DebugKeysAppState() {
    }    

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.app = app;
        this.inputManager = app.getInputManager();
        
        if (app.getInputManager() != null) {
 
            inputManager.addMapping(INPUT_MAPPING_CAMERA_POS, new KeyTrigger(KeyInput.KEY_C));
            inputManager.addMapping(INPUT_MAPPING_MEMORY, new KeyTrigger(KeyInput.KEY_M));
            
            inputManager.addListener(keyListener, 
                                     INPUT_MAPPING_CAMERA_POS, 
                                     INPUT_MAPPING_MEMORY);                   
        }               
    }
            
    @Override
    public void cleanup() {
        super.cleanup();

        if (inputManager.hasMapping(INPUT_MAPPING_CAMERA_POS))
            inputManager.deleteMapping(INPUT_MAPPING_CAMERA_POS);
        if (inputManager.hasMapping(INPUT_MAPPING_MEMORY))
            inputManager.deleteMapping(INPUT_MAPPING_MEMORY);
        
        inputManager.removeListener(keyListener);
    }

    
    private class DebugKeyListener implements ActionListener {

        public void onAction(String name, boolean value, float tpf) {
            if (!value) {
                return;
            }

            if (name.equals(INPUT_MAPPING_CAMERA_POS)) {
                Camera cam = app.getCamera();
                if (cam != null) {
                    Vector3f loc = cam.getLocation();
                    Quaternion rot = cam.getRotation();
                    System.out.println("Camera Position: ("
                            + loc.x + ", " + loc.y + ", " + loc.z + ")");
                    System.out.println("Camera Rotation: " + rot);
                    System.out.println("Camera Direction: " + cam.getDirection());
                    System.out.println("cam.setLocation(new Vector3f("
                            + loc.x + "f, " + loc.y + "f, " + loc.z + "f));");
                    System.out.println("cam.setRotation(new Quaternion(" + rot.getX() + "f, " +rot.getY()+ "f, " + rot.getZ() + "f, " + rot.getW() + "f));");
                  
                }
            } else if (name.equals(INPUT_MAPPING_MEMORY)) {
                BufferUtils.printCurrentDirectMemory(null);
            }
        }
    }
}
