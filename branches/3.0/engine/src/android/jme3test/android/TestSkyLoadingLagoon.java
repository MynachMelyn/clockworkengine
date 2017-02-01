

package jme3test.android;

import com.clockwork.app.SimpleApplication;
import com.clockwork.scene.Spatial;
import com.clockwork.texture.Texture;
import com.clockwork.util.SkyFactory;

public class TestSkyLoadingLagoon extends SimpleApplication {

    public static void main(String[] args){
        TestSkyLoadingLagoon app = new TestSkyLoadingLagoon();
        app.start();
    }

    public void simpleInitApp() {
        
        Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
        Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
        Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
        Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
        Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
        Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");
        
        
        /*
        Texture west = assetManager.loadTexture("Textures/Sky/Primitives/primitives_positive_x.png");
        Texture east = assetManager.loadTexture("Textures/Sky/Primitives/primitives_negative_x.png");
        Texture north = assetManager.loadTexture("Textures/Sky/Primitives/primitives_negative_z.png");
        Texture south = assetManager.loadTexture("Textures/Sky/Primitives/primitives_positive_z.png");
        Texture up = assetManager.loadTexture("Textures/Sky/Primitives/primitives_positive_y.png");
        Texture down = assetManager.loadTexture("Textures/Sky/Primitives/primitives_negative_y.png");
        */
        
        Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        rootNode.attachChild(sky);
    }

}
