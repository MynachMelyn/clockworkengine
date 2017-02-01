

package jme3test.android;

import com.clockwork.app.SimpleApplication;
import com.clockwork.light.AmbientLight;
import com.clockwork.light.DirectionalLight;
import com.clockwork.light.PointLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.FastMath;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.plugins.ogre.OgreMeshKey;
import com.clockwork.scene.shape.Sphere;
import com.clockwork.util.TangentBinormalGenerator;

public class TestBumpModel extends SimpleApplication {

    float angle;
    PointLight pl;
    Spatial lightMdl;

    public static void main(String[] args){
        TestBumpModel app = new TestBumpModel();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Spatial signpost = (Spatial) assetManager.loadAsset(new OgreMeshKey("Models/Sign Post/Sign Post.mesh.xml"));
        signpost.setMaterial( (Material) assetManager.loadMaterial("Models/Sign Post/Sign Post.j3m"));
        TangentBinormalGenerator.generate(signpost);
        rootNode.attachChild(signpost);

        lightMdl = new Geometry("Light", new Sphere(10, 10, 0.1f));
        lightMdl.setMaterial( (Material) assetManager.loadMaterial("Common/Materials/RedColor.j3m"));
        rootNode.attachChild(lightMdl);

        // flourescent main light
        pl = new PointLight();
        pl.setColor(new ColorRGBA(0.88f, 0.92f, 0.95f, 1.0f));
        rootNode.addLight(pl);
        
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(0.44f, 0.40f, 0.20f, 1.0f));
        rootNode.addLight(al);
        
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(1,-1,-1).normalizeLocal());
        dl.setColor(new ColorRGBA(0.92f, 0.85f, 0.8f, 1.0f));
        rootNode.addLight(dl);
    }

    @Override
    public void simpleUpdate(float tpf){
        angle += tpf * 0.25f;
        angle %= FastMath.TWO_PI;

        pl.setPosition(new Vector3f(FastMath.cos(angle) * 6f, 3f, FastMath.sin(angle) * 6f));
        lightMdl.setLocalTranslation(pl.getPosition());
    }

}
