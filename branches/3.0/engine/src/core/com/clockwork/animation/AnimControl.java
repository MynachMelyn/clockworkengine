
package com.clockwork.animation;

import com.clockwork.export.*;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.control.AbstractControl;
import com.clockwork.scene.control.Control;
import com.clockwork.util.TempVars;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Spatial control for use of skeletal animation.
 *
 * Currently supports:
 * Animation blending
 * Multiple animation channels
 * Multiple skins
 * Event listeners
 * Animated model cloning
 * Animated model binary import/export (Serialisation)
 */
public final class AnimControl extends AbstractControl implements Cloneable {

    /**
     * Skeleton object must contain corresponding data for the target's weight buffers.
     */
    Skeleton skeleton;
    /** Outdated, but in the case it is used in an older test, it's left in and marked as deprecated */
    @Deprecated
    private SkeletonControl skeletonControl;
    
    /**
     * List of animations. Hashmapped dictionary to allow association of a String name and an Animation.
     */
    HashMap<String, Animation> animationMap = new HashMap<String, Animation>();
    
    /**
     * Animation channels. Transient to avoid it being serialised (making an object's state persistent)
     * Serialisation stores the object's state in byte form, deserialisation retrieves the state from the bytes.
     * As a rule of thumb, any variable that can simply be calculated from other variables doesn't need to be serialised.
     */
    private transient ArrayList<AnimChannel> channels = new ArrayList<AnimChannel>();
    /**
     * Animation event listeners
     */
    private transient ArrayList<AnimEventListener> listeners = new ArrayList<AnimEventListener>();

    /**
     * Creates a new animation control the supplied skeleton.
     * setAnimations should be called after the constructor in order for this class to be used at all.
     *
     * @param skeleton The skeleton to be animated
     */
    public AnimControl(Skeleton skeleton) {
        this.skeleton = skeleton;
        reset();
    }

    /**
     * Serialisation only. Do not use.
     */
    public AnimControl() {
    }

    /**
     * Internal use only.
     */
    public Control cloneForSpatial(Spatial spatial) {
        try {
            AnimControl clone = (AnimControl) super.clone();
            clone.spatial = spatial;
            clone.channels = new ArrayList<AnimChannel>();
            clone.listeners = new ArrayList<AnimEventListener>();

            if (skeleton != null) {
                clone.skeleton = new Skeleton(skeleton);
            }

            // animationMap is cloned, but only ClonableTracks will be cloned as they need to reference a cloned spatial.
            for (Entry<String, Animation> animEntry : animationMap.entrySet()) {
                clone.animationMap.put(animEntry.getKey(), animEntry.getValue().cloneForSpatial(spatial));
            }
            
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    /**
     * @param animations Set the animations that this <code>AnimControl</code>
     * will be capable of playing. The animations should be compatible
     * with the skeleton given in the constructor.
     */
    public void setAnimations(HashMap<String, Animation> animations) {
        animationMap = animations;
    }

    /**
     * Retrieve an animation from the list of animations.
     * @param name The name of the animation to retrieve.
     * @return The animation corresponding to the given name, or null, if no
     * such named animation exists.
     */
    public Animation getAnim(String name) {
        return animationMap.get(name);
    }

    /**
     * Adds an animation to be available for playing to this
     * <code>AnimControl</code>.
     * @param anim The animation to add.
     */
    public void addAnim(Animation anim) {
        animationMap.put(anim.getName(), anim);
    }

    /**
     * Remove an animation so that it is no longer available for playing.
     * @param anim The animation to remove.
     */
    public void removeAnim(Animation anim) {
        if (!animationMap.containsKey(anim.getName())) {
            throw new IllegalArgumentException("Given animation does not exist "
                    + "in this AnimControl");
        }

        animationMap.remove(anim.getName());
    }

    /**
     * Create a new animation channel, by default assigned to all bones
     * in the skeleton.
     * 
     * @return A new animation channel for this <code>AnimControl</code>.
     */
    public AnimChannel createChannel() {
        AnimChannel channel = new AnimChannel(this);
        channels.add(channel);
        return channel;
    }

    /**
     * Return the animation channel at the given index.
     * @param index The index, starting at 0, to retrieve the <code>AnimChannel</code>.
     * @return The animation channel at the given index, or throws an exception
     * if the index is out of bounds.
     *
     * @throws IndexOutOfBoundsException If no channel exists at the given index.
     */
    public AnimChannel getChannel(int index) {
        return channels.get(index);
    }

    /**
     * @return The number of channels that are controlled by this
     * <code>AnimControl</code>.
     *
     * @see AnimControl#createChannel()
     */
    public int getNumChannels() {
        return channels.size();
    }

    /**
     * Clears all the channels that were created.
     *
     * @see AnimControl#createChannel()
     */
    public void clearChannels() {
        for (AnimChannel animChannel : channels) {
            for (AnimEventListener list : listeners) {
                list.onAnimCycleDone(this, animChannel, animChannel.getAnimationName());
            }
        }
        channels.clear();
    }

    /**
     * @return The skeleton of this <code>AnimControl</code>.
     */
    public Skeleton getSkeleton() {
        return skeleton;
    }

    /**
     * Adds a new listener to receive animation related events.
     * @param listener The listener to add.
     */
    public void addListener(AnimEventListener listener) {
        if (listeners.contains(listener)) {
            throw new IllegalArgumentException("The given listener is already "
                    + "registed at this AnimControl");
        }

        listeners.add(listener);
    }

    /**
     * Removes the given listener from listening to events.
     * @param listener
     * @see AnimControl#addListener(com.clockwork.animation.AnimEventListener)
     */
    public void removeListener(AnimEventListener listener) {
        if (!listeners.remove(listener)) {
            throw new IllegalArgumentException("The given listener is not "
                    + "registed at this AnimControl");
        }
    }

    /**
     * Clears all the listeners added to this <code>AnimControl</code>
     *
     * @see AnimControl#addListener(com.clockwork.animation.AnimEventListener)
     */
    public void clearListeners() {
        listeners.clear();
    }

    void notifyAnimChange(AnimChannel channel, String name) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onAnimChange(this, channel, name);
        }
    }

    void notifyAnimCycleDone(AnimChannel channel, String name) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onAnimCycleDone(this, channel, name);
        }
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial == null && skeletonControl != null) {
            this.spatial.removeControl(skeletonControl);
        }

        super.setSpatial(spatial);

        //Backward compatibility.
        if (spatial != null && skeletonControl != null) {
            spatial.addControl(skeletonControl);
        }
    }
    
    final void reset() {
        if (skeleton != null) {
            skeleton.resetAndUpdate();
        }
    }

    /**
     * @return The names of all animations that this <code>AnimControl</code>
     * can play.
     */
    public Collection<String> getAnimationNames() {
        return animationMap.keySet();
    }

    /**
     * Returns the length of the given named animation.
     * @param name The name of the animation
     * @return The length of time, in seconds, of the named animation.
     */
    public float getAnimationLength(String name) {
        Animation a = animationMap.get(name);
        if (a == null) {
            throw new IllegalArgumentException("The animation " + name
                    + " does not exist in this AnimControl");
        }

        return a.getLength();
    }

    /**
     * Internal use only.
     */
    @Override
    protected void controlUpdate(float tpf) {
        if (skeleton != null) {
            skeleton.reset(); // reset skeleton to bind pose
        }

        TempVars vars = TempVars.get();
        for (int i = 0; i < channels.size(); i++) {
            channels.get(i).update(tpf, vars);
        }
        vars.release();

        if (skeleton != null) {
            skeleton.updateWorldVectors();
        }
    }

    /**
     * Internal use only.
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(skeleton, "skeleton", null);
        oc.writeStringSavableMap(animationMap, "animations", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        skeleton = (Skeleton) in.readSavable("skeleton", null);
        HashMap<String, Animation> loadedAnimationMap = (HashMap<String, Animation>) in.readStringSavableMap("animations", null);
        if (loadedAnimationMap != null) {
            animationMap = loadedAnimationMap;
        }

        if (im.getFormatVersion() == 0) {
            // Changed for backward compatibility with j3o files generated 
            // before the AnimControl/SkeletonControl split.

            // If we find a target mesh array the AnimControl creates the 
            // SkeletonControl for old files and add it to the spatial.        
            // When backward compatibility won't be needed anymore this can deleted        
            Savable[] sav = in.readSavableArray("targets", null);
            if (sav != null) {
                Mesh[] targets = new Mesh[sav.length];
                System.arraycopy(sav, 0, targets, 0, sav.length);
                skeletonControl = new SkeletonControl(targets, skeleton);
                spatial.addControl(skeletonControl);
            }
        }
    }
}
