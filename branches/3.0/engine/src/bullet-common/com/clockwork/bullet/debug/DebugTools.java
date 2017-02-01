
package com.clockwork.bullet.debug;

import com.clockwork.asset.AssetManager;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.debug.Arrow;

/**
 *
 */
public class DebugTools {

    protected final AssetManager manager;
    public Material DEBUG_BLUE;
    public Material DEBUG_RED;
    public Material DEBUG_GREEN;
    public Material DEBUG_YELLOW;
    public Material DEBUG_MAGENTA;
    public Material DEBUG_PINK;
    public Node debugNode = new Node("Debug Node");
    public Arrow arrowBlue = new Arrow(Vector3f.ZERO);
    public Geometry arrowBlueGeom = new Geometry("Blue Arrow", arrowBlue);
    public Arrow arrowGreen = new Arrow(Vector3f.ZERO);
    public Geometry arrowGreenGeom = new Geometry("Green Arrow", arrowGreen);
    public Arrow arrowRed = new Arrow(Vector3f.ZERO);
    public Geometry arrowRedGeom = new Geometry("Red Arrow", arrowRed);
    public Arrow arrowMagenta = new Arrow(Vector3f.ZERO);
    public Geometry arrowMagentaGeom = new Geometry("Magenta Arrow", arrowMagenta);
    public Arrow arrowYellow = new Arrow(Vector3f.ZERO);
    public Geometry arrowYellowGeom = new Geometry("Yellow Arrow", arrowYellow);
    public Arrow arrowPink = new Arrow(Vector3f.ZERO);
    public Geometry arrowPinkGeom = new Geometry("Pink Arrow", arrowPink);
    protected static final Vector3f UNIT_X_CHECK = new Vector3f(1, 0, 0);
    protected static final Vector3f UNIT_Y_CHECK = new Vector3f(0, 1, 0);
    protected static final Vector3f UNIT_Z_CHECK = new Vector3f(0, 0, 1);
    protected static final Vector3f UNIT_XYZ_CHECK = new Vector3f(1, 1, 1);
    protected static final Vector3f ZERO_CHECK = new Vector3f(0, 0, 0);

    public DebugTools(AssetManager manager) {
        this.manager = manager;
        setupMaterials();
        setupDebugNode();
    }

    public void show(RenderManager rm, ViewPort vp) {
        if (!Vector3f.UNIT_X.equals(UNIT_X_CHECK) || !Vector3f.UNIT_Y.equals(UNIT_Y_CHECK) || !Vector3f.UNIT_Z.equals(UNIT_Z_CHECK)
                || !Vector3f.UNIT_XYZ.equals(UNIT_XYZ_CHECK) || !Vector3f.ZERO.equals(ZERO_CHECK)) {
            throw new IllegalStateException("Unit vectors compromised!"
                    + "\nX: " + Vector3f.UNIT_X
                    + "\nY: " + Vector3f.UNIT_Y
                    + "\nZ: " + Vector3f.UNIT_Z
                    + "\nXYZ: " + Vector3f.UNIT_XYZ
                    + "\nZERO: " + Vector3f.ZERO);
        }
        debugNode.updateLogicalState(0);
        debugNode.updateGeometricState();
        rm.renderScene(debugNode, vp);
    }

    public void setBlueArrow(Vector3f location, Vector3f extent) {
        arrowBlueGeom.setLocalTranslation(location);
        arrowBlue.setArrowExtent(extent);
    }

    public void setGreenArrow(Vector3f location, Vector3f extent) {
        arrowGreenGeom.setLocalTranslation(location);
        arrowGreen.setArrowExtent(extent);
    }

    public void setRedArrow(Vector3f location, Vector3f extent) {
        arrowRedGeom.setLocalTranslation(location);
        arrowRed.setArrowExtent(extent);
    }

    public void setMagentaArrow(Vector3f location, Vector3f extent) {
        arrowMagentaGeom.setLocalTranslation(location);
        arrowMagenta.setArrowExtent(extent);
    }

    public void setYellowArrow(Vector3f location, Vector3f extent) {
        arrowYellowGeom.setLocalTranslation(location);
        arrowYellow.setArrowExtent(extent);
    }

    public void setPinkArrow(Vector3f location, Vector3f extent) {
        arrowPinkGeom.setLocalTranslation(location);
        arrowPink.setArrowExtent(extent);
    }

    protected void setupDebugNode() {
        arrowBlueGeom.setMaterial(DEBUG_BLUE);
        arrowGreenGeom.setMaterial(DEBUG_GREEN);
        arrowRedGeom.setMaterial(DEBUG_RED);
        arrowMagentaGeom.setMaterial(DEBUG_MAGENTA);
        arrowYellowGeom.setMaterial(DEBUG_YELLOW);
        arrowPinkGeom.setMaterial(DEBUG_PINK);
        debugNode.attachChild(arrowBlueGeom);
        debugNode.attachChild(arrowGreenGeom);
        debugNode.attachChild(arrowRedGeom);
        debugNode.attachChild(arrowMagentaGeom);
        debugNode.attachChild(arrowYellowGeom);
        debugNode.attachChild(arrowPinkGeom);
    }

    protected void setupMaterials() {
        DEBUG_BLUE = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        DEBUG_BLUE.getAdditionalRenderState().setWireframe(true);
        DEBUG_BLUE.setColor("Color", ColorRGBA.Blue);
        DEBUG_GREEN = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        DEBUG_GREEN.getAdditionalRenderState().setWireframe(true);
        DEBUG_GREEN.setColor("Color", ColorRGBA.Green);
        DEBUG_RED = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        DEBUG_RED.getAdditionalRenderState().setWireframe(true);
        DEBUG_RED.setColor("Color", ColorRGBA.Red);
        DEBUG_YELLOW = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        DEBUG_YELLOW.getAdditionalRenderState().setWireframe(true);
        DEBUG_YELLOW.setColor("Color", ColorRGBA.Yellow);
        DEBUG_MAGENTA = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        DEBUG_MAGENTA.getAdditionalRenderState().setWireframe(true);
        DEBUG_MAGENTA.setColor("Color", ColorRGBA.Magenta);
        DEBUG_PINK = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        DEBUG_PINK.getAdditionalRenderState().setWireframe(true);
        DEBUG_PINK.setColor("Color", ColorRGBA.Pink);
    }
}
