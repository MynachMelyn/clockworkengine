

package clockworktest.helloworld;

import com.clockwork.app.SimpleApplication;
import com.clockwork.font.BitmapText;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.Material;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.shape.Box;

/** Sample 3 - how to load an OBJ model, and OgreXML model, 
 * a material/texture, or text. */
public class HelloAssets extends SimpleApplication {

    public static void main(String[] args) {
        HelloAssets app = new HelloAssets();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        /** Load a teapot model (OBJ file from test-data) */
        Spatial teapot = assetManager.loadModel("Models/Teapot/Teapot.obj");
        Material mat_default = new Material( assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        teapot.setMaterial(mat_default);
        rootNode.attachChild(teapot);

        /** Create a wall (Box with material and texture from test-data) */
        Box box = new Box(Vector3f.ZERO, 2.5f,2.5f,1.0f);
        Spatial wall = new Geometry("Box", box );
        Material mat_brick = new Material( assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_brick.setTexture("ColorMap", assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg"));
        wall.setMaterial(mat_brick);
        wall.setLocalTranslation(2.0f,-2.5f,0.0f);
        rootNode.attachChild(wall);

        /** Display a line of text (default font from test-data) */
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText helloText = new BitmapText(guiFont, false);
        helloText.setSize(guiFont.getCharSet().getRenderedSize());
        helloText.setText("Hello World");
        helloText.setLocalTranslation(300, helloText.getLineHeight(), 0);
        guiNode.attachChild(helloText);

        /** Load a Ninja model (OgreXML + material + texture from test_data) */
        Spatial ninja = assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        ninja.scale(0.05f, 0.05f, 0.05f);
        ninja.rotate(0.0f, -3.0f, 0.0f);
        ninja.setLocalTranslation(0.0f, -5.0f, -2.0f);
        rootNode.attachChild(ninja);
        /** You must add a light to make the model visible */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f).normalizeLocal());
        rootNode.addLight(sun);
    }
}
