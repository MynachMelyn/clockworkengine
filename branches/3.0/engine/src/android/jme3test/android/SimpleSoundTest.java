package jme3test.android;

import com.clockwork.app.SimpleApplication;
import com.clockwork.audio.AudioNode;
import com.clockwork.input.MouseInput;
import com.clockwork.input.controls.InputListener;
import com.clockwork.input.controls.MouseButtonTrigger;
import com.clockwork.math.Vector3f;

public class SimpleSoundTest extends SimpleApplication implements InputListener {

    private AudioNode gun;
    private AudioNode nature;

    @Override
    public void simpleInitApp() {
        gun = new AudioNode(assetManager, "Sound/Effects/Gun.wav");
        gun.setPositional(true);
        gun.setLocalTranslation(new Vector3f(0, 0, 0));
        gun.setMaxDistance(100);
        gun.setRefDistance(5);

        nature = new AudioNode(assetManager, "Sound/Environment/Nature.ogg", true);
        nature.setVolume(3);
        nature.setLooping(true);
        nature.play();

        inputManager.addMapping("click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "click");

        rootNode.attachChild(gun);
        rootNode.attachChild(nature);
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("click") && isPressed) {
            gun.playInstance();
        }
    }
}
