

package clockworktest.scene;

import com.clockwork.app.SimpleApplication;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;

public class TestUserData extends SimpleApplication {

    public static void main(String[] args) {
        TestUserData app = new TestUserData();
        app.start();
    }

    public void simpleInitApp() {
        Node scene = (Node) assetManager.loadModel("Scenes/DotScene/DotScene.scene");
        System.out.println("Scene: " + scene);

        Spatial testNode = scene.getChild("TestNode");
        System.out.println("TestNode: "+ testNode);

        for (String key : testNode.getUserDataKeys()){
            System.out.println("Property " + key + " = " + testNode.getUserData(key));
        }
    }
}
