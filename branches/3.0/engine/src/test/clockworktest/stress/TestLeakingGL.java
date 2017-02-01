

package clockworktest.stress;

import com.clockwork.app.SimpleApplication;
import com.clockwork.material.Material;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial.CullHint;
import com.clockwork.scene.shape.Sphere;
import com.clockwork.util.NativeObjectManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generates 400 new meshes every frame then leaks them.
 * Notice how memory usage stays constant and OpenGL objects
 * are properly destroyed.
 */
public class TestLeakingGL extends SimpleApplication {

    private Material solidColor;
    private Sphere original;

    public static void main(String[] args){
        TestLeakingGL app = new TestLeakingGL();
        app.start();
    }

    public void simpleInitApp() {
        original = new Sphere(4, 4, 1);
        original.setStatic();
        //original.setInterleaved();

        // this will make sure all spheres are rendered always
        rootNode.setCullHint(CullHint.Never);
        solidColor = assetManager.loadMaterial("Common/Materials/RedColor.j3m");
        cam.setLocation(new Vector3f(0, 5, 0));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        Logger.getLogger(Node.class.getName()).setLevel(Level.WARNING);
        Logger.getLogger(NativeObjectManager.class.getName()).setLevel(Level.WARNING);
    }

    @Override
    public void simpleUpdate(float tpf){
        rootNode.detachAllChildren();
        for (int y = -15; y < 15; y++){
            for (int x = -15; x < 15; x++){
                Mesh sphMesh = original.deepClone();
                Geometry sphere = new Geometry("sphere", sphMesh);

                sphere.setMaterial(solidColor);
                sphere.setLocalTranslation(x * 1.5f, 0, y * 1.5f);
                rootNode.attachChild(sphere);
            }
        }
    }
}
