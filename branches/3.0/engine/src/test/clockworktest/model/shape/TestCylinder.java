

package clockworktest.model.shape;

import com.clockwork.app.SimpleApplication;
import com.clockwork.asset.TextureKey;
import com.clockwork.material.Material;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.shape.Cylinder;
import com.clockwork.texture.Texture;

public class TestCylinder extends SimpleApplication {

    public static void main(String[] args){
        TestCylinder app = new TestCylinder();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Cylinder t = new Cylinder(20, 50, 1, 2, true);
        Geometry geom = new Geometry("Cylinder", t);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key = new TextureKey("Interface/Logo/Monkey.jpg", true);
        key.setGenerateMips(true);
        Texture tex = assetManager.loadTexture(key);
        tex.setMinFilter(Texture.MinFilter.Trilinear);
        mat.setTexture("ColorMap", tex);

        geom.setMaterial(mat);
        
        rootNode.attachChild(geom);
    }

}
