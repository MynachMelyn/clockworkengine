
package com.clockwork.bullet.objects.infos;

import com.clockwork.bullet.objects.PhysicsVehicle;
import com.clockwork.math.Matrix3f;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Spatial;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * stores transform info of a PhysicsNode in a threadsafe manner to
 * allow multithreaded access from the jme scenegraph and the bullet physicsspace
 */
public class RigidBodyMotionState {
    long motionStateId = 0;
    private Vector3f worldLocation = new Vector3f();
    private Matrix3f worldRotation = new Matrix3f();
    private Quaternion worldRotationQuat = new Quaternion();
    private Quaternion tmp_inverseWorldRotation = new Quaternion();
    private PhysicsVehicle vehicle;
    private boolean applyPhysicsLocal = false;
//    protected LinkedList<PhysicsMotionStateListener> listeners = new LinkedList<PhysicsMotionStateListener>();

    public RigidBodyMotionState() {
        this.motionStateId = createMotionState();
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Created MotionState {0}", Long.toHexString(motionStateId));
    }

    private native long createMotionState();

    /**
     * applies the current transform to the given jme Node if the location has been updated on the physics side
     * @param spatial
     */
    public boolean applyTransform(Spatial spatial) {
        Vector3f localLocation = spatial.getLocalTranslation();
        Quaternion localRotationQuat = spatial.getLocalRotation();
        boolean physicsLocationDirty = applyTransform(motionStateId, localLocation, localRotationQuat);
        if (!physicsLocationDirty) {
            return false;
        }
        if (!applyPhysicsLocal && spatial.getParent() != null) {
            localLocation.subtractLocal(spatial.getParent().getWorldTranslation());
            localLocation.divideLocal(spatial.getParent().getWorldScale());
            tmp_inverseWorldRotation.set(spatial.getParent().getWorldRotation()).inverseLocal().multLocal(localLocation);

//            localRotationQuat.set(worldRotationQuat);
            tmp_inverseWorldRotation.mult(localRotationQuat, localRotationQuat);

            spatial.setLocalTranslation(localLocation);
            spatial.setLocalRotation(localRotationQuat);
        } else {
            spatial.setLocalTranslation(localLocation);
            spatial.setLocalRotation(localRotationQuat);
//            spatial.setLocalTranslation(worldLocation);
//            spatial.setLocalRotation(worldRotationQuat);
        }
        if (vehicle != null) {
            vehicle.updateWheels();
        }
        return true;
    }

    private native boolean applyTransform(long stateId, Vector3f location, Quaternion rotation);

    /**
     * @return the worldLocation
     */
    public Vector3f getWorldLocation() {
        getWorldLocation(motionStateId, worldLocation);
        return worldLocation;
    }

    private native void getWorldLocation(long stateId, Vector3f vec);

    /**
     * @return the worldRotation
     */
    public Matrix3f getWorldRotation() {
        getWorldRotation(motionStateId, worldRotation);
        return worldRotation;
    }

    private native void getWorldRotation(long stateId, Matrix3f vec);

    /**
     * @return the worldRotationQuat
     */
    public Quaternion getWorldRotationQuat() {
        getWorldRotationQuat(motionStateId, worldRotationQuat);
        return worldRotationQuat;
    }

    private native void getWorldRotationQuat(long stateId, Quaternion vec);

    /**
     * @param vehicle the vehicle to set
     */
    public void setVehicle(PhysicsVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public boolean isApplyPhysicsLocal() {
        return applyPhysicsLocal;
    }

    public void setApplyPhysicsLocal(boolean applyPhysicsLocal) {
        this.applyPhysicsLocal = applyPhysicsLocal;
    }
    
    public long getObjectId(){
        return motionStateId;
    }
//    public void addMotionStateListener(PhysicsMotionStateListener listener){
//        listeners.add(listener);
//    }
//
//    public void removeMotionStateListener(PhysicsMotionStateListener listener){
//        listeners.remove(listener);
//    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Finalizing MotionState {0}", Long.toHexString(motionStateId));
        finalizeNative(motionStateId);
    }

    private native void finalizeNative(long objectId);
}
