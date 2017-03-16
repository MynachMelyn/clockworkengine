
package com.clockwork.cinematic;

import com.clockwork.cinematic.events.CinematicEvent;
import com.clockwork.export.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class KeyFrame implements Savable {

    List<CinematicEvent> cinematicEvents = new ArrayList<CinematicEvent>();
    private int index;

    public List<CinematicEvent> getCinematicEvents() {
        return cinematicEvents;
    }

    public void setCinematicEvents(List<CinematicEvent> cinematicEvents) {
        this.cinematicEvents = cinematicEvents;
    }

    public List<CinematicEvent> trigger() {
        for (CinematicEvent event : cinematicEvents) {
            event.play();
        }
        return cinematicEvents;
    }
    
    public boolean isEmpty(){
        return cinematicEvents.isEmpty();
    }

    public void write(CWExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.writeSavableArrayList((ArrayList) cinematicEvents, "cinematicEvents", null);
        oc.write(index, "index", 0);
    }

    public void read(CWImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        cinematicEvents = ic.readSavableArrayList("cinematicEvents", null);
        index=ic.readInt("index", 0);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


}
