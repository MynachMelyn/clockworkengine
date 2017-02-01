/*
    Improve this to disable new inputs when a move is currently being executed
    Also, inputs that count toward the next move shouldn't be added while a move is being executed
    (You can't input the keys toward a complex move while executing a drawn out one)

    Fix the exploiting of the timing - it's still possible to execute another move during the current one
    But it's fairly hard to execute the exploit.

    Add interruptable moves (i.e. jab?)
*/

package clockworktest.input.combomoves;

import com.clockwork.app.SimpleApplication;
import com.clockwork.font.BitmapText;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.math.ColorRGBA;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TestComboMoves extends SimpleApplication implements ActionListener {

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

    public static void main(String[] args){
        TestComboMoves app = new TestComboMoves();
        app.start();
    }

    @Override
    public void simpleInitApp() {
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

        inputManager.addMapping("Left",    new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right",   new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Up",      new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Down",    new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("Attack1", new KeyTrigger(KeyInput.KEY_Z));
        inputManager.addMapping("Attack2", new KeyTrigger(KeyInput.KEY_X));
        inputManager.addMapping("Attack3", new KeyTrigger(KeyInput.KEY_C));
        inputManager.addMapping("AttackSpecial", new KeyTrigger(KeyInput.KEY_V));
        
        inputManager.addListener(this, "Left", "Right", "Up", "Down", "Attack1", "Attack2", "Attack3", "AttackSpecial");

        fireball = new ComboMove("Fireball");
        fireball.press("Down").notPress("Right").done();
        fireball.press("Right", "Down").done();
        fireball.press("Right").notPress("Down").done();
        fireball.press("Attack1").notPress("Down", "Right").done();
        fireball.notPress("Right", "Down", "Attack1").done();
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
        punch.notPress("Attack1").done();
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

    @Override
    public void simpleUpdate(float tpf){
        time += tpf;

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

    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed){
            pressedMappings.add(name);
        }else{
            pressedMappings.remove(name);
        }

        // the pressed mappings was changed. update combo executions
        List<ComboMove> invokedMoves = new ArrayList<ComboMove>();
        if (shurikenExec.updateState(pressedMappings, time)){
            invokedMoves.add(shuriken);
        }

        if (fireballExec.updateState(pressedMappings, time)){
            invokedMoves.add(fireball);
        }

        if (jabExec.updateState(pressedMappings, time)){
            invokedMoves.add(jab);
        }

        if (punchExec.updateState(pressedMappings, time)){
            invokedMoves.add(punch);
        }
        
        if (hookExec.updateState(pressedMappings, time)){
            invokedMoves.add(hook);
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
    }

}
