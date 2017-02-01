
package clockworktest.model.shape;

import com.clockwork.app.SimpleApplication;
import com.clockwork.material.Material;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.shape.Torus;

public class TestExpandingTorus extends SimpleApplication {

    private float outerRadius = 1.5f;
    private float rate = 1;
    private Torus torus;
    private Geometry geom;
    
    public static void main(String[] args) {
        TestExpandingTorus app = new TestExpandingTorus();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        torus = new Torus(30, 10, .5f, 1f);
        geom = new Geometry("Torus", torus);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        geom.setMaterial(mat);
        rootNode.attachChild(geom);
    }
    
    @Override
    public void simpleUpdate(float tpf){
        if (outerRadius > 2.5f){
            outerRadius = 2.5f;
            rate = -rate;
        }else if (outerRadius < 1f){
            outerRadius = 1f;
            rate = -rate;
        }
        outerRadius += rate * tpf;
        torus.updateGeometry(30, 10, .5f, outerRadius);
    }
}