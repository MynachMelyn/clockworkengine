

package clockworktest.conversion;

import com.clockwork.app.SimpleApplication;
import com.clockwork.font.BitmapText;
import com.clockwork.material.Material;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.shape.Quad;
import com.clockwork.texture.Image;
import com.clockwork.texture.Texture;
import clockworktools.converters.MipMapGenerator;

public class TestMipMapGen extends SimpleApplication {

    public static void main(String[] args){
        TestMipMapGen app = new TestMipMapGen();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        BitmapText txt = guiFont.createLabel("Left: HW Mips");
        txt.setLocalTranslation(0, settings.getHeight() - txt.getLineHeight() * 4, 0);
        guiNode.attachChild(txt);

        txt = guiFont.createLabel("Right: AWT Mips");
        txt.setLocalTranslation(0, settings.getHeight() - txt.getLineHeight() * 3, 0);
        guiNode.attachChild(txt);

        // create a simple plane/quad
        Quad quadMesh = new Quad(1, 1);
        quadMesh.updateGeometry(1, 1, false);
        quadMesh.updateBound();

        Geometry quad1 = new Geometry("Textured Quad", quadMesh);
        Geometry quad2 = new Geometry("Textured Quad 2", quadMesh);

        Texture tex = assetManager.loadTexture("Interface/Logo/Monkey.png");
        tex.setMinFilter(Texture.MinFilter.Trilinear);

        Texture texCustomMip = tex.clone();
        Image imageCustomMip = texCustomMip.getImage().clone();
        MipMapGenerator.generateMipMaps(imageCustomMip);
        texCustomMip.setImage(imageCustomMip);

        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setTexture("ColorMap", tex);

        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setTexture("ColorMap", texCustomMip);

        quad1.setMaterial(mat1);
//        quad1.setLocalTranslation(1, 0, 0);

        quad2.setMaterial(mat2);
        quad2.setLocalTranslation(1, 0, 0);

        rootNode.attachChild(quad1);
        rootNode.attachChild(quad2);
    }

}
