

package clockworktest.asset;

import com.clockwork.app.SimpleApplication;
import com.clockwork.asset.TextureKey;
import com.clockwork.asset.plugins.UrlLocator;
import com.clockwork.material.Material;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.shape.Quad;
import com.clockwork.texture.Texture;

/**
 * Load an image and display it from the internet using the UrlLocator.
 */
public class TestUrlLoading extends SimpleApplication {

    public static void main(String[] args){
        TestUrlLoading app = new TestUrlLoading();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // create a simple plane/quad
        Quad quadMesh = new Quad(1, 1);
        quadMesh.updateGeometry(1, 1, true);

        Geometry quad = new Geometry("Textured Quad", quadMesh);

        assetManager.registerLocator("https://jmonkeyengine.googlecode.com/svn/BookSamples/assets/Textures/",
                                UrlLocator.class);
        TextureKey key = new TextureKey("mucha-window.png", false);
        key.setGenerateMips(true);
        Texture tex = assetManager.loadTexture(key);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        quad.setMaterial(mat);

        float aspect = tex.getImage().getWidth() / (float) tex.getImage().getHeight();
        quad.setLocalScale(new Vector3f(aspect * 1.5f, 1.5f, 1));
        quad.center();

        rootNode.attachChild(quad);
    }

}
