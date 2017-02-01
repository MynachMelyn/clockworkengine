

package clockworktest.model.shape;

import com.clockwork.app.SimpleApplication;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.debug.Arrow;
import com.clockwork.scene.debug.Grid;
import com.clockwork.scene.debug.WireBox;
import com.clockwork.scene.debug.WireSphere;

public class TestDebugShapes extends SimpleApplication {

    public static void main(String[] args){
        TestDebugShapes app = new TestDebugShapes();
        app.start();
    }

    public Geometry putShape(Mesh shape, ColorRGBA color){
        Geometry g = new Geometry("shape", shape);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        rootNode.attachChild(g);
        return g;
    }

    public void putArrow(Vector3f pos, Vector3f dir, ColorRGBA color){
        Arrow arrow = new Arrow(dir);
        arrow.setLineWidth(4); // make arrow thicker
        putShape(arrow, color).setLocalTranslation(pos);
    }

    public void putBox(Vector3f pos, float size, ColorRGBA color){
        putShape(new WireBox(size, size, size), color).setLocalTranslation(pos);
    }

    public void putGrid(Vector3f pos, ColorRGBA color){
        putShape(new Grid(6, 6, 0.2f), color).center().move(pos);
    }

    public void putSphere(Vector3f pos, ColorRGBA color){
        putShape(new WireSphere(1), color).setLocalTranslation(pos);
    }

    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(2,1.5f,2));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        putArrow(Vector3f.ZERO, Vector3f.UNIT_X, ColorRGBA.Red);
        putArrow(Vector3f.ZERO, Vector3f.UNIT_Y, ColorRGBA.Green);
        putArrow(Vector3f.ZERO, Vector3f.UNIT_Z, ColorRGBA.Blue);

        putBox(new Vector3f(2, 0, 0), 0.5f, ColorRGBA.Yellow);
        putGrid(new Vector3f(3.5f, 0, 0), ColorRGBA.White);
        putSphere(new Vector3f(4.5f, 0, 0), ColorRGBA.Magenta);
    }

}
