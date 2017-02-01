
package com.clockwork.terrain.geomipmap.grid;

import com.clockwork.asset.AssetManager;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Vector3f;
import com.clockwork.terrain.geomipmap.TerrainGridTileLoader;
import com.clockwork.terrain.geomipmap.TerrainQuad;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class AssetTileLoader implements TerrainGridTileLoader {

    private AssetManager manager;
    private String assetPath;
    private String name;
    private int size;
    private int patchSize;
    private int quadSize;

    public AssetTileLoader() {
    }

    public AssetTileLoader(AssetManager manager, String name, String assetPath) {
        this.manager = manager;
        this.name = name;
        this.assetPath = assetPath;
    }

    public TerrainQuad getTerrainQuadAt(Vector3f location) {
        String modelName = assetPath + "/" + name + "_" + Math.round(location.x) + "_" + Math.round(location.y) + "_" + Math.round(location.z) + ".j3o";
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Load terrain grid tile: {0}", modelName);
        TerrainQuad quad = null;
        try {
            quad = (TerrainQuad) manager.loadModel(modelName);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        if (quad == null) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Could not load terrain grid tile: {0}", modelName);
            quad = createNewQuad(location);
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Loaded terrain grid tile: {0}", modelName);
        }
        return quad;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public String getName() {
        return name;
    }

    public void setPatchSize(int patchSize) {
        this.patchSize = patchSize;
    }

    public void setQuadSize(int quadSize) {
        this.quadSize = quadSize;
    }

    private TerrainQuad createNewQuad(Vector3f location) {
        TerrainQuad q = new TerrainQuad("Quad" + location, patchSize, quadSize, null);
        return q;
    }

    public void write(JmeExporter ex) throws IOException {
        OutputCapsule c = ex.getCapsule(this);
        c.write(assetPath, "assetPath", null);
        c.write(name, "name", null);
    }

    public void read(JmeImporter im) throws IOException {
        InputCapsule c = im.getCapsule(this);
        manager = im.getAssetManager();
        assetPath = c.readString("assetPath", null);
        name = c.readString("name", null);
    }
}