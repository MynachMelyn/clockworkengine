package jme3test.android;

import com.clockwork.app.SimpleApplication;
import com.clockwork.light.AmbientLight;
import com.clockwork.light.PointLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.shape.Sphere;
import com.clockwork.util.TangentBinormalGenerator;

public class TestUnshadedModel extends SimpleApplication {

    public static void main(String[] args){
        TestUnshadedModel app = new TestUnshadedModel();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Sphere sphMesh = new Sphere(32, 32, 1);
        sphMesh.setTextureMode(Sphere.TextureMode.Projected);
        sphMesh.updateGeometry(32, 32, 1, false, false);
        TangentBinormalGenerator.generate(sphMesh);

        Geometry sphere = new Geometry("Rock Ball", sphMesh);
        Material mat = assetManager.loadMaterial("Textures/Terrain/Pond/Pond.j3m");
        mat.setColor("Ambient", ColorRGBA.DarkGray);
        mat.setColor("Diffuse", ColorRGBA.White);
        mat.setBoolean("UseMaterialColors", true);
        sphere.setMaterial(mat);
        rootNode.attachChild(sphere);

        PointLight pl = new PointLight();
        pl.setColor(ColorRGBA.White);
        pl.setPosition(new Vector3f(4f, 0f, 0f));
        rootNode.addLight(pl);

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White);
        rootNode.addLight(al);
    }
}
