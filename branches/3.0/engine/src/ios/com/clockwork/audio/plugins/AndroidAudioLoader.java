package com.clockwork.audio.plugins;

import com.clockwork.asset.AssetInfo;
import com.clockwork.asset.AssetLoader;
import com.clockwork.audio.android.AndroidAudioData;
import java.io.IOException;

/**
 * <code>AndroidAudioLoader</code> will create an 
 * {@link AndroidAudioData} object with the specified asset key.
 */
public class AndroidAudioLoader implements AssetLoader {

    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        AndroidAudioData result = new AndroidAudioData();
        result.setAssetKey(assetInfo.getKey());
        return result;
    }
}
