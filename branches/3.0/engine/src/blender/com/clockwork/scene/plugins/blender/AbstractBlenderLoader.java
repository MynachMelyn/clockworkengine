
package com.clockwork.scene.plugins.blender;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clockwork.asset.AssetLoader;
import com.clockwork.asset.BlenderKey.FeaturesToLoad;
import com.clockwork.asset.BlenderKey.WorldData;
import com.clockwork.light.AmbientLight;
import com.clockwork.math.ColorRGBA;
import com.clockwork.scene.CameraNode;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.LightNode;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.plugins.blender.cameras.CameraHelper;
import com.clockwork.scene.plugins.blender.exceptions.BlenderFileException;
import com.clockwork.scene.plugins.blender.file.Pointer;
import com.clockwork.scene.plugins.blender.file.Structure;
import com.clockwork.scene.plugins.blender.lights.LightHelper;
import com.clockwork.scene.plugins.blender.meshes.MeshHelper;
import com.clockwork.scene.plugins.blender.objects.ObjectHelper;

/**
 * This class converts blender file blocks into engine data structures.
 * 
 */
/* package */abstract class AbstractBlenderLoader implements AssetLoader {
    private static final Logger LOGGER = Logger.getLogger(AbstractBlenderLoader.class.getName());

    protected BlenderContext    blenderContext;

    /**
     * This method converts the given structure to a scene node.
     * @param structure
     *            structure of a scene
     * @return scene's node
     */
    public Node toScene(Structure structure) {
        if ((blenderContext.getBlenderKey().getFeaturesToLoad() & FeaturesToLoad.SCENES) == 0) {
            return null;
        }
        Node result = new Node(structure.getName());
        try {
            List<Structure> base = ((Structure) structure.getFieldValue("base")).evaluateListBase(blenderContext);
            for (Structure b : base) {
                Pointer pObject = (Pointer) b.getFieldValue("object");
                if (pObject.isNotNull()) {
                    Structure objectStructure = pObject.fetchData(blenderContext.getInputStream()).get(0);
                    Object object = this.toObject(objectStructure);
                    if (object instanceof LightNode && (blenderContext.getBlenderKey().getFeaturesToLoad() & FeaturesToLoad.LIGHTS) != 0) {
                        result.addLight(((LightNode) object).getLight());
                        result.attachChild((LightNode) object);
                    } else if (object instanceof Node && (blenderContext.getBlenderKey().getFeaturesToLoad() & FeaturesToLoad.OBJECTS) != 0) {
                        LOGGER.log(Level.FINE, "{0}: {1}--> {2}", new Object[] { ((Node) object).getName(), ((Node) object).getLocalTranslation().toString(), ((Node) object).getParent() == null ? "null" : ((Node) object).getParent().getName() });
                        if (((Node) object).getParent() == null) {
                            result.attachChild((Spatial) object);
                        }
                    }
                }
            }
        } catch (BlenderFileException e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
        return result;
    }

    /**
     * This method converts the given structure to a camera.
     * @param structure
     *            structure of a camera
     * @return camera's node
     */
    public CameraNode toCamera(Structure structure) throws BlenderFileException {
        CameraHelper cameraHelper = blenderContext.getHelper(CameraHelper.class);
        if (cameraHelper.shouldBeLoaded(structure, blenderContext)) {
            return cameraHelper.toCamera(structure, blenderContext);
        }
        return null;
    }

    /**
     * This method converts the given structure to a light.
     * @param structure
     *            structure of a light
     * @return light's node
     */
    public LightNode toLight(Structure structure) throws BlenderFileException {
        LightHelper lightHelper = blenderContext.getHelper(LightHelper.class);
        if (lightHelper.shouldBeLoaded(structure, blenderContext)) {
            return lightHelper.toLight(structure, blenderContext);
        }
        return null;
    }

    /**
     * This method converts the given structure to a node.
     * @param structure
     *            structure of an object
     * @return object's node
     */
    public Object toObject(Structure structure) throws BlenderFileException {
        ObjectHelper objectHelper = blenderContext.getHelper(ObjectHelper.class);
        if (objectHelper.shouldBeLoaded(structure, blenderContext)) {
            return objectHelper.toObject(structure, blenderContext);
        }
        return null;
    }

    /**
     * This method converts the given structure to a list of geometries.
     * @param structure
     *            structure of a mesh
     * @return list of geometries
     */
    public List<Geometry> toMesh(Structure structure) throws BlenderFileException {
        MeshHelper meshHelper = blenderContext.getHelper(MeshHelper.class);
        if (meshHelper.shouldBeLoaded(structure, blenderContext)) {
            return meshHelper.toMesh(structure, blenderContext);
        }
        return null;
    }

    // /**
    // * This method converts the given structure to a material.
    // * @param structure
    // * structure of a material
    // * @return material's node
    // */
    // public Material toMaterial(Structure structure) throws BlenderFileException {
    // MaterialHelper materialHelper = blenderContext.getHelper(MaterialHelper.class);
    // if (materialHelper.shouldBeLoaded(structure, blenderContext)) {
    // return materialHelper.toMaterial(structure, blenderContext);
    // }
    // return null;
    // }

    /**
     * This method returns the data read from the WORLD file block. The block contains data that can be stored as
     * separate CW features and therefore cannot be returned as a single CW scene feature.
     * @param structure
     *            the structure with WORLD block data
     * @return data read from the WORLD block that can be added to the scene
     */
    public WorldData toWorldData(Structure structure) {
        WorldData result = new WorldData();

        // reading ambient light
        AmbientLight ambientLight = new AmbientLight();
        float ambr = ((Number) structure.getFieldValue("ambr")).floatValue();
        float ambg = ((Number) structure.getFieldValue("ambg")).floatValue();
        float ambb = ((Number) structure.getFieldValue("ambb")).floatValue();
        ambientLight.setColor(new ColorRGBA(ambr, ambg, ambb, 0.0f));
        result.setAmbientLight(ambientLight);

        return result;
    }
}
