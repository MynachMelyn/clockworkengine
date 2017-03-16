
package com.clockwork.post.filters;

import com.clockwork.asset.AssetManager;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.material.Material;
import com.clockwork.post.Filter;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import java.io.IOException;

/**
 *
 * Fade Filter allows you to make an animated fade effect on a scene.
 * implemented from boxjar implementation
 * see <a href="http://jmonkeyengine.org/groups/graphics/forum/topic/newbie-question-general-fade-inout-effect/#post-105559">http://jmonkeyengine.org/groups/graphics/forum/topic/newbie-question-general-fade-inout-effect/#post-105559</a>
 */
public class FadeFilter extends Filter {

    private float value = 1;
    private boolean playing = false;
    private float direction = 1;
    private float duration = 1;

    /**
     * Creates a FadeFilter
     */
    public FadeFilter() {
        super("Fade In/Out");
    }

    /**
     * Creates a FadeFilter with the given duration
     * @param duration 
     */
    public FadeFilter(float duration) {
        this();
        this.duration = duration;
    }

    @Override
    protected Material getMaterial() {
        material.setFloat("Value", value);
        return material;
    }

    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        material = new Material(manager, "Common/MatDefs/Post/Fade.j3md");
    }

    @Override
    protected void preFrame(float tpf) {
        if (playing) {
            value += tpf * direction / duration;

            if (direction > 0 && value > 1) {
                value = 1;
                playing = false;
            }
            if (direction < 0 && value < 0) {
                value = 0;
                playing = false;
            }    
        }
    }

    /**
     * returns the duration of the effect 
     * @return 
     */
    public float getDuration() {
        return duration;
    }

    /**
     * Sets the duration of the filter default is 1 second
     * @param duration 
     */
    public void setDuration(float duration) {
        this.duration = duration;
    }

    /**
     * fades the scene in (black to scene)
     */
    public void fadeIn() {
        setEnabled(true);
        direction = 1;
        playing = true;
    }

    /**
     * fades the scene out (scene to black)
     */
    public void fadeOut() {
        setEnabled(true);
        direction = -1;
        playing = true;

    }

    public void pause() {
        playing = false;
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(duration, "duration", 1);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        duration = ic.readFloat("duration", 1);
    }

    /**
     * return the current value of the fading
     * can be used to chack if fade is complete (eg value=1)
     * @return 
     */
    public float getValue() {
        return value;
    }

    /**
     * sets the fade value
     * can be used to force complete black or compete scene
     * @param value 
     */
    public void setValue(float value) {
        this.value = value;       
        if (material != null) {
            material.setFloat("Value", value);
        }
    }
}
