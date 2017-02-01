

package clockworktest.stress;

import com.clockwork.app.SimpleApplication;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.Material;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.control.LodControl;

public class TestLodStress extends SimpleApplication {

    public static void main(String[] args){
        TestLodStress app = new TestLodStress();
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        app.start();
    }

    public void simpleInitApp() {
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-1,-1,-1).normalizeLocal());
        rootNode.addLight(dl);

        Node teapotNode = (Node) assetManager.loadModel("Models/Teapot/Teapot.mesh.xml");
        Geometry teapot = (Geometry) teapotNode.getChild(0);
        
//        Sphere sph = new Sphere(16, 16, 4);
//        Geometry teapot = new Geometry("teapot", sph);

        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setFloat("Shininess", 16f);
        mat.setBoolean("VertexLighting", true);
        teapot.setMaterial(mat);
        
       // show normals as material
        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");

        for (int y = -10; y < 10; y++){
            for (int x = -10; x < 10; x++){
                Geometry clonePot = teapot.clone();
                
                //clonePot.setMaterial(mat);
                clonePot.setLocalTranslation(x * .5f, 0, y * .5f);
                clonePot.setLocalScale(.15f);
                
                LodControl control = new LodControl();
                clonePot.addControl(control);
                rootNode.attachChild(clonePot);
            }
        }

        cam.setLocation(new Vector3f(8.378951f, 5.4324f, 8.795956f));
        cam.setRotation(new Quaternion(-0.083419204f, 0.90370524f, -0.20599906f, -0.36595422f));
    }

}
