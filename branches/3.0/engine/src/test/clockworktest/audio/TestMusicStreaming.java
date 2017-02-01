

package clockworktest.audio;

import com.clockwork.app.SimpleApplication;
import com.clockwork.asset.plugins.UrlLocator;
import com.clockwork.audio.AudioNode;

public class TestMusicStreaming extends SimpleApplication {

    public static void main(String[] args){
        TestMusicStreaming test = new TestMusicStreaming();
        test.start();
    }

    @Override
    public void simpleInitApp(){
        assetManager.registerLocator("http://www.vorbis.com/music/", UrlLocator.class);
        AudioNode audioSource = new AudioNode(assetManager, "Lumme-Badloop.ogg", true);
        audioSource.setPositional(false);
        audioSource.setReverbEnabled(false);
        audioSource.play();
    }

    @Override
    public void simpleUpdate(float tpf){}

}