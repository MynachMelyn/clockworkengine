
package com.clockwork.cinematic.events;

import com.clockwork.animation.LoopMode;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.NullScreen;
import java.io.IOException;

/**
 *
 */
public class GuiEvent extends AbstractCinematicEvent {

    protected String screen;
    protected Nifty nifty;

    public GuiEvent() {
    }

    public GuiEvent(Nifty nifty, String screen) {
        this.screen = screen;
        this.nifty = nifty;
    }

    public GuiEvent(Nifty nifty, String screen, float initialDuration) {
        super(initialDuration);
        this.screen = screen;
        this.nifty = nifty;
    }

    public GuiEvent(Nifty nifty, String screen, LoopMode loopMode) {
        super(loopMode);
        this.screen = screen;
        this.nifty = nifty;
    }

    public GuiEvent(Nifty nifty, String screen, float initialDuration, LoopMode loopMode) {
        super(initialDuration, loopMode);
        this.screen = screen;
        this.nifty = nifty;
    }

    @Override
    public void onPlay() {
        System.out.println("screen should be " + screen);
        nifty.gotoScreen(screen);
    }

    @Override
    public void onStop() {        if (!(nifty.getCurrentScreen() instanceof NullScreen)) {
            nifty.getCurrentScreen().endScreen(null);
        }
    }

    @Override
    public void onPause() {
    }

    public void setNifty(Nifty nifty) {
        this.nifty = nifty;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    @Override
    public void onUpdate(float tpf) {
    }

    @Override
    public void write(CWExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(screen, "screen", "");
    }

    @Override
    public void read(CWImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        screen = ic.readString("screen", "");
    }
}
