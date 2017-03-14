
package clockworktest.material;

import com.clockwork.app.SimpleApplication;
import com.clockwork.effect.*;
import com.clockwork.effect.shapes.*;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.AnalogListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.*;
import com.clockwork.material.*;
import com.clockwork.math.*;
import com.clockwork.post.*;
import com.clockwork.post.filters.*;
import com.clockwork.renderer.queue.*;
import com.clockwork.renderer.queue.RenderQueue.ShadowMode;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.shadow.*;
import com.clockwork.texture.*;
import com.clockwork.texture.Texture.WrapMode;
import com.clockwork.util.SkyFactory;
import com.clockwork.util.TangentBinormalGenerator;
import com.clockwork.water.*;

public class TestParallax extends SimpleApplication {

    //private Vector3f lightDir = new Vector3f(-1, -1, .5f).normalizeLocal();
    private Vector3f lightDir = new Vector3f(-1f, -0.2f, 1f).normalizeLocal();
    private static float WATER_HEIGHT = 9;
    private WaterFilter water;
    private Node particleNode;
    private FilterPostProcessor fpp;
    private TranslucentBucketFilter tbf;
    private DepthOfFieldFilter dofFilter;
    private FogFilter fog;
    public static void main(String[] args) {
        TestParallax app = new TestParallax();
        app.start();
    }

    public void setupSkyBox() {
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Scenes/Beach/FullskiesSunset0068.dds", false));
        //rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
    }
    DirectionalLight dl;

    public void setupLighting() {

        dl = new DirectionalLight();
        dl.setDirection(lightDir);
        dl.setColor(new ColorRGBA(.9f, .9f, .9f, 1));
        //dl.setColor(ColorRGBA.White.clone().multLocal(2));
        rootNode.addLight(dl);
        
        AmbientLight amb = new AmbientLight();
        amb.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 1));
        rootNode.addLight(amb);
    }
    
    private void createParticles() {
        
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        material.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        material.setFloat("Softness", 3f);       
        
        ParticleEmitter smoke = new ParticleEmitter("Smoke", ParticleMesh.Type.Triangle, 30);
        smoke.setMaterial(material);
        smoke.setShape(new EmitterSphereShape(Vector3f.ZERO, 5));
        smoke.setImagesX(1);
        smoke.setImagesY(1); // 2x2 texture animation
        smoke.setStartColor(new ColorRGBA(0.1f, 0.1f, 0.1f,1f)); // dark gray
        smoke.setEndColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.3f)); // gray      
        smoke.setStartSize(8f);
        smoke.setEndSize(9f);
        smoke.setGravity(0, -0.001f, 0);
        smoke.setLowLife(100f);
        smoke.setHighLife(100f);
        smoke.setLocalTranslation(16f, 18f, 16f);        
        smoke.emitAllParticles();
        
        particleNode.attachChild(smoke);
        
        //fpp = new FilterPostProcessor(assetManager);        
        tbf = new TranslucentBucketFilter(true);
        fpp.addFilter(tbf);
        viewPort.addProcessor(fpp);
    }
    
    public void setupWater() {
        //FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        water = new WaterFilter(rootNode, lightDir);
        //water.setCenter(new Vector3f(16f, 3f, 16f));
        //water.setRadius(16);//260
        water.setWaveScale(0.003f);//0.003
        water.setMaxAmplitude(0.5f);//2
        water.setFoamExistence(new Vector3f(1f, 4, 0.5f));
        water.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
        water.setRefractionStrength(0.2f);
        water.setWaterHeight(WATER_HEIGHT * 2);
        water.setSpeed(0.05f);
        
        fpp.addFilter(water);
        
        viewPort.addProcessor(fpp);
    }
    Material mat;

    public void setupFloor() {
        mat = assetManager.loadMaterial("Textures/Terrain/BrickWall/BrickWall2.j3m");
        mat.getTextureParam("DiffuseMap").getTextureValue().setWrap(WrapMode.Repeat);
        mat.getTextureParam("NormalMap").getTextureValue().setWrap(WrapMode.Repeat);

       // Node floorGeom = (Node) assetManager.loadAsset("Models/WaterTest/WaterTest.mesh.xml");
        //Geometry g = ((Geometry) floorGeom.getChild(0));
        //g.getMesh().scaleTextureCoordinates(new Vector2f(10, 10));
        Spatial floorMesh = assetManager.loadModel("Blender/Pool/Pool.mesh.xml");
        Node floorGeom = new Node("floorGeom");
        /*
        Quad q = new Quad(100, 100);
        q.scaleTextureCoordinates(new Vector2f(10, 10));
        Geometry g = new Geometry("geom", q);
        g.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        */
        floorGeom.attachChild(floorMesh);
        
        
        TangentBinormalGenerator.generate(floorGeom);
        //floorGeom.setLocalTranslation(-50, 22, 60);
        floorGeom.setLocalTranslation(16f, 3f, 16f);
        floorGeom.setLocalScale(20);

        floorGeom.setMaterial(mat);        
        floorGeom.setShadowMode(ShadowMode.Receive);
        rootNode.attachChild(floorGeom);
    }
    
    public void setupPlants(){
        Spatial trunkModel = assetManager.loadModel("Blender/Foliage/foliage/tree_oak_joined.mesh.xml");
        Spatial leafModel = assetManager.loadModel("Blender/Foliage/foliage/tree_oak_joined_leaves.mesh.xml");
        TangentBinormalGenerator.generate(leafModel);        
        TangentBinormalGenerator.generate(trunkModel);        

        Material barkMaterial = assetManager.loadMaterial("Blender/Foliage/foliage/bark_brown.j3m");
        Material leafMaterial = assetManager.loadMaterial("Blender/Foliage/foliage/tree_leaves.j3m");
        
        leafMaterial.getTextureParam("DiffuseMap").getTextureValue().setWrap(WrapMode.Repeat);
        leafMaterial.getTextureParam("NormalMap").getTextureValue().setWrap(WrapMode.Repeat);
        leafMaterial.getTextureParam("AlphaMap").getTextureValue().setWrap(WrapMode.Repeat);
        barkMaterial.getTextureParam("DiffuseMap").getTextureValue().setWrap(WrapMode.Repeat);
        barkMaterial.getTextureParam("NormalMap").getTextureValue().setWrap(WrapMode.Repeat);
        leafMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        leafMaterial.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        leafMaterial.getAdditionalRenderState().setAlphaTest(true);
        
        trunkModel.setMaterial(barkMaterial);
        leafModel.setMaterial(leafMaterial);
        
        leafModel.setQueueBucket(RenderQueue.Bucket.Transparent);

        Node tree_full = new Node("Complete Tree");
        
        tree_full.rotate(0, FastMath.HALF_PI, FastMath.HALF_PI);
        tree_full.setLocalTranslation(18f, 22f, 0f);
        tree_full.setLocalScale(4);
        trunkModel.setShadowMode(ShadowMode.CastAndReceive);
        leafModel.setShadowMode(ShadowMode.Cast);
        
        tree_full.attachChild(leafModel);
        tree_full.attachChild(trunkModel);
        rootNode.attachChild(tree_full);
    }

    public void setupSignpost() {
        Spatial signpost = assetManager.loadModel("Models/Sign Post/Sign Post.mesh.xml");
        Material mat = assetManager.loadMaterial("Models/Sign Post/Sign Post.j3m");
        TangentBinormalGenerator.generate(signpost);
        signpost.setMaterial(mat);
        signpost.rotate(0, FastMath.HALF_PI, 0);
        signpost.setLocalTranslation(18, 25.7f, 31);
        signpost.setLocalScale(4);
        signpost.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(signpost);
    }
    
    private void setupShadows(DirectionalLight dl){
        
        final DirectionalLightShadowRenderer pssmRenderer = new DirectionalLightShadowRenderer(assetManager, 1024, 4);
        viewPort.addProcessor(pssmRenderer);
        pssmRenderer.setLight(dl);
        pssmRenderer.setLambda(0.55f);
        pssmRenderer.setShadowIntensity(0.55f);
        pssmRenderer.setShadowCompareMode(com.clockwork.shadow.CompareMode.Software);
        pssmRenderer.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
        
        //FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        LightScatteringFilter filter = new LightScatteringFilter(lightDir.multLocal(-3000));
        filter.setLightDensity(0.2f);
        fpp.addFilter(filter);
        viewPort.addProcessor(fpp);
    }
    
    private void setupFog(){
        fog=new FogFilter();
        fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
        fog.setFogDistance(155);
        fog.setFogDensity(2.0f);
        fpp.addFilter(fog);
    }
   
    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(-15.445636f, 30.162927f, 60.252777f));
        cam.setRotation(new Quaternion(0.05173137f, 0.92363626f, -0.13454558f, 0.35513034f));
        flyCam.setMoveSpeed(30);
        
        particleNode = new Node("particleNode");
        rootNode.attachChild(particleNode);
        fpp = new FilterPostProcessor(assetManager);

        setupLighting();
        setupSkyBox();
        setupFloor();
        setupSignpost();
        setupPlants();
        setupShadows(dl);
        setupWater();
        createParticles();
        //setupFog();

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
        //time+=tpf;
        //lightDir.set(FastMath.sin(time), -1, FastMath.cos(time));
        //bsr.setDirection(lightDir);
        //dl.setDirection(lightDir);
    }
}
