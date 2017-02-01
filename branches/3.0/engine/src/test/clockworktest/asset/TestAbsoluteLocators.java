

package clockworktest.asset;

import com.clockwork.asset.AssetManager;
import com.clockwork.asset.DesktopAssetManager;
import com.clockwork.asset.plugins.ClasspathLocator;
import com.clockwork.audio.AudioData;
import com.clockwork.audio.plugins.WAVLoader;
import com.clockwork.texture.Texture;
import com.clockwork.texture.plugins.AWTLoader;

public class TestAbsoluteLocators {
    public static void main(String[] args){
        AssetManager am = new DesktopAssetManager();

        am.registerLoader(AWTLoader.class, "jpg");
        am.registerLoader(WAVLoader.class, "wav");

        // register absolute locator
        am.registerLocator("/",  ClasspathLocator.class);

        // find a sound
        AudioData audio = am.loadAudio("Sound/Effects/Gun.wav");

        // find a texture
        Texture tex = am.loadTexture("Textures/Terrain/Pond/Pond.jpg");

        if (audio == null)
            throw new RuntimeException("Cannot find audio!");
        else
            System.out.println("Audio loaded from Sounds/Effects/Gun.wav");

        if (tex == null)
            throw new RuntimeException("Cannot find texture!");
        else
            System.out.println("Texture loaded from Textures/Terrain/Pond/Pond.jpg");

        System.out.println("Success!");
    }
}
