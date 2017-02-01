
package com.clockwork.bullet.util;

import com.clockwork.math.Vector3f;
import com.clockwork.util.BufferUtils;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 *
 */
public class DebugMeshCallback {

    private ArrayList<Vector3f> list = new ArrayList<Vector3f>();

    public void addVector(float x, float y, float z, int part, int index) {
        list.add(new Vector3f(x, y, z));
    }

    public FloatBuffer getVertices() {
        FloatBuffer buf = BufferUtils.createFloatBuffer(list.size() * 3);
        for (int i = 0; i < list.size(); i++) {
            Vector3f vector3f = list.get(i);
            buf.put(vector3f.x);
            buf.put(vector3f.y);
            buf.put(vector3f.z);
        }
        return buf;
    }
}
