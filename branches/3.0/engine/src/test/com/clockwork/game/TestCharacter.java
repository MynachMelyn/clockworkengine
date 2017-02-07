package com.clockwork.game;

import clockworktest.input.combomoves.ComboMove;
import clockworktest.input.combomoves.ComboMoveExecution;
import java.util.ArrayList;

/*	Introduce TestComboMoves to this, and physics with animation from TestBoneRagdoll
* 	Make a better chase camera, over shoulder? Can aim, can strafe with AD, camera can be moved when still, but decides forward direction when moving
*  (Camera like in old program)
*
*  Introduce SFX
*/

import com.clockwork.animation.AnimChannel;
import com.clockwork.animation.AnimControl;
import com.clockwork.animation.AnimEventListener;
import com.clockwork.animation.LoopMode;
import com.clockwork.animation.SkeletonControl;
import com.clockwork.app.FlyCamAppState;
import com.clockwork.app.SimpleApplication;
import com.clockwork.asset.BlenderKey;
import com.clockwork.bullet.BulletAppState;
import com.clockwork.bullet.PhysicsSpace;
import com.clockwork.bullet.collision.shapes.CapsuleCollisionShape;
//import com.clockwork.bullet.control.BetterCharacterControl;
import com.clockwork.bullet.control.BetterCharacterControl;
import com.clockwork.bullet.control.CharacterControl;
import com.clockwork.bullet.control.RigidBodyControl;
import com.clockwork.bullet.objects.PhysicsRigidBody;
import com.clockwork.font.BitmapText;
import com.clockwork.renderer.queue.RenderQueue;
import com.clockwork.renderer.queue.RenderQueue.ShadowMode;
import com.clockwork.input.BetterChaseCamera;
//import com.clockwork.input.ChaseCamera;
import com.clockwork.input.KeyInput;
import com.clockwork.input.MouseInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.AnalogListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.input.controls.MouseAxisTrigger;
import com.clockwork.input.controls.MouseButtonTrigger;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.RenderManager;
import com.clockwork.scene.CameraNode;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.control.CameraControl.ControlDirection;
import com.clockwork.system.AppSettings;
import com.clockwork.system.NanoTimer;
import com.clockwork.system.Timer;
import java.util.HashSet;
import java.util.List;

public class TestCharacter extends SimpleApplication implements /*AnimEventListener, */ActionListener {
    
    private BulletAppState bulletAppState;
    private CameraNode camNode;
    boolean rotate = false;
   
    //public float speed_stand = 1.0f;
    
    public float defaultTPF = 0.0f;
    
    public static float PIXELSMOVED_TO_RADIANSROTATED = 0.01f;
    
    private boolean timeSlowed;
        
    private HashSet<String> pressedMappings = new HashSet<String>();
    
    private BitmapText fireballText;    
    private BitmapText zawarudoText;    
    private BitmapText shurikenText;
    private BitmapText jabText;
    private BitmapText hookText;
    private BitmapText heavyhitText;
    private BitmapText punchText;
    
    private BitmapText fighterStateText;
    
    private FighterNode playerNode;
    private FighterNode standNode;
    
    public static void main(String[] args) {
        TestCharacter app = new TestCharacter();
        
        app.setShowSettings(true);
        AppSettings settings = new AppSettings(true);
        
        /*		settings.put("Width", 1280);
        settings.put("Height", 720);
        * */
        /*settings.put("Width", 1900);
        settings.put("Height", 1060);
        settings.put("Title", "Test Game");
        settings.put("VSync", true);
        settings.put("Fullscreen", false);*/
        
        //Anti-Aliasing - will not work on non-GPU PCs
        //settings.put("Samples", 4);
        app.setSettings(settings);
        app.start();
    }
    /**
     * Sets up the camera to track the player from the rear. <br>
     * Doesn't rotate the player unless they're moving. <br>
     * @param target - The parent of the camera (The player).
     */
    private void setupChaseCamera(Node target) {
        flyCam.setEnabled(false);
        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(-5, 2, 0));
        camNode.lookAt(new Vector3f(target.getLocalTranslation()).add(0, 1, 0), Vector3f.UNIT_Y);
        target.attachChild(camNode);
    }
    
    private void setupKeys() {
    }
    
    @Override
    public void simpleInitApp() {
        // Activate Physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        // Initialise a physics test scene
        PhysicsTestHelper.createPhysicsTestWorldSoccer(rootNode, assetManager, bulletAppState.getPhysicsSpace());
               
        
        //######################################################################
        Spatial fighterModel = assetManager.loadModel("/Blender/2.5x/export/BaseMesh_01.mesh.xml");
        Material fighterMaterial = new Material(getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        fighterMaterial.setColor("Diffuse", ColorRGBA.Blue);
        fighterModel.setMaterial(fighterMaterial);
        fighterModel.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        
        playerNode = new FighterNode("Stand User", true, fighterModel, inputManager);
        //playerNode.getFighterAnimControl().addListener(this);
        
        getPhysicsSpace().add(playerNode.getPhysicsControl());
        rootNode.attachChild(playerNode);
        
        playerNode.getPhysicsControl().warp(new Vector3f(0, 2, 0));
        
        setupChaseCamera(playerNode);
        
        fighterStateText = new BitmapText(guiFont);
        fighterStateText.setColor(ColorRGBA.Orange);
        fighterStateText.setLocalTranslation(0, fighterStateText.getLineHeight(), 0);
        guiNode.attachChild(fighterStateText);
        
        //######################################################################
        
        setupFightingGame();
        
        
        
   
        
        
        //Spatial stand_model = assetManager.loadModel("/Blender/2.5x/export/BaseMesh_01.mesh.xml");
        //model.center();

        /*Material standMaterial = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        standMaterial.getAdditionalRenderState().setWireframe(true);
        standMaterial.setColor("Color", ColorRGBA.Blue);
        stand_model.setMaterial(standMaterial);*/
        
  
//        stand_model.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        ////
        
        /*BlenderKey blenderKey = new BlenderKey("Blender/2.5x/BaseMesh_256.blend");
        Spatial scene = (Spatial) assetManager.loadModel(blenderKey);
        rootNode.attachChild(scene);
        Spatial model = this.findNode(rootNode, "BaseMesh_01");
        model.center();
        control = model.getControl(AnimControl.class);
        channel = control.createChannel();
        channel.setAnim("run_01"); */
        


     
        
      
        
        // Disable the default FPS fly cam (Don't forget this!)
        flyCam.setEnabled(false);
        
      
        // The variable dl can be reused as adding it to RootNode makes a copy of it
        
        // sunset light
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f,-0.7f,1).normalizeLocal());
        dl.setColor(new ColorRGBA(0.44f, 0.30f, 0.20f, 1.0f));
        rootNode.addLight(dl);
        
        // skylight
        dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.6f,-1,-0.6f).normalizeLocal());
        dl.setColor(new ColorRGBA(0.10f, 0.22f, 0.44f, 1.0f));
        rootNode.addLight(dl);
        
        // white ambient light
        dl = new DirectionalLight();
        dl.setDirection(new Vector3f(1, -0.5f,-0.1f).normalizeLocal());
        dl.setColor(new ColorRGBA(0.80f, 0.70f, 0.80f, 1.0f));
        rootNode.addLight(dl);
    }
    
    private void setupFightingGame() {
        setDisplayFps(false);
        setDisplayStatView(false);
        
        // Create debug text
        BitmapText helpText = new BitmapText(guiFont);
        helpText.setLocalTranslation(0, settings.getHeight(), 0);
        helpText.setText("Moves:\n" +
                "Fireball: Down, Down+Right, Right\n"+
                "Shuriken: Left, Down, Attack1(Z)\n"+
                "Jab: Attack1(Z)\n"+
                "Punch: Attack1(Z), Attack1(Z)\n"+
                "The World: Right, Heavy, Jab, Right, Special (V)\n");
        guiNode.attachChild(helpText);
        
   /*     fireballText = new BitmapText(guiFont);
        fireballText.setColor(ColorRGBA.Orange);
        fireballText.setLocalTranslation(0, fireballText.getLineHeight(), 0);
        guiNode.attachChild(fireballText);
        
        shurikenText = new BitmapText(guiFont);
        shurikenText.setColor(ColorRGBA.Cyan);
        shurikenText.setLocalTranslation(0, shurikenText.getLineHeight()*2f, 0);
        guiNode.attachChild(shurikenText);
        
        jabText = new BitmapText(guiFont);
        jabText.setColor(ColorRGBA.Red);
        jabText.setLocalTranslation(0, jabText.getLineHeight()*3f, 0);
        guiNode.attachChild(jabText);
        
        punchText = new BitmapText(guiFont);
        punchText.setColor(ColorRGBA.Green);
        punchText.setLocalTranslation(0, punchText.getLineHeight()*4f, 0);
        guiNode.attachChild(punchText);
        
        hookText = new BitmapText(guiFont);
        hookText.setColor(ColorRGBA.Red);
        hookText.setLocalTranslation(0, hookText.getLineHeight()*5f, 0);
        guiNode.attachChild(hookText);
        
        heavyhitText = new BitmapText(guiFont);
        heavyhitText.setColor(ColorRGBA.Red);
        heavyhitText.setLocalTranslation(0, heavyhitText.getLineHeight()*6f, 0);
        guiNode.attachChild(heavyhitText);
        
        zawarudoText = new BitmapText(guiFont);
        zawarudoText.setColor(ColorRGBA.White);
        zawarudoText.setLocalTranslation(0, zawarudoText.getLineHeight()*7f, 0);
        guiNode.attachChild(zawarudoText);*/
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        
        playerNode.simpleUpdate(tpf);
        
        
        fighterStateText.setText(playerNode.fighterState.toString());
//        if(walkDirection.equals(Vector3f.ZERO)){
//            if (!channel_player.getAnimationName().equals("base_stand")){
//                channel_player.setAnim("base_stand", 0.50f);
//                channel_player.setLoopMode(LoopMode.Loop);
//                updateChannelSpeed(channel_player);
//            }
//        }
        
//        if(channel_player.getAnimationName().equals("jump")){
//            if(physicsCharacter.isOnGround() && channel_player.getTime() >= channel_player.getAnimMaxTime()/2 ){
//                if(physicsCharacter.getWalkDirection().x != 0 || physicsCharacter.getWalkDirection().z != 0){
//                    channel_player.setAnim("run_01", 0.50f);
//                    channel_player.setLoopMode(LoopMode.Loop);
//                    updateChannelSpeed(channel_player);
//                }
//                else{
//                    channel_player.setAnim("base_stand", 0.50f);
//                    channel_player.setLoopMode(LoopMode.DontLoop);
//                    updateChannelSpeed(channel_player);
//                }
//            }
//        }        
    }
    
    @Override
    public void onAction(String binding, boolean value, float tpf) {

    }
    
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
    
/*    private void SetSlowMotion(float timescale){
        speed = timescale;
        this.getPhysicsSpace().setAccuracy((1/60f)*timescale);
        //channel_stand.setSpeed(1/timescale);
        //channel.setSpeed(1/timescale);
        updateChannelSpeed(channel_player);
        updateChannelSpeed(channel_stand);
        //standNode.getControl(0).update(timescale * defaultTPF);
        //characterNode.getControl(0).update(timescale * defaultTPF);
        
        for(Spatial spatial: this.getRootNode().getChildren()){
            if(spatial.getNumControls() > 0){
                RigidBodyControl temp = spatial.getControl(RigidBodyControl.class);
                if(temp != null){
                    //temp.applyCentralForce(new Vector3f(0, 0, 0));
                    //temp.setEnabled(!temp.isEnabled());
                    temp.setMass(temp.getMass() * 10);
                    //physicsCharacter.setMass(physicsCharacter.getMass() / 4);
                }
            }
        }
    }*/
    
/*    private void ResumeTime(){
        speed = 1;
        this.getPhysicsSpace().setAccuracy((1/60f));
        updateChannelSpeed(channel_player);
        updateChannelSpeed(channel_stand);
        //standNode.getControl(0).update(timescale * defaultTPF);
        //characterNode.getControl(0).update(timescale * defaultTPF);
        
        for(Spatial spatial: this.getRootNode().getChildren()){
            if(spatial.getNumControls() > 0){
                RigidBodyControl temp = spatial.getControl(RigidBodyControl.class);
                if(temp != null){
                    temp.setMass(temp.getMass() / 10);
                    //physicsCharacter.setMass(physicsCharacter.getMass() * 4);
                }
            }
        }
    }*/
    
    /**
     * This method finds a node of a given name.
     * @param rootNode the root node to search
     * @param name the name of the searched node
     * @return the found node or null
     */
    private Spatial findNode(Node rootNode, String name) {
        if (name.equals(rootNode.getName())) {
            return rootNode;
        }
        return rootNode.getChild(name);
    }
    
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: Add Render Code
    }
    
    /*@Override
    public void onAnimChange(AnimControl arg0, AnimChannel arg1, String arg2) {
        
    }
    
    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        
    }*/
    
    private void updateChannelSpeed(AnimChannel input){
        input.setSpeed(1/speed);
    }
}
