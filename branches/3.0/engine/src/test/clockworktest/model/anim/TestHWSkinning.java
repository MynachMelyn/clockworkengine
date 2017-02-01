
package clockworktest.model.anim;

import com.clockwork.animation.*;
import com.clockwork.app.SimpleApplication;
import com.clockwork.font.BitmapText;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.DirectionalLight;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Spatial;
import java.util.ArrayList;
import java.util.List;

public class TestHWSkinning extends SimpleApplication implements ActionListener{

    private AnimChannel channel;
    private AnimControl control;
    private String[] animNames = {"Dodge", "Walk", "pull", "push"};
    private final static int SIZE = 10;
    private boolean hwSkinningEnable = true;
    private List<SkeletonControl> skControls = new ArrayList<SkeletonControl>();
    private BitmapText hwsText;

    public static void main(String[] args) {
        TestHWSkinning app = new TestHWSkinning();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10f);
        cam.setLocation(new Vector3f(3.8664846f, 6.2704787f, 9.664585f));
        cam.setRotation(new Quaternion(-0.054774776f, 0.94064945f, -0.27974048f, -0.18418397f));
        makeHudText();
 
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f, -0.7f, -1).normalizeLocal());
        dl.setColor(new ColorRGBA(1f, 1f, 1f, 1.0f));
        rootNode.addLight(dl);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Spatial model = (Spatial) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
                model.setLocalScale(0.1f);
                model.setLocalTranslation(i - SIZE / 2, 0, j - SIZE / 2);
                control = model.getControl(AnimControl.class);

                channel = control.createChannel();
                channel.setAnim(animNames[(i + j) % 4]);
                SkeletonControl skeletonControl = model.getControl(SkeletonControl.class);
                skeletonControl.setHardwareSkinningPreferred(hwSkinningEnable);
                skControls.add(skeletonControl);
                rootNode.attachChild(model);
            }
        }

        inputManager.addListener(this, "toggleHWS");
        inputManager.addMapping("toggleHWS", new KeyTrigger(KeyInput.KEY_SPACE));
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(isPressed && name.equals("toggleHWS")){
            hwSkinningEnable = !hwSkinningEnable;
            for (SkeletonControl control : skControls) {
                control.setHardwareSkinningPreferred(hwSkinningEnable);
                hwsText.setText("HWS : "+ hwSkinningEnable);
            }
        }
    }

    private void makeHudText() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        hwsText = new BitmapText(guiFont, false);
        hwsText.setSize(guiFont.getCharSet().getRenderedSize());
        hwsText.setText("HWS : "+ hwSkinningEnable);
        hwsText.setLocalTranslation(0, cam.getHeight(), 0);
        guiNode.attachChild(hwsText);
    }
}
