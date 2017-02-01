
package com.clockwork.system.ios;

import com.clockwork.asset.AssetInfo;
import com.clockwork.asset.AssetLoader;
import com.clockwork.texture.Image;
import com.clockwork.texture.Image.Format;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class IosImageLoader implements AssetLoader {

    public Object load(AssetInfo info) throws IOException {
        InputStream in = info.openStream();
        Image img = null;
        try {
            img = loadImageData(Image.Format.RGBA8, in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
        return img;
    }

    /**
     * Loads images via iOS native API
     *
     * @param format has to be Image.Format.RGBA8
     * @param inputStream the InputStream to load the image data from
     * @return the loaded Image
     */
    private static native Image loadImageData(Format format, InputStream inputStream);
}
