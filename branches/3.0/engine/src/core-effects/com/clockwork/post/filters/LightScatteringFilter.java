
package com.clockwork.post.filters;

import com.clockwork.asset.AssetManager;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.material.Material;
import com.clockwork.math.FastMath;
import com.clockwork.math.Vector3f;
import com.clockwork.post.Filter;
import com.clockwork.renderer.Camera;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.renderer.queue.RenderQueue;
import java.io.IOException;

/**
 * Creates rays, god-rays, from light sources.
 */
public class LightScatteringFilter extends Filter {

    private Vector3f lightPosition;
    private Vector3f screenLightPos = new Vector3f();
    private int nbSamples = 50;
    private float blurStart = 0.02f;
    private float blurWidth = 0.9f;
    private float lightDensity = 1.4f;
    private boolean adaptative = true;
    Vector3f viewLightPos = new Vector3f();
    private boolean display = true;
    private float innerLightDensity;
    private ViewPort viewPort;

    /**
     * creates a lightScaterring filter
     */
    public LightScatteringFilter() {
        super("Light Scattering");
    }

    /**
     * Creates a lightScatteringFilter
     * @param lightPosition 
     */
    public LightScatteringFilter(Vector3f lightPosition) {
        this();
        this.lightPosition = lightPosition;
    }

    @Override
    protected boolean isRequiresDepthTexture() {
        return true;
    }

    @Override
    protected Material getMaterial() {
        material.setVector3("LightPosition", screenLightPos);
        material.setInt("NbSamples", nbSamples);
        material.setFloat("BlurStart", blurStart);
        material.setFloat("BlurWidth", blurWidth);
        material.setFloat("LightDensity", innerLightDensity);
        material.setBoolean("Display", display);
        return material;
    }

    @Override
    protected void postQueue(RenderQueue queue) {
        getClipCoordinates(lightPosition, screenLightPos, viewPort.getCamera());
        viewPort.getCamera().getViewMatrix().mult(lightPosition, viewLightPos);        
        if (adaptative) {
            float densityX = 1f - FastMath.clamp(FastMath.abs(screenLightPos.x - 0.5f), 0, 1);
            float densityY = 1f - FastMath.clamp(FastMath.abs(screenLightPos.y - 0.5f), 0, 1);
            innerLightDensity = lightDensity * densityX * densityY;
        } else {
            innerLightDensity = lightDensity;
        }
        display = innerLightDensity != 0.0 && viewLightPos.z < 0;
    }

    private Vector3f getClipCoordinates(Vector3f worldPosition, Vector3f store, Camera cam) {

        float w = cam.getViewProjectionMatrix().multProj(worldPosition, store);
        store.divideLocal(w);

        store.x = ((store.x + 1f) * (cam.getViewPortRight() - cam.getViewPortLeft()) / 2f + cam.getViewPortLeft());
        store.y = ((store.y + 1f) * (cam.getViewPortTop() - cam.getViewPortBottom()) / 2f + cam.getViewPortBottom());
        store.z = (store.z + 1f) / 2f;

        return store;
    }

    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        this.viewPort = vp;
        material = new Material(manager, "Common/MatDefs/Post/LightScattering.j3md");
    }

    /**
     * returns the blur start of the scattering 
     * see #setBlurStart(float blurStart)}
     * @return 
     */
    public float getBlurStart() {
        return blurStart;
    }

    /**
     * sets the blur start
     * at which distance from the light source the effect starts default is 0.02
     * @param blurStart 
     */
    public void setBlurStart(float blurStart) {
        this.blurStart = blurStart;
    }

    /**
     * returns the blur width
     * see #setBlurWidth(float blurWidth)}
     * @return 
     */
    public float getBlurWidth() {
        return blurWidth;
    }

    /**
     * sets the blur width default is 0.9
     * @param blurWidth 
     */
    public void setBlurWidth(float blurWidth) {
        this.blurWidth = blurWidth;
    }

    /**
     * returns the light density
     * see #setLightDensity(float lightDensity)}
     * 
     * @return 
     */
    public float getLightDensity() {
        return lightDensity;
    }

    /**
     * sets how much the effect is visible over the rendered scene default is 1.4
     * @param lightDensity 
     */
    public void setLightDensity(float lightDensity) {
        this.lightDensity = lightDensity;
    }

    /**
     * returns the light position
     * @return 
     */
    public Vector3f getLightPosition() {
        return lightPosition;
    }

    /**
     * sets the light position
     * @param lightPosition 
     */
    public void setLightPosition(Vector3f lightPosition) {
        this.lightPosition = lightPosition;
    }

    /**
     * returns the nmber of samples for the radial blur
     * @return 
     */
    public int getNbSamples() {
        return nbSamples;
    }

    /**
     * sets the number of samples for the radial blur default is 50
     * the higher the value the higher the quality, but the slower the performances.
     * @param nbSamples 
     */
    public void setNbSamples(int nbSamples) {
        this.nbSamples = nbSamples;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(lightPosition, "lightPosition", Vector3f.ZERO);
        oc.write(nbSamples, "nbSamples", 50);
        oc.write(blurStart, "blurStart", 0.02f);
        oc.write(blurWidth, "blurWidth", 0.9f);
        oc.write(lightDensity, "lightDensity", 1.4f);
        oc.write(adaptative, "adaptative", true);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        lightPosition = (Vector3f) ic.readSavable("lightPosition", Vector3f.ZERO);
        nbSamples = ic.readInt("nbSamples", 50);
        blurStart = ic.readFloat("blurStart", 0.02f);
        blurWidth = ic.readFloat("blurWidth", 0.9f);
        lightDensity = ic.readFloat("lightDensity", 1.4f);
        adaptative = ic.readBoolean("adaptative", true);
    }
}
