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

public class TestCharacter extends SimpleApplication implements AnimEventListener, ActionListener {
    
    private BulletAppState bulletAppState;
    //private CharacterControl physicsCharacter;
    private BetterCharacterControl physicsCharacter;
    private Node characterNode;
    private Node standNode;
    private CameraNode camNode;
    boolean rotate = false;
    
    private final float speedFactorConst = 4f;
    private final float strafeFactorConst = 8f;
    
    private float speedFactor = speedFactorConst;
    private float strafeFactor = strafeFactorConst;
    
    private Vector3f walkDirection = new Vector3f(0,0,0);
    private Vector3f viewDirection = new Vector3f(0,0,0);
    boolean leftStrafe = false, rightStrafe = false, forward = false, backward = false,
            leftRotate = false, rightRotate = false, upRotate = false, downRotate = false;
    
    
    //public float speed_stand = 1.0f;
    
    public float defaultTPF = 0.0f;
    
    private Quaternion cameraToParentRotation = new Quaternion();
    
    public static float PIXELSMOVED_TO_RADIANSROTATED = 0.01f;
    
    private AnimChannel channel_player;
    private AnimControl control_player;
    
    private AnimChannel channel_stand;
    private AnimControl control_stand;
    
    private BetterChaseCamera chaseCam;
    
    private boolean timeSlowed;
    
    ArrayList rigidForces;
    ArrayList rigidAngularForces;
    
    
    private HashSet<String> pressedMappings = new HashSet<String>();
    
    private ComboMove fireball;
    private ComboMoveExecution fireballExec;
    private BitmapText fireballText;
    
    private ComboMove zawarudo;
    private ComboMoveExecution zawarudoExec;
    private BitmapText zawarudoText;
    
    private ComboMove shuriken;
    private ComboMoveExecution shurikenExec;
    private BitmapText shurikenText;
    
    private ComboMove jab;
    private ComboMoveExecution jabExec;
    private BitmapText jabText;
    
    private ComboMove hook;
    private ComboMoveExecution hookExec;
    private BitmapText hookText;
    
    private ComboMove heavyhit;
    private ComboMoveExecution heavyhitExec;
    private BitmapText heavyhitText;
    
    private ComboMove punch;
    private ComboMoveExecution punchExec;
    private BitmapText punchText;
    
    private ComboMove currentMove = null;
    private float currentMoveCastTime = 0;
    private float time = 0;
    
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
     * @param target - The parent of the camera.
     */
    private void setupChaseCamera(Node target) {
        flyCam.setEnabled(false);
        /*chaseCam = new BetterChaseCamera(cam, target, inputManager);
        chaseCam.setDragToRotate(false);
        chaseCam.setDefaultDistance(5.0f);
        inputManager.setCursorVisible(false);
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        chaseCam.setLookAtOffset(new Vector3f(0, 0.5f, 0));
        chaseCam.setTrailingEnabled(false);*/        
        //characterNode.getLocalRotation();        
        //cam.setRotation(new Quaternion(90,180,0,1));
        
        // set forward camera node that follows the character
        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(-5, 2, 0));
        camNode.lookAt(new Vector3f(characterNode.getLocalTranslation()).add(0, 1, 0), Vector3f.UNIT_Y);
        characterNode.attachChild(camNode);
        
        //target.addControl(camNode);
    }
    
    private void setupKeys() {
        /*inputManager.addMapping("MouseMoved",
        new MouseAxisTrigger(MouseInput.AXIS_X, false),
        new MouseAxisTrigger(MouseInput.AXIS_X, true)
        );*/
        
        inputManager.addMapping("Strafe Left",
                new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Strafe Right",
                new KeyTrigger(KeyInput.KEY_D));
        
        /*inputManager.addMapping("Rotate Right",
        new MouseAxisTrigger(MouseInput.AXIS_X, true)
        );
        inputManager.addMapping("Rotate Left",
        new MouseAxisTrigger(MouseInput.AXIS_X, false)
        );
        inputManager.addMapping("Rotate Up",
        new MouseAxisTrigger(MouseInput.AXIS_Y, true)
        );
        inputManager.addMapping("Rotate Down",
        new MouseAxisTrigger(MouseInput.AXIS_Y, false)
        );*/
        
        /*inputManager.addMapping("Rotate Right",
        new KeyTrigger(KeyInput.KEY_D),
        new KeyTrigger(KeyInput.KEY_RIGHT));*/
        inputManager.addMapping("Jump",
                new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Crouch",
                new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump",
                new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Shoot",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        
     
        // Light punch
        // Self-combo to make a barrage.
        inputManager.addMapping("Punch",
                new KeyTrigger(KeyInput.KEY_E));
        
        // Kick
        // Staggers Enemies.
        inputManager.addMapping("Kick",
                new KeyTrigger(KeyInput.KEY_R));
        
        // Special
        // Summon Stand. Also used in combos.
        inputManager.addMapping("Special",
                new KeyTrigger(KeyInput.KEY_Q));
        
        
        inputManager.addListener(this, "Strafe Left", "Strafe Right");
        inputManager.addListener(this, "Rotate Left", "Rotate Right");
        inputManager.addListener(this, "Rotate Down", "Rotate Up");
        inputManager.addListener(this, "Walk Forward", "Walk Backward");
        inputManager.addListener(this, "Jump", "Shoot");
        inputManager.addListener(this, "Punch", "Kick");
        inputManager.addListener(this, "Special");
        
        /*inputManager.addListener(new AnalogListener() {
        
        @Override
        public void onAnalog(String name, float value, float tpf) {
        inputManager.getCursorPosition();
        
        float centredX=inputManager.getCursorPosition().x-0.5f*settings.getWidth();
        
        Quaternion quat=new Quaternion();
        quat.fromAngles(0, PIXELSMOVED_TO_RADIANSROTATED * centredX, 0);
        
        
        cameraToParentRotation = quat;
        //geom.setLocalRotation(quat);
        
        }
        }, "MouseMoved");*/
    }
    @Override
    public void simpleInitApp() {
        // activate physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        // init a physical test scene
        PhysicsTestHelper.createPhysicsTestWorldSoccer(rootNode, assetManager, bulletAppState.getPhysicsSpace());
        //setupKeys();
        setupFightingGame();
        
        // Add a physics character to the world
        // physicsCharacter = new CharacterControl(new CapsuleCollisionShape(0.5f, 1.8f), .1f);
        physicsCharacter = new BetterCharacterControl(0.1f, 1f, 0.1f); // Radius, Height, Mass
        
        //physicsCharacter.setPhysicsLocation(new Vector3f(0, 1, 0));
        characterNode = new Node("character node");
        
        standNode = new Node("stand node");
        
        
        //Spatial model = assetManager.loadModel("Models/Sinbad/Sinbad.mesh.xml");
        Spatial model = assetManager.loadModel("/Blender/2.5x/export/BaseMesh_01.mesh.xml");
        Spatial stand_model = assetManager.loadModel("/Blender/2.5x/export/BaseMesh_01.mesh.xml");
        //model.center();
        
        Material guyMaterial = new Material(getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        //guyMaterial.getAdditionalRenderState().setWireframe(true);
        //guyMaterial.setColor("Color", ColorRGBA.Green);
        guyMaterial.setColor("Diffuse", ColorRGBA.Green);
        model.setMaterial(guyMaterial);
        
        Material standMaterial = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        standMaterial.getAdditionalRenderState().setWireframe(true);
        standMaterial.setColor("Color", ColorRGBA.Blue);
        stand_model.setMaterial(standMaterial);
        
        model.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        stand_model.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        ////
        
        /*BlenderKey blenderKey = new BlenderKey("Blender/2.5x/BaseMesh_256.blend");
        Spatial scene = (Spatial) assetManager.loadModel(blenderKey);
        rootNode.attachChild(scene);
        Spatial model = this.findNode(rootNode, "BaseMesh_01");
        model.center();
        control = model.getControl(AnimControl.class);
        channel = control.createChannel();
        channel.setAnim("run_01"); */
        
        // Stand User
        
        control_player = model.getControl(AnimControl.class);
        control_player.addListener(this);
        channel_player = control_player.createChannel();
        
        for (String anim : control_player.getAnimationNames()){
            System.out.println(anim);
        }
        
        channel_player.setAnim("base_stand");
        //geom = (Geometry)((Node)model).getChild(0);
        //SkeletonControl skeletonControl = model.getControl(SkeletonControl.class);
        ////
        
        // Stand
        control_stand = stand_model.getControl(AnimControl.class);
        control_stand.addListener(this);
        channel_stand = control_stand.createChannel();
        
        for (String anim : control_stand.getAnimationNames()){
            System.out.println(anim);
        }
        
        channel_stand.setAnim("ghost_float");
        SkeletonControl skeletonControl_stand = stand_model.getControl(SkeletonControl.class);
        
        // STAND USER
        characterNode.addControl(physicsCharacter);
        getPhysicsSpace().add(physicsCharacter); //TODO
        rootNode.attachChild(characterNode); //TODO
        characterNode.attachChild(model);
        physicsCharacter.warp(new Vector3f(0, 2, 0));
        
        
        // STAND
        //standNode.addControl(skeletonControl_stand);
        
        //getPhysicsSpace().add(physicsCharacter);
        //rootNode.attachChild(standNode);
        //stand_model.setLocalTranslation(new Vector3f(0.6f, 0.5f, -0.75f));
        stand_model.setLocalTranslation(new Vector3f(0.4f, 0.5f, -0.55f));
        characterNode.attachChild(standNode);
        standNode.attachChild(stand_model);
        //physicsCharacter.warp(new Vector3f(0, 2, 0));
        
        //Now chasecam
        //camNode = new CameraNode()
        
        
        //disable the default 1st-person flyCam (don't forget this)
        flyCam.setEnabled(false);
        
        setupChaseCamera(characterNode);
        
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
        
        rigidForces = new ArrayList<Vector3f>();
        rigidAngularForces = new ArrayList<Vector3f>();
    }
    
    /**
     * Key List:
     * (Some are dependent on facing direction)
     * W - Up/Jump
     * A - Left
     * D - Right
     * S - Crouch
     * J - Light Attack
     * I - Medium Attack
     * L - Hard Attack
     * K - Special
     */
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
        
        fireballText = new BitmapText(guiFont);
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
        guiNode.attachChild(zawarudoText);
        
        inputManager.addMapping("Left",    new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right",   new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up",      new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down",    new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Attack1", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Attack2", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("Attack3", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("AttackSpecial", new KeyTrigger(KeyInput.KEY_K));
        
        inputManager.addListener(this, "Left", "Right", "Up", "Down", "Attack1", "Attack2", "Attack3", "AttackSpecial");
        
        fireball = new ComboMove("Fireball");
        fireball.press("Down").notPress("Right").done();
        fireball.press("Right", "Down").done();
        fireball.press("Right").notPress("Down").done();
        fireball.press("Attack1").notPress("Down", "Right").done();
        //fireball.notPress("Right", "Down", "Attack1").done();
        fireball.setUseFinalState(false); // no waiting on final state
        
        shuriken = new ComboMove("Shuriken");
        shuriken.press("Left").notPress("Down", "Attack1").done();
        shuriken.press("Down").notPress("Attack1").timeElapsed(0.11f).done();
        shuriken.press("Attack1").notPress("Left").timeElapsed(0.11f).done();
        shuriken.notPress("Left", "Down", "Attack1").done();
        
        jab = new ComboMove("Jab");
        jab.setPriority(0.5f); // Jab is less important, to allow other moves to execute
        jab.press("Attack1").done();
        
        hook = new ComboMove("Hook");
        hook.setPriority(0.5f); // Hook is less important, to allow other moves to execute
        hook.press("Attack2").done();
        
        heavyhit = new ComboMove("Heavy Hit");
        heavyhit.setPriority(0.5f); // Heavy is less important, to allow other moves to execute
        heavyhit.press("Attack3").done();
        
        punch = new ComboMove("Punch");
        punch.press("Attack1").done();
        //punch.notPress("Attack1").done();
        punch.press("Attack1").done();
        
        zawarudo = new ComboMove("Time Stop!");
        zawarudo.press("Right").notPress("Attack3", "Attack1").done();
        zawarudo.press("Attack3").notPress("Attack1", "Right").timeElapsed(0.11f).done();
        zawarudo.press("Attack1").notPress("Right", "Attack3").timeElapsed(0.11f).done();
        zawarudo.press("Right").notPress("AttackSpecial").timeElapsed(0.11f).done();
        zawarudo.press("AttackSpecial").notPress("Right", "Attack1", "Attack3").timeElapsed(0.11f).done();
        zawarudo.notPress("Left", "Down", "Attack1").done();
        
        fireballExec = new ComboMoveExecution(fireball);
        shurikenExec = new ComboMoveExecution(shuriken);
        jabExec = new ComboMoveExecution(jab);
        punchExec = new ComboMoveExecution(punch);
        hookExec = new ComboMoveExecution(hook);
        heavyhitExec = new ComboMoveExecution(heavyhit);
        zawarudoExec = new ComboMoveExecution(zawarudo);
    }
    
    private void checkForCombos(float time, float tpf){
                // check every frame if any executions are expired
        shurikenExec.updateExpiration(time);
        shurikenText.setText("Shuriken Exec: " + shurikenExec.getDebugString());

        fireballExec.updateExpiration(time);
        fireballText.setText("Fireball Exec: " + fireballExec.getDebugString());

        jabExec.updateExpiration(time);
        jabText.setText("Jab Exec: " + jabExec.getDebugString());
        
        hookExec.updateExpiration(time);
        hookText.setText("Hook Exec: " + hookExec.getDebugString());
        
        heavyhitExec.updateExpiration(time);
        heavyhitText.setText("Heavy Exec: " + heavyhitExec.getDebugString());

        punchExec.updateExpiration(time);
        punchText.setText("Punch Exec: " + punchExec.getDebugString());
        
        zawarudoExec.updateExpiration(time);
        zawarudoText.setText("Time Stop Exec: " + zawarudoExec.getDebugString());

        if (currentMove != null){
            currentMoveCastTime -= tpf;
            if (currentMoveCastTime <= 0){
                System.out.println("DONE CASTING " + currentMove.getMoveName());
                currentMoveCastTime = 0;
                currentMove = null;
            }
        }
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        time += tpf;
        
        checkForCombos(time, tpf);
        
        strafeFactor = strafeFactorConst / speed;
        speedFactor = speedFactorConst / speed;
        
        Vector3f camDir = cam.getDirection().mult(0.2f);
        Vector3f camLeft = cam.getLeft().mult(0.2f);
        camDir.y = 0;
        camDir.normalizeLocal();
        camLeft.y = 0;
        walkDirection.set(0, 0, 0);
        
        if (leftStrafe) {
            walkDirection.addLocal(new Vector3f(camLeft).mult(strafeFactor));
        } else if (rightStrafe) {
            walkDirection.addLocal(new Vector3f(camLeft.negate()).mult(strafeFactor));
        }
        /*if (leftRotate) {
        viewDirection.addLocal(camLeft.mult(0.02f));
        } else if (rightRotate) {
        viewDirection.addLocal(camLeft.mult(0.02f).negate());
        }
        
        if (upRotate) {
        viewDirection.addLocal(camUp.mult(0.02f));
        } else if (downRotate) {
        viewDirection.addLocal(camUp.mult(0.02f).negate());
        }
        */
        //Vector3f temp_look = new Vector3f(characterNode.getLocalRotation().);
        
        Vector3f temp_look = new Vector3f(0, 0, 1);
        
        if (forward) {
            walkDirection.addLocal(new Vector3f(temp_look).mult(speedFactor));
        } else if (backward) {
            walkDirection.addLocal(new Vector3f(temp_look.negate()).mult(speedFactor));
        }
        if(walkDirection.equals(Vector3f.ZERO)){
            if (!channel_player.getAnimationName().equals("base_stand")){
                channel_player.setAnim("base_stand", 0.50f);
                channel_player.setLoopMode(LoopMode.Loop);
                updateChannelSpeed(channel_player);
            }
        }
        //physicsCharacter.setWalkDirection(walkDirection.mult(speedFactor));
        
        physicsCharacter.setWalkDirection(walkDirection);
       // physicsCharacter.setViewDirection(viewDirection);
        
        if(channel_player.getAnimationName().equals("jump")){
            if(physicsCharacter.isOnGround() && channel_player.getTime() >= channel_player.getAnimMaxTime()/2 ){
                if(physicsCharacter.getWalkDirection().x != 0 || physicsCharacter.getWalkDirection().z != 0){
                    channel_player.setAnim("run_01", 0.50f);
                    channel_player.setLoopMode(LoopMode.Loop);
                    updateChannelSpeed(channel_player);
                }
                else{
                    channel_player.setAnim("base_stand", 0.50f);
                    channel_player.setLoopMode(LoopMode.DontLoop);
                    updateChannelSpeed(channel_player);
                }
            }
        }
        
        //System.out.println(characterNode.getNumControls());
        
        if(walkDirection.length() != 0){
            viewDirection.set(camDir);
        }
    }
    
    @Override
    public void onAction(String binding, boolean value, float tpf) {
        
        
        //if not moving
        /*if (!channel.getAnimationName().equals("base_stand")){
        channel.setAnim("base_stand", 0.50f);
        channel.setLoopMode(LoopMode.Loop);
        updateChannelSpeed(channel);
        }*/
        if (value){
            pressedMappings.add(binding);
        }else{
            pressedMappings.remove(binding);
        }

        // The pressed mappings were changed. Update the combo executions.
        List<ComboMove> invokedMoves = new ArrayList<ComboMove>();
        if (shurikenExec.updateState(pressedMappings, time)){
            invokedMoves.add(shuriken);
        }

        if (fireballExec.updateState(pressedMappings, time)){
            invokedMoves.add(fireball);
            //if (!channel_stand.getAnimationName().equals("ghost_barrage")){
                channel_stand.setAnim("ghost_barrage", 0.25f);
                channel_stand.setLoopMode(LoopMode.DontLoop);
                updateChannelSpeed(channel_stand);
            //}
        }

        if (jabExec.updateState(pressedMappings, time)){
            invokedMoves.add(jab);
        }

        if (punchExec.updateState(pressedMappings, time)){
            invokedMoves.add(punch);
        }
        
        if (hookExec.updateState(pressedMappings, time)){
            invokedMoves.add(hook);
            //if (!channel_stand.getAnimationName().equals("ghost_punch")){
                channel_stand.setAnim("ghost_punch", 0.25f);
                channel_stand.setLoopMode(LoopMode.DontLoop);
                updateChannelSpeed(channel_stand);
            //}
        }
        
        if (heavyhitExec.updateState(pressedMappings, time)){
            invokedMoves.add(heavyhit);
        }
        
        if (zawarudoExec.updateState(pressedMappings, time)){
            invokedMoves.add(zawarudo);
        }

        if (invokedMoves.size() > 0){
            // choose move with highest priority
            float priority = 0;
            ComboMove toExec = null;
            for (ComboMove move : invokedMoves){
                if (move.getPriority() > priority){
                    priority = move.getPriority();
                    toExec = move;
                }
            }
            if (currentMove != null && currentMove.getPriority() > toExec.getPriority()){
                return;
            }

            currentMove = toExec;
            currentMoveCastTime = currentMove.getCastTime();
        }
        
//################################
        
        
        // May have to use Cycle instead of Loop?
        /*
        if (binding.equals("Strafe Left")) {
            if (value) {
                leftStrafe = true;
                if (!channel.getAnimationName().equals("run_sideway_left")){
                    channel.setAnim("run_sideway_left", 0.50f);
                    channel.setLoopMode(LoopMode.Loop);
                    updateChannelSpeed(channel);
                }
            } else {
                leftStrafe = false;
            }
        } else if (binding.equals("Strafe Right")) {
            if (value) {
                rightStrafe = true;
                if (!channel.getAnimationName().equals("run_sideway_right")){
                    channel.setAnim("run_sideway_right", 0.50f);
                    channel.setLoopMode(LoopMode.Loop);
                    updateChannelSpeed(channel);
                }
            } else {
                rightStrafe = false;
            }
        } else if (binding.equals("Rotate Left")) {
            if (value) {
                leftRotate = true;
            } else {
                leftRotate = false;
            }
        } else if (binding.equals("Rotate Right")) {
            if (value) {
                rightRotate = true;
            } else {
                rightRotate = false;
            }
        }
        else if (binding.equals("Rotate Up")) {
            if (value) {
                upRotate = true;
            } else {
                upRotate = false;
            }
        }
        else if (binding.equals("Rotate Down")) {
            if (value) {
                downRotate = true;
            } else {
                downRotate = false;
            }
        }
        else*/ if (binding.equals("Right")) {
            if (value) {
                forward = true;
                if (!channel_player.getAnimationName().equals("run_01")){
                    channel_player.setAnim("run_01", 0.50f);
                    channel_player.setLoopMode(LoopMode.Loop);
                    updateChannelSpeed(channel_player);
                }
            } else {
                forward = false;
            }
        } else if (binding.equals("Left")) {
            if (value) {
                backward = true;
                if (!channel_player.getAnimationName().equals("run_01")){
                    channel_player.setAnim("run_01", 0.50f);
                    channel_player.setLoopMode(LoopMode.Loop);
                    updateChannelSpeed(channel_player);
                    channel_player.setSpeed(1);
                }
            } else {
                backward = false;
            }
        } else if (binding.equals("Up")) {
            if (!channel_player.getAnimationName().equals("jump") && value){
                channel_player.setAnim("jump", 0.05f);
                //channel.setLoopMode(LoopMode.Loop);
                channel_player.setLoopMode(LoopMode.DontLoop);
                updateChannelSpeed(channel_player);
            }
            physicsCharacter.jump();
        }
    }
    
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
    
    private void SetSlowMotion(float timescale){
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
    }
    
    private void ResumeTime(){
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
    }
    
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
    
    public void TheWorld(){
        /*if(this.speed > 0.1f){
        this.speed = 0.0001f;
        }
        else{
        this.speed = 1f;
        }*/
    }
    public void Dora(){
        
    }
    public void KillerQueen(){
        
    }
    
    
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    @Override
    public void onAnimChange(AnimControl arg0, AnimChannel arg1, String arg2) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        /*if (animName.equals("jump")){
        channel.setAnim("base_stand", 0.50f);
        channel.setLoopMode(LoopMode.DontLoop);
        updateChannelSpeed(channel);
        }*/
        if (animName.equals("ghost_punch") || animName.equals("ghost_barrage")){
            channel.setAnim("ghost_float", 0.50f);
            channel.setLoopMode(LoopMode.Loop);
            updateChannelSpeed(channel);
        }
    }
    
    private void updateChannelSpeed(AnimChannel input){
        input.setSpeed(1/speed);
        //input.setSpeed(1);
    }
}
