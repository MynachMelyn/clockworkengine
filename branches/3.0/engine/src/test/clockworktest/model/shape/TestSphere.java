

package clockworktest.model.shape;

import com.clockwork.app.SimpleApplication;
import com.clockwork.material.Material;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.shape.Sphere;

public class TestSphere extends SimpleApplication  {

    public static void main(String[] args){
        TestSphere app = new TestSphere();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Sphere sphMesh = new Sphere(14, 14, 1);
        Material solidColor = assetManager.loadMaterial("Common/Materials/RedColor.j3m");

        for (int y = -5; y < 5; y++){
            for (int x = -5; x < 5; x++){
                Geometry sphere = new Geometry("sphere", sphMesh);
                sphere.setMaterial(solidColor);
                sphere.setLocalTranslation(x * 2, 0, y * 2);
                rootNode.attachChild(sphere);
            }
        }
        cam.setLocation(new Vector3f(0, 5, 0));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }

}
