

package clockworktest.model.anim;

import com.clockwork.animation.AnimChannel;
import com.clockwork.animation.AnimControl;
import com.clockwork.app.SimpleApplication;
import com.clockwork.asset.BlenderKey;
import com.clockwork.light.DirectionalLight;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;

public class TestBlenderObjectAnim extends SimpleApplication {

    private AnimChannel channel;
    private AnimControl control;

    public static void main(String[] args) {
    	TestBlenderObjectAnim app = new TestBlenderObjectAnim();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10f);
        cam.setLocation(new Vector3f(6.4013605f, 7.488437f, 12.843031f));
        cam.setRotation(new Quaternion(-0.060740203f, 0.93925786f, -0.2398315f, -0.2378785f));

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f, -0.7f, -1).normalizeLocal());
        dl.setColor(new ColorRGBA(1f, 1f, 1f, 1.0f));
        rootNode.addLight(dl);

        BlenderKey blenderKey = new BlenderKey("Blender/2.4x/animtest.blend");
        
        Spatial scene = (Spatial) assetManager.loadModel(blenderKey);
        rootNode.attachChild(scene);
        
        Spatial model = this.findNode(rootNode, "TestAnim");
        model.center();
        
        control = model.getControl(AnimControl.class);
        channel = control.createChannel();

        channel.setAnim("TestAnim");
    }
    
    /**
     * This method finds a node of a given name.
     * @param rootNode the root node to search
     * @param name the name of the searched node
     * @return the found node or null
     */
    private Spatial findNode(Node rootNode, String name) {
        if (name.equals(rootNode.getName())) {
            return rootNode;
        }
        return rootNode.getChild(name);
    }
}
