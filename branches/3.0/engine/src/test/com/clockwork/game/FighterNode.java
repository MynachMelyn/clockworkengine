package com.clockwork.game;

import clockworktest.input.combomoves.ComboMove;
import com.clockwork.animation.AnimChannel;
import com.clockwork.animation.AnimControl;
import com.clockwork.bullet.control.BetterCharacterControl;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import java.util.ArrayList;
import clockworktest.input.combomoves.ComboMove.moveTypeList;
import clockworktest.input.combomoves.ComboMoveExecution;
import com.clockwork.input.InputManager;
import com.clockwork.animation.AnimEventListener;
import com.clockwork.input.controls.ActionListener;
import java.util.HashSet;
import com.clockwork.math.Vector3f;

public class FighterNode extends Node implements AnimEventListener, ActionListener{
   
    /**
     * Will the player's inputs control the Fighter, or are they autonomous?<br>
     * Use for Stands.<br>
     * Stands should not be controlled as players are. They should be Transform parented, have their animations set in parallel to the player parent.<br>
     * Stands should not have the same interruptables/uninterruptables as a player, and instead the player's current move should decide it.<br>
     * Do this by setting the Stand's move/animation at the same time as the player's. The attack hit-box can then be put on the Stand's hand.
     */
    protected boolean isPlayerControlled;

    protected enum currentAction{
        idle,                       // Standing still
        moving,                     // Moving left/right
        crouched,                   // Crouched on ground
        midAir,                     // In the air
        doingInterruptableAttack,   // Currently doing an attack that cannot be cancelled
        doingUninterruptableAttack, // Currently doing an attack that can be cancelled into another attack or a movement.
        staggered,                  // Being hit, or shortly after being hit - cannot do anything during this.
        downed,                     // On ground - cannot do anything or be attacked in this state.
        downedMidAir                // Cannot recover until contact with ground - unlike downed, can still be hit in this state.
    }
    
    protected currentAction fighterState;
    
    protected ArrayList MoveSet;
    
    
    /**
     * Hash the key inputs for high-speed access - as little delay as is possible.
     */
    private HashSet<String> pressedMappings = new java.util.HashSet<String>();
    
  
   /**
     * The physics for the player. Not to be used for ghosts, only collision is needed for them.
     */
    protected BetterCharacterControl physicsController;
    
    // Base versions of movement values - cannot be edited.
    // To actively and temporarily change a fighter's speed, use the next set of variables. *
    private final float movementSpeedBase = 10.0f;
    private final float jumpForceBase = 10.0f;
    // * These are referenced directly when moving/jumping etc.
    // Modify these to temporarily change the player's speed.
    // To reset them back to their defaults, just set them to the variables above.
    private float movementSpeed;
    private float jumpForce;
    
    private Vector3f walkDirection;
    private boolean facingRight;
    
    private boolean forward = false, backward = false;    
    
    protected AnimControl animControl;
    protected AnimChannel animChannel;
    protected InputManager inputManager;

    ComboMove lightAttack,  // Light jab - spammable
            mediumAttack,   // Hook - slower, more powerful
            heavyAttack,    // Heavy swing - much slower, a lot more powerful. Leaves player vulnerable.
            upperCut,       // Heavy upward punch, quick but with a lot of cooldown time afterward in comparison to heavy attack. Will knock enemy airborne and downed, breaks guard.
            punchBarrage,   // Continuous barrage of punches from the Stand. Leaves player vulnerable, as it's uncancellable. High damage, decent duration, knocks airborne, ignores guard, constant stagger.
            timeStop;       // Slow cast time, but stops time. Might be replaced with a ranged powerful attack, i.e. explosion.

    ComboMoveExecution lightAttackExec,
            mediumAttackExec,
            heavyAttackExec,
            upperCutExec,
            punchBarrageExec,
            timeStopExec;
    
    private ComboMove currentMove = null;
    private float currentMoveCastTime = 0;
    private float time = 0;
    
    public FighterNode(String name, boolean isPlayerControlled, Spatial model, InputManager inputManager){
        super(name);
        
        fighterState = currentAction.idle;
        
        this.movementSpeed = movementSpeedBase;
        this.jumpForce = jumpForceBase;
        this.facingRight = true;
        
        this.isPlayerControlled = isPlayerControlled;
        animControl = model.getControl(AnimControl.class);
        animChannel = animControl.createChannel();
        
        // If an Input Manager was not given during construction, then
        // assume this isn't a player character.
        if(inputManager == null){
            this.isPlayerControlled = false;
        }
        
        // Add a capsule-surrounded physics character controller to the scene node.
        if(this.isPlayerControlled){
            physicsController = new BetterCharacterControl(0.1f, 1f, 0.1f);
        }
        
    }
    
    // Called from main App's SimpleUpdate
    public void simpleUpdate(){
        if (forward) {
            walkDirection.addLocal(new Vector3f(0,0,(facingRight) ? 1 : 0).mult(movementSpeed));
        } else if (backward) {
            walkDirection.addLocal(new Vector3f(0,0,(!facingRight) ? 1 : 0).mult(movementSpeed));
        }
        
        checkFighterState();
        if(isPlayerControlled){
            physicsController = new BetterCharacterControl(0.1f, 1f, 0.1f);
        }
    }
    
    public void generateMoveList(){
        //For now, using generic list
        defaultMoveSet();
    }
    
    private void defaultMoveSet(){
        MoveSet = new ArrayList<ComboMove>();
        ComboMove lightAttack, mediumAttack, heavyAttack, upperCut, punchBarrage, timeStop;       
        ComboMoveExecution lightAttackExec, mediumAttackExec, heavyAttackExec, upperCutExec, punchBarrageExec, timeStopExec;  
        
        lightAttack = new ComboMove("Light Attack");
        mediumAttack = new ComboMove("Medium Attack");
        heavyAttack = new ComboMove("Heavy Attack");
        upperCut = new ComboMove("Uppercut");
        punchBarrage = new ComboMove("Punch Barrage");
        timeStop = new ComboMove("Time Stop");
        lightAttack.setMoveType(moveTypeList.interruptable);
        mediumAttack.setMoveType(moveTypeList.interruptable);
        heavyAttack.setMoveType(moveTypeList.uninterruptable);
        upperCut.setMoveType(moveTypeList.uninterruptable);
        punchBarrage.setMoveType(moveTypeList.uninterruptable);
        timeStop.setMoveType(moveTypeList.uninterruptable);
        
        punchBarrage.press("Down").notPress("Right").done();
        punchBarrage.press("Right", "Down").done();
        punchBarrage.press("Right").notPress("Down").done();
        punchBarrage.press("Attack1").notPress("Down", "Right").done();
        punchBarrage.notPress("Right", "Down").done();
        //punchBarrage.notPress("Right", "Down", "Attack1").done();
        punchBarrage.setUseFinalState(false); // no waiting on final state
        
        upperCut.press("Left").notPress("Down", "Attack1").done();
        upperCut.press("Down").notPress("Attack1").timeElapsed(0.11f).done();
        upperCut.press("Attack1").notPress("Left").timeElapsed(0.11f).done();
        upperCut.notPress("Left", "Down", "Attack1").done();
        
        timeStop.press("Right").notPress("Attack3", "Attack1").done();
        timeStop.press("Attack3").notPress("Attack1", "Right").timeElapsed(0.11f).done();
        timeStop.press("Attack1").notPress("Right", "Attack3").timeElapsed(0.11f).done();
        timeStop.press("Right").notPress("AttackSpecial").timeElapsed(0.11f).done();
        timeStop.press("AttackSpecial").notPress("Right", "Attack1", "Attack3").timeElapsed(0.11f).done();
        timeStop.notPress("Left", "Down", "Attack1").done();
        
        lightAttackExec = new ComboMoveExecution(lightAttack);
        mediumAttackExec = new ComboMoveExecution(mediumAttack);
        heavyAttackExec = new ComboMoveExecution(heavyAttack);
        timeStopExec = new ComboMoveExecution(timeStop);
        punchBarrageExec = new ComboMoveExecution(punchBarrage);
        upperCutExec = new ComboMoveExecution(upperCut);
       
        /*
        Key List:
        W A S D - Jump, Left, Crouch, Right
        J, I, L - Light, Medium, Heavy Attacks
        K - Special
        
        String together in the predetermined ways to pull off combos.
        */
        
        inputManager.addMapping("Left",    new com.clockwork.input.controls.KeyTrigger(com.clockwork.input.KeyInput.KEY_A));
        inputManager.addMapping("Right",   new com.clockwork.input.controls.KeyTrigger(com.clockwork.input.KeyInput.KEY_D));
        inputManager.addMapping("Up",      new com.clockwork.input.controls.KeyTrigger(com.clockwork.input.KeyInput.KEY_W));
        inputManager.addMapping("Down",    new com.clockwork.input.controls.KeyTrigger(com.clockwork.input.KeyInput.KEY_S));
        inputManager.addMapping("Attack1", new com.clockwork.input.controls.KeyTrigger(com.clockwork.input.KeyInput.KEY_J));
        inputManager.addMapping("Attack2", new com.clockwork.input.controls.KeyTrigger(com.clockwork.input.KeyInput.KEY_I));
        inputManager.addMapping("Attack3", new com.clockwork.input.controls.KeyTrigger(com.clockwork.input.KeyInput.KEY_L));
        inputManager.addMapping("AttackSpecial", new com.clockwork.input.controls.KeyTrigger(com.clockwork.input.KeyInput.KEY_K));
        
        inputManager.addListener(this, "Left", "Right", "Up", "Down", "Attack1", "Attack2", "Attack3", "AttackSpecial");
    }
    
    private void checkForCombos(float time, float tpf) {
        // Check every frame if any executions are expired
        lightAttackExec.updateExpiration(time);
        //lightAttackText.setText("Shuriken Exec: " + lightAttack.getDebugString());
        
        mediumAttackExec.updateExpiration(time);
        //lightAttackText.setText("Shuriken Exec: " + lightAttack.getDebugString());

        heavyAttackExec.updateExpiration(time);
        //lightAttackText.setText("Shuriken Exec: " + lightAttack.getDebugString());

        upperCutExec.updateExpiration(time);
        //lightAttackText.setText("Shuriken Exec: " + lightAttack.getDebugString());

        punchBarrageExec.updateExpiration(time);
        //lightAttackText.setText("Shuriken Exec: " + lightAttack.getDebugString());

        timeStopExec.updateExpiration(time);
        //lightAttackText.setText("Shuriken Exec: " + lightAttack.getDebugString());

        if (currentMove != null) {
            currentMoveCastTime -= tpf;
            if (currentMoveCastTime <= 0) {
                System.out.println("DONE CASTING " + currentMove.getMoveName());
                currentMoveCastTime = 0;
                currentMove = null;
            }
        }
    }
    
    private void checkFighterState(){
        // If grounded
        if (physicsController.isOnGround()) {
            // If stationary.
            if (walkDirection.equals(Vector3f.ZERO)) {
                fighterState = currentAction.idle;
            } // If not stationary
            else {
                // If moving forward, relative to facing direction
                if (forward) {
                    fighterState = currentAction.moving;
                    //ANIMATION
                } // If moving backward, relative to facing direction
                else if (backward) {
                    fighterState = currentAction.moving;
                    //ANIMATION
                }
            }
        } 
        // If airborne
        else {
               
        }
    }
    
    @Override
    public void onAction(String binding, boolean value, float tpf) {
        
        if (value){
            pressedMappings.add(binding);
        }else{
            pressedMappings.remove(binding);
        }

        // The pressed mappings were changed. Update the combo executions.
        java.util.List<ComboMove> invokedMoves = new ArrayList<ComboMove>();
        
        if (lightAttackExec.updateState(pressedMappings, time)){
            invokedMoves.add(lightAttack);
        }
        
        if (mediumAttackExec.updateState(pressedMappings, time)){
            invokedMoves.add(mediumAttack);
        }
        
        if (heavyAttackExec.updateState(pressedMappings, time)){
            invokedMoves.add(heavyAttack);
        }

        if (punchBarrageExec.updateState(pressedMappings, time)){
            invokedMoves.add(punchBarrage);
        }
        
        if (upperCutExec.updateState(pressedMappings, time)){
            invokedMoves.add(upperCut);
        }
        
        if (timeStopExec.updateState(pressedMappings, time)){
            invokedMoves.add(timeStop);
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
        if (binding.equals("Right")) {
            if (value) {
                forward = true;
            } else {
                forward = false;
            }
        } else if (binding.equals("Left")) {
            if (value) {
                backward = true;
            } else {
                backward = false;
            }
        } else if (binding.equals("Up")) {
            physicsController.jump();
        }
    }
    
    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
