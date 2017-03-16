
package com.clockwork.collision;

/**
 * Interface for Collidable objects.
 * Classes that implement this interface are marked as collidable, meaning
 * they support collision detection between other objects that are also
 * collidable.
 * 
 */
public interface Collidable {

    /**
     * Check collision with another Collidable.
     * 
     * @param other The object to check collision against
     * @param results Will contain the list of CollisionResult}s.
     * @return how many collisions were found between this and other
     */
    public int collideWith(Collidable other, CollisionResults results) throws UnsupportedCollisionException;
}
