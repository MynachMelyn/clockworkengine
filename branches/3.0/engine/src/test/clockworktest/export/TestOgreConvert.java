

package clockworktest.export;

import com.clockwork.animation.AnimChannel;
import com.clockwork.animation.AnimControl;
import com.clockwork.app.SimpleApplication;
import com.clockwork.export.binary.BinaryExporter;
import com.clockwork.export.binary.BinaryImporter;
import com.clockwork.light.DirectionalLight;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestOgreConvert extends SimpleApplication {

    public static void main(String[] args){
        TestOgreConvert app = new TestOgreConvert();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Spatial ogreModel = assetManager.loadModel("Models/Oto/Oto.mesh.xml");

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(0,-1,-1).normalizeLocal());
        rootNode.addLight(dl);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BinaryExporter exp = new BinaryExporter();
            exp.save(ogreModel, baos);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            BinaryImporter imp = new BinaryImporter();
            imp.setAssetManager(assetManager);
            Node ogreModelReloaded = (Node) imp.load(bais, null, null);
            
            AnimControl control = ogreModelReloaded.getControl(AnimControl.class);
            AnimChannel chan = control.createChannel();
            chan.setAnim("Walk");

            rootNode.attachChild(ogreModelReloaded);
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
