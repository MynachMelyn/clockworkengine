
package com.clockwork.cinematic.events;

import com.clockwork.animation.AnimChannel;
import com.clockwork.animation.AnimControl;
import com.clockwork.animation.LoopMode;
import com.clockwork.app.Application;
import com.clockwork.cinematic.Cinematic;
import com.clockwork.cinematic.PlayState;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.scene.Spatial;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * An event based on an animation of a model. The model has to hold an
 * AnimControl with valid animation (bone or spatial animations).
 *
 * It helps to schedule the playback of an animation on a model in a Cinematic.
 *
 *
 */
public class AnimationEvent extends AbstractCinematicEvent {

    // Version #2: directly keeping track on the model instead of trying to retrieve 
    //it from the scene according to its name, because the name is not supposed to be unique
    //For backward compatibility, if the model is null it's looked up into the scene
    public static final int SAVABLE_VERSION = 2;
    private static final Logger log = Logger.getLogger(AnimationEvent.class.getName());
    public static final String MODEL_CHANNELS = "modelChannels";
    protected AnimChannel channel;
    protected String animationName;
    protected Spatial model;
    //kept for backward compatibility
    protected String modelName;
    protected float blendTime = 0;
    protected int channelIndex = 0;
    // parent cinematic
    protected Cinematic cinematic;

    /**
     * used for serialisation don't call directly use one of the following
     * contructors
     */
    public AnimationEvent() {
    }

    /**
     * creates an animation event
     *
     * @param model the model on which the animation will be played
     * @param animationName the name of the animation to play
     */
    public AnimationEvent(Spatial model, String animationName) {
        this.model = model;
        this.animationName = animationName;
        initialDuration = model.getControl(AnimControl.class).getAnimationLength(animationName);
    }

    /**
     * creates an animation event
     *
     * @param model the model on which the animation will be played
     * @param animationName the name of the animation to play
     * @param initialDuration the initialduration of the event
     */
    public AnimationEvent(Spatial model, String animationName, float initialDuration) {
        super(initialDuration);
        this.model = model;
        this.animationName = animationName;
    }

    /**
     * creates an animation event
     *
     * @param model the model on which the animation will be played
     * @param animationName the name of the animation to play
     * @param loopMode the loopMode
     * see LoopMode
     */
    public AnimationEvent(Spatial model, String animationName, LoopMode loopMode) {
        super(loopMode);
        initialDuration = model.getControl(AnimControl.class).getAnimationLength(animationName);
        this.model = model;
        this.animationName = animationName;
    }

    /**
     * creates an animation event
     *
     * @param model the model on which the animation will be played
     * @param animationName the name of the animation to play
     * @param initialDuration the initialduration of the event
     * @param loopMode the loopMode
     * see LoopMode
     */
    public AnimationEvent(Spatial model, String animationName, float initialDuration, LoopMode loopMode) {
        super(initialDuration, loopMode);
        this.model = model;
        this.animationName = animationName;
    }

    /**
     * creates an animation event
     *
     * @param model the model on which the animation will be played
     * @param animationName the name of the animation to play
     * @param initialDuration the initialduration of the event
     * @param blendTime the time during the animation are gonna be blended
     * see AnimChannel#setAnim(java.lang.String, float)
     */
    public AnimationEvent(Spatial model, String animationName, float initialDuration, float blendTime) {
        super(initialDuration);
        this.model = model;
        this.animationName = animationName;
        this.blendTime = blendTime;
    }

    /**
     * creates an animation event
     *
     * @param model the model on which the animation will be played
     * @param animationName the name of the animation to play
     * @param loopMode the loopMode
     * see LoopMode
     * @param blendTime the time during the animation are gonna be blended
     * see AnimChannel#setAnim(java.lang.String, float)
     */
    public AnimationEvent(Spatial model, String animationName, LoopMode loopMode, float blendTime) {
        super(loopMode);
        initialDuration = model.getControl(AnimControl.class).getAnimationLength(animationName);
        this.model = model;
        this.animationName = animationName;
        this.blendTime = blendTime;
    }

    /**
     * creates an animation event
     *
     * @param model the model on which the animation will be played
     * @param animationName the name of the animation to play
     * @param initialDuration the initialduration of the event
     * @param loopMode the loopMode
     * see LoopMode
     * @param blendTime the time during the animation are gonna be blended
     * see AnimChannel#setAnim(java.lang.String, float)
     */
    public AnimationEvent(Spatial model, String animationName, float initialDuration, LoopMode loopMode, float blendTime) {
        super(initialDuration, loopMode);
        this.model = model;
        this.animationName = animationName;
        this.blendTime = blendTime;
    }

    /**
     * creates an animation event
     *
     * @param model the model on which the animation will be played
     * @param animationName the name of the animation to play
     * @param loopMode the loopMode
     * see LoopMode
     * @param channelIndex the index of the channel default is 0. Events on the
     * same channelIndex will use the same channel.
     */
    public AnimationEvent(Spatial model, String animationName, LoopMode loopMode, int channelIndex) {
        super(loopMode);
        initialDuration = model.getControl(AnimControl.class).getAnimationLength(animationName);
        this.model = model;
        this.animationName = animationName;
        this.channelIndex = channelIndex;
    }

    /**
     * creates an animation event
     *
     * @param model the model on which the animation will be played
     * @param animationName the name of the animation to play
     * @param channelIndex the index of the channel default is 0. Events on the
     * same channelIndex will use the same channel.
     */
    public AnimationEvent(Spatial model, String animationName, int channelIndex) {
        this.model = model;
        this.animationName = animationName;
        initialDuration = model.getControl(AnimControl.class).getAnimationLength(animationName);
        this.channelIndex = channelIndex;
    }

    /**
     * creates an animation event
     *
     * @param model the model on which the animation will be played
     * @param animationName the name of the animation to play
     * @param initialDuration the initialduration of the event
     * @param channelIndex the index of the channel default is 0. Events on the
     * same channelIndex will use the same channel.
     */
    public AnimationEvent(Spatial model, String animationName, float initialDuration, int channelIndex) {
        super(initialDuration);
        this.model = model;
        this.animationName = animationName;
        this.channelIndex = channelIndex;
    }

    /**
     * creates an animation event
     *
     * @param model the model on which the animation will be played
     * @param animationName the name of the animation to play
     * @param initialDuration the initialduration of the event
     * @param loopMode the loopMode
     * see LoopMode
     * @param channelIndex the index of the channel default is 0. Events on the
     * same channelIndex will use the same channel.
     */
    public AnimationEvent(Spatial model, String animationName, float initialDuration, LoopMode loopMode, int channelIndex) {
        super(initialDuration, loopMode);
        this.model = model;
        this.animationName = animationName;
        this.channelIndex = channelIndex;
    }

    @Override
    public void initEvent(Application app, Cinematic cinematic) {
        super.initEvent(app, cinematic);
        this.cinematic = cinematic;
        if (channel == null) {
            Object s = cinematic.getEventData(MODEL_CHANNELS, model);
            if (s == null) {
                s = new HashMap<Integer, AnimChannel>();
                cinematic.putEventData(MODEL_CHANNELS, model, s);
            }

            Map<Integer, AnimChannel> map = (Map<Integer, AnimChannel>) s;
            this.channel = map.get(channelIndex);
            if (this.channel == null) {
                if (model == null) {
                    //the model is null we try to find it according to the name
                    //this should occur only when loading an old saved cinematic
                    //othewise it's an error
                    model = cinematic.getScene().getChild(modelName);
                }
                if (model != null) {
                    channel = model.getControl(AnimControl.class).createChannel();
                    map.put(channelIndex, channel);
                } else {
                    //it's an error
                    throw new UnsupportedOperationException("model should not be null");
                }
            } 

        }
    }

    @Override
    public void setTime(float time) {
        super.setTime(time);
        if (!animationName.equals(channel.getAnimationName())) {
            channel.setAnim(animationName, blendTime);
        }
        float t = time;
        if (loopMode == loopMode.Loop) {
            t = t % channel.getAnimMaxTime();
        }
        if (loopMode == loopMode.Cycle) {
            float parity = (float) Math.ceil(time / channel.getAnimMaxTime());
            if (parity > 0 && parity % 2 == 0) {
                t = channel.getAnimMaxTime() - t % channel.getAnimMaxTime();
            } else {
                t = t % channel.getAnimMaxTime();
            }

        }
        if (t < 0) {
            channel.setTime(0);
            channel.reset(true);
        }
        if (t > channel.getAnimMaxTime()) {
            channel.setTime(t);
            channel.getControl().update(0);
            stop();
        } else {
            channel.setTime(t);
            channel.getControl().update(0);
        }
    }

    @Override
    public void onPlay() {
        channel.getControl().setEnabled(true);
        if (playState == PlayState.Stopped) {
            channel.setAnim(animationName, blendTime);
            channel.setSpeed(speed);
            channel.setLoopMode(loopMode);
            channel.setTime(0);
        }
    }

    @Override
    public void setSpeed(float speed) {
        super.setSpeed(speed);
        if (channel != null) {
            channel.setSpeed(speed);
        }
    }

    @Override
    public void onUpdate(float tpf) {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void forceStop() {
        if (channel != null) {
            channel.setTime(time);
            channel.reset(false);
        }
        super.forceStop();
    }

    @Override
    public void onPause() {
        if (channel != null) {
            channel.getControl().setEnabled(false);
        }
    }

    @Override
    public void setLoopMode(LoopMode loopMode) {
        super.setLoopMode(loopMode);
        if (channel != null) {
            channel.setLoopMode(loopMode);
        }
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);

        oc.write(model, "model", null);
        oc.write(animationName, "animationName", "");
        oc.write(blendTime, "blendTime", 0f);
        oc.write(channelIndex, "channelIndex", 0);

    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        if (im.getFormatVersion() == 0) {
            modelName = ic.readString("modelName", "");
        }
        //FIXME always the same issue, because of the clonning of assets, this won't work
        //we have to somehow store userdata in the spatial and then recurse the 
        //scene sub scenegraph to find the correct instance of the model
        //This brings a reflaxion about the cinematic being an appstate, 
        //shouldn't it be a control over the scene
        // this would allow to use the cloneForSpatial method and automatically 
        //rebind cloned references of original objects.
        //for now as nobody probably ever saved a cinematic, this is not a critical issue
        model = (Spatial) ic.readSavable("model", null);
        animationName = ic.readString("animationName", "");
        blendTime = ic.readFloat("blendTime", 0f);
        channelIndex = ic.readInt("channelIndex", 0);
    }

    @Override
    public void dispose() {
        super.dispose();
        Object o = cinematic.getEventData(MODEL_CHANNELS, model);
        if (o != null) {
            ArrayList<AnimChannel> list = (ArrayList<AnimChannel>) o;
            list.remove(channel);
            if (list.isEmpty()) {
                cinematic.removeEventData(MODEL_CHANNELS, model);
            }
        }
        cinematic = null;
        channel = null;
    }
}
