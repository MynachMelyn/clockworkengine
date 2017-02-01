
package clockworktest.post;

import com.clockwork.app.SimpleApplication;
import com.clockwork.light.AmbientLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector2f;
import com.clockwork.math.Vector3f;
import com.clockwork.post.FilterPostProcessor;
import com.clockwork.post.ssao.SSAOFilter;
import com.clockwork.scene.Geometry;
import com.clockwork.texture.Texture;

public class TestSSAO extends SimpleApplication {

    Geometry model;

    public static void main(String[] args) {
        TestSSAO app = new TestSSAO();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(68.45442f, 8.235511f, 7.9676695f));
        cam.setRotation(new Quaternion(0.046916496f, -0.69500375f, 0.045538206f, 0.7160271f));


        flyCam.setMoveSpeed(50);

        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture diff = assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
        diff.setWrap(Texture.WrapMode.Repeat);
        Texture norm = assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall_normal.jpg");
        norm.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("DiffuseMap", diff);
        mat.setTexture("NormalMap", norm);
        mat.setFloat("Shininess", 2.0f);


        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(1.8f, 1.8f, 1.8f, 1.0f));

        rootNode.addLight(al);

        model = (Geometry) assetManager.loadModel("Models/Sponza/Sponza.j3o");
        model.getMesh().scaleTextureCoordinates(new Vector2f(2, 2));

        model.setMaterial(mat);

        rootNode.attachChild(model);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        SSAOFilter ssaoFilter = new SSAOFilter(12.940201f, 43.928635f, 0.32999992f, 0.6059958f);
        fpp.addFilter(ssaoFilter);
        SSAOUI ui = new SSAOUI(inputManager, ssaoFilter);

        viewPort.addProcessor(fpp);
    }

    @Override
    public void simpleUpdate(float tpf) {
    }
}
