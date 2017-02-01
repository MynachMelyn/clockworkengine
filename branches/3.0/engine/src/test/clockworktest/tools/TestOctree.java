

package clockworktest.tools;

import com.clockwork.app.SimpleApplication;
import com.clockwork.bounding.BoundingBox;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.Material;
import com.clockwork.material.MaterialList;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector3f;
import com.clockwork.post.SceneProcessor;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.renderer.queue.RenderQueue;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.debug.WireBox;
import com.clockwork.scene.plugins.ogre.MeshLoader;
import com.clockwork.scene.plugins.ogre.OgreMeshKey;
import com.clockwork.texture.FrameBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import clockworktools.optimize.FastOctnode;
import clockworktools.optimize.Octree;


public class TestOctree extends SimpleApplication implements SceneProcessor {

    private Octree tree;
    private FastOctnode fastRoot;
    private Geometry[] globalGeoms;
    private BoundingBox octBox;

    private Set<Geometry> renderSet = new HashSet<Geometry>(300);
    private Material mat, mat2;
    private WireBox box = new WireBox(1,1,1);

    public static void main(String[] args){
        TestOctree app = new TestOctree();
        app.start();
    }

    public void simpleInitApp() {
//        this.flyCam.setMoveSpeed(2000);
//        this.cam.setFrustumFar(10000);
//        mat = new Material(assetManager, "Common/MatDefs/Misc/WireColor.j3md");
//        mat.setColor("Color", ColorRGBA.White);

//        mat2 = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");

        assetManager.registerLocator("quake3level.zip", com.clockwork.asset.plugins.ZipLocator.class);
        MaterialList matList = (MaterialList) assetManager.loadAsset("Scene.material");
        OgreMeshKey key = new OgreMeshKey("main.meshxml", matList);
        Spatial scene = assetManager.loadModel(key);

//        Spatial scene = assetManager.loadModel("Models/Teapot/teapot.obj");
//        scene.scale(3);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(-1, -1, -1).normalize());
        rootNode.addLight(dl);

        DirectionalLight dl2 = new DirectionalLight();
        dl2.setColor(ColorRGBA.White);
        dl2.setDirection(new Vector3f(1, -1, 1).normalize());
        rootNode.addLight(dl2);

        // generate octree
//        tree = new Octree(scene, 20000);
        tree = new Octree(scene);
        tree.construct(50, 0.01f, 50);
        
        ArrayList<Geometry> globalGeomList = new ArrayList<Geometry>();
        tree.createFastOctnodes(globalGeomList);
        tree.generateFastOctnodeLinks();

        for (Geometry geom : globalGeomList){
            geom.addLight(dl);
            geom.addLight(dl2);
            geom.updateGeometricState();
        }
        
        globalGeoms = globalGeomList.toArray(new Geometry[0]);
        fastRoot = tree.getFastRoot();
        octBox = tree.getBound();

        viewPort.addProcessor(this);
    }

    public void initialize(RenderManager rm, ViewPort vp) {
    }

    public void reshape(ViewPort vp, int w, int h) {
    }

    public boolean isInitialized() {
        return true;
    }

    public void preFrame(float tpf) {
    }

    public void postQueue(RenderQueue rq) {
        renderSet.clear();
        //tree.generateRenderSet(renderSet, cam);
        fastRoot.generateRenderSet(globalGeoms, renderSet, cam, octBox, true);
//        System.out.println("Geoms: "+renderSet.size());
        int tris = 0;

        for (Geometry geom : renderSet){
            tris += geom.getTriangleCount();
//            geom.setMaterial(mat2);
            rq.addToQueue(geom, geom.getQueueBucket());
        }

//        Matrix4f transform = new Matrix4f();
//        transform.setScale(0.2f, 0.2f, 0.2f);
//        System.out.println("Tris: "+tris);
        
//        tree.renderBounds(rq, transform, box, mat);

//        renderManager.flushQueue(viewPort);
    }

    public void postFrame(FrameBuffer out) {
    }

    public void cleanup() {
    }
}
