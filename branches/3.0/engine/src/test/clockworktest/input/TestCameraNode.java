
package clockworktest.input;

import com.clockwork.app.SimpleApplication;
import com.clockwork.input.MouseInput;
import com.clockwork.input.controls.*;
import com.clockwork.material.Material;
import com.clockwork.math.FastMath;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.CameraNode;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.control.CameraControl.ControlDirection;
import com.clockwork.scene.shape.Quad;
import com.clockwork.system.AppSettings;

/**
 * A 3rd-person camera node follows a target (teapot).  Follow the teapot with
 * WASD keys, rotate by dragging the mouse.
 */
public class TestCameraNode extends SimpleApplication implements AnalogListener, ActionListener {

  private Geometry teaGeom;
  private Node teaNode;
  CameraNode camNode;
  boolean rotate = false;
  Vector3f direction = new Vector3f();

  public static void main(String[] args) {
    TestCameraNode app = new TestCameraNode();
    AppSettings s = new AppSettings(true);
    s.setFrameRate(100);
    app.setSettings(s);
    app.start();
  }

  public void simpleInitApp() {
    // load a teapot model 
    teaGeom = (Geometry) assetManager.loadModel("Models/Teapot/Teapot.obj");
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
    teaGeom.setMaterial(mat);
    //create a node to attach the geometry and the camera node
    teaNode = new Node("teaNode");
    teaNode.attachChild(teaGeom);
    rootNode.attachChild(teaNode);
    // create a floor
    mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));
    Geometry ground = new Geometry("ground", new Quad(50, 50));
    ground.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
    ground.setLocalTranslation(-25, -1, 25);
    ground.setMaterial(mat);
    rootNode.attachChild(ground);

    //creating the camera Node
    camNode = new CameraNode("CamNode", cam);
    //Setting the direction to Spatial to camera, this means the camera will copy the movements of the Node
    camNode.setControlDir(ControlDirection.SpatialToCamera);
    //attaching the camNode to the teaNode
    teaNode.attachChild(camNode);
    //setting the local translation of the cam node to move it away from the teanNode a bit
    camNode.setLocalTranslation(new Vector3f(-10, 0, 0));
    //setting the camNode to look at the teaNode
    camNode.lookAt(teaNode.getLocalTranslation(), Vector3f.UNIT_Y);

    //disable the default 1st-person flyCam (don't forget this!!)
    flyCam.setEnabled(false);

    registerInput();
  }

  public void registerInput() {
    inputManager.addMapping("moveForward", new KeyTrigger(keyInput.KEY_UP), new KeyTrigger(keyInput.KEY_W));
    inputManager.addMapping("moveBackward", new KeyTrigger(keyInput.KEY_DOWN), new KeyTrigger(keyInput.KEY_S));
    inputManager.addMapping("moveRight", new KeyTrigger(keyInput.KEY_RIGHT), new KeyTrigger(keyInput.KEY_D));
    inputManager.addMapping("moveLeft", new KeyTrigger(keyInput.KEY_LEFT), new KeyTrigger(keyInput.KEY_A));
    inputManager.addMapping("toggleRotate", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    inputManager.addMapping("rotateRight", new MouseAxisTrigger(MouseInput.AXIS_X, true));
    inputManager.addMapping("rotateLeft", new MouseAxisTrigger(MouseInput.AXIS_X, false));
    inputManager.addListener(this, "moveForward", "moveBackward", "moveRight", "moveLeft");
    inputManager.addListener(this, "rotateRight", "rotateLeft", "toggleRotate");
  }

  public void onAnalog(String name, float value, float tpf) {
    //computing the normalized direction of the cam to move the teaNode
    direction.set(cam.getDirection()).normalizeLocal();
    if (name.equals("moveForward")) {
      direction.multLocal(5 * tpf);
      teaNode.move(direction);
    }
    if (name.equals("moveBackward")) {
      direction.multLocal(-5 * tpf);
      teaNode.move(direction);
    }
    if (name.equals("moveRight")) {
      direction.crossLocal(Vector3f.UNIT_Y).multLocal(5 * tpf);
      teaNode.move(direction);
    }
    if (name.equals("moveLeft")) {
      direction.crossLocal(Vector3f.UNIT_Y).multLocal(-5 * tpf);
      teaNode.move(direction);
    }
    if (name.equals("rotateRight") && rotate) {
      teaNode.rotate(0, 5 * tpf, 0);
    }
    if (name.equals("rotateLeft") && rotate) {
      teaNode.rotate(0, -5 * tpf, 0);
    }

  }

  public void onAction(String name, boolean keyPressed, float tpf) {
    //toggling rotation on or off
    if (name.equals("toggleRotate") && keyPressed) {
      rotate = true;
      inputManager.setCursorVisible(false);
    }
    if (name.equals("toggleRotate") && !keyPressed) {
      rotate = false;
      inputManager.setCursorVisible(true);
    }

  }
}
