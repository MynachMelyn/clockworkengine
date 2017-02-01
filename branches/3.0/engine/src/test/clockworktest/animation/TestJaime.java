
package clockworktest.animation;

import com.clockwork.animation.AnimControl;
import com.clockwork.animation.AnimationFactory;
import com.clockwork.animation.LoopMode;
import com.clockwork.app.DebugKeysAppState;
import com.clockwork.app.FlyCamAppState;
import com.clockwork.app.ResetStatsState;
import com.clockwork.app.SimpleApplication;
import com.clockwork.app.StatsAppState;
import com.clockwork.cinematic.Cinematic;
import com.clockwork.cinematic.MotionPath;
import com.clockwork.cinematic.PlayState;
import com.clockwork.cinematic.events.AnimationEvent;
import com.clockwork.cinematic.events.MotionEvent;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.AmbientLight;
import com.clockwork.light.PointLight;
import com.clockwork.light.SpotLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.FastMath;
import com.clockwork.math.Vector2f;
import com.clockwork.math.Vector3f;
import com.clockwork.post.FilterPostProcessor;
import com.clockwork.post.filters.FXAAFilter;
import com.clockwork.post.ssao.SSAOFilter;
import com.clockwork.renderer.queue.RenderQueue;
import com.clockwork.scene.CameraNode;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.shape.Quad;
import com.clockwork.shadow.EdgeFilteringMode;
import com.clockwork.shadow.SpotLightShadowRenderer;

/**
 *
 * @author Nehon
 */
public class TestJaime  extends SimpleApplication {

   
    Cinematic cinematic;
    
    public static void main(String... argv){
        TestJaime app = new TestJaime();
        app.start();
    }

    
    
    @Override
    public void simpleInitApp() {
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        stateManager.detach(stateManager.getState(ResetStatsState.class));
        stateManager.detach(stateManager.getState(DebugKeysAppState.class));
        stateManager.detach(stateManager.getState(StatsAppState.class));
        final Node jaime = LoadModel();
        
        setupLights();        
        setupCamera();
        setupFloor();
        setupCinematic(jaime);
        setupInput();
    }
    
    public Node LoadModel() {
        Node jaime = (Node)assetManager.loadModel("Models/Jaime/Jaime.j3o");
        jaime.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.attachChild(jaime);
        return jaime;
    }

    public void setupLights() {
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(0.1f, 0.1f, 0.1f, 1));
        rootNode.addLight(al);
        
        SpotLight sl = new SpotLight();
        sl.setColor(ColorRGBA.White.mult(1.0f));
        sl.setPosition(new Vector3f(1.2074411f, 10.6868908f, 4.1489987f));
        sl.setDirection(sl.getPosition().mult(-1)); 
        sl.setSpotOuterAngle(0.1f);
        sl.setSpotInnerAngle(0.004f);      
        rootNode.addLight(sl);
        
        //pointlight to fake indirect light coming from the ground
        PointLight pl = new PointLight();
        pl.setColor(ColorRGBA.White.mult(1.5f));
        pl.setPosition(new Vector3f(0, 0, 1));
        pl.setRadius(2);
        rootNode.addLight(pl);
        
        SpotLightShadowRenderer shadows = new SpotLightShadowRenderer(assetManager, 1024);
        shadows.setLight(sl);
        shadows.setShadowIntensity(0.3f);
        shadows.setEdgeFilteringMode(EdgeFilteringMode.PCF8);
        viewPort.addProcessor(shadows);

        
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        SSAOFilter filter = new SSAOFilter(0.10997847f,0.440001f,0.39999998f,-0.008000026f);;
        fpp.addFilter(filter);
        fpp.addFilter(new FXAAFilter());
        fpp.addFilter(new FXAAFilter());     
        
        viewPort.addProcessor(fpp);
    }

    public void setupCamera() {
         flyCam.setEnabled(false);
    }

    public void setupCinematic(final Node jaime) {
        cinematic = new Cinematic(rootNode, 60);
        stateManager.attach(cinematic);
        
        jaime.move(0, 0, -3);
        AnimationFactory af = new AnimationFactory(0.7f, "JumpForward");
        af.addTimeTranslation(0, new Vector3f(0, 0, -3));
        af.addTimeTranslation(0.35f, new Vector3f(0, 1, -1.5f));
        af.addTimeTranslation(0.7f, new Vector3f(0, 0, 0));
        jaime.getControl(AnimControl.class).addAnim(af.buildAnimation());
   
        cinematic.enqueueCinematicEvent(new AnimationEvent(jaime, "Idle",3, LoopMode.DontLoop));
        float jumpStart = cinematic.enqueueCinematicEvent(new AnimationEvent(jaime, "JumpStart", LoopMode.DontLoop));
        cinematic.addCinematicEvent(jumpStart+0.2f, new AnimationEvent(jaime, "JumpForward", LoopMode.DontLoop,1));        
        cinematic.enqueueCinematicEvent( new AnimationEvent(jaime, "JumpEnd", LoopMode.DontLoop));                
        cinematic.enqueueCinematicEvent( new AnimationEvent(jaime, "Punches", LoopMode.DontLoop));
        cinematic.enqueueCinematicEvent( new AnimationEvent(jaime, "SideKick", LoopMode.DontLoop));        
        float camStart = cinematic.enqueueCinematicEvent( new AnimationEvent(jaime, "Taunt", LoopMode.DontLoop));
        cinematic.enqueueCinematicEvent( new AnimationEvent(jaime, "Idle",1, LoopMode.DontLoop));
        cinematic.enqueueCinematicEvent( new AnimationEvent(jaime, "Wave", LoopMode.DontLoop));
        cinematic.enqueueCinematicEvent( new AnimationEvent(jaime, "Idle", LoopMode.DontLoop));        
        
        CameraNode camNode = cinematic.bindCamera("cam", cam);
        camNode.setLocalTranslation(new Vector3f(1.1f, 1.2f, 2.9f));
        camNode.lookAt(new Vector3f(0, 0.5f, 0), Vector3f.UNIT_Y);
        
        MotionPath path = new MotionPath();
        path.addWayPoint(new Vector3f(1.1f, 1.2f, 2.9f));
        path.addWayPoint(new Vector3f(0f, 1.2f, 3.0f));
        path.addWayPoint(new Vector3f(-1.1f, 1.2f, 2.9f));        
        path.enableDebugShape(assetManager, rootNode);
        path.setCurveTension(0.8f);
        
        MotionEvent camMotion = new MotionEvent(camNode, path,6);
        camMotion.setDirectionType(MotionEvent.Direction.LookAt);
        camMotion.setLookAt(new Vector3f(0, 0.5f, 0), Vector3f.UNIT_Y);
        cinematic.addCinematicEvent(camStart, camMotion);
        cinematic.activateCamera(0, "cam");
       
        
        cinematic.fitDuration();
        cinematic.setSpeed(1.2f);
        cinematic.setLoopMode(LoopMode.Loop);
        cinematic.play();
    }

    public void setupFloor() {
        Quad q = new Quad(20, 20);
       q.scaleTextureCoordinates(Vector2f.UNIT_XY.mult(10));
       Geometry geom = new Geometry("floor", q);
       Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
       mat.setColor("Diffuse", ColorRGBA.White);
       mat.setColor("Specular", ColorRGBA.White);
       mat.setColor("Ambient", ColorRGBA.Black);
       mat.setBoolean("UseMaterialColors", true);
       mat.setFloat("Shininess", 0);
       geom.setMaterial(mat);

       geom.rotate(-FastMath.HALF_PI, 0, 0);
       geom.center();
       geom.setShadowMode(RenderQueue.ShadowMode.Receive);
       rootNode.attachChild(geom);
    }

    public void setupInput() {
        inputManager.addMapping("start", new KeyTrigger(KeyInput.KEY_PAUSE));
        inputManager.addListener(new ActionListener() {

            public void onAction(String name, boolean isPressed, float tpf) {
                if(name.equals("start") && isPressed){
                    if(cinematic.getPlayState() != PlayState.Playing){                                                
                        cinematic.play();
                    }else{
                        cinematic.pause();
                    }
                }
            }
        }, "start");
    }

   
    
}
