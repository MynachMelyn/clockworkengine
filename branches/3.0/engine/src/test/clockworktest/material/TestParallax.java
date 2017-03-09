
package clockworktest.material;

import com.clockwork.app.SimpleApplication;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.AnalogListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.Material;
import com.clockwork.math.*;
import com.clockwork.post.FilterPostProcessor;
import com.clockwork.post.filters.FXAAFilter;
import com.clockwork.renderer.queue.RenderQueue.ShadowMode;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.shape.Quad;
import com.clockwork.texture.Texture.WrapMode;
import com.clockwork.util.SkyFactory;
import com.clockwork.util.TangentBinormalGenerator;

public class TestParallax extends SimpleApplication {

    private Vector3f lightDir = new Vector3f(-1, -1, .5f).normalizeLocal();

    public static void main(String[] args) {
        TestParallax app = new TestParallax();
        app.start();
    }

    public void setupSkyBox() {
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Scenes/Beach/FullskiesSunset0068.dds", false));
    }
    DirectionalLight dl;

    public void setupLighting() {

        dl = new DirectionalLight();
        dl.setDirection(lightDir);
        dl.setColor(new ColorRGBA(.9f, .9f, .9f, 1));
        rootNode.addLight(dl);
    }
    Material mat;

    public void setupFloor() {
        mat = assetManager.loadMaterial("Textures/Terrain/BrickWall/BrickWall2.j3m");
        mat.getTextureParam("DiffuseMap").getTextureValue().setWrap(WrapMode.Repeat);
        mat.getTextureParam("NormalMap").getTextureValue().setWrap(WrapMode.Repeat);

       // Node floorGeom = (Node) assetManager.loadAsset("Models/WaterTest/WaterTest.mesh.xml");
        //Geometry g = ((Geometry) floorGeom.getChild(0));
        //g.getMesh().scaleTextureCoordinates(new Vector2f(10, 10));
                
        Node floorGeom = new Node("floorGeom");
        Quad q = new Quad(100, 100);
        q.scaleTextureCoordinates(new Vector2f(10, 10));
        Geometry g = new Geometry("geom", q);
        g.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        floorGeom.attachChild(g);
        
        
        TangentBinormalGenerator.generate(floorGeom);
        floorGeom.setLocalTranslation(-50, 22, 60);
        //floorGeom.setLocalScale(100);

        floorGeom.setMaterial(mat);        
        floorGeom.setShadowMode(ShadowMode.Receive);
        rootNode.attachChild(floorGeom);
    }

    public void setupSignpost() {
        Spatial signpost = assetManager.loadModel("Models/Sign Post/Sign Post.mesh.xml");
        Material mat = assetManager.loadMaterial("Models/Sign Post/Sign Post.j3m");
        TangentBinormalGenerator.generate(signpost);
        signpost.setMaterial(mat);
        signpost.rotate(0, FastMath.HALF_PI, 0);
        signpost.setLocalTranslation(12, 23.5f, 30);
        signpost.setLocalScale(4);
        signpost.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(signpost);
    }

    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(-15.445636f, 30.162927f, 60.252777f));
        cam.setRotation(new Quaternion(0.05173137f, 0.92363626f, -0.13454558f, 0.35513034f));
        flyCam.setMoveSpeed(30);


        setupLighting();
        setupSkyBox();
        setupFloor();
        setupSignpost();

        inputManager.addListener(new AnalogListener() {

            public void onAnalog(String name, float value, float tpf) {
                if ("heightUP".equals(name)) {
                    parallaxHeigh += 0.0001;
                    mat.setFloat("ParallaxHeight", parallaxHeigh);
                }
                if ("heightDown".equals(name)) {
                    parallaxHeigh -= 0.0001;
                    parallaxHeigh = Math.max(parallaxHeigh, 0);
                    mat.setFloat("ParallaxHeight", parallaxHeigh);
                }

            }
        }, "heightUP", "heightDown");
        inputManager.addMapping("heightUP", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("heightDown", new KeyTrigger(KeyInput.KEY_K));

        inputManager.addListener(new ActionListener() {

            public void onAction(String name, boolean isPressed, float tpf) {
                if (isPressed && "toggleSteep".equals(name)) {
                    steep = !steep;
                    mat.setBoolean("SteepParallax", steep);
                }
            }
        }, "toggleSteep");
        inputManager.addMapping("toggleSteep", new KeyTrigger(KeyInput.KEY_SPACE));
    }
    float parallaxHeigh = 0.05f;
    float time = 0;
    boolean steep = false;

    @Override
    public void simpleUpdate(float tpf) {
//        time+=tpf;
//        lightDir.set(FastMath.sin(time), -1, FastMath.cos(time));
//        bsr.setDirection(lightDir);
//        dl.setDirection(lightDir);
    }
}
