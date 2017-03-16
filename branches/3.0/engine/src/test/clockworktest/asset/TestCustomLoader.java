

package clockworktest.asset;

import com.clockwork.asset.AssetLoader;
import com.clockwork.asset.AssetManager;
import com.clockwork.asset.plugins.ClasspathLocator;
import com.clockwork.system.CWSystem;

/**
 * Demonstrates loading a file from a custom AssetLoader}
 */
public class TestCustomLoader {
    public static void main(String[] args){
        AssetManager assetManager = CWSystem.newAssetManager();
        assetManager.registerLocator("/", ClasspathLocator.class);
        assetManager.registerLoader(TextLoader.class, "fnt");
        System.out.println(assetManager.loadAsset("Interface/Fonts/Console.fnt"));
    }
}
