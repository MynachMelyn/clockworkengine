

package jme3test.android;

import android.media.SoundPool;
import com.clockwork.app.SimpleApplication;
import com.clockwork.audio.AudioNode;
import com.clockwork.math.Vector3f;

public class TestAmbient extends SimpleApplication {

    private AudioNode footsteps, beep;
    private AudioNode nature, waves;
    
    SoundPool soundPool;
    
//    private PointAudioSource waves;
    private float time = 0;
    private float nextTime = 1;

    public static void main(String[] args){
        TestAmbient test = new TestAmbient();
        test.start();
    }
    

    @Override
    public void simpleInitApp()
    {     
        /*
        footsteps  = new AudioNode(audioRenderer, assetManager, "Sound/Effects/Foot steps.ogg", true);
        
        footsteps.setPositional(true);
        footsteps.setLocalTranslation(new Vector3f(4, -1, 30));
        footsteps.setMaxDistance(5);
        footsteps.setRefDistance(1);
        footsteps.setLooping(true);

        beep = new AudioNode(audioRenderer, assetManager, "Sound/Effects/Beep.ogg", true);
        beep.setVolume(3);
        beep.setLooping(true);
        
        audioRenderer.playSourceInstance(footsteps);
        audioRenderer.playSource(beep);
        */
        
        waves  = new AudioNode(assetManager, "Sound/Environment/Ocean Waves.ogg", true);
        waves.setPositional(true);

        nature = new AudioNode(assetManager, "Sound/Environment/Nature.ogg", true);
        
        waves.setLocalTranslation(new Vector3f(4, -1, 30));
        waves.setMaxDistance(5);
        waves.setRefDistance(1);
        
        nature.setVolume(3);
        audioRenderer.playSourceInstance(waves);
        audioRenderer.playSource(nature);
    }

    @Override
    public void simpleUpdate(float tpf)
    {

    }

}
