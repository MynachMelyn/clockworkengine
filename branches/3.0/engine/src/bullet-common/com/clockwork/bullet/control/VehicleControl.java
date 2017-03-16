
package com.clockwork.bullet.control;

import com.clockwork.bullet.PhysicsSpace;
import com.clockwork.bullet.collision.shapes.CollisionShape;
import com.clockwork.bullet.objects.PhysicsVehicle;
import com.clockwork.bullet.objects.VehicleWheel;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.control.Control;
import java.io.IOException;
import java.util.Iterator;

/**
 *
 */
public class VehicleControl extends PhysicsVehicle implements PhysicsControl {

    protected Spatial spatial;
    protected boolean enabled = true;
    protected PhysicsSpace space = null;
    protected boolean added = false;

    public VehicleControl() {
    }

    /**
     * Creates a new PhysicsNode with the supplied collision shape
     * @param shape
     */
    public VehicleControl(CollisionShape shape) {
        super(shape);
    }

    public VehicleControl(CollisionShape shape, float mass) {
        super(shape, mass);
    }

    public boolean isApplyPhysicsLocal() {
        return motionState.isApplyPhysicsLocal();
    }

    /**
     * When set to true, the physics coordinates will be applied to the local
     * translation of the Spatial
     * @param applyPhysicsLocal
     */
    public void setApplyPhysicsLocal(boolean applyPhysicsLocal) {
        motionState.setApplyPhysicsLocal(applyPhysicsLocal);
        for (Iterator<VehicleWheel> it = wheels.iterator(); it.hasNext();) {
            VehicleWheel vehicleWheel = it.next();
            vehicleWheel.setApplyLocal(applyPhysicsLocal);
        }
    }

    private Vector3f getSpatialTranslation(){
        if(motionState.isApplyPhysicsLocal()){
            return spatial.getLocalTranslation();
        }
        return spatial.getWorldTranslation();
    }

    private Quaternion getSpatialRotation(){
        if(motionState.isApplyPhysicsLocal()){
            return spatial.getLocalRotation();
        }
        return spatial.getWorldRotation();
    }

    public Control cloneForSpatial(Spatial spatial) {
        VehicleControl control = new VehicleControl(collisionShape, mass);
        control.setAngularFactor(getAngularFactor());
        control.setAngularSleepingThreshold(getAngularSleepingThreshold());
        control.setAngularVelocity(getAngularVelocity());
        control.setCcdMotionThreshold(getCcdMotionThreshold());
        control.setCcdSweptSphereRadius(getCcdSweptSphereRadius());
        control.setCollideWithGroups(getCollideWithGroups());
        control.setCollisionGroup(getCollisionGroup());
        control.setDamping(getLinearDamping(), getAngularDamping());
        control.setFriction(getFriction());
        control.setGravity(getGravity());
        control.setKinematic(isKinematic());
        control.setLinearSleepingThreshold(getLinearSleepingThreshold());
        control.setLinearVelocity(getLinearVelocity());
        control.setPhysicsLocation(getPhysicsLocation());
        control.setPhysicsRotation(getPhysicsRotationMatrix());
        control.setRestitution(getRestitution());

        control.setFrictionSlip(getFrictionSlip());
        control.setMaxSuspensionTravelCm(getMaxSuspensionTravelCm());
        control.setSuspensionStiffness(getSuspensionStiffness());
        control.setSuspensionCompression(tuning.suspensionCompression);
        control.setSuspensionDamping(tuning.suspensionDamping);
        control.setMaxSuspensionForce(getMaxSuspensionForce());

        for (Iterator<VehicleWheel> it = wheels.iterator(); it.hasNext();) {
            VehicleWheel wheel = it.next();
            VehicleWheel newWheel = control.addWheel(wheel.getLocation(), wheel.getDirection(), wheel.getAxle(), wheel.getRestLength(), wheel.getRadius(), wheel.isFrontWheel());
            newWheel.setFrictionSlip(wheel.getFrictionSlip());
            newWheel.setMaxSuspensionTravelCm(wheel.getMaxSuspensionTravelCm());
            newWheel.setSuspensionStiffness(wheel.getSuspensionStiffness());
            newWheel.setWheelsDampingCompression(wheel.getWheelsDampingCompression());
            newWheel.setWheelsDampingRelaxation(wheel.getWheelsDampingRelaxation());
            newWheel.setMaxSuspensionForce(wheel.getMaxSuspensionForce());

            //TODO: bad way finding children!
            if (spatial instanceof Node) {
                Node node = (Node) spatial;
                Spatial wheelSpat = node.getChild(wheel.getWheelSpatial().getName());
                if (wheelSpat != null) {
                    newWheel.setWheelSpatial(wheelSpat);
                }
            }
        }
        control.setApplyPhysicsLocal(isApplyPhysicsLocal());
        return control;
    }

    public void setSpatial(Spatial spatial) {
        this.spatial = spatial;
        setUserObject(spatial);
        if (spatial == null) {
            return;
        }
        setPhysicsLocation(getSpatialTranslation());
        setPhysicsRotation(getSpatialRotation());
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (space != null) {
            if (enabled && !added) {
                if(spatial!=null){
                    setPhysicsLocation(getSpatialTranslation());
                    setPhysicsRotation(getSpatialRotation());
                }
                space.addCollisionObject(this);
                added = true;
            } else if (!enabled && added) {
                space.removeCollisionObject(this);
                added = false;
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void update(float tpf) {
        if (enabled && spatial != null) {
            if (getMotionState().applyTransform(spatial)) {
                spatial.getWorldTransform();
                applyWheelTransforms();
            }
        } else if (enabled) {
            applyWheelTransforms();
        }
    }

    public void render(RenderManager rm, ViewPort vp) {
    }

    public void setPhysicsSpace(PhysicsSpace space) {
        createVehicle(space);
        if (space == null) {
            if (this.space != null) {
                this.space.removeCollisionObject(this);
                added = false;
            }
        } else {
            if(this.space==space) return;
            space.addCollisionObject(this);
            added = true;
        }
        this.space = space;
    }

    public PhysicsSpace getPhysicsSpace() {
        return space;
    }

    @Override
    public void write(CWExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(enabled, "enabled", true);
        oc.write(motionState.isApplyPhysicsLocal(), "applyLocalPhysics", false);
        oc.write(spatial, "spatial", null);
    }

    @Override
    public void read(CWImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        enabled = ic.readBoolean("enabled", true);
        spatial = (Spatial) ic.readSavable("spatial", null);
        motionState.setApplyPhysicsLocal(ic.readBoolean("applyLocalPhysics", false));
        setUserObject(spatial);
    }
}
