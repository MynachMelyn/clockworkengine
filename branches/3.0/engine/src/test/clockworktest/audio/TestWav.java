
package clockworktest.audio;

import com.clockwork.app.SimpleApplication;
import com.clockwork.audio.AudioNode;

public class TestWav extends SimpleApplication {

  private float time = 0;
  private AudioNode audioSource;

  public static void main(String[] args) {
    TestWav test = new TestWav();
    test.start();
  }

  @Override
  public void simpleUpdate(float tpf) {
    time += tpf;
    if (time > 1f) {
      audioSource.playInstance();
      time = 0;
    }

  }

  @Override
  public void simpleInitApp() {
    audioSource = new AudioNode(assetManager, "Sound/Effects/Gun.wav", false);
    audioSource.setLooping(false);
  }
}
