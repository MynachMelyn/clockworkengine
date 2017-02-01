
package com.clockwork.material;

import com.clockwork.asset.AssetKey;
import com.clockwork.asset.AssetProcessor;

public class MaterialProcessor implements AssetProcessor {

    public Object postProcess(AssetKey key, Object obj) {
        return null;
    }

    public Object createClone(Object obj) {
        return ((Material) obj).clone();
    }
}
