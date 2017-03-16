
package com.clockwork.collision;

import com.clockwork.math.Triangle;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Mesh;

/**
 * A CollisionResult represents a single collision instance
 * between two Collidable}. A collision check can result in many 
 * collision instances (places where collision has occured).
 * 
 */
public class CollisionResult implements Comparable<CollisionResult> {

    private Geometry geometry;
    private Vector3f contactPoint;
    private Vector3f contactNormal;
    private float distance;
    private int triangleIndex;

    public CollisionResult(Geometry geometry, Vector3f contactPoint, float distance, int triangleIndex) {
        this.geometry = geometry;
        this.contactPoint = contactPoint;
        this.distance = distance;
        this.triangleIndex = triangleIndex;
    }

    public CollisionResult(Vector3f contactPoint, float distance) {
        this.contactPoint = contactPoint;
        this.distance = distance;
    }

    public CollisionResult(){
    }

    public void setGeometry(Geometry geom){
        this.geometry = geom;
    }

    public void setContactNormal(Vector3f norm){
        this.contactNormal = norm;
    }

    public void setContactPoint(Vector3f point){
        this.contactPoint = point;
    }

    public void setDistance(float dist){
        this.distance = dist;
    }

    public void setTriangleIndex(int index){
        this.triangleIndex = index;
    }

    public Triangle getTriangle(Triangle store){
        if (store == null)
            store = new Triangle();

        Mesh m = geometry.getMesh();
        m.getTriangle(triangleIndex, store);
        store.calculateCenter();
        store.calculateNormal();
        return store;
    }

    public int compareTo(CollisionResult other) {
        return Float.compare(distance, other.distance);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CollisionResult){
            return ((CollisionResult)obj).compareTo(this) == 0;
        }
        return super.equals(obj);
    }
    
    public Vector3f getContactPoint() {
        return contactPoint;
    }

    public Vector3f getContactNormal() {
        return contactNormal;
    }

    public float getDistance() {
        return distance;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public int getTriangleIndex() {
        return triangleIndex;
    }

}
