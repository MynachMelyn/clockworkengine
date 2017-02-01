

package clockworktest.helloworld;

import com.clockwork.animation.AnimChannel;
import com.clockwork.animation.AnimControl;
import com.clockwork.animation.AnimEventListener;
import com.clockwork.animation.LoopMode;
import com.clockwork.app.SimpleApplication;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.DirectionalLight;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Node;

/** Sample 7 - how to load an OgreXML model and play an animation, 
 * using channels, a controller, and an AnimEventListener. */
public class HelloAnimation extends SimpleApplication
                         implements AnimEventListener {

  Node player;
  private AnimChannel channel;
  private AnimControl control;

  public static void main(String[] args) {
    HelloAnimation app = new HelloAnimation();
    app.start();
  }

  @Override
  public void simpleInitApp() {
    viewPort.setBackgroundColor(ColorRGBA.LightGray);
    initKeys();

    /** Add a light source so we can see the model */
    DirectionalLight dl = new DirectionalLight();
    dl.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
    rootNode.addLight(dl);

    /** Load a model that contains animation */
    player = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
    player.setLocalScale(0.5f);
    rootNode.attachChild(player);

    /** Create a controller and channels. */
    control = player.getControl(AnimControl.class);
    control.addListener(this);
    channel = control.createChannel();
    channel.setAnim("stand");
  }

  /** Use this listener to trigger something after an animation is done. */
  public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
    if (animName.equals("Walk")) {
      /** After "walk", reset to "stand". */
      channel.setAnim("stand", 0.50f);
      channel.setLoopMode(LoopMode.DontLoop);
      channel.setSpeed(1f);
    }
  }

  /** Use this listener to trigger something between two animations. */
  public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    // unused
  }

  /** Custom Keybindings: Mapping a named action to a key input. */
  private void initKeys() {
    inputManager.addMapping("Walk", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(actionListener, "Walk");
  }

  /** Definining the named action that can be triggered by key inputs. */
  private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
      if (name.equals("Walk") && !keyPressed) {
        if (!channel.getAnimationName().equals("Walk")) {
          /** Play the "walk" animation! */
          channel.setAnim("Walk", 0.50f);
          channel.setLoopMode(LoopMode.Loop);
        }
      }
    }
  };

}
