
package clockworktest.stress;

import com.clockwork.app.SimpleApplication;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.Material;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.control.LodControl;
import clockworktools.optimize.GeometryBatchFactory;

public class TestBatchLod extends SimpleApplication {

    private boolean lod = false;

    public static void main(String[] args) {
        TestBatchLod app = new TestBatchLod();
        app.start();
    }

    public void simpleInitApp() {
//        inputManager.registerKeyBinding("USELOD", KeyInput.KEY_L);

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
        rootNode.addLight(dl);

        Node teapotNode = (Node) assetManager.loadModel("Models/Teapot/Teapot.mesh.xml");
        Geometry teapot = (Geometry) teapotNode.getChild(0);

        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setFloat("Shininess", 16f);
        mat.setBoolean("VertexLighting", true);
        teapot.setMaterial(mat);

        // show normals as material
        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        flyCam.setMoveSpeed(5);
        for (int y = -5; y < 5; y++) {
            for (int x = -5; x < 5; x++) {
                Geometry clonePot = teapot.clone();

                //clonePot.setMaterial(mat);
                clonePot.setLocalTranslation(x * .5f, 0, y * .5f);
                clonePot.setLocalScale(.15f);
                clonePot.setMaterial(mat);
                rootNode.attachChild(clonePot);
            }
        }
        GeometryBatchFactory.optimize(rootNode, true);
        LodControl control = new LodControl();
        rootNode.getChild(0).addControl(control);
        cam.setLocation(new Vector3f(-1.0748308f, 1.35778f, -1.5380064f));
        cam.setRotation(new Quaternion(0.18343268f, 0.34531063f, -0.069015436f, 0.9177962f));

    }
}
