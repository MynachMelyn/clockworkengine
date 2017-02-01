
package com.clockwork.bullet.debug;

import com.clockwork.bullet.objects.PhysicsVehicle;
import com.clockwork.bullet.objects.VehicleWheel;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.debug.Arrow;

/**
 *
 */
public class BulletVehicleDebugControl extends AbstractPhysicsDebugControl {

    protected final PhysicsVehicle body;
    protected final Node suspensionNode;
    protected final Vector3f location = new Vector3f();
    protected final Quaternion rotation = new Quaternion();

    public BulletVehicleDebugControl(BulletDebugAppState debugAppState, PhysicsVehicle body) {
        super(debugAppState);
        this.body = body;
        suspensionNode = new Node("Suspension");
        createVehicle();
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null && spatial instanceof Node) {
            Node node = (Node) spatial;
            node.attachChild(suspensionNode);
        } else if (spatial == null && this.spatial != null) {
            Node node = (Node) this.spatial;
            node.detachChild(suspensionNode);
        }
        super.setSpatial(spatial);
    }

    private void createVehicle() {
        suspensionNode.detachAllChildren();
        for (int i = 0; i < body.getNumWheels(); i++) {
            VehicleWheel physicsVehicleWheel = body.getWheel(i);
            Vector3f location = physicsVehicleWheel.getLocation().clone();
            Vector3f direction = physicsVehicleWheel.getDirection().clone();
            Vector3f axle = physicsVehicleWheel.getAxle().clone();
            float restLength = physicsVehicleWheel.getRestLength();
            float radius = physicsVehicleWheel.getRadius();

            Arrow locArrow = new Arrow(location);
            Arrow axleArrow = new Arrow(axle.normalizeLocal().multLocal(0.3f));
            Arrow wheelArrow = new Arrow(direction.normalizeLocal().multLocal(radius));
            Arrow dirArrow = new Arrow(direction.normalizeLocal().multLocal(restLength));
            Geometry locGeom = new Geometry("WheelLocationDebugShape" + i, locArrow);
            Geometry dirGeom = new Geometry("WheelDirectionDebugShape" + i, dirArrow);
            Geometry axleGeom = new Geometry("WheelAxleDebugShape" + i, axleArrow);
            Geometry wheelGeom = new Geometry("WheelRadiusDebugShape" + i, wheelArrow);
            dirGeom.setLocalTranslation(location);
            axleGeom.setLocalTranslation(location.add(direction));
            wheelGeom.setLocalTranslation(location.add(direction));
            locGeom.setMaterial(debugAppState.DEBUG_MAGENTA);
            dirGeom.setMaterial(debugAppState.DEBUG_MAGENTA);
            axleGeom.setMaterial(debugAppState.DEBUG_MAGENTA);
            wheelGeom.setMaterial(debugAppState.DEBUG_MAGENTA);
            suspensionNode.attachChild(locGeom);
            suspensionNode.attachChild(dirGeom);
            suspensionNode.attachChild(axleGeom);
            suspensionNode.attachChild(wheelGeom);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        for (int i = 0; i < body.getNumWheels(); i++) {
            VehicleWheel physicsVehicleWheel = body.getWheel(i);
            Vector3f location = physicsVehicleWheel.getLocation().clone();
            Vector3f direction = physicsVehicleWheel.getDirection().clone();
            Vector3f axle = physicsVehicleWheel.getAxle().clone();
            float restLength = physicsVehicleWheel.getRestLength();
            float radius = physicsVehicleWheel.getRadius();

            Geometry locGeom = (Geometry) suspensionNode.getChild("WheelLocationDebugShape" + i);
            Geometry dirGeom = (Geometry) suspensionNode.getChild("WheelDirectionDebugShape" + i);
            Geometry axleGeom = (Geometry) suspensionNode.getChild("WheelAxleDebugShape" + i);
            Geometry wheelGeom = (Geometry) suspensionNode.getChild("WheelRadiusDebugShape" + i);

            Arrow locArrow = (Arrow) locGeom.getMesh();
            locArrow.setArrowExtent(location);
            Arrow axleArrow = (Arrow) axleGeom.getMesh();
            axleArrow.setArrowExtent(axle.normalizeLocal().multLocal(0.3f));
            Arrow wheelArrow = (Arrow) wheelGeom.getMesh();
            wheelArrow.setArrowExtent(direction.normalizeLocal().multLocal(radius));
            Arrow dirArrow = (Arrow) dirGeom.getMesh();
            dirArrow.setArrowExtent(direction.normalizeLocal().multLocal(restLength));

            dirGeom.setLocalTranslation(location);
            axleGeom.setLocalTranslation(location.addLocal(direction));
            wheelGeom.setLocalTranslation(location);
            i++;
        }
        applyPhysicsTransform(body.getPhysicsLocation(location), body.getPhysicsRotation(rotation));
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
