

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
import com.clockwork.scene.shape.Torus;

public class TestLightRadius extends SimpleApplication {

    float pos, vel=1;
    PointLight pl;
    Geometry lightMdl;

    public static void main(String[] args){
        TestLightRadius app = new TestLightRadius();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Torus torus = new Torus(10, 6, 1, 3);
//        Torus torus = new Torus(50, 30, 1, 3);
        Geometry g = new Geometry("Torus Geom", torus);
        g.rotate(-FastMath.HALF_PI, 0, 0);
        g.center();
//        g.move(0, 1, 0);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setFloat("Shininess", 32f);
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Ambient",  ColorRGBA.Black);
        mat.setColor("Diffuse",  ColorRGBA.White);
        mat.setColor("Specular", ColorRGBA.White);
//        mat.setBoolean("VertexLighting", true);
//        mat.setBoolean("LowQuality", true);
        g.setMaterial(mat);

        rootNode.attachChild(g);

        lightMdl = new Geometry("Light", new Sphere(10, 10, 0.1f));
        lightMdl.setMaterial(assetManager.loadMaterial("Common/Materials/RedColor.j3m"));
        rootNode.attachChild(lightMdl);

        pl = new PointLight();
        pl.setColor(ColorRGBA.Green);
        pl.setRadius(4f);
        rootNode.addLight(pl);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.Red);
        dl.setDirection(new Vector3f(0, 1, 0));
        rootNode.addLight(dl);
    }

    @Override
    public void simpleUpdate(float tpf){
//        cam.setLocation(new Vector3f(5.0347548f, 6.6481347f, 3.74853f));
//        cam.setRotation(new Quaternion(-0.19183293f, 0.80776674f, -0.37974006f, -0.40805697f));

        pos += tpf * vel * 5f;
        if (pos > 15){
            vel *= -1;
        }else if (pos < -15){
            vel *= -1;
        }
        
        pl.setPosition(new Vector3f(pos, 2, 0));
        lightMdl.setLocalTranslation(pl.getPosition());
    }

}
