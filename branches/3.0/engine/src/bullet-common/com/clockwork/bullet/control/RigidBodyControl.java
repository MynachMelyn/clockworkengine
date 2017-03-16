
package com.clockwork.bullet.control;

import com.clockwork.bullet.PhysicsSpace;
import com.clockwork.bullet.collision.shapes.BoxCollisionShape;
import com.clockwork.bullet.collision.shapes.CollisionShape;
import com.clockwork.bullet.collision.shapes.SphereCollisionShape;
import com.clockwork.bullet.objects.PhysicsRigidBody;
import com.clockwork.bullet.util.CollisionShapeFactory;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.control.Control;
import com.clockwork.scene.shape.Box;
import com.clockwork.scene.shape.Sphere;
import java.io.IOException;

/**
 *
 */
public class RigidBodyControl extends PhysicsRigidBody implements PhysicsControl {

    protected Spatial spatial;
    protected boolean enabled = true;
    protected boolean added = false;
    protected PhysicsSpace space = null;
    protected boolean kinematicSpatial = true;

    public RigidBodyControl() {
    }

    /**
     * When using this constructor, the CollisionShape for the RigidBody is generated
     * automatically when the Control is added to a Spatial.
     * @param mass When not 0, a HullCollisionShape is generated, otherwise a MeshCollisionShape is used. For geometries with box or sphere meshes the proper box or sphere collision shape is used.
     */
    public RigidBodyControl(float mass) {
        this.mass = mass;
    }

    /**
     * Creates a new PhysicsNode with the supplied collision shape and mass 1
     * @param shape
     */
    public RigidBodyControl(CollisionShape shape) {
        super(shape);
    }

    public RigidBodyControl(CollisionShape shape, float mass) {
        super(shape, mass);
    }

    public Control cloneForSpatial(Spatial spatial) {
        RigidBodyControl control = new RigidBodyControl(collisionShape, mass);
        control.setAngularFactor(getAngularFactor());
        control.setAngularSleepingThreshold(getAngularSleepingThreshold());
        control.setCcdMotionThreshold(getCcdMotionThreshold());
        control.setCcdSweptSphereRadius(getCcdSweptSphereRadius());
        control.setCollideWithGroups(getCollideWithGroups());
        control.setCollisionGroup(getCollisionGroup());
        control.setDamping(getLinearDamping(), getAngularDamping());
        control.setFriction(getFriction());
        control.setGravity(getGravity());
        control.setKinematic(isKinematic());
        control.setKinematicSpatial(isKinematicSpatial());
        control.setLinearSleepingThreshold(getLinearSleepingThreshold());
        control.setPhysicsLocation(getPhysicsLocation(null));
        control.setPhysicsRotation(getPhysicsRotationMatrix(null));
        control.setRestitution(getRestitution());

        if (mass > 0) {
            control.setAngularVelocity(getAngularVelocity());
            control.setLinearVelocity(getLinearVelocity());
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
        if (collisionShape == null) {
            createCollisionShape();
            rebuildRigidBody();
        }
        setPhysicsLocation(getSpatialTranslation());
        setPhysicsRotation(getSpatialRotation());
    }

    protected void createCollisionShape() {
        if (spatial == null) {
            return;
        }
        if (spatial instanceof Geometry) {
            Geometry geom = (Geometry) spatial;
            Mesh mesh = geom.getMesh();
            if (mesh instanceof Sphere) {
                collisionShape = new SphereCollisionShape(((Sphere) mesh).getRadius());
                return;
            } else if (mesh instanceof Box) {
                collisionShape = new BoxCollisionShape(new Vector3f(((Box) mesh).getXExtent(), ((Box) mesh).getYExtent(), ((Box) mesh).getZExtent()));
                return;
            }
        }
        if (mass > 0) {
            collisionShape = CollisionShapeFactory.createDynamicMeshShape(spatial);
        } else {
            collisionShape = CollisionShapeFactory.createMeshShape(spatial);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (space != null) {
            if (enabled && !added) {
                if (spatial != null) {
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

    /**
     * Checks if this control is in kinematic spatial mode.
     * @return true if the spatial location is applied to this kinematic rigidbody
     */
    public boolean isKinematicSpatial() {
        return kinematicSpatial;
    }

    /**
     * Sets this control to kinematic spatial mode so that the spatials transform will
     * be applied to the rigidbody in kinematic mode, defaults to true.
     * @param kinematicSpatial
     */
    public void setKinematicSpatial(boolean kinematicSpatial) {
        this.kinematicSpatial = kinematicSpatial;
    }

    public boolean isApplyPhysicsLocal() {
        return motionState.isApplyPhysicsLocal();
    }

    /**
     * When set to true, the physics coordinates will be applied to the local
     * translation of the Spatial instead of the world traslation.
     * @param applyPhysicsLocal
     */
    public void setApplyPhysicsLocal(boolean applyPhysicsLocal) {
        motionState.setApplyPhysicsLocal(applyPhysicsLocal);
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

    public void update(float tpf) {
        if (enabled && spatial != null) {
            if (isKinematic() && kinematicSpatial) {
                super.setPhysicsLocation(getSpatialTranslation());
                super.setPhysicsRotation(getSpatialRotation());
            } else {
                getMotionState().applyTransform(spatial);
            }
        }
    }

    public void render(RenderManager rm, ViewPort vp) {
    }

    public void setPhysicsSpace(PhysicsSpace space) {
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
        oc.write(kinematicSpatial, "kinematicSpatial", true);
        oc.write(spatial, "spatial", null);
    }

    @Override
    public void read(CWImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        enabled = ic.readBoolean("enabled", true);
        kinematicSpatial = ic.readBoolean("kinematicSpatial", true);
        spatial = (Spatial) ic.readSavable("spatial", null);
        motionState.setApplyPhysicsLocal(ic.readBoolean("applyLocalPhysics", false));
        setUserObject(spatial);
    }
}
