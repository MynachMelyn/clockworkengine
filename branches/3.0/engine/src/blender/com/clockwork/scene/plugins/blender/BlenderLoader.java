
package com.clockwork.scene.plugins.blender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clockwork.asset.AssetInfo;
import com.clockwork.asset.BlenderKey;
import com.clockwork.asset.BlenderKey.FeaturesToLoad;
import com.clockwork.asset.BlenderKey.LoadingResults;
import com.clockwork.asset.BlenderKey.WorldData;
import com.clockwork.asset.ModelKey;
import com.clockwork.scene.CameraNode;
import com.clockwork.scene.LightNode;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.plugins.blender.animations.ArmatureHelper;
import com.clockwork.scene.plugins.blender.animations.IpoHelper;
import com.clockwork.scene.plugins.blender.cameras.CameraHelper;
import com.clockwork.scene.plugins.blender.constraints.ConstraintHelper;
import com.clockwork.scene.plugins.blender.curves.CurvesHelper;
import com.clockwork.scene.plugins.blender.exceptions.BlenderFileException;
import com.clockwork.scene.plugins.blender.file.BlenderInputStream;
import com.clockwork.scene.plugins.blender.file.FileBlockHeader;
import com.clockwork.scene.plugins.blender.file.Structure;
import com.clockwork.scene.plugins.blender.lights.LightHelper;
import com.clockwork.scene.plugins.blender.materials.MaterialHelper;
import com.clockwork.scene.plugins.blender.meshes.MeshHelper;
import com.clockwork.scene.plugins.blender.modifiers.ModifierHelper;
import com.clockwork.scene.plugins.blender.objects.ObjectHelper;
import com.clockwork.scene.plugins.blender.particles.ParticlesHelper;
import com.clockwork.scene.plugins.blender.textures.TextureHelper;

/**
 * This is the main loading class. Have in notice that asset manager needs to have loaders for resources like textures.
 * @author Marcin Roguski (Kaelthas)
 */
public class BlenderLoader extends AbstractBlenderLoader {

    private static final Logger     LOGGER = Logger.getLogger(BlenderLoader.class.getName());

    /** The blocks read from the file. */
    protected List<FileBlockHeader> blocks;

    public Spatial load(AssetInfo assetInfo) throws IOException {
        try {
            this.setup(assetInfo);

            List<FileBlockHeader> sceneBlocks = new ArrayList<FileBlockHeader>();
            BlenderKey blenderKey = blenderContext.getBlenderKey();
            LoadingResults loadingResults = blenderKey.prepareLoadingResults();
            WorldData worldData = null;// a set of data used in different scene aspects
            for (FileBlockHeader block : blocks) {
                switch (block.getCode()) {
                    case FileBlockHeader.BLOCK_OB00:// Object
                        Object object = this.toObject(block.getStructure(blenderContext));
                        if (object instanceof LightNode && (blenderKey.getFeaturesToLoad() & FeaturesToLoad.LIGHTS) != 0) {
                            loadingResults.addLight((LightNode) object);
                        } else if (object instanceof CameraNode && (blenderKey.getFeaturesToLoad() & FeaturesToLoad.CAMERAS) != 0) {
                            loadingResults.addCamera((CameraNode) object);
                        } else if (object instanceof Node && (blenderKey.getFeaturesToLoad() & FeaturesToLoad.OBJECTS) != 0) {
                            LOGGER.log(Level.FINE, "{0}: {1}--> {2}", new Object[] { ((Node) object).getName(), ((Node) object).getLocalTranslation().toString(), ((Node) object).getParent() == null ? "null" : ((Node) object).getParent().getName() });
                            if (this.isRootObject(loadingResults, (Node) object)) {
                                loadingResults.addObject((Node) object);
                            }
                        }
                        break;
                    // case FileBlockHeader.BLOCK_MA00:// Material
                    // if (blenderKey.isLoadUnlinkedAssets() && (blenderKey.getFeaturesToLoad() & FeaturesToLoad.MATERIALS) != 0) {
                    // loadingResults.addMaterial(this.toMaterial(block.getStructure(blenderContext)));
                    // }
                    // break;
                    case FileBlockHeader.BLOCK_SC00:// Scene
                        if ((blenderKey.getFeaturesToLoad() & FeaturesToLoad.SCENES) != 0) {
                            sceneBlocks.add(block);
                        }
                        break;
                    case FileBlockHeader.BLOCK_WO00:// World
                        if (blenderKey.isLoadUnlinkedAssets() && worldData == null) {// onlu one world data is used
                            Structure worldStructure = block.getStructure(blenderContext);
                            String worldName = worldStructure.getName();
                            if (blenderKey.getUsedWorld() == null || blenderKey.getUsedWorld().equals(worldName)) {
                                worldData = this.toWorldData(worldStructure);
                                if ((blenderKey.getFeaturesToLoad() & FeaturesToLoad.LIGHTS) != 0) {
                                    loadingResults.addLight(worldData.getAmbientLight());
                                }
                            }
                        }
                        break;
                }
            }

            // bake constraints after everything is loaded
            ConstraintHelper constraintHelper = blenderContext.getHelper(ConstraintHelper.class);
            constraintHelper.bakeConstraints(blenderContext);

            // load the scene at the very end so that the root nodes have no parent during loading or constraints applying
            for (FileBlockHeader sceneBlock : sceneBlocks) {
                loadingResults.addScene(this.toScene(sceneBlock.getStructure(blenderContext)));
            }

            blenderContext.dispose();
            return loadingResults;
        } catch (BlenderFileException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    /**
     * This method indicates if the given spatial is a root object. It means it
     * has no parent or is directly attached to one of the already loaded scene
     * nodes.
     * 
     * @param loadingResults
     *            loading results containing the scene nodes
     * @param spatial
     *            spatial object
     * @return <b>true</b> if the given spatial is a root object and
     *         <b>false</b> otherwise
     */
    protected boolean isRootObject(LoadingResults loadingResults, Spatial spatial) {
        if (spatial.getParent() == null) {
            return true;
        }
        for (Node scene : loadingResults.getScenes()) {
            if (spatial.getParent().equals(scene)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method sets up the loader.
     * @param assetInfo
     *            the asset info
     * @throws BlenderFileException
     *             an exception is throw when something wrong happens with blender file
     */
    protected void setup(AssetInfo assetInfo) throws BlenderFileException {
        // registering loaders
        ModelKey modelKey = (ModelKey) assetInfo.getKey();
        BlenderKey blenderKey;
        if (modelKey instanceof BlenderKey) {
            blenderKey = (BlenderKey) modelKey;
        } else {
            blenderKey = new BlenderKey(modelKey.getName());
            blenderKey.setAssetRootPath(modelKey.getFolder());
        }

        // opening stream
        BlenderInputStream inputStream = new BlenderInputStream(assetInfo.openStream());

        // reading blocks
        blocks = new ArrayList<FileBlockHeader>();
        FileBlockHeader fileBlock;
        blenderContext = new BlenderContext();
        blenderContext.setBlenderVersion(inputStream.getVersionNumber());
        blenderContext.setAssetManager(assetInfo.getManager());
        blenderContext.setInputStream(inputStream);
        blenderContext.setBlenderKey(blenderKey);

        // creating helpers
        blenderContext.putHelper(ArmatureHelper.class, new ArmatureHelper(inputStream.getVersionNumber(), blenderContext));
        blenderContext.putHelper(TextureHelper.class, new TextureHelper(inputStream.getVersionNumber(), blenderContext));
        blenderContext.putHelper(MeshHelper.class, new MeshHelper(inputStream.getVersionNumber(), blenderContext));
        blenderContext.putHelper(ObjectHelper.class, new ObjectHelper(inputStream.getVersionNumber(), blenderContext));
        blenderContext.putHelper(CurvesHelper.class, new CurvesHelper(inputStream.getVersionNumber(), blenderContext));
        blenderContext.putHelper(LightHelper.class, new LightHelper(inputStream.getVersionNumber(), blenderContext));
        blenderContext.putHelper(CameraHelper.class, new CameraHelper(inputStream.getVersionNumber(), blenderContext));
        blenderContext.putHelper(ModifierHelper.class, new ModifierHelper(inputStream.getVersionNumber(), blenderContext));
        blenderContext.putHelper(MaterialHelper.class, new MaterialHelper(inputStream.getVersionNumber(), blenderContext));
        blenderContext.putHelper(ConstraintHelper.class, new ConstraintHelper(inputStream.getVersionNumber(), blenderContext));
        blenderContext.putHelper(IpoHelper.class, new IpoHelper(inputStream.getVersionNumber(), blenderContext));
        blenderContext.putHelper(ParticlesHelper.class, new ParticlesHelper(inputStream.getVersionNumber(), blenderContext));

        // reading the blocks (dna block is automatically saved in the blender context when found)
        FileBlockHeader sceneFileBlock = null;
        do {
            fileBlock = new FileBlockHeader(inputStream, blenderContext);
            if (!fileBlock.isDnaBlock()) {
                blocks.add(fileBlock);
                // save the scene's file block
                if (fileBlock.getCode() == FileBlockHeader.BLOCK_SC00) {
                    sceneFileBlock = fileBlock;
                }
            }
        } while (!fileBlock.isLastBlock());
        if (sceneFileBlock != null) {
            blenderContext.setSceneStructure(sceneFileBlock.getStructure(blenderContext));
        }
    }
}
