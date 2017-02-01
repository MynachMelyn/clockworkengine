
package clockworktest.light;

import com.clockwork.app.SimpleApplication;
import com.clockwork.font.BitmapText;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.AnalogListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.AmbientLight;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.FastMath;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector2f;
import com.clockwork.math.Vector3f;
import com.clockwork.post.FilterPostProcessor;
import com.clockwork.renderer.queue.RenderQueue.ShadowMode;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.shape.Box;
import com.clockwork.scene.shape.Sphere;
import com.clockwork.shadow.DirectionalLightShadowFilter;
import com.clockwork.shadow.DirectionalLightShadowRenderer;
import com.clockwork.shadow.EdgeFilteringMode;
import com.clockwork.texture.Texture;
import com.clockwork.texture.Texture.WrapMode;
import com.clockwork.util.SkyFactory;
import com.clockwork.util.TangentBinormalGenerator;

public class TestDirectionalLightShadow extends SimpleApplication implements ActionListener, AnalogListener {
    public static final int SHADOWMAP_SIZE = 1024;

    private Spatial[] obj;
    private Material[] mat;
    private DirectionalLightShadowRenderer dlsr;
    private DirectionalLightShadowFilter dlsf;
    private Geometry ground;
    private Material matGroundU;
    private Material matGroundL;

    public static void main(String[] args) {
        TestDirectionalLightShadow app = new TestDirectionalLightShadow();
        app.start();
    }
    private float frustumSize = 100;

    public void onAnalog(String name, float value, float tpf) {
        if (cam.isParallelProjection()) {
            // Instead of moving closer/farther to object, we zoom in/out.
            if (name.equals("Size-")) {
                frustumSize += 5f * tpf;
            } else {
                frustumSize -= 5f * tpf;
            }

            float aspect = (float) cam.getWidth() / cam.getHeight();
            cam.setFrustum(-1000, 1000, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);
        }
    }

    public void loadScene() {
        obj = new Spatial[2];
        // Setup first view


        mat = new Material[2];
        mat[0] = assetManager.loadMaterial("Common/Materials/RedColor.j3m");
        mat[1] = assetManager.loadMaterial("Textures/Terrain/Pond/Pond.j3m");
        mat[1].setBoolean("UseMaterialColors", true);
        mat[1].setColor("Ambient", ColorRGBA.White.mult(0.5f));
        mat[1].setColor("Diffuse", ColorRGBA.White.clone());


        obj[0] = new Geometry("sphere", new Sphere(30, 30, 2));
        obj[0].setShadowMode(ShadowMode.CastAndReceive);
        obj[1] = new Geometry("cube", new Box(1.0f, 1.0f, 1.0f));
        obj[1].setShadowMode(ShadowMode.CastAndReceive);
        TangentBinormalGenerator.generate(obj[1]);
        TangentBinormalGenerator.generate(obj[0]);


        for (int i = 0; i < 60; i++) {
            Spatial t = obj[FastMath.nextRandomInt(0, obj.length - 1)].clone(false);
            t.setLocalScale(FastMath.nextRandomFloat() * 10f);
            t.setMaterial(mat[FastMath.nextRandomInt(0, mat.length - 1)]);
            rootNode.attachChild(t);
            t.setLocalTranslation(FastMath.nextRandomFloat() * 200f, FastMath.nextRandomFloat() * 30f + 20, 30f * (i + 2f));
        }

        Box b = new Box(new Vector3f(0, 10, 550), 1000, 2, 1000);
        b.scaleTextureCoordinates(new Vector2f(10, 10));
        ground = new Geometry("soil", b);
        matGroundU = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matGroundU.setColor("Color", ColorRGBA.Green);


        matGroundL = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        grass.setWrap(WrapMode.Repeat);
        matGroundL.setTexture("DiffuseMap", grass);

        ground.setMaterial(matGroundL);

        ground.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(ground);

        l = new DirectionalLight();
        //l.setDirection(new Vector3f(0.5973172f, -0.16583486f, 0.7846725f).normalizeLocal());
        l.setDirection(new Vector3f(-1, -1, -1));
        rootNode.addLight(l);


        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.5f));
        rootNode.addLight(al);

        Spatial sky = SkyFactory.createSky(assetManager, "Scenes/Beach/FullskiesSunset0068.dds", false);
        sky.setLocalScale(350);

        rootNode.attachChild(sky);
    }
    DirectionalLight l;

    @Override
    public void simpleInitApp() {
        // put the camera in a bad position
        cam.setLocation(new Vector3f(65.25412f, 44.38738f, 9.087874f));
        cam.setRotation(new Quaternion(0.078139365f, 0.050241485f, -0.003942559f, 0.9956679f));

        flyCam.setMoveSpeed(100);

        loadScene();

        dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setLight(l);
        dlsr.setLambda(0.55f);
        dlsr.setShadowIntensity(0.6f);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
        dlsr.displayDebug();
        viewPort.addProcessor(dlsr);

        dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
        dlsf.setLight(l);
        dlsf.setLambda(0.55f);
        dlsf.setShadowIntensity(0.6f);
        dlsf.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
        dlsf.setEnabled(false);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);

        viewPort.addProcessor(fpp);

        initInputs();
    }

    private void initInputs() {

        inputManager.addMapping("ThicknessUp", new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addMapping("ThicknessDown", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("lambdaUp", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("lambdaDown", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("switchGroundMat", new KeyTrigger(KeyInput.KEY_M));
        inputManager.addMapping("debug", new KeyTrigger(KeyInput.KEY_X));
        inputManager.addMapping("stabilize", new KeyTrigger(KeyInput.KEY_B));


        inputManager.addMapping("up", new KeyTrigger(KeyInput.KEY_NUMPAD8));
        inputManager.addMapping("down", new KeyTrigger(KeyInput.KEY_NUMPAD2));
        inputManager.addMapping("right", new KeyTrigger(KeyInput.KEY_NUMPAD6));
        inputManager.addMapping("left", new KeyTrigger(KeyInput.KEY_NUMPAD4));
        inputManager.addMapping("fwd", new KeyTrigger(KeyInput.KEY_PGUP));
        inputManager.addMapping("back", new KeyTrigger(KeyInput.KEY_PGDN));
        inputManager.addMapping("pp", new KeyTrigger(KeyInput.KEY_P));


        inputManager.addListener(this, "lambdaUp", "lambdaDown", "ThicknessUp", "ThicknessDown",
                "switchGroundMat", "debug", "up", "down", "right", "left", "fwd", "back", "pp","stabilize");

        ShadowTestUIManager uiMan = new ShadowTestUIManager(assetManager, dlsr, dlsf, guiNode, inputManager, viewPort);

        inputManager.addListener(this, "Size+", "Size-");
        inputManager.addMapping("Size+", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Size-", new KeyTrigger(KeyInput.KEY_S));
 
        shadowStabilizationText = new BitmapText(guiFont, false);
        shadowStabilizationText.setSize(guiFont.getCharSet().getRenderedSize() * 0.75f);
        shadowStabilizationText.setText("(b:on/off) Shadow stabilization : " + dlsr.isEnabledStabilization());
        shadowStabilizationText.setLocalTranslation(10, viewPort.getCamera().getHeight() - 100, 0);
        guiNode.attachChild(shadowStabilizationText);
    }
    private  BitmapText shadowStabilizationText ;

    public void onAction(String name, boolean keyPressed, float tpf) {


        if (name.equals("pp") && keyPressed) {
            if (cam.isParallelProjection()) {
                cam.setFrustumPerspective(45, (float) cam.getWidth() / cam.getHeight(), 1, 1000);
            } else {
                cam.setParallelProjection(true);
                float aspect = (float) cam.getWidth() / cam.getHeight();
                cam.setFrustum(-1000, 1000, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);

            }
        }

        if (name.equals("lambdaUp") && keyPressed) {
            dlsr.setLambda(dlsr.getLambda() + 0.01f);
            dlsf.setLambda(dlsr.getLambda() + 0.01f);
            System.out.println("Lambda : " + dlsr.getLambda());
        } else if (name.equals("lambdaDown") && keyPressed) {
            dlsr.setLambda(dlsr.getLambda() - 0.01f);
            dlsf.setLambda(dlsr.getLambda() - 0.01f);
            System.out.println("Lambda : " + dlsr.getLambda());
        }


        if (name.equals("debug") && keyPressed) {
            dlsr.displayFrustum();
        }

        if (name.equals("stabilize") && keyPressed) {
            dlsr.setEnabledStabilization(!dlsr.isEnabledStabilization());
            dlsf.setEnabledStabilization(!dlsf.isEnabledStabilization());  
            shadowStabilizationText.setText("(b:on/off) Shadow stabilization : " + dlsr.isEnabledStabilization());
        }

        if (name.equals("switchGroundMat") && keyPressed) {
            if (ground.getMaterial() == matGroundL) {
                ground.setMaterial(matGroundU);
            } else {
                ground.setMaterial(matGroundL);
            }
        }

        if (name.equals("up")) {
            up = keyPressed;
        }
        if (name.equals("down")) {
            down = keyPressed;
        }
        if (name.equals("right")) {
            right = keyPressed;
        }
        if (name.equals("left")) {
            left = keyPressed;
        }
        if (name.equals("fwd")) {
            fwd = keyPressed;
        }
        if (name.equals("back")) {
            back = keyPressed;
        }

    }
    boolean up = false;
    boolean down = false;
    boolean left = false;
    boolean right = false;
    boolean fwd = false;
    boolean back = false;
    float time = 0;
    float s = 1f;

    @Override
    public void simpleUpdate(float tpf) {
        if (up) {
            Vector3f v = l.getDirection();
            v.y += tpf / s;
            setDir(v);
        }
        if (down) {
            Vector3f v = l.getDirection();
            v.y -= tpf / s;
            setDir(v);
        }
        if (right) {
            Vector3f v = l.getDirection();
            v.x += tpf / s;
            setDir(v);
        }
        if (left) {
            Vector3f v = l.getDirection();
            v.x -= tpf / s;
            setDir(v);
        }
        if (fwd) {
            Vector3f v = l.getDirection();
            v.z += tpf / s;
            setDir(v);
        }
        if (back) {
            Vector3f v = l.getDirection();
            v.z -= tpf / s;
            setDir(v);
        }

    }

    private void setDir(Vector3f v) {
        l.setDirection(v);
    }
}
