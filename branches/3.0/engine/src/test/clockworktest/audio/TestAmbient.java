
package clockworktest.audio;

import com.clockwork.app.SimpleApplication;
import com.clockwork.audio.AudioNode;
import com.clockwork.audio.Environment;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.shape.Box;

public class TestAmbient extends SimpleApplication {

  private AudioNode nature, waves;

  public static void main(String[] args) {
    TestAmbient test = new TestAmbient();
    test.start();
  }

  @Override
  public void simpleInitApp() {
    float[] eax = new float[]{15, 38.0f, 0.300f, -1000, -3300, 0,
      1.49f, 0.54f, 1.00f, -2560, 0.162f, 0.00f, 0.00f,
      0.00f, -229, 0.088f, 0.00f, 0.00f, 0.00f, 0.125f, 1.000f,
      0.250f, 0.000f, -5.0f, 5000.0f, 250.0f, 0.00f, 0x3f};
    Environment env = new Environment(eax);
    audioRenderer.setEnvironment(env);

    waves = new AudioNode(assetManager, "Sound/Environment/Ocean Waves.ogg", false);
    waves.setPositional(true);
    waves.setLocalTranslation(new Vector3f(0, 0,0));
    waves.setMaxDistance(100);
    waves.setRefDistance(5);

    nature = new AudioNode(assetManager, "Sound/Environment/Nature.ogg", true);
    nature.setPositional(false);
    nature.setVolume(3);
    
    waves.playInstance();
    nature.play();
    
    // just a blue box to mark the spot
    Box box1 = new Box(Vector3f.ZERO, .5f, .5f, .5f);
    Geometry player = new Geometry("Player", box1);
    Material mat1 = new Material(assetManager,
            "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.Blue);
    player.setMaterial(mat1);
    rootNode.attachChild(player);
  }

  @Override
  public void simpleUpdate(float tpf) {
  }
}
