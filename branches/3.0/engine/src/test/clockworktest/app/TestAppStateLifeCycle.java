

package clockworktest.app;

import com.clockwork.app.Application;
import com.clockwork.app.SimpleApplication;
import com.clockwork.app.state.AbstractAppState;
import com.clockwork.app.state.AppStateManager;
import com.clockwork.material.Material;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.RenderManager;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.shape.Box;


/**
 *  Tests the app state lifecycles.
 *
 */
public class TestAppStateLifeCycle extends SimpleApplication {

    public static void main(String[] args){
        TestAppStateLifeCycle app = new TestAppStateLifeCycle();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));
        geom.setMaterial(mat);
        rootNode.attachChild(geom);

        System.out.println("Attaching test state.");
        stateManager.attach(new TestState());        
    }

    @Override
    public void simpleUpdate(float tpf) {
    
        if(stateManager.getState(TestState.class) != null) {
            System.out.println("Detaching test state."); 
            stateManager.detach(stateManager.getState(TestState.class));
            System.out.println("Done"); 
        }        
    }
    
    public class TestState extends AbstractAppState {
 
        @Override
        public void initialize(AppStateManager stateManager, Application app) {
            super.initialize(stateManager, app);
            System.out.println("Initialized");
        }
 
        @Override
        public void stateAttached(AppStateManager stateManager) {
            super.stateAttached(stateManager);
            System.out.println("Attached");
        }
 
        @Override
        public void update(float tpf) {
            super.update(tpf);
            System.out.println("update");
        }

        @Override
        public void render(RenderManager rm) {
            super.render(rm);
            System.out.println("render");
        }

        @Override
        public void postRender() {
            super.postRender();
            System.out.println("postRender");
        }

        @Override
        public void stateDetached(AppStateManager stateManager) {
            super.stateDetached(stateManager);
            System.out.println("Detached");
        }
 
        @Override
        public void cleanup() {
            super.cleanup();
            System.out.println("Cleanup"); 
        }

    }    
}
