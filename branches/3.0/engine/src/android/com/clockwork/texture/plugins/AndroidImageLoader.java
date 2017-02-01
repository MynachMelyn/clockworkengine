package com.clockwork.texture.plugins;

import android.graphics.Bitmap;
import com.clockwork.asset.AndroidImageInfo;
import com.clockwork.asset.AssetInfo;
import com.clockwork.asset.AssetLoader;
import com.clockwork.texture.Image;
import java.io.IOException;

public class AndroidImageLoader implements AssetLoader {

    public Object load(AssetInfo info) throws IOException {
        AndroidImageInfo imageInfo = new AndroidImageInfo(info);
        Bitmap bitmap = imageInfo.getBitmap();
        
        Image image = new Image(imageInfo.getFormat(), bitmap.getWidth(), bitmap.getHeight(), null);
        image.setEfficentData(imageInfo);
        return image;
    }
}
