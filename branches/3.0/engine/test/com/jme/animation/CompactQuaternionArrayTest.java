package com.clockwork.animation;

import com.clockwork.animation.*;
import java.util.*;
import static org.junit.Assert.assertTrue;
import org.junit.*;

public class CompactQuaternionArrayTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testCompactQuaternionArrayQuaternionArray() {
        com.clockwork.math.Quaternion[] objArray = new com.clockwork.math.Quaternion[]{
            new com.clockwork.math.Quaternion(1, 0, 1, 1),
            new com.clockwork.math.Quaternion(1, 1, 1, 0),
            new com.clockwork.math.Quaternion(0, 1, 1, 0),
            new com.clockwork.math.Quaternion(1, 1, 1, 0),
            new com.clockwork.math.Quaternion(1, 0, 1, 1),};
        CompactQuaternionArray compact = new CompactQuaternionArray();
        compact.add(objArray);
        assertTrue(Arrays.equals(compact.getIndex(objArray), new int[]{0, 1, 2, 1, 0}));
        assertTrue(Arrays.equals(compact.getSerialisedData(), new float[]{1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0}));
    }

    @Test
    public void testCompactQuaternionArrayDoubleArrayIntArray() {
        int[] indexArray = new int[]{0, 1, 2, 1, 0};
        float[] dataArray = new float[]{1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0};
        com.clockwork.math.Quaternion[] objArray = new com.clockwork.math.Quaternion[]{
            new com.clockwork.math.Quaternion(1, 0, 1, 1),
            new com.clockwork.math.Quaternion(1, 1, 1, 0),
            new com.clockwork.math.Quaternion(0, 1, 1, 0),
            new com.clockwork.math.Quaternion(1, 1, 1, 0),
            new com.clockwork.math.Quaternion(1, 0, 1, 1),};
        CompactQuaternionArray compact = new CompactQuaternionArray(dataArray, indexArray);
        assertTrue(Arrays.deepEquals(compact.toObjectArray(), objArray));
    }
}
