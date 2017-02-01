
package clockworktest.post;

import com.clockwork.app.SimpleApplication;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.Material;
import com.clockwork.math.*;
import com.clockwork.post.FilterPostProcessor;
import com.clockwork.post.filters.ColorOverlayFilter;
import com.clockwork.post.filters.FadeFilter;
import com.clockwork.post.filters.RadialBlurFilter;
import com.clockwork.renderer.Caps;
import com.clockwork.renderer.queue.RenderQueue.ShadowMode;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.Spatial.CullHint;
import com.clockwork.scene.shape.Box;
import com.clockwork.texture.Texture;
import com.clockwork.texture.Texture.WrapMode;
import com.clockwork.util.SkyFactory;
import com.clockwork.util.TangentBinormalGenerator;

public class TestPostFilters extends SimpleApplication implements ActionListener {

    private FilterPostProcessor fpp;
    private Vector3f lightDir = new Vector3f(-1, -1, .5f).normalizeLocal();
    FadeFilter fade;

    public static void main(String[] args) {
        TestPostFilters app = new TestPostFilters();
        app.start();
    }

    public void setupFilters() {
        if (renderer.getCaps().contains(Caps.GLSL100)) {
            fpp = new FilterPostProcessor(assetManager);
            // fpp.setNumSamples(4);
            fpp.addFilter(new ColorOverlayFilter(ColorRGBA.LightGray));
            fpp.addFilter(new RadialBlurFilter());
            //fade=new FadeFilter(1.0f);
            //fpp.addFilter(fade);


            viewPort.addProcessor(fpp);
        }
    }

    public void setupSkyBox() {
        Texture envMap;
        if (renderer.getCaps().contains(Caps.FloatTexture)) {
            envMap = assetManager.loadTexture("Textures/Sky/St Peters/StPeters.hdr");
        } else {
            envMap = assetManager.loadTexture("Textures/Sky/St Peters/StPeters.jpg");
        }
        rootNode.attachChild(SkyFactory.createSky(assetManager, envMap, new Vector3f(-1, -1, -1), true));
    }

    public void setupLighting() {

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(lightDir);

        dl.setColor(new ColorRGBA(.9f, .9f, .9f, 1));

        rootNode.addLight(dl);

        dl = new DirectionalLight();
        dl.setDirection(new Vector3f(1, 0, -1).normalizeLocal());

        dl.setColor(new ColorRGBA(.4f, .4f, .4f, 1));

        rootNode.addLight(dl);
    }

    public void setupFloor() {
        Material mat = assetManager.loadMaterial("Textures/Terrain/BrickWall/BrickWall.j3m");
        mat.getTextureParam("DiffuseMap").getTextureValue().setWrap(WrapMode.Repeat);
        mat.getTextureParam("NormalMap").getTextureValue().setWrap(WrapMode.Repeat);
        mat.getTextureParam("ParallaxMap").getTextureValue().setWrap(WrapMode.Repeat);
        Box floor = new Box(Vector3f.ZERO, 50, 1f, 50);
        TangentBinormalGenerator.generate(floor);
        floor.scaleTextureCoordinates(new Vector2f(5, 5));
        Geometry floorGeom = new Geometry("Floor", floor);
        floorGeom.setMaterial(mat);
        floorGeom.setShadowMode(ShadowMode.Receive);
        rootNode.attachChild(floorGeom);
    }

    public void setupSignpost() {
        Spatial signpost = assetManager.loadModel("Models/Sign Post/Sign Post.mesh.xml");
        Material mat = assetManager.loadMaterial("Models/Sign Post/Sign Post.j3m");
        signpost.setMaterial(mat);
        signpost.rotate(0, FastMath.HALF_PI, 0);
        signpost.setLocalTranslation(12, 3.5f, 30);
        signpost.setLocalScale(4);
        signpost.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(signpost);
    }

    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(-32.295086f, 54.80136f, 79.59805f));
        cam.setRotation(new Quaternion(0.074364014f, 0.92519957f, -0.24794696f, 0.27748522f));
        cam.update();

        cam.setFrustumFar(300);
        flyCam.setMoveSpeed(30);

        rootNode.setCullHint(CullHint.Never);

        setupLighting();
        setupSkyBox();


        setupFloor();

        setupSignpost();

        setupFilters();

        initInput();

    }

    protected void initInput() {
        flyCam.setMoveSpeed(3);
        //init input
        inputManager.addMapping("fadein", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addListener(this, "fadein");
        inputManager.addMapping("fadeout", new KeyTrigger(KeyInput.KEY_O));
        inputManager.addListener(this, "fadeout");

    }

    public void onAction(String name, boolean value, float tpf) {
        if (name.equals("fadein") && value) {
            fade.fadeIn();
            System.out.println("fade in");

        }
        if (name.equals("fadeout") && value) {
            fade.fadeOut();
            System.out.println("fade out");
        }
    }
}
