
package clockworktest.animation;

import com.clockwork.animation.AnimControl;
import com.clockwork.animation.AnimationFactory;
import com.clockwork.animation.LoopMode;
import com.clockwork.app.SimpleApplication;
import com.clockwork.cinematic.Cinematic;
import com.clockwork.cinematic.MotionPath;
import com.clockwork.cinematic.PlayState;
import com.clockwork.cinematic.events.*;
import com.clockwork.font.BitmapText;
import com.clockwork.input.ChaseCamera;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.light.DirectionalLight;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.FastMath;
import com.clockwork.math.Vector3f;
import com.clockwork.niftygui.NiftyJmeDisplay;
import com.clockwork.post.FilterPostProcessor;
import com.clockwork.post.filters.FadeFilter;
import com.clockwork.renderer.Caps;
import com.clockwork.renderer.queue.RenderQueue.ShadowMode;
import com.clockwork.scene.CameraNode;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.shape.Box;
import com.clockwork.shadow.PssmShadowRenderer;
import de.lessvoid.nifty.Nifty;

public class TestCinematic extends SimpleApplication {

    private Spatial model;
    private Spatial teapot;
    private MotionPath path;
    private MotionEvent cameraMotionEvent;
    private Cinematic cinematic;
    private ChaseCamera chaseCam;
    private FilterPostProcessor fpp;
    private FadeFilter fade;
    private float time = 0;

    public static void main(String[] args) {
        TestCinematic app = new TestCinematic();
        app.start();



    }

    @Override
    public void simpleInitApp() {
        //just some text
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(getAssetManager(),
                getInputManager(),
                getAudioRenderer(),
                getGuiViewPort());
        Nifty nifty;
        nifty = niftyDisplay.getNifty();
        nifty.fromXmlWithoutStartScreen("Interface/Nifty/CinematicTest.xml");
        getGuiViewPort().addProcessor(niftyDisplay);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        final BitmapText text = new BitmapText(guiFont, false);
        text.setSize(guiFont.getCharSet().getRenderedSize());
        text.setText("Press enter to play/pause cinematic");
        text.setLocalTranslation((cam.getWidth() - text.getLineWidth()) / 2, cam.getHeight(), 0);
        guiNode.attachChild(text);


        createScene();

        cinematic = new Cinematic(rootNode, 20);
        stateManager.attach(cinematic);

        createCameraMotion();

        //creating spatial animation for the teapot
        AnimationFactory factory = new AnimationFactory(20, "teapotAnim");
        factory.addTimeTranslation(0, new Vector3f(10, 0, 10));
        factory.addTimeTranslation(20, new Vector3f(10, 0, -10));
        factory.addTimeScale(10, new Vector3f(4, 4, 4));
        factory.addTimeScale(20, new Vector3f(1, 1, 1));
        factory.addTimeRotationAngles(20, 0, 4 * FastMath.TWO_PI, 0);
        AnimControl control = new AnimControl();
        control.addAnim(factory.buildAnimation());
        teapot.addControl(control);

        //fade in
        cinematic.addCinematicEvent(0, new FadeEvent(true));
        // cinematic.activateCamera(0, "aroundCam");
        cinematic.addCinematicEvent(0, new AnimationEvent(teapot, "teapotAnim", LoopMode.DontLoop));
        cinematic.addCinematicEvent(0, cameraMotionEvent);
        cinematic.addCinematicEvent(0, new SoundEvent("Sound/Environment/Nature.ogg", LoopMode.Loop));
        cinematic.addCinematicEvent(3f, new SoundEvent("Sound/Effects/kick.wav"));
        cinematic.addCinematicEvent(3, new SubtitleTrack(nifty, "start", 3, "engines really kick A..."));
        cinematic.addCinematicEvent(5.1f, new SoundEvent("Sound/Effects/Beep.ogg", 1));
        cinematic.addCinematicEvent(2, new AnimationEvent(model, "Walk", LoopMode.Loop));
        cinematic.activateCamera(0, "topView");
        //  cinematic.activateCamera(10, "aroundCam");

        //fade out
        cinematic.addCinematicEvent(19, new FadeEvent(false));
//        cinematic.addCinematicEvent(19, new AbstractCinematicEvent() {
//
//            @Override
//            public void onPlay() {
//                fade.setDuration(1f / cinematic.getSpeed());
//                fade.fadeOut();
//
//            }
//
//            @Override
//            public void onUpdate(float tpf) {
//            }
//
//            @Override
//            public void onStop() {
//            }
//
//            @Override
//            public void onPause() {
//            }
//        });

        cinematic.addListener(new CinematicEventListener() {

            public void onPlay(CinematicEvent cinematic) {
                chaseCam.setEnabled(false);
                System.out.println("play");
            }

            public void onPause(CinematicEvent cinematic) {
                System.out.println("pause");
            }

            public void onStop(CinematicEvent cinematic) {
                chaseCam.setEnabled(true);
                fade.setValue(1);
                System.out.println("stop");
            }
        });

        //cinematic.setSpeed(2);
        flyCam.setEnabled(false);
        chaseCam = new ChaseCamera(cam, model, inputManager);
        initInputs();

    }

    private void createCameraMotion() {

        CameraNode camNode = cinematic.bindCamera("topView", cam);
        camNode.setLocalTranslation(new Vector3f(0, 50, 0));
        camNode.lookAt(teapot.getLocalTranslation(), Vector3f.UNIT_Y);

        CameraNode camNode2 = cinematic.bindCamera("aroundCam", cam);
        path = new MotionPath();
        path.setCycle(true);
        path.addWayPoint(new Vector3f(20, 3, 0));
        path.addWayPoint(new Vector3f(0, 3, 20));
        path.addWayPoint(new Vector3f(-20, 3, 0));
        path.addWayPoint(new Vector3f(0, 3, -20));
        path.setCurveTension(0.83f);
        cameraMotionEvent = new MotionEvent(camNode2, path);
        cameraMotionEvent.setLoopMode(LoopMode.Loop);
        cameraMotionEvent.setLookAt(model.getWorldTranslation(), Vector3f.UNIT_Y);
        cameraMotionEvent.setDirectionType(MotionEvent.Direction.LookAt);

    }

    private void createScene() {

        model = (Spatial) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        model.center();
        model.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(model);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Cyan);

        teapot = assetManager.loadModel("Models/Teapot/Teapot.obj");
        teapot.setLocalTranslation(10, 0, 10);
        teapot.setMaterial(mat);
        teapot.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(teapot);

        Material matSoil = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matSoil.setBoolean("UseMaterialColors", true);
        matSoil.setColor("Ambient", ColorRGBA.Gray);
        matSoil.setColor("Diffuse", ColorRGBA.Green);
        matSoil.setColor("Specular", ColorRGBA.Black);

        Geometry soil = new Geometry("soil", new Box(new Vector3f(0, -6.0f, 0), 50, 1, 50));
        soil.setMaterial(matSoil);
        soil.setShadowMode(ShadowMode.Receive);
        rootNode.attachChild(soil);
        DirectionalLight light = new DirectionalLight();
        light.setDirection(new Vector3f(0, -1, -1).normalizeLocal());
        light.setColor(ColorRGBA.White.mult(1.5f));
        rootNode.addLight(light);

        fpp = new FilterPostProcessor(assetManager);
        fade = new FadeFilter();
        fpp.addFilter(fade);

        if (renderer.getCaps().contains(Caps.GLSL100)) {
            PssmShadowRenderer pssm = new PssmShadowRenderer(assetManager, 512, 1);
            pssm.setDirection(new Vector3f(0, -1, -1).normalizeLocal());
            pssm.setShadowIntensity(0.4f);
            viewPort.addProcessor(pssm);
            viewPort.addProcessor(fpp);
        }
    }

    private void initInputs() {
        inputManager.addMapping("togglePause", new KeyTrigger(keyInput.KEY_RETURN));
        inputManager.addMapping("navFwd", new KeyTrigger(keyInput.KEY_RIGHT));
        inputManager.addMapping("navBack", new KeyTrigger(keyInput.KEY_LEFT));
        ActionListener acl = new ActionListener() {

            public void onAction(String name, boolean keyPressed, float tpf) {
                if (name.equals("togglePause") && keyPressed) {
                    if (cinematic.getPlayState() == PlayState.Playing) {
                        cinematic.pause();
                        time = cinematic.getTime();
                    } else {
                        cinematic.play();
                    }
                }

                if (cinematic.getPlayState() != PlayState.Playing) {
                    if (name.equals("navFwd") && keyPressed) {
                        time += 0.25;
                        FastMath.clamp(time, 0, cinematic.getInitialDuration());
                        cinematic.setTime(time);
                    }
                    if (name.equals("navBack") && keyPressed) {
                        time -= 0.25;
                        FastMath.clamp(time, 0, cinematic.getInitialDuration());
                        cinematic.setTime(time);
                    }

                }
            }
        };
        inputManager.addListener(acl, "togglePause", "navFwd", "navBack");
    }

    private class FadeEvent extends AbstractCinematicEvent {

        boolean in = true;
        float value = 0;

        public FadeEvent(boolean in) {
            super(1);
            this.in = in;
            value = in ? 0 : 1;
        }

        @Override
        public void onPlay() {

            fade.setDuration(1f / cinematic.getSpeed());
            if (in) {
                fade.fadeIn();
            } else {
                fade.fadeOut();
            }
            fade.setValue(value);

        }

        @Override
        public void setTime(float time) {
            super.setTime(time);
            if (time >= fade.getDuration()) {
                value = in ? 1 : 0;
                fade.setValue(value);
            } else {
                value = time;
                if (in) {
                    fade.setValue(time / cinematic.getSpeed());
                } else {
                    fade.setValue(1 - time / cinematic.getSpeed());
                }
            }
        }

        @Override
        public void onUpdate(float tpf) {
        }

        @Override
        public void onStop() {
        }

        @Override
        public void onPause() {
            value = fade.getValue();
            fade.pause();
        }
    }
}
