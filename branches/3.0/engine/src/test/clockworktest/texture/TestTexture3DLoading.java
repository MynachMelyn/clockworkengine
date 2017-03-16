
package clockworktest.texture;

import com.clockwork.app.SimpleApplication;
import com.clockwork.asset.TextureKey;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector2f;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.shape.Quad;
import com.clockwork.texture.Texture;

public class TestTexture3DLoading extends SimpleApplication {

    public static void main(String[] args) {
        TestTexture3DLoading app = new TestTexture3DLoading();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        flyCam.setEnabled(false);


        Quad q = new Quad(10, 10);

        Geometry geom = new Geometry("Quad", q);
        Material material = new Material(assetManager, "CWtest/texture/tex3DThumb.j3md");
        TextureKey key = new TextureKey("Textures/3D/flame.dds");
        key.setGenerateMips(true);
        key.setAsTexture3D(true);

        Texture t = assetManager.loadTexture(key);

        int rows = 4;//4 * 4

        q.scaleTextureCoordinates(new Vector2f(rows, rows));

        //The image only have 8 pictures and we have 16 thumbs, the data will be interpolated by the GPU
        material.setFloat("InvDepth", 1f / 16f);
        material.setInt("Rows", rows);
        material.setTexture("Texture", t);
        geom.setMaterial(material);

        rootNode.attachChild(geom);

        cam.setLocation(new Vector3f(4.7444625f, 5.160054f, 13.1939f));
    }
}