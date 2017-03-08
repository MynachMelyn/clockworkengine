package clockworktest.effect;

import com.clockwork.app.SimpleApplication;
import com.clockwork.effect.ParticleEmitter;
import com.clockwork.effect.ParticleMesh;
import com.clockwork.effect.shapes.EmitterSphereShape;
import com.clockwork.input.KeyInput;
import com.clockwork.input.MouseInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.input.controls.MouseButtonTrigger;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.post.FilterPostProcessor;
import com.clockwork.post.filters.TranslucentBucketFilter;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.shape.Box;

public class TestSoftParticles extends SimpleApplication {

    private boolean softParticles = true;
    private FilterPostProcessor fpp;
    private TranslucentBucketFilter tbf;
    private Node particleNode;

    public static void main(String[] args) {
        TestSoftParticles app = new TestSoftParticles();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        cam.setLocation(new Vector3f(-7.2221026f, 4.1183004f, 7.759811f));
        cam.setRotation(new Quaternion(0.06152846f, 0.91236454f, -0.1492115f, 0.37621948f));

        flyCam.setMoveSpeed(10);


        //floor
        Box b = new Box(Vector3f.ZERO, 10, 0.1f, 10);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Gray);
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));
        geom.setMaterial(mat);
        rootNode.attachChild(geom);

        Box b2 = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom2 = new Geometry("Box", b2);
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.DarkGray);
        geom2.setMaterial(mat2);
        rootNode.attachChild(geom2);
        geom2.setLocalScale(0.1f, 0.2f, 1);

        fpp = new FilterPostProcessor(assetManager);        
        tbf = new TranslucentBucketFilter(true);
        fpp.addFilter(tbf);
        viewPort.addProcessor(fpp);

        particleNode = new Node("particleNode");
        rootNode.attachChild(particleNode);
        
        createParticles();

        
        inputManager.addListener(new ActionListener() {

            public void onAction(String name, boolean isPressed, float tpf) {
                if(isPressed && name.equals("toggle")){
               //     tbf.setEnabled(!tbf.isEnabled());     
                    softParticles = !softParticles;
                    if(softParticles){
                        viewPort.addProcessor(fpp);
                    }else{
                        viewPort.removeProcessor(fpp);
                    }
                }
            }
        }, "toggle");
        inputManager.addMapping("toggle", new KeyTrigger(KeyInput.KEY_SPACE));
        
        // Clear particles and emit a new set
        inputManager.addListener(new ActionListener() {
            public void onAction(String name, boolean isPressed, float tpf) {
                if(isPressed && name.equals("refire")) {
                    //fpp.removeFilter(tbf); // Add back in to fix?
                    particleNode.detachAllChildren();
                    createParticles();
                    //fpp.addFilter(tbf);
                }
            }
        }, "refire");
        inputManager.addMapping("refire", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    }

    private void createParticles() {
        
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        material.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        material.setFloat("Softness", 3f); // 
        
        //Fire
        ParticleEmitter fire = new ParticleEmitter("Fire", ParticleMesh.Type.Triangle, 30);
        fire.setMaterial(material);
        fire.setShape(new EmitterSphereShape(Vector3f.ZERO, 0.1f));
        fire.setImagesX(2);
        fire.setImagesY(2); // 2x2 texture animation
        fire.setEndColor(new ColorRGBA(1f, 0f, 0f, 1f)); // red
        fire.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
        fire.setStartSize(0.6f);
        fire.setEndSize(0.01f);
        fire.setGravity(0, -0.3f, 0);
        fire.setLowLife(0.5f);
        fire.setHighLife(3f);
        fire.setLocalTranslation(0, 0.2f, 0);

        particleNode.attachChild(fire);
        
        
        ParticleEmitter smoke = new ParticleEmitter("Smoke", ParticleMesh.Type.Triangle, 30);
        smoke.setMaterial(material);
        smoke.setShape(new EmitterSphereShape(Vector3f.ZERO, 5));
        smoke.setImagesX(1);
        smoke.setImagesY(1); // 2x2 texture animation
        smoke.setStartColor(new ColorRGBA(0.1f, 0.1f, 0.1f,1f)); // dark gray
        smoke.setEndColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.3f)); // gray      
        smoke.setStartSize(3f);
        smoke.setEndSize(5f);
        smoke.setGravity(0, -0.001f, 0);
        smoke.setLowLife(100f);
        smoke.setHighLife(100f);
        smoke.setLocalTranslation(0, 0.1f, 0);        
        smoke.emitAllParticles();
        
        particleNode.attachChild(smoke);
    }
}
