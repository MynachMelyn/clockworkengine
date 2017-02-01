
package clockworktest.batching;

import com.clockwork.app.SimpleApplication;
import com.clockwork.bounding.BoundingBox;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.FastMath;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.BatchNode;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.debug.WireFrustum;
import com.clockwork.scene.shape.Box;
import com.clockwork.system.NanoTimer;
import com.clockwork.util.TangentBinormalGenerator;

/**
 *
 */
public class TestBatchNode extends SimpleApplication {

    public static void main(String[] args) {

        TestBatchNode app = new TestBatchNode();
        app.start();
    }
    BatchNode batch;
    WireFrustum frustum;
    Geometry frustumMdl;
    private Vector3f[] points;

    {
        points = new Vector3f[8];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Vector3f();
        }
    }

    @Override
    public void simpleInitApp() {
        timer = new NanoTimer();
        batch = new BatchNode("theBatchNode");



        /**
         * A cube with a color "bleeding" through transparent texture. Uses
         * Texture from jme3-test-data library!
         */
        Box boxshape4 = new Box(Vector3f.ZERO, 1f, 1f, 1f);
        cube = new Geometry("cube1", boxshape4);
        Material mat = assetManager.loadMaterial("Textures/Terrain/Pond/Pond.j3m");
        cube.setMaterial(mat);
//        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");        
//        mat.setColor("Diffuse", ColorRGBA.Blue);
//        mat.setBoolean("UseMaterialColors", true);
        /**
         * A cube with a color "bleeding" through transparent texture. Uses
         * Texture from jme3-test-data library!
         */
        Box box = new Box(Vector3f.ZERO, 1f, 1f, 1f);
        cube2 = new Geometry("cube2", box);
        cube2.setMaterial(mat);

        TangentBinormalGenerator.generate(cube);
        TangentBinormalGenerator.generate(cube2);


        n = new Node("aNode");
        // n.attachChild(cube2);
        batch.attachChild(cube);
        //  batch.attachChild(cube2);
        //  batch.setMaterial(mat);
        batch.batch();
        rootNode.attachChild(batch);
        cube.setLocalTranslation(3, 0, 0);
        cube2.setLocalTranslation(0, 20, 0);


        updateBoindPoints(points);
        frustum = new WireFrustum(points);
        frustumMdl = new Geometry("f", frustum);
        frustumMdl.setCullHint(Spatial.CullHint.Never);
        frustumMdl.setMaterial(new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"));
        frustumMdl.getMaterial().getAdditionalRenderState().setWireframe(true);
        frustumMdl.getMaterial().setColor("Color", ColorRGBA.Red);
        rootNode.attachChild(frustumMdl);
        dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White.mult(2));
        dl.setDirection(new Vector3f(1, -1, -1));
        rootNode.addLight(dl);
        flyCam.setMoveSpeed(10);
    }
    Node n;
    Geometry cube;
    Geometry cube2;
    float time = 0;
    DirectionalLight dl;
    boolean done = false;

    @Override
    public void simpleUpdate(float tpf) {
        if (!done) {
            done = true;
            batch.attachChild(cube2);
            batch.batch();
        }
        updateBoindPoints(points);
        frustum.update(points);
        time += tpf;
        dl.setDirection(cam.getDirection());
        cube2.setLocalTranslation(FastMath.sin(-time) * 3, FastMath.cos(time) * 3, 0);
        cube2.setLocalRotation(new Quaternion().fromAngleAxis(time, Vector3f.UNIT_Z));
        cube2.setLocalScale(Math.max(FastMath.sin(time), 0.5f));

//        batch.setLocalRotation(new Quaternion().fromAngleAxis(time, Vector3f.UNIT_Z));

    }
//    

    public void updateBoindPoints(Vector3f[] points) {
        BoundingBox bb = (BoundingBox) batch.getWorldBound();
        float xe = bb.getXExtent();
        float ye = bb.getYExtent();
        float ze = bb.getZExtent();
        float x = bb.getCenter().x;
        float y = bb.getCenter().y;
        float z = bb.getCenter().z;

        points[0].set(new Vector3f(x - xe, y - ye, z - ze));
        points[1].set(new Vector3f(x - xe, y + ye, z - ze));
        points[2].set(new Vector3f(x + xe, y + ye, z - ze));
        points[3].set(new Vector3f(x + xe, y - ye, z - ze));

        points[4].set(new Vector3f(x + xe, y - ye, z + ze));
        points[5].set(new Vector3f(x - xe, y - ye, z + ze));
        points[6].set(new Vector3f(x - xe, y + ye, z + ze));
        points[7].set(new Vector3f(x + xe, y + ye, z + ze));
    }
}
