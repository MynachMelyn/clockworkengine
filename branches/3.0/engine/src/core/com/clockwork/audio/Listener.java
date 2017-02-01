
package com.clockwork.audio;

import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;

public class Listener {

    private Vector3f location;
    private Vector3f velocity;
    private Quaternion rotation;
    private float volume = 1;
    private AudioRenderer renderer;

    public Listener(){
        location = new Vector3f();
        velocity = new Vector3f();
        rotation = new Quaternion();
    }
    
    public Listener(Listener source){
        location = source.location.clone();
        velocity = source.velocity.clone();
        rotation = source.rotation.clone();
        volume = source.volume;
    }

    public void setRenderer(AudioRenderer renderer){
        this.renderer = renderer;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
        if (renderer != null)
            renderer.updateListenerParam(this, ListenerParam.Volume);
    }
    
    public Vector3f getLocation() {
        return location;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public Vector3f getLeft(){
        return rotation.getRotationColumn(0);
    }

    public Vector3f getUp(){
        return rotation.getRotationColumn(1);
    }

    public Vector3f getDirection(){
        return rotation.getRotationColumn(2);
    }
    
    public void setLocation(Vector3f location) {
        this.location.set(location);
        if (renderer != null)
            renderer.updateListenerParam(this, ListenerParam.Position);
    }

    public void setRotation(Quaternion rotation) {
        this.rotation.set(rotation);
        if (renderer != null)
            renderer.updateListenerParam(this, ListenerParam.Rotation);
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity.set(velocity);
        if (renderer != null)
            renderer.updateListenerParam(this, ListenerParam.Velocity);
    }
}
