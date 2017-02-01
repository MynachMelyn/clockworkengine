

package clockworktest.helloworld;

import com.clockwork.app.SimpleApplication;
import com.clockwork.input.KeyInput;
import com.clockwork.input.MouseInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.AnalogListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.input.controls.MouseButtonTrigger;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.shape.Box;

/** Sample 5 - how to map keys and mousebuttons to actions */
public class HelloInput extends SimpleApplication {

  public static void main(String[] args) {
    HelloInput app = new HelloInput();
    app.start();
  }
  protected Geometry player;
  Boolean isRunning=true;

  @Override
  public void simpleInitApp() {
    Box b = new Box(Vector3f.ZERO, 1, 1, 1);
    player = new Geometry("Player", b);
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", ColorRGBA.Blue);
    player.setMaterial(mat);
    rootNode.attachChild(player);
    initKeys(); // load my custom keybinding
  }

  /** Custom Keybinding: Map named actions to inputs. */
  private void initKeys() {
    /** You can map one or several inputs to one named mapping. */
    inputManager.addMapping("Pause",  new KeyTrigger(keyInput.KEY_P));
    inputManager.addMapping("Left",   new KeyTrigger(KeyInput.KEY_J));
    inputManager.addMapping("Right",  new KeyTrigger(KeyInput.KEY_K));
    inputManager.addMapping("Rotate", new KeyTrigger(KeyInput.KEY_SPACE), // spacebar!
                                      new MouseButtonTrigger(MouseInput.BUTTON_LEFT) );        // left click!
    /** Add the named mappings to the action listeners. */
    inputManager.addListener(actionListener,"Pause");
    inputManager.addListener(analogListener,"Left", "Right", "Rotate");
  }

  /** Use this listener for KeyDown/KeyUp events */
  private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
      if (name.equals("Pause") && !keyPressed) {
        isRunning = !isRunning;
      }
    }
  };

  /** Use this listener for continuous events */
  private AnalogListener analogListener = new AnalogListener() {
    public void onAnalog(String name, float value, float tpf) {
      if (isRunning) {
        if (name.equals("Rotate")) {
          player.rotate(0, value, 0);
        }
        if (name.equals("Right")) {
          player.move((new Vector3f(value, 0,0)) );
        }
        if (name.equals("Left")) {
          player.move(new Vector3f(-value, 0,0));
        }
      } else {
        System.out.println("Press P to unpause.");
      }
    }
  };

}
