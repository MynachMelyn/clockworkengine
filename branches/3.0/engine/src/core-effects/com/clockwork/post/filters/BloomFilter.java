
package com.clockwork.post.filters;

import com.clockwork.asset.AssetManager;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.post.Filter;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.renderer.queue.RenderQueue;
import com.clockwork.texture.Image.Format;
import java.io.IOException;
import java.util.ArrayList;

/**
 * BloomFilter is used to make objects in the scene have a glow effect.
 * There are 2 mode : Scene and Objects.
 * Scene mode extracts the bright parts of the scene to make them glow
 * Object mode make objects glow according to their material's glowMap or their GlowColor
 * see <a href="http://jmonkeyengine.org/wiki/doku.php/CW:advanced:bloom_and_glow">advanced:bloom_and_glow</a> for more details
 * 
 * 
 */
public class BloomFilter extends Filter {

    /**
     * GlowMode specifies if the glow will be applied to the whole scene,or to objects that have aglow color or a glow map
     */
    public enum GlowMode {

        /**
         * Apply bloom filter to bright areas in the scene.
         */
        Scene,
        /**
         * Apply bloom only to objects that have a glow map or a glow color.
         */
        Objects,
        /**
         * Apply bloom to both bright parts of the scene and objects with glow map.
         */
        SceneAndObjects;
    }

    private GlowMode glowMode = GlowMode.Scene;
    //Bloom parameters
    private float blurScale = 1.5f;
    private float exposurePower = 5.0f;
    private float exposureCutOff = 0.0f;
    private float bloomIntensity = 2.0f;
    private float downSamplingFactor = 1;
    private Pass preGlowPass;
    private Pass extractPass;
    private Pass horizontalBlur = new Pass();
    private Pass verticalalBlur = new Pass();
    private Material extractMat;
    private Material vBlurMat;
    private Material hBlurMat;
    private int screenWidth;
    private int screenHeight;    
    private RenderManager renderManager;
    private ViewPort viewPort;

    private AssetManager assetManager;
    private int initalWidth;
    private int initalHeight;
    
    /**
     * Creates a Bloom filter
     */
    public BloomFilter() {
        super("BloomFilter");
    }

    /**
     * Creates the bloom filter with the specific glow mode
     * @param glowMode
     */
    public BloomFilter(GlowMode glowMode) {
        this();
        this.glowMode = glowMode;
    }

    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
             this.renderManager = renderManager;
        this.viewPort = vp;

        this.assetManager = manager;
        this.initalWidth = w;
        this.initalHeight = h;
                
        screenWidth = (int) Math.max(1, (w / downSamplingFactor));
        screenHeight = (int) Math.max(1, (h / downSamplingFactor));
        //    System.out.println(screenWidth + " " + screenHeight);
        if (glowMode != GlowMode.Scene) {
            preGlowPass = new Pass();
            preGlowPass.init(renderManager.getRenderer(), screenWidth, screenHeight, Format.RGBA8, Format.Depth);
        }

        postRenderPasses = new ArrayList<Pass>();
        //configuring extractPass
        extractMat = new Material(manager, "Common/MatDefs/Post/BloomExtract.j3md");
        extractPass = new Pass() {

            @Override
            public boolean requiresSceneAsTexture() {
                return true;
            }

            @Override
            public void beforeRender() {
                extractMat.setFloat("ExposurePow", exposurePower);
                extractMat.setFloat("ExposureCutoff", exposureCutOff);
                if (glowMode != GlowMode.Scene) {
                    extractMat.setTexture("GlowMap", preGlowPass.getRenderedTexture());
                }
                extractMat.setBoolean("Extract", glowMode != GlowMode.Objects);
            }
        };

        extractPass.init(renderManager.getRenderer(), screenWidth, screenHeight, Format.RGBA8, Format.Depth, 1, extractMat);
        postRenderPasses.add(extractPass);

        //configuring horizontal blur pass
        hBlurMat = new Material(manager, "Common/MatDefs/Blur/HGaussianBlur.j3md");
        horizontalBlur = new Pass() {

            @Override
            public void beforeRender() {
                hBlurMat.setTexture("Texture", extractPass.getRenderedTexture());
                hBlurMat.setFloat("Size", screenWidth);
                hBlurMat.setFloat("Scale", blurScale);
            }
        };

        horizontalBlur.init(renderManager.getRenderer(), screenWidth, screenHeight, Format.RGBA8, Format.Depth, 1, hBlurMat);
        postRenderPasses.add(horizontalBlur);

        //configuring vertical blur pass
        vBlurMat = new Material(manager, "Common/MatDefs/Blur/VGaussianBlur.j3md");
        verticalalBlur = new Pass() {

            @Override
            public void beforeRender() {
                vBlurMat.setTexture("Texture", horizontalBlur.getRenderedTexture());
                vBlurMat.setFloat("Size", screenHeight);
                vBlurMat.setFloat("Scale", blurScale);
            }
        };

        verticalalBlur.init(renderManager.getRenderer(), screenWidth, screenHeight, Format.RGBA8, Format.Depth, 1, vBlurMat);
        postRenderPasses.add(verticalalBlur);


        //final material
        material = new Material(manager, "Common/MatDefs/Post/BloomFinal.j3md");
        material.setTexture("BloomTex", verticalalBlur.getRenderedTexture());
    }


    protected void reInitFilter() {
        initFilter(assetManager, renderManager, viewPort, initalWidth, initalHeight);
    }
    
    @Override
    protected Material getMaterial() {
        material.setFloat("BloomIntensity", bloomIntensity);
        return material;
    }

    @Override
    protected void postQueue(RenderQueue queue) {
        if (glowMode != GlowMode.Scene) {           
            renderManager.getRenderer().setBackgroundColor(ColorRGBA.BlackNoAlpha);            
            renderManager.getRenderer().setFrameBuffer(preGlowPass.getRenderFrameBuffer());
            renderManager.getRenderer().clearBuffers(true, true, true);
            renderManager.setForcedTechnique("Glow");
            renderManager.renderViewPortQueues(viewPort, false);         
            renderManager.setForcedTechnique(null);
            renderManager.getRenderer().setFrameBuffer(viewPort.getOutputFrameBuffer());
        }
    }

    /**
     * returns the bloom intensity
     * @return 
     */
    public float getBloomIntensity() {
        return bloomIntensity;
    }

    /**
     * intensity of the bloom effect default is 2.0
     * @param bloomIntensity
     */
    public void setBloomIntensity(float bloomIntensity) {
        this.bloomIntensity = bloomIntensity;
    }

    /**
     * returns the blur scale
     * @return 
     */
    public float getBlurScale() {
        return blurScale;
    }

    /**
     * sets The spread of the bloom default is 1.5f
     * @param blurScale
     */
    public void setBlurScale(float blurScale) {
        this.blurScale = blurScale;
    }

    /**
     * returns the exposure cutoff
     * for more details see #setExposureCutOff(float exposureCutOff)}
     * @return 
     */    
    public float getExposureCutOff() {
        return exposureCutOff;
    }

    /**
     * Define the color threshold on which the bloom will be applied (0.0 to 1.0)
     * @param exposureCutOff
     */
    public void setExposureCutOff(float exposureCutOff) {
        this.exposureCutOff = exposureCutOff;
    }

    /**
     * returns the exposure power
     * form more details see #setExposurePower(float exposurePower)}
     * @return 
     */
    public float getExposurePower() {
        return exposurePower;
    }

    /**
     * defines how many time the bloom extracted color will be multiplied by itself. default id 5.0
     * a high value will reduce rough edges in the bloom and somhow the range of the bloom area     * 
     * @param exposurePower
     */
    public void setExposurePower(float exposurePower) {
        this.exposurePower = exposurePower;
    }

    /**
     * returns the downSampling factor
     * form more details see #setDownSamplingFactor(float downSamplingFactor)}
     * @return
     */
    public float getDownSamplingFactor() {
        return downSamplingFactor;
    }

    /**
     * Sets the downSampling factor : the size of the computed texture will be divided by this factor. default is 1 for no downsampling
     * A 2 value is a good way of widening the blur
     * @param downSamplingFactor
     */
    public void setDownSamplingFactor(float downSamplingFactor) {
        this.downSamplingFactor = downSamplingFactor;
        if (assetManager != null) // dirty isInitialised check
            reInitFilter();
    }

    @Override
    public void write(CWExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(glowMode, "glowMode", GlowMode.Scene);
        oc.write(blurScale, "blurScale", 1.5f);
        oc.write(exposurePower, "exposurePower", 5.0f);
        oc.write(exposureCutOff, "exposureCutOff", 0.0f);
        oc.write(bloomIntensity, "bloomIntensity", 2.0f);
        oc.write(downSamplingFactor, "downSamplingFactor", 1);
    }

    @Override
    public void read(CWImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        glowMode = ic.readEnum("glowMode", GlowMode.class, GlowMode.Scene);
        blurScale = ic.readFloat("blurScale", 1.5f);
        exposurePower = ic.readFloat("exposurePower", 5.0f);
        exposureCutOff = ic.readFloat("exposureCutOff", 0.0f);
        bloomIntensity = ic.readFloat("bloomIntensity", 2.0f);
        downSamplingFactor = ic.readFloat("downSamplingFactor", 1);
    }
}
