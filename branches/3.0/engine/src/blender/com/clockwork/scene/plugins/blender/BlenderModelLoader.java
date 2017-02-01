
package com.clockwork.scene.plugins.blender;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clockwork.asset.AssetInfo;
import com.clockwork.asset.BlenderKey;
import com.clockwork.asset.BlenderKey.FeaturesToLoad;
import com.clockwork.scene.LightNode;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.plugins.blender.constraints.ConstraintHelper;
import com.clockwork.scene.plugins.blender.exceptions.BlenderFileException;
import com.clockwork.scene.plugins.blender.file.FileBlockHeader;

/**
 * This is the main loading class. Have in notice that asset manager needs to have loaders for resources like textures.
 */
public class BlenderModelLoader extends BlenderLoader {

    private static final Logger LOGGER = Logger.getLogger(BlenderModelLoader.class.getName());

    @Override
    public Spatial load(AssetInfo assetInfo) throws IOException {
        try {
            this.setup(assetInfo);

            BlenderKey blenderKey = blenderContext.getBlenderKey();
            Node modelRoot = new Node(blenderKey.getName());

            for (FileBlockHeader block : blocks) {
                if (block.getCode() == FileBlockHeader.BLOCK_OB00) {
                    Object object = this.toObject(block.getStructure(blenderContext));

                    if (object instanceof LightNode && (blenderKey.getFeaturesToLoad() & FeaturesToLoad.LIGHTS) != 0) {
                        modelRoot.addLight(((LightNode) object).getLight());
                        modelRoot.attachChild((LightNode) object);
                    } else if (object instanceof Node && (blenderKey.getFeaturesToLoad() & FeaturesToLoad.OBJECTS) != 0) {
                        LOGGER.log(Level.FINE, "{0}: {1}--> {2}", new Object[] { ((Node) object).getName(), ((Node) object).getLocalTranslation().toString(), ((Node) object).getParent() == null ? "null" : ((Node) object).getParent().getName() });
                        if (((Node) object).getParent() == null) {
                            modelRoot.attachChild((Node) object);
                        }
                    }
                }
            }

            // bake constraints after everything is loaded
            ConstraintHelper constraintHelper = blenderContext.getHelper(ConstraintHelper.class);
            constraintHelper.bakeConstraints(blenderContext);

            blenderContext.dispose();
            return modelRoot;
        } catch (BlenderFileException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }
}
