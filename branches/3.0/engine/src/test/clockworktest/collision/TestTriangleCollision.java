

package clockworktest.collision;

import com.clockwork.app.SimpleApplication;
import com.clockwork.bounding.BoundingVolume;
import com.clockwork.collision.CollisionResults;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.AnalogListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.shape.Box;

public class TestTriangleCollision extends SimpleApplication {

    Geometry geom1;

    Spatial golem;

    public static void main(String[] args) {
        TestTriangleCollision app = new TestTriangleCollision();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Create two boxes
        Mesh mesh1 = new Box(0.5f, 0.5f, 0.5f);
        geom1 = new Geometry("Box", mesh1);
        geom1.move(2, 2, -.5f);
        Material m1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        m1.setColor("Color", ColorRGBA.Blue);
        geom1.setMaterial(m1);
        rootNode.attachChild(geom1);

        // load a character from jme3test-test-data
        golem = assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        golem.scale(0.5f);
        golem.setLocalTranslation(-1.0f, -1.5f, -0.6f);

        // We must add a light to make the model visible
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f).normalizeLocal());
        golem.addLight(sun);
        rootNode.attachChild(golem);

        // Create input
        inputManager.addMapping("MoveRight", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("MoveLeft", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("MoveUp", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("MoveDown", new KeyTrigger(KeyInput.KEY_K));

        inputManager.addListener(analogListener, new String[]{
                    "MoveRight", "MoveLeft", "MoveUp", "MoveDown"
                });
    }
    private AnalogListener analogListener = new AnalogListener() {

        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("MoveRight")) {
                geom1.move(2 * tpf, 0, 0);
            }

            if (name.equals("MoveLeft")) {
                geom1.move(-2 * tpf, 0, 0);
            }

            if (name.equals("MoveUp")) {
                geom1.move(0, 2 * tpf, 0);
            }

            if (name.equals("MoveDown")) {
                geom1.move(0, -2 * tpf, 0);
            }
        }
    };

    @Override
    public void simpleUpdate(float tpf) {
        CollisionResults results = new CollisionResults();
        BoundingVolume bv = geom1.getWorldBound();
        golem.collideWith(bv, results);

        if (results.size() > 0) {
            geom1.getMaterial().setColor("Color", ColorRGBA.Red);
        }else{
            geom1.getMaterial().setColor("Color", ColorRGBA.Blue);
        }
    }
}
