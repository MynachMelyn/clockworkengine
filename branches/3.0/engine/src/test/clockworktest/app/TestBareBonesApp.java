

package clockworktest.app;

import com.clockwork.app.Application;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.shape.Box;

/**
 * Test a bare-bones application, without SimpleApplication.
 */
public class TestBareBonesApp extends Application {

    private Geometry boxGeom;

    public static void main(String[] args){
        TestBareBonesApp app = new TestBareBonesApp();
        app.start();
    }

    @Override
    public void initialize(){
        super.initialize();

        System.out.println("Initialize");

        // create a box
        boxGeom = new Geometry("Box", new Box(Vector3f.ZERO, 2, 2, 2));

        // load some default material
        boxGeom.setMaterial(assetManager.loadMaterial("Interface/Logo/Logo.j3m"));

        // attach box to display in primary viewport
        viewPort.attachScene(boxGeom);
    }

    @Override
    public void update(){
        super.update();

        // do some animation
        float tpf = timer.getTimePerFrame();
        boxGeom.rotate(tpf * 2, tpf * 4, tpf * 3);
        
        // dont forget to update the scenes
        boxGeom.updateLogicalState(tpf);
        boxGeom.updateGeometricState();

        // render the viewports
        renderManager.render(tpf, context.isRenderable());
    }

    @Override
    public void destroy(){
        super.destroy();

        System.out.println("Destroy");
    }
}
