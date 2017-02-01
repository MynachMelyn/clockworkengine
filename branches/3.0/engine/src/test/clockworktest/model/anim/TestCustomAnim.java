

package clockworktest.model.anim;

import com.clockwork.animation.Bone;
import com.clockwork.animation.Skeleton;
import com.clockwork.animation.SkeletonControl;
import com.clockwork.app.SimpleApplication;
import com.clockwork.light.AmbientLight;
import com.clockwork.light.DirectionalLight;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.VertexBuffer;
import com.clockwork.scene.VertexBuffer.Format;
import com.clockwork.scene.VertexBuffer.Type;
import com.clockwork.scene.VertexBuffer.Usage;
import com.clockwork.scene.shape.Box;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class TestCustomAnim extends SimpleApplication {

    private Bone bone;
    private Skeleton skeleton;
    private Quaternion rotation = new Quaternion();

    public static void main(String[] args) {
        TestCustomAnim app = new TestCustomAnim();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        AmbientLight al = new AmbientLight();
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(Vector3f.UNIT_XYZ.negate());
        rootNode.addLight(dl);

        Box box = new Box(1, 1, 1);

        // Setup bone weight buffer
        FloatBuffer weights = FloatBuffer.allocate( box.getVertexCount() * 4 );
        VertexBuffer weightsBuf = new VertexBuffer(Type.BoneWeight);
        weightsBuf.setupData(Usage.CpuOnly, 4, Format.Float, weights);
        box.setBuffer(weightsBuf);

        // Setup bone index buffer
        ByteBuffer indices = ByteBuffer.allocate( box.getVertexCount() * 4 );
        VertexBuffer indicesBuf = new VertexBuffer(Type.BoneIndex);
        indicesBuf.setupData(Usage.CpuOnly, 4, Format.UnsignedByte, indices);
        box.setBuffer(indicesBuf);

        // Create bind pose buffers
        box.generateBindPose(true);

        // Create skeleton
        bone = new Bone("root");
        bone.setBindTransforms(Vector3f.ZERO, Quaternion.IDENTITY, Vector3f.UNIT_XYZ);
        bone.setUserControl(true);
        skeleton = new Skeleton(new Bone[]{ bone });

        // Assign all verticies to bone 0 with weight 1
        for (int i = 0; i < box.getVertexCount() * 4; i += 4){
            // assign vertex to bone index 0
            indices.array()[i+0] = 0;
            indices.array()[i+1] = 0;
            indices.array()[i+2] = 0;
            indices.array()[i+3] = 0;

            // set weight to 1 only for first entry
            weights.array()[i+0] = 1;
            weights.array()[i+1] = 0;
            weights.array()[i+2] = 0;
            weights.array()[i+3] = 0;
        }

        // Maximum number of weights per bone is 1
        box.setMaxNumWeights(1);

        // Create model
        Geometry geom = new Geometry("box", box);
        geom.setMaterial(assetManager.loadMaterial("Textures/Terrain/BrickWall/BrickWall.j3m"));
        Node model = new Node("model");
        model.attachChild(geom);

        // Create skeleton control
        SkeletonControl skeletonControl = new SkeletonControl(skeleton);
        model.addControl(skeletonControl);

        rootNode.attachChild(model);
    }

    @Override
    public void simpleUpdate(float tpf){
        // Rotate around X axis
        Quaternion rotate = new Quaternion();
        rotate.fromAngleAxis(tpf, Vector3f.UNIT_X);

        // Combine rotation with previous
        rotation.multLocal(rotate);

        // Set new rotation into bone
        bone.setUserTransforms(Vector3f.ZERO, rotation, Vector3f.UNIT_XYZ);

        // After changing skeleton transforms, must update world data
        skeleton.updateWorldVectors();
    }

}
