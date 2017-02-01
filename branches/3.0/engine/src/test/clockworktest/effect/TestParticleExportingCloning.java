

package clockworktest.effect;

import com.clockwork.app.SimpleApplication;
import com.clockwork.effect.ParticleEmitter;
import com.clockwork.effect.ParticleMesh.Type;
import com.clockwork.effect.shapes.EmitterSphereShape;
import com.clockwork.export.binary.BinaryExporter;
import com.clockwork.export.binary.BinaryImporter;
import com.clockwork.material.Material;
import com.clockwork.math.Vector3f;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestParticleExportingCloning extends SimpleApplication {

    public static void main(String[] args){
        TestParticleExportingCloning app = new TestParticleExportingCloning();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        ParticleEmitter emit = new ParticleEmitter("Emitter", Type.Triangle, 200);
        emit.setShape(new EmitterSphereShape(Vector3f.ZERO, 1f));
        emit.setGravity(0, 0, 0);
        emit.setLowLife(5);
        emit.setHighLife(10);
        emit.setInitialVelocity(new Vector3f(0, 0, 0));
        emit.setImagesX(15);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", assetManager.loadTexture("Effects/Smoke/Smoke.png"));
        emit.setMaterial(mat);

        ParticleEmitter emit2 = emit.clone();
        emit2.move(3, 0, 0);
        
        rootNode.attachChild(emit);
        rootNode.attachChild(emit2);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            BinaryExporter.getInstance().save(emit, out);
            
            BinaryImporter imp = new BinaryImporter();
            imp.setAssetManager(assetManager);
            ParticleEmitter emit3 = (ParticleEmitter) imp.load(out.toByteArray());
            
            emit3.move(-3, 0, 0);
            rootNode.attachChild(emit3);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

            //        Camera cam2 = cam.clone();
    //        cam.setViewPortTop(0.5f);
    //        cam2.setViewPortBottom(0.5f);
    //        ViewPort vp = renderManager.createMainView("SecondView", cam2);
    //        viewPort.setClearEnabled(false);
    //        vp.attachScene(rootNode);
    }

}
