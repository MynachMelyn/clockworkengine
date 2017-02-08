package com.clockwork.game;

import com.clockwork.animation.*;
import com.clockwork.scene.*;

public class StandNode extends FighterNode{
    
    protected FighterNode standUser = null;
    
    public StandNode(String name, Spatial model, FighterNode user) {
        super(name, false, model, null);
        standUser = user;
        animChannel.setAnim("ghost_float");
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        
    }
    
    public void setUser(StandNode stand){
        standUser = stand;
    }
    
    public FighterNode getUser(){
        return standUser;
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if(animChannel.getLoopMode() == LoopMode.DontLoop){
            // When the Stand is done doing a non-looped animation (almost always an attack),
            // force the user into the default standing animation for idling, thus ending the attack.
            setAnimation("ghost_float", 0.5f, true);
            standUser.setAnimation("base_stand", 0.15f, true);
            standUser.fighterState = FighterNode.currentAction.idle;
        }
    }

    @Override
    public void onAnimChange(AnimControl control,AnimChannel channel, String animName) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
