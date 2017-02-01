

package clockworktest.asset;

import com.clockwork.app.SimpleApplication;
import com.clockwork.asset.TextureKey;
import com.clockwork.asset.plugins.HttpZipLocator;
import com.clockwork.material.Material;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.shape.Quad;
import com.clockwork.texture.Texture;

/**
 * This tests loading a file from a jar stored online.
 */
public class TestOnlineJar extends SimpleApplication {

    public static void main(String[] args){
        TestOnlineJar app = new TestOnlineJar();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // create a simple plane/quad
        Quad quadMesh = new Quad(1, 1);
        quadMesh.updateGeometry(1, 1, true);

        Geometry quad = new Geometry("Textured Quad", quadMesh);
        assetManager.registerLocator("http://jmonkeyengine.googlecode.com/files/town.zip",
                           HttpZipLocator.class);

        TextureKey key = new TextureKey("grass.jpg", false);
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
