

package clockworktest.material;

import com.clockwork.app.SimpleApplication;
import com.clockwork.light.PointLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.FastMath;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.shape.Quad;
import com.clockwork.scene.shape.Sphere;
import com.clockwork.util.TangentBinormalGenerator;

// phong cutoff for light to normal angle > 90?
public class TestSimpleBumps extends SimpleApplication {

    float angle;
    PointLight pl;
    Spatial lightMdl;

    public static void main(String[] args){
        TestSimpleBumps app = new TestSimpleBumps();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Quad quadMesh = new Quad(1, 1);

        Geometry sphere = new Geometry("Rock Ball", quadMesh);
        Material mat = assetManager.loadMaterial("Textures/BumpMapTest/SimpleBump.j3m");
        sphere.setMaterial(mat);
        TangentBinormalGenerator.generate(sphere);
        rootNode.attachChild(sphere);

        lightMdl = new Geometry("Light", new Sphere(10, 10, 0.1f));
        lightMdl.setMaterial(assetManager.loadMaterial("Common/Materials/RedColor.j3m"));
        rootNode.attachChild(lightMdl);

        pl = new PointLight();
        pl.setColor(ColorRGBA.White);
        pl.setPosition(new Vector3f(0f, 0f, 4f));
        rootNode.addLight(pl);

//        DirectionalLight dl = new DirectionalLight();
//        dl.setDirection(new Vector3f(1, -1, -1).normalizeLocal());
//        dl.setColor(new ColorRGBA(0.22f, 0.15f, 0.1f, 1.0f));
//        rootNode.addLight(dl);
    }

    @Override
    public void simpleUpdate(float tpf){
        angle += tpf * 0.25f;
        angle %= FastMath.TWO_PI;

        pl.setPosition(new Vector3f(FastMath.cos(angle) * 4f, 0.5f, FastMath.sin(angle) * 4f));
        lightMdl.setLocalTranslation(pl.getPosition());
    }

}
