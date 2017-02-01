

package clockworktest.model.anim;

import com.clockwork.animation.*;
import com.clockwork.app.SimpleApplication;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.DirectionalLight;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.shape.Box;

public class TestOgreAnim extends SimpleApplication 
        implements AnimEventListener, ActionListener {

    private AnimChannel channel;
    private AnimControl control;
    private Geometry geom;

    public static void main(String[] args) {
        TestOgreAnim app = new TestOgreAnim();
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

        Spatial model = (Spatial) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        model.center();

        control = model.getControl(AnimControl.class);
        control.addListener(this);
        channel = control.createChannel();

        for (String anim : control.getAnimationNames())
            System.out.println(anim);

        channel.setAnim("stand");
        geom = (Geometry)((Node)model).getChild(0);
        SkeletonControl skeletonControl = model.getControl(SkeletonControl.class);

        Box b = new Box(.25f,3f,.25f);
        Geometry item = new Geometry("Item", b);
        item.move(0, 1.5f, 0);
        item.setMaterial(assetManager.loadMaterial("Common/Materials/RedColor.j3m"));
        Node n = skeletonControl.getAttachmentsNode("hand.right");
        n.attachChild(item);

        rootNode.attachChild(model);

        inputManager.addListener(this, "Attack");
        inputManager.addMapping("Attack", new KeyTrigger(KeyInput.KEY_SPACE));
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
//                        geom.getMesh().createCollisionData();

    }
    

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if (animName.equals("Dodge")){
            channel.setAnim("stand", 0.50f);
            channel.setLoopMode(LoopMode.DontLoop);
            channel.setSpeed(1f);
        }
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Attack") && value){
            if (!channel.getAnimationName().equals("Dodge")){
                channel.setAnim("Dodge", 0.50f);
                channel.setLoopMode(LoopMode.Cycle);
                channel.setSpeed(0.10f);
            }
        }
    }

}
