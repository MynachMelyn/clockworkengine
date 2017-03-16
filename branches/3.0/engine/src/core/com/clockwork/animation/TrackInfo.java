
package com.clockwork.animation;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.export.Savable;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is intended as a UserData added to a Spatial that is referenced by a Track.
 * (ParticleEmitter for EffectTrack and AudioNode for AudioTrack)
 * It holds the list of tracks that are directly referencing the Spatial.
 * 
 * This is used when loading a Track to find the cloned reference of a Spatial in the cloned model returned by the assetManager.
 *
 */
public class TrackInfo implements Savable {

    ArrayList<Track> tracks = new ArrayList<Track>();

    public TrackInfo() {
    }

    public void write(CWExporter ex) throws IOException {
        OutputCapsule c = ex.getCapsule(this);
        c.writeSavableArrayList(tracks, "tracks", null);
    }

    public void read(CWImporter im) throws IOException {
        InputCapsule c = im.getCapsule(this);
        tracks = c.readSavableArrayList("tracks", null);
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public void addTrack(Track track) {
        tracks.add(track);
    }
}
