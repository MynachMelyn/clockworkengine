
package com.clockwork.animation;

import com.clockwork.export.*;
import com.clockwork.scene.Spatial;
import com.clockwork.util.SafeArrayList;
import com.clockwork.util.TempVars;
import java.io.IOException;

/**
 * The animation class updates the animation target with the tracks of a given type.
 * 
 */
public class Animation implements Savable, Cloneable {

    /** 
     * The name of the animation. 
     */
    private String name;
    /** 
     * The length of the animation. 
     */
    private float length;
    /** 
     * The tracks of the animation. 
     */
    private SafeArrayList<Track> tracks = new SafeArrayList<Track>(Track.class);

    /**
     * Serialisation-only. Do not use.
     */
    public Animation() {
    }

    /**
     * Creates a new <code>Animation</code> with the given name and length.
     * 
     * @param name The name of the animation.
     * @param length Length in seconds of the animation.
     */
    public Animation(String name, float length) {
        this.name = name;
        this.length = length;
    }

    /**
     * The name of the bone animation
     * @return name of the bone animation
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the length in seconds of this animation
     * 
     * @return the length in seconds of this animation
     */
    public float getLength() {
        return length;
    }

    /**
     * This method sets the current time of the animation.
     * This method behaves differently for every known track type.
     * Override this method if you have your own type of track.
     * 
     * @param time the time of the animation
     * @param blendAmount the blend amount factor
     * @param control the animation control
     * @param channel the animation channel
     */
    void setTime(float time, float blendAmount, AnimControl control, AnimChannel channel, TempVars vars) {
        if (tracks == null) {
            return;
        }

        for (Track track : tracks) {
            track.setTime(time, blendAmount, control, channel, vars);
        }
    }

    /**
     * Set the {@link Track}s to be used by this animation.
     * 
     * @param tracksArray The tracks to set.
     */
    public void setTracks(Track[] tracksArray) {
        for (Track track : tracksArray) {
            tracks.add(track);
        }
    }

    /**
     * Adds a track to this animation
     * @param track the track to add
     */
    public void addTrack(Track track) {
        tracks.add(track);
    }

    /**
     * removes a track from this animation
     * @param track the track to remove
     */
    public void removeTrack(Track track) {
        tracks.remove(track);
        if (track instanceof ClonableTrack) {
            ((ClonableTrack) track).cleanUp();
        }
    }

    /**
     * Returns the tracks set in {@link #setTracks(com.clockwork.animation.Track[]) }.
     * 
     * @return the tracks set previously
     */
    public Track[] getTracks() {
        return tracks.getArray();
    }

    /**
     * This method creates a clone of the current object.
     * @return a clone of the current object
     */
    @Override
    public Animation clone() {
        try {
            Animation result = (Animation) super.clone();
            result.tracks = new SafeArrayList<Track>(Track.class);
            for (Track track : tracks) {
                result.tracks.add(track.clone());
            }
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * 
     * @param spat
     * @return 
     */
    public Animation cloneForSpatial(Spatial spat) {
        try {
            Animation result = (Animation) super.clone();
            result.tracks = new SafeArrayList<Track>(Track.class);
            for (Track track : tracks) {
                if (track instanceof ClonableTrack) {
                    result.tracks.add(((ClonableTrack) track).cloneForSpatial(spat));
                } else {
                    result.tracks.add(track);
                }
            }
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name=" + name + ", length=" + length + ']';
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(name, "name", null);
        out.write(length, "length", 0f);
        out.write(tracks.getArray(), "tracks", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        name = in.readString("name", null);
        length = in.readFloat("length", 0f);

        Savable[] arr = in.readSavableArray("tracks", null);
        if (arr != null) {
            // NOTE: Backward compat only .. Some animations have no
            // tracks set at all even though it makes no sense.
            // Since there's a null check in setTime(),
            // its only appropriate that the check is made here as well.
            tracks = new SafeArrayList<Track>(Track.class);
            for (Savable savable : arr) {
                tracks.add((Track) savable);
            }
        }
    }
}
