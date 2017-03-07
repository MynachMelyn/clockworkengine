
package jme3test.android;

import com.clockwork.app.SimpleApplication;
import com.clockwork.effect.ParticleEmitter;
import com.clockwork.effect.ParticleMesh.Type;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.AmbientLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.FastMath;
import com.clockwork.math.Vector3f;

/**
 * Particle that moves in a circle.
 *
 */
public class TestMovingParticle extends SimpleApplication {
    
    private ParticleEmitter emit;
    private float angle = 0;
    
    public static void main(String[] args) {
        TestMovingParticle app = new TestMovingParticle();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        emit = new ParticleEmitter("Emitter", Type.Triangle, 300);
        emit.setGravity(0, 0, 0);
        emit.setVelocityVariation(1);
        emit.setLowLife(1);
        emit.setHighLife(1);
        emit.setInitialVelocity(new Vector3f(0, .5f, 0));
        emit.setImagesX(15);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", assetManager.loadTexture("Effects/Smoke/Smoke.png"));
        emit.setMaterial(mat);
        
        rootNode.attachChild(emit);
        
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(0.84f, 0.80f, 0.80f, 1.0f));
        rootNode.addLight(al);
        
        
        
        inputManager.addListener(new ActionListener() {
            
            public void onAction(String name, boolean isPressed, float tpf) {
                if ("setNum".equals(name) && isPressed) {
                    emit.setNumParticles(1000);
                }
            }
        }, "setNum");
        
        inputManager.addMapping("setNum", new KeyTrigger(KeyInput.KEY_SPACE));
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        angle += tpf;
        angle %= FastMath.TWO_PI;
        float x = FastMath.cos(angle) * 2;
        float y = FastMath.sin(angle) * 2;
        emit.setLocalTranslation(x, 0, y);
    }
}
