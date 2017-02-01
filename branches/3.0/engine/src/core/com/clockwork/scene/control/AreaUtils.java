
package com.clockwork.scene.control;

import com.clockwork.bounding.BoundingBox;
import com.clockwork.bounding.BoundingSphere;
import com.clockwork.bounding.BoundingVolume;
import com.clockwork.math.FastMath;

/**
 * <code>AreaUtils</code> is used to calculate the area of various objects, such as bounding volumes.  These
 * functions are very loose approximations.
 * @version $Id: AreaUtils.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */

public class AreaUtils {

  /**
   * calcScreenArea -- in Pixels
   * Aproximates the screen area of a bounding volume.  If the volume isn't a
   * BoundingSphere, BoundingBox, or OrientedBoundingBox 0 is returned.
   *
   * @param bound The bounds to calculate the volume from.
   * @param distance The distance from camera to object.
   * @param screenWidth The width of the screen.
   * @return The area in pixels on the screen of the bounding volume.
   */
  public static float calcScreenArea(BoundingVolume bound, float distance, float screenWidth) {
      if (bound.getType() == BoundingVolume.Type.Sphere){
          return calcScreenArea((BoundingSphere) bound, distance, screenWidth);
      }else if (bound.getType() == BoundingVolume.Type.AABB){
          return calcScreenArea((BoundingBox) bound, distance, screenWidth);
      }
      return 0.0f;
  }

  private static float calcScreenArea(BoundingSphere bound, float distance, float screenWidth) {
    // Where is the center point and a radius point that lies in a plan parallel to the view plane?
//    // Calc radius based on these two points and plug into circle area formula.
//    Vector2f centerSP = null;
//    Vector2f outerSP = null;
//    float radiusSq = centerSP.subtract(outerSP).lengthSquared();
      float radius = (bound.getRadius() * screenWidth) / (distance * 2);
      return radius * radius * FastMath.PI;
  }

  private static float calcScreenArea(BoundingBox bound, float distance, float screenWidth) {
      // Calc as if we are a BoundingSphere for now...
      float radiusSquare = bound.getXExtent() * bound.getXExtent()
                         + bound.getYExtent() * bound.getYExtent()
                         + bound.getZExtent() * bound.getZExtent();
      return ((radiusSquare * screenWidth * screenWidth) / (distance * distance * 4)) * FastMath.PI;
  }
}