
package com.clockwork.bullet.debug;

import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.control.AbstractControl;

/**
 *
 */
public abstract class AbstractPhysicsDebugControl extends AbstractControl {

    private final Quaternion tmp_inverseWorldRotation = new Quaternion();
    protected final BulletDebugAppState debugAppState;

    public AbstractPhysicsDebugControl(BulletDebugAppState debugAppState) {
        this.debugAppState = debugAppState;
    }

    /**
     * This is called on the physics thread for debug controls
     */
    @Override
    protected abstract void controlUpdate(float tpf);

    protected void applyPhysicsTransform(Vector3f worldLocation, Quaternion worldRotation) {
        applyPhysicsTransform(worldLocation, worldRotation, this.spatial);
    }

    protected void applyPhysicsTransform(Vector3f worldLocation, Quaternion worldRotation, Spatial spatial) {
        if (spatial != null) {
            Vector3f localLocation = spatial.getLocalTranslation();
            Quaternion localRotationQuat = spatial.getLocalRotation();
            if (spatial.getParent() != null) {
                localLocation.set(worldLocation).subtractLocal(spatial.getParent().getWorldTranslation());
                localLocation.divideLocal(spatial.getParent().getWorldScale());
                tmp_inverseWorldRotation.set(spatial.getParent().getWorldRotation()).inverseLocal().multLocal(localLocation);
                localRotationQuat.set(worldRotation);
                tmp_inverseWorldRotation.set(spatial.getParent().getWorldRotation()).inverseLocal().mult(localRotationQuat, localRotationQuat);
                spatial.setLocalTranslation(localLocation);
                spatial.setLocalRotation(localRotationQuat);
            } else {
                spatial.setLocalTranslation(worldLocation);
                spatial.setLocalRotation(worldRotation);
            }
        }

    }
}
