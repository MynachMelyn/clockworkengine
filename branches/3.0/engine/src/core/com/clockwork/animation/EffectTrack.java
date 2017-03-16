
package com.clockwork.animation;

import com.clockwork.effect.ParticleEmitter;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.Spatial.CullHint;
import com.clockwork.scene.control.AbstractControl;
import com.clockwork.scene.control.Control;
import com.clockwork.util.TempVars;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * EffectTrack is a track to add to an existing animation, to emmit particles
 * during animations for example : exhausts, dust raised by foot steps, shock
 * waves, lightnings etc...
 *
 * usage is
 * 
 * AnimControl control model.getControl(AnimControl.class);
 * EffectTrack track = new EffectTrack(existingEmmitter, control.getAnim("TheAnim").getLength());
 * control.getAnim("TheAnim").addTrack(track);
 * 
 *
 * if the emitter has emmits 0 particles per seconds emmitAllPArticles will be
 * called on it at time 0 + startOffset. if it he it has more it will start
 * emmit normally at time 0 + startOffset.
 *
 *
 */
public class EffectTrack implements ClonableTrack {

    private static final Logger logger = Logger.getLogger(EffectTrack.class.getName());
    private ParticleEmitter emitter;
    private float startOffset = 0;
    private float particlesPerSeconds = 0;
    private float length = 0;
    private boolean emitted = false;
    private boolean initialized = false;
    //control responsible for disable and cull the emitter once all particles are gone
    private KillParticleControl killParticles = new KillParticleControl();

    public static class KillParticleControl extends AbstractControl {

        ParticleEmitter emitter;
        boolean stopRequested = false;
        boolean remove = false;

        public KillParticleControl() {
        }

        @Override
        public void setSpatial(Spatial spatial) {
            super.setSpatial(spatial);
            if (spatial != null) {
                if (spatial instanceof ParticleEmitter) {
                    emitter = (ParticleEmitter) spatial;
                } else {
                    throw new IllegalArgumentException("KillParticleEmitter can only ba attached to ParticleEmitter");
                }
            }


        }

        @Override
        protected void controlUpdate(float tpf) {
            if (remove) {
                emitter.removeControl(this);
                return;
            }
            if (emitter.getNumVisibleParticles() == 0) {
                emitter.setCullHint(CullHint.Always);
                emitter.setEnabled(false);
                emitter.removeControl(this);
                stopRequested = false;
            }
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
        }

        @Override
        public Control cloneForSpatial(Spatial spatial) {

            KillParticleControl c = new KillParticleControl();
            //this control should be removed as it shouldn't have been persisted in the first place
            //In the quest to find the less hackish solution to achieve this, 
            //making it remove itself from the spatial in the first update loop when loaded was the less bad. 
            c.remove = true;
            c.setSpatial(spatial);
            return c;

        }
    };

    //Anim listener that stops the Emmitter when the animation is finished or changed.
    private class OnEndListener implements AnimEventListener {

        public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
            stop();
        }

        public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        }
    }

    /**
     * default constructor only for serialisation
     */
    public EffectTrack() {
    }

    /**
     * Creates and EffectTrack
     *
     * @param emitter the emmitter of the track
     * @param length the length of the track (usually the length of the
     * animation you want to add the track to)
     */
    public EffectTrack(ParticleEmitter emitter, float length) {
        this.emitter = emitter;
        //saving particles per second value
        this.particlesPerSeconds = emitter.getParticlesPerSec();
        //setting the emmitter to not emmit.
        this.emitter.setParticlesPerSec(0);
        this.length = length;
        //Marking the emitter with a reference to this track for further use in deserialisation.
        setUserData(this);

    }

    /**
     * Creates and EffectTrack
     *
     * @param emitter the emmitter of the track
     * @param length the length of the track (usually the length of the
     * animation you want to add the track to)
     * @param startOffset the time in second when the emitter will be triggerd
     * after the animation starts (default is 0)
     */
    public EffectTrack(ParticleEmitter emitter, float length, float startOffset) {
        this(emitter, length);
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
        //first time adding the Animation listener to stop the track at the end of the animation
        if (!initialized) {
            control.addListener(new OnEndListener());
            initialized = true;
        }
        //checking fo time to trigger the effect
        if (!emitted && time >= startOffset) {
            emitted = true;
            emitter.setCullHint(CullHint.Dynamic);
            emitter.setEnabled(true);
            //if the emitter has 0 particles per seconds emmit all particles in one shot
            if (particlesPerSeconds == 0) {
                emitter.emitAllParticles();
                if (!killParticles.stopRequested) {
                    emitter.addControl(killParticles);
                    killParticles.stopRequested = true;
                }
            } else {
                //else reset its former particlePerSec value to let it emmit.
                emitter.setParticlesPerSec(particlesPerSeconds);
            }
        }
    }

    //stops the emmiter to emit.
    private void stop() {
        emitter.setParticlesPerSec(0);
        emitted = false;
        if (!killParticles.stopRequested) {
            emitter.addControl(killParticles);
            killParticles.stopRequested = true;
        }

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
        return new EffectTrack(emitter, length, startOffset);
    }

    /**
     * This method clone the Track and search for the cloned counterpart of the
     * original emmitter in the given cloned spatial. The spatial is assumed to
     * be the Spatial holding the AnimControl controling the animation using
     * this Track.
     *
     * @param spatial the Spatial holding the AnimControl
     * @return the cloned Track with proper reference
     */
    public Track cloneForSpatial(Spatial spatial) {
        EffectTrack effectTrack = new EffectTrack();
        effectTrack.particlesPerSeconds = this.particlesPerSeconds;
        effectTrack.length = this.length;
        effectTrack.startOffset = this.startOffset;

        //searching for the newly cloned ParticleEmitter
        effectTrack.emitter = findEmitter(spatial);
        if (effectTrack.emitter == null) {
            logger.log(Level.WARNING, "{0} was not found in {1} or is not bound to this track", new Object[]{emitter.getName(), spatial.getName()});
            effectTrack.emitter = emitter;
        }

        removeUserData(this);
        //setting user data on the new emmitter and marking it with a reference to the cloned Track.
        setUserData(effectTrack);
        effectTrack.emitter.setParticlesPerSec(0);
        return effectTrack;
    }

    /**
     * recursive function responsible for finding the newly cloned Emitter
     *
     * @param spat
     * @return
     */
    private ParticleEmitter findEmitter(Spatial spat) {
        if (spat instanceof ParticleEmitter) {
            //spat is a PArticleEmitter
            ParticleEmitter em = (ParticleEmitter) spat;
            //getting the UserData TrackInfo so check if it should be attached to this Track
            TrackInfo t = (TrackInfo) em.getUserData("TrackInfo");
            if (t != null && t.getTracks().contains(this)) {
                return em;
            }
            return null;

        } else if (spat instanceof Node) {
            for (Spatial child : ((Node) spat).getChildren()) {
                ParticleEmitter em = findEmitter(child);
                if (em != null) {
                    return em;
                }
            }
        }
        return null;
    }

    public void cleanUp() {
        TrackInfo t = (TrackInfo) emitter.getUserData("TrackInfo");
        t.getTracks().remove(this);
        if (t.getTracks().isEmpty()) {
            emitter.setUserData("TrackInfo", null);
        }
    }

    /**
     *
     * @return the emitter used by this track
     */
    public ParticleEmitter getEmitter() {
        return emitter;
    }

    /**
     * Sets the Emitter to use in this track
     *
     * @param emitter
     */
    public void setEmitter(ParticleEmitter emitter) {
        if (this.emitter != null) {
            TrackInfo data = (TrackInfo) emitter.getUserData("TrackInfo");
            data.getTracks().remove(this);
        }
        this.emitter = emitter;
        //saving particles per second value
        this.particlesPerSeconds = emitter.getParticlesPerSec();
        //setting the emmitter to not emmit.
        this.emitter.setParticlesPerSec(0);
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

    private void setUserData(EffectTrack effectTrack) {
        //fetching the UserData TrackInfo.
        TrackInfo data = (TrackInfo) effectTrack.emitter.getUserData("TrackInfo");

        //if it does not exist, we create it and attach it to the emitter.
        if (data == null) {
            data = new TrackInfo();
            effectTrack.emitter.setUserData("TrackInfo", data);
        }

        //adding the given Track to the TrackInfo.
        data.addTrack(effectTrack);


    }

    private void removeUserData(EffectTrack effectTrack) {
        //fetching the UserData TrackInfo.
        TrackInfo data = (TrackInfo) effectTrack.emitter.getUserData("TrackInfo");

        //if it does not exist, we create it and attach it to the emitter.
        if (data == null) {
            return;
        }

        //removing the given Track to the TrackInfo.
        data.getTracks().remove(effectTrack);


    }

    /**
     * Internal use only serialisation
     *
     * @param ex exporter
     * @throws IOException exception
     */
    public void write(CWExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        //reseting the particle emission rate on the emitter before saving.
        emitter.setParticlesPerSec(particlesPerSeconds);
        out.write(emitter, "emitter", null);
        out.write(particlesPerSeconds, "particlesPerSeconds", 0);
        out.write(length, "length", 0);
        out.write(startOffset, "startOffset", 0);
        //Setting emission rate to 0 so that this track can go on being used.
        emitter.setParticlesPerSec(0);
    }

    /**
     * Internal use only serialisation
     *
     * @param im importer
     * @throws IOException Exception
     */
    public void read(CWImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        this.particlesPerSeconds = in.readFloat("particlesPerSeconds", 0);
        //reading the emitter even if the track will then reference its cloned counter part if it's loaded with the assetManager.
        //This also avoid null pointer exception if the model is not loaded via the AssetManager.
        emitter = (ParticleEmitter) in.readSavable("emitter", null);
        emitter.setParticlesPerSec(0);
        //if the emitter was saved with a KillParticleControl we remove it.
//        Control c = emitter.getControl(KillParticleControl.class);
//        if(c!=null){
//            emitter.removeControl(c);
//        }
        //emitter.removeControl(KillParticleControl.class);
        length = in.readFloat("length", length);
        startOffset = in.readFloat("startOffset", 0);
    }
}
