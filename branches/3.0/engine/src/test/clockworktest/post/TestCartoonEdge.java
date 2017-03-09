

package clockworktest.post;

import com.clockwork.app.SimpleApplication;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.FastMath;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.post.FilterPostProcessor;
import com.clockwork.post.filters.CartoonEdgeFilter;
import com.clockwork.renderer.Caps;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.Spatial.CullHint;
import com.clockwork.texture.Texture;

public class TestCartoonEdge extends SimpleApplication {

    private FilterPostProcessor fpp;

    public static void main(String[] args){
        TestCartoonEdge app = new TestCartoonEdge();
        app.start();
    }

    public void setupFilters(){
        if (renderer.getCaps().contains(Caps.GLSL100)){
            fpp=new FilterPostProcessor(assetManager);
            //fpp.setNumSamples(4);
            CartoonEdgeFilter toon=new CartoonEdgeFilter();
            toon.setEdgeColor(ColorRGBA.Black);
            fpp.addFilter(toon);
            viewPort.addProcessor(fpp);
        }
    }

    public void makeToonish(Spatial spatial){
        if (spatial instanceof Node){
            Node n = (Node) spatial;
            for (Spatial child : n.getChildren())
                makeToonish(child);
        }else if (spatial instanceof Geometry){
            Geometry g = (Geometry) spatial;
            Material m = g.getMaterial();
            if (m.getMaterialDef().getName().equals("Phong Lighting")){
                Texture t = assetManager.loadTexture("Textures/ColorRamp/toon.png");
//                t.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
//                t.setMagFilter(Texture.MagFilter.Nearest);
                m.setTexture("ColorRamp", t);
                m.setBoolean("UseMaterialColors", true);
                m.setColor("Specular", ColorRGBA.Black);
                m.setColor("Diffuse", ColorRGBA.White);
                m.setBoolean("VertexLighting", true);
            }
        }
    }

    public void setupLighting(){
   
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-1, -1, 1).normalizeLocal());
        dl.setColor(new ColorRGBA(2,2,2,1));

        rootNode.addLight(dl);
    }

    public void setupModel(){
        Spatial model = assetManager.loadModel("Models/MonkeyHead/MonkeyHead.mesh.xml");
        
        Material material = assetManager.loadMaterial("Models/MonkeyHead/MonkeyHead2.j3m");
        material.setBoolean("UseMaterialColors", true);
        material.setColor("Specular", ColorRGBA.Black);
        material.setColor("Diffuse", ColorRGBA.White);
        material.setBoolean("VertexLighting", true);
        model.setMaterial(material);
        
        makeToonish(model);
        model.rotate(0, FastMath.PI, 0);
//        signpost.setLocalTranslation(12, 3.5f, 30);
//        model.scale(0.10f);
//        signpost.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(model);
    }

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.Gray);

        cam.setLocation(new Vector3f(-5.6310086f, 5.0892987f, -13.000479f));
        cam.setRotation(new Quaternion(0.1779095f, 0.20036356f, -0.03702727f, 0.96272093f));
        cam.update();

        cam.setFrustumFar(300);
        flyCam.setMoveSpeed(30);

        rootNode.setCullHint(CullHint.Never);

        setupLighting();
        setupModel();
        setupFilters();
    }

}
