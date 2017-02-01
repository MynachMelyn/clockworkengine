
package com.clockwork.bullet.joints.motors;

import com.clockwork.math.Vector3f;

/**
 *
 */
public class TranslationalLimitMotor {

    private long motorId = 0;

    public TranslationalLimitMotor(long motor) {
        this.motorId = motor;
    }

    public long getMotor() {
        return motorId;
    }

    public Vector3f getLowerLimit() {
        Vector3f vec = new Vector3f();
        getLowerLimit(motorId, vec);
        return vec;
    }

    private native void getLowerLimit(long motorId, Vector3f vector);

    public void setLowerLimit(Vector3f lowerLimit) {
        setLowerLimit(motorId, lowerLimit);
    }

    private native void setLowerLimit(long motorId, Vector3f vector);
    
    public Vector3f getUpperLimit() {
        Vector3f vec = new Vector3f();
        getUpperLimit(motorId, vec);
        return vec;
    }

    private native void getUpperLimit(long motorId, Vector3f vector);

    public void setUpperLimit(Vector3f upperLimit) {
        setUpperLimit(motorId, upperLimit);
    }

    private native void setUpperLimit(long motorId, Vector3f vector);

    public Vector3f getAccumulatedImpulse() {
        Vector3f vec = new Vector3f();
        getAccumulatedImpulse(motorId, vec);
        return vec;
    }

    private native void getAccumulatedImpulse(long motorId, Vector3f vector);
    
    public void setAccumulatedImpulse(Vector3f accumulatedImpulse) {
        setAccumulatedImpulse(motorId, accumulatedImpulse);
    }

    private native void setAccumulatedImpulse(long motorId, Vector3f vector);

    public float getLimitSoftness() {
        return getLimitSoftness(motorId);
    }
    
    private native float getLimitSoftness(long motorId);

    public void setLimitSoftness(float limitSoftness) {
        setLimitSoftness(motorId, limitSoftness);
    }
    
    private native void setLimitSoftness(long motorId, float limitSoftness);

    public float getDamping() {
        return getDamping(motorId);
    }

    private native float getDamping(long motorId);
    
    public void setDamping(float damping) {
        setDamping(motorId, damping);
    }

    private native void setDamping(long motorId, float damping);
    
    public float getRestitution() {
        return getRestitution(motorId);
    }
    
    private native float getRestitution(long motorId);

    public void setRestitution(float restitution) {
        setRestitution(motorId, restitution);
    }

    private native void setRestitution(long motorId, float restitution);
}
