
package com.clockwork.bullet.control;

import com.clockwork.bullet.PhysicsSpace;
import com.clockwork.bullet.collision.shapes.CollisionShape;
import com.clockwork.bullet.objects.PhysicsCharacter;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.control.Control;
import java.io.IOException;

/**
 * @deprecated in favor of <code>BetterCharacterControl</code>
 */
@Deprecated
public class CharacterControl extends PhysicsCharacter implements PhysicsControl {

    protected Spatial spatial;
    protected boolean enabled = true;
    protected boolean added = false;
    protected PhysicsSpace space = null;
    protected Vector3f viewDirection = new Vector3f(Vector3f.UNIT_Z);
    protected boolean useViewDirection = true;
    protected boolean applyLocal = false;

    public CharacterControl() {
    }

    public CharacterControl(CollisionShape shape, float stepHeight) {
        super(shape, stepHeight);
    }

    public boolean isApplyPhysicsLocal() {
        return applyLocal;
    }

    /**
     * When set to true, the physics coordinates will be applied to the local
     * translation of the Spatial
     * @param applyPhysicsLocal
     */
    public void setApplyPhysicsLocal(boolean applyPhysicsLocal) {
        applyLocal = applyPhysicsLocal;
    }

    private Vector3f getSpatialTranslation() {
        if (applyLocal) {
            return spatial.getLocalTranslation();
        }
        return spatial.getWorldTranslation();
    }

    public Control cloneForSpatial(Spatial spatial) {
        CharacterControl control = new CharacterControl(collisionShape, stepHeight);
        control.setCcdMotionThreshold(getCcdMotionThreshold());
        control.setCcdSweptSphereRadius(getCcdSweptSphereRadius());
        control.setCollideWithGroups(getCollideWithGroups());
        control.setCollisionGroup(getCollisionGroup());
        control.setFallSpeed(getFallSpeed());
        control.setGravity(getGravity());
        control.setJumpSpeed(getJumpSpeed());
        control.setMaxSlope(getMaxSlope());
        control.setPhysicsLocation(getPhysicsLocation());
        control.setUpAxis(getUpAxis());
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
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (space != null) {
            if (enabled && !added) {
                if (spatial != null) {
                    warp(getSpatialTranslation());
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

    public void setViewDirection(Vector3f vec) {
        viewDirection.set(vec);
    }

    public Vector3f getViewDirection() {
        return viewDirection;
    }

    public boolean isUseViewDirection() {
        return useViewDirection;
    }

    public void setUseViewDirection(boolean viewDirectionEnabled) {
        this.useViewDirection = viewDirectionEnabled;
    }

    public void update(float tpf) {
        if (enabled && spatial != null) {
            Quaternion localRotationQuat = spatial.getLocalRotation();
            Vector3f localLocation = spatial.getLocalTranslation();
            if (!applyLocal && spatial.getParent() != null) {
                getPhysicsLocation(localLocation);
                localLocation.subtractLocal(spatial.getParent().getWorldTranslation());
                localLocation.divideLocal(spatial.getParent().getWorldScale());
                tmp_inverseWorldRotation.set(spatial.getParent().getWorldRotation()).inverseLocal().multLocal(localLocation);
                spatial.setLocalTranslation(localLocation);

                if (useViewDirection) {
                    localRotationQuat.lookAt(viewDirection, Vector3f.UNIT_Y);
                    spatial.setLocalRotation(localRotationQuat);
                }
            } else {
                spatial.setLocalTranslation(getPhysicsLocation());
                localRotationQuat.lookAt(viewDirection, Vector3f.UNIT_Y);
                spatial.setLocalRotation(localRotationQuat);
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
            if (this.space == space) {
                return;
            }
            space.addCollisionObject(this);
            added = true;
        }
        this.space = space;
    }

    public PhysicsSpace getPhysicsSpace() {
        return space;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(enabled, "enabled", true);
        oc.write(applyLocal, "applyLocalPhysics", false);
        oc.write(useViewDirection, "viewDirectionEnabled", true);
        oc.write(viewDirection, "viewDirection", new Vector3f(Vector3f.UNIT_Z));
        oc.write(spatial, "spatial", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        enabled = ic.readBoolean("enabled", true);
        useViewDirection = ic.readBoolean("viewDirectionEnabled", true);
        viewDirection = (Vector3f) ic.readSavable("viewDirection", new Vector3f(Vector3f.UNIT_Z));
        applyLocal = ic.readBoolean("applyLocalPhysics", false);
        spatial = (Spatial) ic.readSavable("spatial", null);
        setUserObject(spatial);
    }
}
