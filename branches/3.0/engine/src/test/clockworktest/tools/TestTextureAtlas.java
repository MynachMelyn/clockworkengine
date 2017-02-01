
package clockworktest.tools;

import com.clockwork.app.SimpleApplication;
import com.clockwork.light.AmbientLight;
import com.clockwork.light.DirectionalLight;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.shape.Quad;
import clockworktools.optimize.TextureAtlas;

public class TestTextureAtlas extends SimpleApplication {

    public static void main(String[] args) {
        TestTextureAtlas app = new TestTextureAtlas();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(50);
        Node scene = new Node("Scene");
        Spatial obj1 = assetManager.loadModel("Models/Ferrari/Car.scene");
        obj1.setLocalTranslation(-4, 0, 0);
        Spatial obj2 = assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        obj2.setLocalTranslation(-2, 0, 0);
        Spatial obj3 = assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        obj3.setLocalTranslation(-0, 0, 0);
        Spatial obj4 = assetManager.loadModel("Models/Sinbad/Sinbad.mesh.xml");
        obj4.setLocalTranslation(2, 0, 0);
        Spatial obj5 = assetManager.loadModel("Models/Tree/Tree.mesh.j3o");
        obj5.setLocalTranslation(4, 0, 0);
        scene.attachChild(obj1);
        scene.attachChild(obj2);
        scene.attachChild(obj3);
        scene.attachChild(obj4);
        scene.attachChild(obj5);

        Geometry geom = TextureAtlas.makeAtlasBatch(scene, assetManager, 2048);

        AmbientLight al = new AmbientLight();
        rootNode.addLight(al);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0.69077975f, -0.6277887f, -0.35875428f).normalizeLocal());
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
        rootNode.addLight(sun);

        rootNode.attachChild(geom);

        //quad to display material
        Geometry box = new Geometry("displayquad", new Quad(4, 4));
        box.setMaterial(geom.getMaterial());
        box.setLocalTranslation(0, 1, 3);
        rootNode.attachChild(box);
    }
}
