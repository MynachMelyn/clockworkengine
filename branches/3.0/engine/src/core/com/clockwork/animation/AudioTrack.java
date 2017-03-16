
package com.clockwork.animation;

import com.clockwork.audio.AudioNode;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.util.TempVars;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AudioTrack is a track to add to an existing animation, to paly a sound during
 * an animations for example : gun shot, foot step, shout, etc...
 *
 * usage is
 * 
 * AnimControl control model.getControl(AnimControl.class);
 * AudioTrack track = new AudioTrack(existionAudioNode, control.getAnim("TheAnim").getLength());
 * control.getAnim("TheAnim").addTrack(track);
 * 
 *
 * This is mostly intended for short sounds, playInstance will be called on the
 * AudioNode at time 0 + startOffset.
 *
 *
 */
public class AudioTrack implements ClonableTrack {

    private static final Logger logger = Logger.getLogger(AudioTrack.class.getName());
    private AudioNode audio;
    private float startOffset = 0;
    private float length = 0;
    private boolean initialized = false;
    private boolean started = false;
    private boolean played = false;

    //Animation listener to stop the sound when the animation ends or is changed
    private class OnEndListener implements AnimEventListener {

        public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
            stop();
        }

        public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        }
    }

    /**
     * default constructor for serialisation only
     */
    public AudioTrack() {
    }

    /**
     * Creates an AudioTrack
     *
     * @param audio the AudioNode
     * @param length the length of the track (usually the length of the
     * animation you want to add the track to)
     */
    public AudioTrack(AudioNode audio, float length) {
        this.audio = audio;
        this.length = length;
        setUserData(this);
    }

    /**
     * Creates an AudioTrack
     *
     * @param audio the AudioNode
     * @param length the length of the track (usually the length of the
     * animation you want to add the track to)
     * @param startOffset the time in second when the sound will be played after
     * the animation starts (default is 0)
     */
    public AudioTrack(AudioNode audio, float length, float startOffset) {
        this(audio, length);
        this.startOffset = startOffset;
    }

    /**
     * Internal use only
     *
     * see Track#setTime(float, float, com.clockwork.animation.AnimControl,
     * com.clockwork.animation.AnimChannel, com.clockwork.util.TempVars)
     */
    public void setTime(float time, float weight, AnimControl control, AnimChannel channel, TempVars vars) {

        if (time >= length) {
            return;
        }
        if (!initialized) {
            control.addListener(new OnEndListener());
            initialized = true;
        }
        if (!started && time >= startOffset) {
            started = true;
            audio.playInstance();
        }
    }

    //stops the sound
    private void stop() {
        audio.stop();
        started = false;
    }

    /**
     * Retruns the length of the track
     *
     * @return length of the track
     */
    public float getLength() {
        return length;
    }

    /**
     * Clone this track
     *
     * @return
     */
    @Override
    public Track clone() {
        return new AudioTrack(audio, length, startOffset);
    }

    /**
     * This method clone the Track and search for the cloned counterpart of the
     * original audio node in the given cloned spatial. The spatial is assumed
     * to be the Spatial holding the AnimControl controling the animation using
     * this Track.
     *
     * @param spatial the Spatial holding the AnimControl
     * @return the cloned Track with proper reference
     */
    public Track cloneForSpatial(Spatial spatial) {
        AudioTrack audioTrack = new AudioTrack();
        audioTrack.length = this.length;
        audioTrack.startOffset = this.startOffset;

        //searching for the newly cloned AudioNode
        audioTrack.audio = findAudio(spatial);
        if (audioTrack.audio == null) {
            logger.log(Level.WARNING, "{0} was not found in {1} or is not bound to this track", new Object[]{audio.getName(), spatial.getName()});
            audioTrack.audio = audio;
        }

        //setting user data on the new AudioNode and marking it with a reference to the cloned Track.
        setUserData(audioTrack);

        return audioTrack;
    }

    /**
     * recursive function responsible for finding the newly cloned AudioNode
     *
     * @param spat
     * @return
     */
    private AudioNode findAudio(Spatial spat) {
        if (spat instanceof AudioNode) {
            //spat is an AudioNode
            AudioNode em = (AudioNode) spat;
            //getting the UserData TrackInfo so check if it should be attached to this Track
            TrackInfo t = (TrackInfo) em.getUserData("TrackInfo");
            if (t != null && t.getTracks().contains(this)) {
                return em;
            }
            return null;

        } else if (spat instanceof Node) {
            for (Spatial child : ((Node) spat).getChildren()) {
                AudioNode em = findAudio(child);
                if (em != null) {
                    return em;
                }
            }
        }
        return null;
    }

    private void setUserData(AudioTrack audioTrack) {
        //fetching the UserData TrackInfo.
        TrackInfo data = (TrackInfo) audioTrack.audio.getUserData("TrackInfo");

        //if it does not exist, we create it and attach it to the AudioNode.
        if (data == null) {
            data = new TrackInfo();
            audioTrack.audio.setUserData("TrackInfo", data);
        }

        //adding the given Track to the TrackInfo.
        data.addTrack(audioTrack);
    }

    public void cleanUp() {
        TrackInfo t = (TrackInfo) audio.getUserData("TrackInfo");
        t.getTracks().remove(this);
        if (!t.getTracks().isEmpty()) {
            audio.setUserData("TrackInfo", null);
        }
    }

    /**
     *
     * @return the audio node used by this track
     */
    public AudioNode getAudio() {
        return audio;
    }

    /**
     * sets the audio node to be used for this track
     *
     * @param audio
     */
    public void setAudio(AudioNode audio) {
        if (this.audio != null) {
            TrackInfo data = (TrackInfo) audio.getUserData("TrackInfo");
            data.getTracks().remove(this);
        }
        this.audio = audio;
        setUserData(this);
    }

    /**
     *
     * @return the start offset of the track
     */
    public float getStartOffset() {
        return startOffset;
    }

    /**
     * set the start offset of the track
     *
     * @param startOffset
     */
    public void setStartOffset(float startOffset) {
        this.startOffset = startOffset;
    }

    /**
     * Internal use only serialisation
     *
     * @param ex exporter
     * @throws IOException exception
     */
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(audio, "audio", null);
        out.write(length, "length", 0);
        out.write(startOffset, "startOffset", 0);
    }

    /**
     * Internal use only serialisation
     *
     * @param im importer
     * @throws IOException Exception
     */
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        audio = (AudioNode) in.readSavable("audio", null);
        length = in.readFloat("length", length);
        startOffset = in.readFloat("startOffset", 0);
    }
}
