
package com.clockwork.shadow;

import com.clockwork.asset.AssetManager;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.light.PointLight;
import com.clockwork.material.Material;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.Camera;
import com.clockwork.renderer.queue.GeometryList;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import java.io.IOException;

/**
 * PointLightShadowRenderer renders shadows for a point light
 *
 */
public class PointLightShadowRenderer extends AbstractShadowRenderer {

    public static final int CAM_NUMBER = 6;
    protected PointLight light;
    protected Camera[] shadowCams;
    private Geometry[] frustums = null;

    /**
     * Used for serialization use
     * PointLightShadowRenderer"PointLightShadowRenderer(AssetManager
     * assetManager, int shadowMapSize)
     */
    public PointLightShadowRenderer() {
        super();
    }

    /**
     * Creates a PointLightShadowRenderer
     *
     * @param assetManager the application asset manager
     * @param shadowMapSize the size of the rendered shadowmaps (512,1024,2048,
     * etc...)
     */
    public PointLightShadowRenderer(AssetManager assetManager, int shadowMapSize) {
        super(assetManager, shadowMapSize, CAM_NUMBER);
        init(shadowMapSize);
    }

    private void init(int shadowMapSize) {
        shadowCams = new Camera[CAM_NUMBER];
        for (int i = 0; i < CAM_NUMBER; i++) {
            shadowCams[i] = new Camera(shadowMapSize, shadowMapSize);
        }
    }

    @Override
    protected void updateShadowCams(Camera viewCam) {

        if (light == null) {
            throw new IllegalStateException("The light can't be null for a " + this.getClass().getName());
        }

        //bottom
        shadowCams[0].setAxes(Vector3f.UNIT_X.mult(-1f), Vector3f.UNIT_Z.mult(-1f), Vector3f.UNIT_Y.mult(-1f));

        //top
        shadowCams[1].setAxes(Vector3f.UNIT_X.mult(-1f), Vector3f.UNIT_Z, Vector3f.UNIT_Y);

        //forward
        shadowCams[2].setAxes(Vector3f.UNIT_X.mult(-1f), Vector3f.UNIT_Y, Vector3f.UNIT_Z.mult(-1f));

        //backward
        shadowCams[3].setAxes(Vector3f.UNIT_X, Vector3f.UNIT_Y, Vector3f.UNIT_Z);

        //left
        shadowCams[4].setAxes(Vector3f.UNIT_Z, Vector3f.UNIT_Y, Vector3f.UNIT_X.mult(-1f));

        //right
        shadowCams[5].setAxes(Vector3f.UNIT_Z.mult(-1f), Vector3f.UNIT_Y, Vector3f.UNIT_X);

        for (int i = 0; i < CAM_NUMBER; i++) {
            shadowCams[i].setFrustumPerspective(90f, 1f, 0.1f, light.getRadius());
            shadowCams[i].setLocation(light.getPosition());
            shadowCams[i].update();
            shadowCams[i].updateViewProjection();
        }

    }

    @Override
    protected GeometryList getOccludersToRender(int shadowMapIndex, GeometryList sceneOccluders, GeometryList sceneReceivers, GeometryList shadowMapOccluders) {
        ShadowUtil.getGeometriesInCamFrustum(sceneOccluders, shadowCams[shadowMapIndex], shadowMapOccluders);
        return shadowMapOccluders;
    }

    @Override
    GeometryList getReceivers(GeometryList sceneReceivers, GeometryList lightReceivers) {
        lightReceivers.clear();
        ShadowUtil.getGeometriesInLightRadius(sceneReceivers, shadowCams, lightReceivers);
        return lightReceivers;
    }

    @Override
    protected Camera getShadowCam(int shadowMapIndex) {
        return shadowCams[shadowMapIndex];
    }

    @Override
    protected void doDisplayFrustumDebug(int shadowMapIndex) {
        if (frustums == null) {
            frustums = new Geometry[CAM_NUMBER];
            Vector3f[] points = new Vector3f[8];
            for (int i = 0; i < 8; i++) {
                points[i] = new Vector3f();
            }
            for (int i = 0; i < CAM_NUMBER; i++) {
                ShadowUtil.updateFrustumPoints2(shadowCams[i], points);
                frustums[i] = createFrustum(points, i);
            }
        }
        if (frustums[shadowMapIndex].getParent() == null) {
            ((Node) viewPort.getScenes().get(0)).attachChild(frustums[shadowMapIndex]);
        }
    }

    @Override
    protected void setMaterialParameters(Material material) {
        material.setVector3("LightPos", light.getPosition());
    }

    /**
     * gets the point light used to cast shadows with this processor
     *
     * @return the point light
     */
    public PointLight getLight() {
        return light;
    }

    /**
     * sets the light to use for casting shadows with this processor
     *
     * @param light the point light
     */
    public void setLight(PointLight light) {
        this.light = light;
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = (InputCapsule) im.getCapsule(this);
        light = (PointLight) ic.readSavable("light", null);
        init((int) shadowMapSize);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = (OutputCapsule) ex.getCapsule(this);
        oc.write(light, "light", null);
    }
}
