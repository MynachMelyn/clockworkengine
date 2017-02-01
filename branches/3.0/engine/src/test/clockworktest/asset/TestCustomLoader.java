

package clockworktest.asset;

import com.clockwork.asset.AssetLoader;
import com.clockwork.asset.AssetManager;
import com.clockwork.asset.plugins.ClasspathLocator;
import com.clockwork.system.JmeSystem;

/**
 * Demonstrates loading a file from a custom {@link AssetLoader}
 */
public class TestCustomLoader {
    public static void main(String[] args){
        AssetManager assetManager = JmeSystem.newAssetManager();
        assetManager.registerLocator("/", ClasspathLocator.class);
        assetManager.registerLoader(TextLoader.class, "fnt");
        System.out.println(assetManager.loadAsset("Interface/Fonts/Console.fnt"));
    }
}
