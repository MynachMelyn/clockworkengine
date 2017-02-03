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

public class FighterNode extends Node{
   
    /**
     * Will the player's inputs control the Fighter, or are they autonomous?<br>
     * Use for Stands.
     */
    protected boolean isPlayerControlled;
    
    protected enum currentAction{
        doingInterruptableAttack,
        doingUninterruptableAttack,
        staggered,
        downed,
        downedMidAir
    }
    
    protected ArrayList MoveSet;
    
    /**
     * The physics for the player. Not to be used for ghosts, only collision is needed for them.
     */
    protected BetterCharacterControl physicsController;
    
    protected AnimControl animControl;
    protected AnimChannel animChannel;
    
    public FighterNode(String name, boolean isPlayerControlled, Spatial model){
        super(name);
        this.isPlayerControlled = isPlayerControlled;
        animControl = model.getControl(AnimControl.class);
        animChannel = animControl.createChannel();
        
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
    }
}
