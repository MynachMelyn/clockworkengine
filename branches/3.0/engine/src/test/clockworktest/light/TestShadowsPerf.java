
package clockworktest.light;

import com.clockwork.app.SimpleApplication;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.AmbientLight;
import com.clockwork.light.DirectionalLight;
import com.clockwork.light.PointLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector2f;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.queue.RenderQueue.ShadowMode;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.shape.Box;
import com.clockwork.scene.shape.Sphere;
import com.clockwork.shadow.DirectionalLightShadowRenderer;
import com.clockwork.shadow.EdgeFilteringMode;
import com.clockwork.util.TangentBinormalGenerator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestShadowsPerf extends SimpleApplication {

    float angle;
    PointLight pl;
    Spatial lightMdl;

    public static void main(String[] args) {
        TestShadowsPerf app = new TestShadowsPerf();
        app.start();
    }
    Geometry sphere;
    Material mat;

    @Override
    public void simpleInitApp() {
        Logger.getLogger("com.clockwork").setLevel(Level.SEVERE);
        flyCam.setMoveSpeed(50);
        flyCam.setEnabled(false);
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        cam.setLocation(new Vector3f(-53.952988f, 27.15874f, -32.875023f));
        cam.setRotation(new Quaternion(0.1564309f, 0.6910534f, -0.15713608f, 0.6879555f));

//        cam.setLocation(new Vector3f(53.64627f, 130.56f, -11.247704f));
//        cam.setRotation(new Quaternion(-6.5737107E-4f, 0.76819664f, -0.64021313f, -7.886125E-4f));   
//// 
        cam.setFrustumFar(500);

        mat = assetManager.loadMaterial("Textures/Terrain/Pond/Pond.j3m");

        Box b = new Box(Vector3f.ZERO, 800, 1, 700);
        b.scaleTextureCoordinates(new Vector2f(50, 50));
        Geometry ground = new Geometry("ground", b);
        ground.setMaterial(mat);
        rootNode.attachChild(ground);
        ground.setShadowMode(ShadowMode.Receive);

        Sphere sphMesh = new Sphere(32, 32, 1);
        sphMesh.setTextureMode(Sphere.TextureMode.Projected);
        sphMesh.updateGeometry(32, 32, 1, false, false);
        TangentBinormalGenerator.generate(sphMesh);

        sphere = new Geometry("Rock Ball", sphMesh);
        sphere.setLocalTranslation(0, 5, 0);
        sphere.setMaterial(mat);
        sphere.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(sphere);




        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(0, -1, 0).normalizeLocal());
        dl.setColor(ColorRGBA.White);
        rootNode.addLight(dl);

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.7f));
        rootNode.addLight(al);
        //rootNode.setShadowMode(ShadowMode.CastAndReceive);

        createballs();

        final DirectionalLightShadowRenderer pssmRenderer = new DirectionalLightShadowRenderer(assetManager, 1024, 4);
        viewPort.addProcessor(pssmRenderer);
//        
//        final PssmShadowFilter pssmRenderer = new PssmShadowFilter(assetManager, 1024, 4);
//        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);        
//        fpp.addFilter(pssmRenderer);
//        viewPort.addProcessor(fpp);
                
        pssmRenderer.setLight(dl);
        pssmRenderer.setLambda(0.55f);
        pssmRenderer.setShadowIntensity(0.55f);
        pssmRenderer.setShadowCompareMode(com.clockwork.shadow.CompareMode.Software);
        pssmRenderer.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
        //pssmRenderer.displayDebug();

        inputManager.addListener(new ActionListener() {

            public void onAction(String name, boolean isPressed, float tpf) {
                if (name.equals("display") && isPressed) {
                     //pssmRenderer.debugFrustrums();
                    System.out.println("tetetetet");
                }
                if (name.equals("add") && isPressed) {
                    createballs();
                }
            }
        }, "display", "add");
        inputManager.addMapping("display", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("add", new KeyTrigger(KeyInput.KEY_RETURN));
    }
    int val = 0;

    private void createballs() {
        System.out.println((frames / time) + ";" + val);


        for (int i = val; i < val+1 ; i++) {

            Geometry s = sphere.clone().clone(false);
            s.setMaterial(mat);
            s.setLocalTranslation(i - 30, 5, (((i) * 2) % 40) - 50);
            s.setShadowMode(ShadowMode.CastAndReceive);
            rootNode.attachChild(s);
        }
        if (val == 300) {
            stop();
        }
        val += 1;
        time = 0;
        frames = 0;
    }
    float time;
    float frames = 0;

    @Override
    public void simpleUpdate(float tpf) {
        time += tpf;
        frames++;
        if (time > 1) {
            createballs();
        }
    }
}
