

package clockworktest.app;

import com.clockwork.app.SimpleApplication;
import com.clockwork.material.Material;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.shape.Box;
import com.clockwork.util.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class TestReleaseDirectMemory extends SimpleApplication {

    public static void main(String[] args){
        TestReleaseDirectMemory app = new TestReleaseDirectMemory();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));
        geom.setMaterial(mat);
        rootNode.attachChild(geom);
    }

    @Override
    public void simpleUpdate(float tpf) {
        ByteBuffer buf = BufferUtils.createByteBuffer(500000);
        BufferUtils.destroyDirectBuffer(buf);
        
        FloatBuffer buf2 = BufferUtils.createFloatBuffer(500000);
        BufferUtils.destroyDirectBuffer(buf2);
    }
    
}
