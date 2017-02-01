

package clockworktest.light;

import com.clockwork.app.SimpleApplication;
import com.clockwork.light.DirectionalLight;
import com.clockwork.light.PointLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.FastMath;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.shape.Sphere;
import com.clockwork.util.TangentBinormalGenerator;

public class TestSimpleLighting extends SimpleApplication {

    float angle;
    PointLight pl;
    Geometry lightMdl;

    public static void main(String[] args){
        TestSimpleLighting app = new TestSimpleLighting();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Geometry teapot = (Geometry) assetManager.loadModel("Models/Teapot/Teapot.obj");
        TangentBinormalGenerator.generate(teapot.getMesh(), true);

        teapot.setLocalScale(2f);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
//        mat.selectTechnique("GBuf");
        mat.setFloat("Shininess", 12);
        mat.setBoolean("UseMaterialColors", true);

//        mat.setTexture("ColorRamp", assetManager.loadTexture("Textures/ColorRamp/cloudy.png"));
//
//        mat.setBoolean("VTangent", true);
//        mat.setBoolean("Minnaert", true);
//        mat.setBoolean("WardIso", true);
//        mat.setBoolean("VertexLighting", true);
//        mat.setBoolean("LowQuality", true);
//        mat.setBoolean("HighQuality", true);

        mat.setColor("Ambient",  ColorRGBA.Black);
        mat.setColor("Diffuse",  ColorRGBA.Gray);
        mat.setColor("Specular", ColorRGBA.Gray);
        
        teapot.setMaterial(mat);
        rootNode.attachChild(teapot);

        lightMdl = new Geometry("Light", new Sphere(10, 10, 0.1f));
        lightMdl.setMaterial(assetManager.loadMaterial("Common/Materials/RedColor.j3m"));
        lightMdl.getMesh().setStatic();
        rootNode.attachChild(lightMdl);

        pl = new PointLight();
        pl.setColor(ColorRGBA.White);
        pl.setRadius(4f);
        rootNode.addLight(pl);

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
        dl.setColor(ColorRGBA.Green);
        rootNode.addLight(dl);
    }

    @Override
    public void simpleUpdate(float tpf){
//        cam.setLocation(new Vector3f(2.0632997f, 1.9493936f, 2.6885238f));
//        cam.setRotation(new Quaternion(-0.053555284f, 0.9407851f, -0.17754152f, -0.28378546f));

        angle += tpf;
        angle %= FastMath.TWO_PI;
        
        pl.setPosition(new Vector3f(FastMath.cos(angle) * 2f, 0.5f, FastMath.sin(angle) * 2f));
        lightMdl.setLocalTranslation(pl.getPosition());
    }

}
