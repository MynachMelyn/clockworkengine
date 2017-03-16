
package com.clockwork.shadow;

import com.clockwork.bounding.BoundingBox;
import com.clockwork.math.FastMath;
import com.clockwork.math.Matrix4f;
import com.clockwork.renderer.Camera;
import com.clockwork.renderer.queue.GeometryList;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Includes various useful shadow mapping functions.
 *
 * see
 * 
 * <a href="http://appsrv.cse.cuhk.edu.hk/~fzhang/pssm_vrcia/">http://appsrv.cse.cuhk.edu.hk/~fzhang/pssm_vrcia/</a>
 * <a href="http://http.developer.nvidia.com/GPUGems3/gpugems3_ch10.html">http://http.developer.nvidia.com/GPUGems3/gpugems3_ch10.html</a>
 * 
 * for more info.
 */
public final class PssmShadowUtil {

    /**
     * Updates the frustum splits stores in splits using PSSM.
     */
    public static void updateFrustumSplits(float[] splits, float near, float far, float lambda) {
        for (int i = 0; i < splits.length; i++) {
            float IDM = i / (float) splits.length;
            float log = near * FastMath.pow((far / near), IDM);
            float uniform = near + (far - near) * IDM;
            splits[i] = log * lambda + uniform * (1.0f - lambda);
        }

        // This is used to improve the correctness of the calculations. Our main near- and farplane
        // of the camera always stay the same, no matter what happens.
        splits[0] = near;
        splits[splits.length - 1] = far;
    }

    /**
     * Compute the Zfar in the model vieuw to adjust the Zfar distance for the splits calculation
     */
    public static float computeZFar(GeometryList occ, GeometryList recv, Camera cam) {
        Matrix4f mat = cam.getViewMatrix();
        BoundingBox bbOcc = ShadowUtil.computeUnionBound(occ, mat);
        BoundingBox bbRecv = ShadowUtil.computeUnionBound(recv, mat);

        return min(max(bbOcc.getZExtent() - bbOcc.getCenter().z, bbRecv.getZExtent() - bbRecv.getCenter().z), cam.getFrustumFar());
    }
}
