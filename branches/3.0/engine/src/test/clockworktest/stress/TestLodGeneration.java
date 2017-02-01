
package clockworktest.stress;

import com.clockwork.animation.AnimChannel;
import com.clockwork.animation.AnimControl;
import com.clockwork.animation.SkeletonControl;
import com.clockwork.app.SimpleApplication;
import com.clockwork.bounding.BoundingBox;
import com.clockwork.font.BitmapText;
import com.clockwork.input.ChaseCamera;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.AmbientLight;
import com.clockwork.light.DirectionalLight;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.FastMath;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.VertexBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import clockworktools.optimize.LodGenerator;

public class TestLodGeneration extends SimpleApplication {

    public static void main(String[] args) {
        TestLodGeneration app = new TestLodGeneration();
        app.start();
    }
    boolean wireFrame = false;
    float reductionvalue = 0.0f;
    private int lodLevel = 0;
    private Node model;
    private BitmapText hudText;
    private List<Geometry> listGeoms = new ArrayList<Geometry>();
    private ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(5);
    private AnimChannel ch;

    public void simpleInitApp() {

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
        rootNode.addLight(dl);
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.6f));
        rootNode.addLight(al);

       // model = (Node) assetManager.loadModel("Models/Sinbad/Sinbad.mesh.xml");
       model = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");
        BoundingBox b = ((BoundingBox) model.getWorldBound());
        model.setLocalScale(1.2f / (b.getYExtent() * 2));
        //  model.setLocalTranslation(0,-(b.getCenter().y - b.getYExtent())* model.getLocalScale().y, 0);
        for (Spatial spatial : model.getChildren()) {
            if (spatial instanceof Geometry) {
                listGeoms.add((Geometry) spatial);
            }
        }
        ChaseCamera chaseCam = new ChaseCamera(cam, inputManager);
        model.addControl(chaseCam);
        chaseCam.setLookAtOffset(b.getCenter());
        chaseCam.setDefaultDistance(5);
        chaseCam.setMinVerticalRotation(-FastMath.HALF_PI + 0.01f);
        chaseCam.setZoomSensitivity(0.5f);



//           ch = model.getControl(AnimControl.class).createChannel();
//          ch.setAnim("Wave");
        SkeletonControl c = model.getControl(SkeletonControl.class);
        if (c != null) {
            c.setEnabled(false);
        }


        reductionvalue = 0.80f;
        lodLevel = 1;
        for (final Geometry geometry : listGeoms) {
            LodGenerator lodGenerator = new LodGenerator(geometry);          
            lodGenerator.bakeLods(LodGenerator.TriangleReductionMethod.PROPORTIONAL, reductionvalue);
            geometry.setLodLevel(lodLevel);

        }

        rootNode.attachChild(model);
        flyCam.setEnabled(false);



        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize());
        hudText.setText(computeNbTri() + " tris");
        hudText.setLocalTranslation(cam.getWidth() / 2, hudText.getLineHeight(), 0);
        guiNode.attachChild(hudText);

        inputManager.addListener(new ActionListener() {
            public void onAction(String name, boolean isPressed, float tpf) {
                if (isPressed) {
                    if (name.equals("plus")) {
//                        lodLevel++;
//                        for (Geometry geometry : listGeoms) {
//                            if (geometry.getMesh().getNumLodLevels() <= lodLevel) {
//                                lodLevel = 0;
//                            }
//                            geometry.setLodLevel(lodLevel);
//                        }
//                        jaimeText.setText(computeNbTri() + " tris");



                        reductionvalue += 0.05f;
                        updateLod();



                    }
                    if (name.equals("minus")) {
//                        lodLevel--;
//                        for (Geometry geometry : listGeoms) {
//                            if (lodLevel < 0) {
//                                lodLevel = geometry.getMesh().getNumLodLevels() - 1;
//                            }
//                            geometry.setLodLevel(lodLevel);
//                        }
//                        jaimeText.setText(computeNbTri() + " tris");



                        reductionvalue -= 0.05f;
                        updateLod();


                    }
                    if (name.equals("wireFrame")) {
                        wireFrame = !wireFrame;
                        for (Geometry geometry : listGeoms) {
                            geometry.getMaterial().getAdditionalRenderState().setWireframe(wireFrame);
                        }
                    }

                }

            }

            private void updateLod() {
                reductionvalue = FastMath.clamp(reductionvalue, 0.0f, 1.0f);
                makeLod(LodGenerator.TriangleReductionMethod.PROPORTIONAL, reductionvalue, 1);
            }
        }, "plus", "minus", "wireFrame");

        inputManager.addMapping("plus", new KeyTrigger(KeyInput.KEY_ADD));
        inputManager.addMapping("minus", new KeyTrigger(KeyInput.KEY_SUBTRACT));
        inputManager.addMapping("wireFrame", new KeyTrigger(KeyInput.KEY_SPACE));



    }

    @Override
    public void simpleUpdate(float tpf) {
        //    model.rotate(0, tpf, 0);        
    }

    private int computeNbTri() {
        int nbTri = 0;
        for (Geometry geometry : listGeoms) {
            if (geometry.getMesh().getNumLodLevels() > 0) {
                nbTri += geometry.getMesh().getLodLevel(lodLevel).getNumElements();
            } else {
                nbTri += geometry.getMesh().getTriangleCount();
            }
        }
        return nbTri;
    }

    @Override
    public void destroy() {
        super.destroy();
        exec.shutdown();
    }

    private void makeLod(final LodGenerator.TriangleReductionMethod method, final float value, final int ll) {
        exec.execute(new Runnable() {
            public void run() {
                for (final Geometry geometry : listGeoms) {
                    LodGenerator lODGenerator = new LodGenerator(geometry);
                    final VertexBuffer[] lods = lODGenerator.computeLods(method, value);

                    enqueue(new Callable<Void>() {
                        public Void call() throws Exception {
                            geometry.getMesh().setLodLevels(lods);
                            lodLevel = 0;
                            if (geometry.getMesh().getNumLodLevels() > ll) {
                                lodLevel = ll;
                            }
                            geometry.setLodLevel(lodLevel);
                            hudText.setText(computeNbTri() + " tris");
                            return null;
                        }
                    });
                }
            }
        });

    }
}
