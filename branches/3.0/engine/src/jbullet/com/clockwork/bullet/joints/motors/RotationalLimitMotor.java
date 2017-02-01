
package com.clockwork.bullet.joints.motors;

/**
 *
 */
public class RotationalLimitMotor {

    private com.bulletphysics.dynamics.constraintsolver.RotationalLimitMotor motor;

    public RotationalLimitMotor(com.bulletphysics.dynamics.constraintsolver.RotationalLimitMotor motor) {
        this.motor = motor;
    }

    public com.bulletphysics.dynamics.constraintsolver.RotationalLimitMotor getMotor() {
        return motor;
    }

    public float getLoLimit() {
        return motor.loLimit;
    }

    public void setLoLimit(float loLimit) {
        motor.loLimit = loLimit;
    }

    public float getHiLimit() {
        return motor.hiLimit;
    }

    public void setHiLimit(float hiLimit) {
        motor.hiLimit = hiLimit;
    }

    public float getTargetVelocity() {
        return motor.targetVelocity;
    }

    public void setTargetVelocity(float targetVelocity) {
        motor.targetVelocity = targetVelocity;
    }

    public float getMaxMotorForce() {
        return motor.maxMotorForce;
    }

    public void setMaxMotorForce(float maxMotorForce) {
        motor.maxMotorForce = maxMotorForce;
    }

    public float getMaxLimitForce() {
        return motor.maxLimitForce;
    }

    public void setMaxLimitForce(float maxLimitForce) {
        motor.maxLimitForce = maxLimitForce;
    }

    public float getDamping() {
        return motor.damping;
    }

    public void setDamping(float damping) {
        motor.damping = damping;
    }

    public float getLimitSoftness() {
        return motor.limitSoftness;
    }

    public void setLimitSoftness(float limitSoftness) {
        motor.limitSoftness = limitSoftness;
    }

    public float getERP() {
        return motor.ERP;
    }

    public void setERP(float ERP) {
        motor.ERP = ERP;
    }

    public float getBounce() {
        return motor.bounce;
    }

    public void setBounce(float bounce) {
        motor.bounce = bounce;
    }

    public boolean isEnableMotor() {
        return motor.enableMotor;
    }

    public void setEnableMotor(boolean enableMotor) {
        motor.enableMotor = enableMotor;
    }
}
