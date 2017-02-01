

package clockworktest.audio;

import com.clockwork.app.SimpleApplication;
import com.clockwork.audio.AudioNode;
import com.clockwork.audio.Environment;
import com.clockwork.math.FastMath;
import com.clockwork.math.Vector3f;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

/**
 * Test Doppler Effect
 */
public class TestDoppler extends SimpleApplication {

    private AudioNode ufo;

    private float x = 20, z = 0;
    private float rate     = -0.05f;
    private float xDist    = 20;
    private float zDist    = 5;
    private float angle    = FastMath.TWO_PI;
    
    public static void main(String[] args){
        TestDoppler test = new TestDoppler();
        test.start();
    }

    @Override
    public void simpleInitApp(){
        audioRenderer.setEnvironment(Environment.Dungeon);
        AL10.alDistanceModel(AL11.AL_EXPONENT_DISTANCE);
        
        ufo  = new AudioNode(assetManager, "Sound/Effects/Beep.ogg", false);
        ufo.setPositional(true);
        ufo.setLooping(true);
        ufo.setReverbEnabled(true);
        ufo.setRefDistance(100000000);
        ufo.setMaxDistance(100000000);
        ufo.play();
    }

    @Override
    public void simpleUpdate(float tpf){
        //float x  = (float) (Math.cos(angle) * xDist);
        float dx = (float)  Math.sin(angle) * xDist; 
        
        //float z  = (float) (Math.sin(angle) * zDist);
        float dz = (float)(-Math.cos(angle) * zDist);
        
        x += dx * tpf * 0.05f;
        z += dz * tpf * 0.05f;
        
        angle += tpf * rate;
        
        if (angle > FastMath.TWO_PI){
            angle = FastMath.TWO_PI;
            rate = -rate;
        }else if (angle < -0){
            angle = -0;
            rate = -rate;
        }
        
        ufo.setVelocity(new Vector3f(dx, 0, dz));
        ufo.setLocalTranslation(x, 0, z);
        ufo.updateGeometricState();
        
        System.out.println("LOC: " + (int)x +", " + (int)z + 
                ", VEL: " + (int)dx + ", " + (int)dz);
    }

}
