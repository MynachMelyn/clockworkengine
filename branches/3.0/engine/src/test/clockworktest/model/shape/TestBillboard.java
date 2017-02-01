

package clockworktest.model.shape;

import com.clockwork.app.SimpleApplication;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.control.BillboardControl;
import com.clockwork.scene.shape.Box;
import com.clockwork.scene.shape.Quad;

/**
 *
 */
public class TestBillboard extends SimpleApplication {

    public void simpleInitApp() {
        flyCam.setMoveSpeed(10);

        Quad q = new Quad(2, 2);
        Geometry g = new Geometry("Quad", q);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        g.setMaterial(mat);

        Quad q2 = new Quad(1, 1);
        Geometry g3 = new Geometry("Quad2", q2);
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Yellow);
        g3.setMaterial(mat2);
        g3.setLocalTranslation(.5f, .5f, .01f);

        Box b = new Box(new Vector3f(0, 0, 3), .25f, .5f, .25f);
        Geometry g2 = new Geometry("Box", b);
        g2.setMaterial(mat);

        Node bb = new Node("billboard");

        BillboardControl control=new BillboardControl();
        
        bb.addControl(control);
        bb.attachChild(g);
        bb.attachChild(g3);       
        

        n=new Node("parent");
        n.attachChild(g2);
        n.attachChild(bb);
        rootNode.attachChild(n);

        n2=new Node("parentParent");
        n2.setLocalTranslation(Vector3f.UNIT_X.mult(5));
        n2.attachChild(n);

        rootNode.attachChild(n2);


//        rootNode.attachChild(bb);
//        rootNode.attachChild(g2);
    }
 Node n;
 Node n2;
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        n.rotate(0, tpf, 0);
        n.move(0.1f*tpf, 0, 0);
        n2.rotate(0, 0, -tpf);
    }



    public static void main(String[] args) {
        TestBillboard app = new TestBillboard();
        app.start();
    }
}