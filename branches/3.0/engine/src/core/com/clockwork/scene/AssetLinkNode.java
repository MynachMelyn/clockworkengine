
package com.clockwork.scene;

import com.clockwork.asset.AssetInfo;
import com.clockwork.asset.AssetManager;
import com.clockwork.asset.ModelKey;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.export.binary.BinaryImporter;
import com.clockwork.util.SafeArrayList;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The AssetLinkNode does not store its children when exported to file.
 * Instead, you can add a list of AssetKeys that will be loaded and attached
 * when the AssetLinkNode is restored.
 * 
 */
public class AssetLinkNode extends Node {

    protected ArrayList<ModelKey> assetLoaderKeys = new ArrayList<ModelKey>();
    protected Map<ModelKey, Spatial> assetChildren = new HashMap<ModelKey, Spatial>();

    public AssetLinkNode() {
    }

    public AssetLinkNode(ModelKey key) {
        this(key.getName(), key);
    }

    public AssetLinkNode(String name, ModelKey key) {
        super(name);
        assetLoaderKeys.add(key);
    }

    /**
     * Add a "linked" child. These are loaded from the assetManager when the
     * AssetLinkNode is loaded from a binary file.
     * @param key
     */
    public void addLinkedChild(ModelKey key) {
        if (assetLoaderKeys.contains(key)) {
            return;
        }
        assetLoaderKeys.add(key);
    }

    public void removeLinkedChild(ModelKey key) {
        assetLoaderKeys.remove(key);
    }

    public ArrayList<ModelKey> getAssetLoaderKeys() {
        return assetLoaderKeys;
    }

    public void attachLinkedChild(AssetManager manager, ModelKey key) {
        addLinkedChild(key);
        Spatial child = manager.loadAsset(key);
        assetChildren.put(key, child);
        attachChild(child);
    }

    public void attachLinkedChild(Spatial spat, ModelKey key) {
        addLinkedChild(key);
        assetChildren.put(key, spat);
        attachChild(spat);
    }

    public void detachLinkedChild(ModelKey key) {
        Spatial spatial = assetChildren.get(key);
        if (spatial != null) {
            detachChild(spatial);
        }
        removeLinkedChild(key);
        assetChildren.remove(key);
    }

    public void detachLinkedChild(Spatial child, ModelKey key) {
        removeLinkedChild(key);
        assetChildren.remove(key);
        detachChild(child);
    }

    /**
     * Loads the linked children AssetKeys from the AssetManager and attaches them to the Node
     * If they are already attached, they will be reloaded.
     * @param manager
     */
    public void attachLinkedChildren(AssetManager manager) {
        detachLinkedChildren();
        for (Iterator<ModelKey> it = assetLoaderKeys.iterator(); it.hasNext();) {
            ModelKey assetKey = it.next();
            Spatial curChild = assetChildren.get(assetKey);
            if (curChild != null) {
                curChild.removeFromParent();
            }
            Spatial child = manager.loadAsset(assetKey);
            attachChild(child);
            assetChildren.put(assetKey, child);
        }
    }

    public void detachLinkedChildren() {
        Set<Entry<ModelKey, Spatial>> set = assetChildren.entrySet();
        for (Iterator<Entry<ModelKey, Spatial>> it = set.iterator(); it.hasNext();) {
            Entry<ModelKey, Spatial> entry = it.next();
            entry.getValue().removeFromParent();
            it.remove();
        }
    }

    @Override
    public void read(CWImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        BinaryImporter importer = BinaryImporter.getInstance();
        AssetManager loaderManager = e.getAssetManager();

        assetLoaderKeys = (ArrayList<ModelKey>) capsule.readSavableArrayList("assetLoaderKeyList", new ArrayList<ModelKey>());
        for (Iterator<ModelKey> it = assetLoaderKeys.iterator(); it.hasNext();) {
            ModelKey modelKey = it.next();
            AssetInfo info = loaderManager.locateAsset(modelKey);
            Spatial child = null;
            if (info != null) {
                child = (Spatial) importer.load(info);
            }
            if (child != null) {
                child.parent = this;
                children.add(child);
                assetChildren.put(modelKey, child);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot locate {0} for asset link node {1}", 
                                                                    new Object[]{ modelKey, key });
            }
        }
    }

    @Override
    public void write(CWExporter e) throws IOException {
        SafeArrayList<Spatial> childs = children;
        children = new SafeArrayList<Spatial>(Spatial.class);
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.writeSavableArrayList(assetLoaderKeys, "assetLoaderKeyList", null);
        children = childs;
    }
}
