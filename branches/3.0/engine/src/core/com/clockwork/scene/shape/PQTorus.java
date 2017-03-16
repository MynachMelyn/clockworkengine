
// $Id: PQTorus.java 4131 2009-03-19 20:15:28Z blaine.dev $
package com.clockwork.scene.shape;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.math.FastMath;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.VertexBuffer.Type;
import static com.clockwork.util.BufferUtils.*;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * A parameterized torus, also known as a <em>pq</em> torus.
 * 
 * @version $Revision: 4131 $, $Date: 2009-03-19 16:15:28 -0400 (Thu, 19 Mar 2009) $
 */
public class PQTorus extends Mesh {

    private float p, q;

    private float radius, width;

    private int steps, radialSamples;

    public PQTorus() {
    }

    /**
     * Creates a parameterized torus.
     * 
     * Steps and radialSamples are both degree of accuracy values.
     * 
     * @param p the x/z oscillation.
     * @param q the y oscillation.
     * @param radius the radius of the PQTorus.
     * @param width the width of the torus.
     * @param steps the steps along the torus.
     * @param radialSamples radial samples for the torus.
     */
    public PQTorus(float p, float q, float radius, float width,
            int steps, int radialSamples) {
        super();
        updateGeometry(p, q, radius, width, steps, radialSamples);
    }

    public float getP() {
        return p;
    }

    public float getQ() {
        return q;
    }

    public int getRadialSamples() {
        return radialSamples;
    }

    public float getRadius() {
        return radius;
    }

    public int getSteps() {
        return steps;
    }

    public float getWidth() {
        return width;
    }

    /**
     * Rebuilds this torus based on a new set of parameters.
     * 
     * @param p the x/z oscillation.
     * @param q the y oscillation.
     * @param radius the radius of the PQTorus.
     * @param width the width of the torus.
     * @param steps the steps along the torus.
     * @param radialSamples radial samples for the torus.
     */
    public void updateGeometry(float p, float q, float radius, float width, int steps, int radialSamples) {
        this.p = p;
        this.q = q;
        this.radius = radius;
        this.width = width;
        this.steps = steps;
        this.radialSamples = radialSamples;

        final float thetaStep = (FastMath.TWO_PI / steps);
        final float betaStep = (FastMath.TWO_PI / radialSamples);
        Vector3f[] torusPoints = new Vector3f[steps];

        // Allocate all of the required buffers
        int vertCount = radialSamples * steps;
        
        FloatBuffer fpb = createVector3Buffer(vertCount);
        FloatBuffer fnb = createVector3Buffer(vertCount);
        FloatBuffer ftb = createVector2Buffer(vertCount);

        Vector3f pointB, T, N, B;
        Vector3f tempNorm = new Vector3f();
        float r, x, y, z, theta = 0.0f, beta;
        int nvertex = 0;

        // Move along the length of the pq torus
        for (int i = 0; i < steps; i++) {
            theta += thetaStep;
            float circleFraction = ((float) i) / (float) steps;

            // Find the point on the torus
            r = (0.5f * (2.0f + FastMath.sin(q * theta)) * radius);
            x = (r * FastMath.cos(p * theta) * radius);
            y = (r * FastMath.sin(p * theta) * radius);
            z = (r * FastMath.cos(q * theta) * radius);
            torusPoints[i] = new Vector3f(x, y, z);

            // Now find a point slightly farther along the torus
            r = (0.5f * (2.0f + FastMath.sin(q * (theta + 0.01f))) * radius);
            x = (r * FastMath.cos(p * (theta + 0.01f)) * radius);
            y = (r * FastMath.sin(p * (theta + 0.01f)) * radius);
            z = (r * FastMath.cos(q * (theta + 0.01f)) * radius);
            pointB = new Vector3f(x, y, z);

            // Approximate the Frenet Frame
            T = pointB.subtract(torusPoints[i]);
            N = torusPoints[i].add(pointB);
            B = T.cross(N);
            N = B.cross(T);

            // Normalise the two vectors and then use them to create an oriented circle
            N = N.normalize();
            B = B.normalize();
            beta = 0.0f;
            for (int j = 0; j < radialSamples; j++, nvertex++) {
                beta += betaStep;
                float cx = FastMath.cos(beta) * width;
                float cy = FastMath.sin(beta) * width;
                float radialFraction = ((float) j) / radialSamples;
                tempNorm.x = (cx * N.x + cy * B.x);
                tempNorm.y = (cx * N.y + cy * B.y);
                tempNorm.z = (cx * N.z + cy * B.z);
                fnb.put(tempNorm.x).put(tempNorm.y).put(tempNorm.z);
                tempNorm.addLocal(torusPoints[i]);
                fpb.put(tempNorm.x).put(tempNorm.y).put(tempNorm.z);
                ftb.put(radialFraction).put(circleFraction);
            }
        }

        // Update the indices data
        ShortBuffer sib = createShortBuffer(6 * vertCount);
        for (int i = 0; i < vertCount; i++) {
            sib.put(new short[] {
                    (short)(i),
                    (short)(i - radialSamples),
                    (short)(i + 1),
                    (short)(i + 1),
                    (short)(i - radialSamples),
                    (short)(i - radialSamples + 1)
            });
        }
        for (int i = 0, len = sib.capacity(); i < len; i++) {
            int ind = sib.get(i);
            if (ind < 0) {
                ind += vertCount;
                sib.put(i, (short) ind);
            } else if (ind >= vertCount) {
                ind -= vertCount;
                sib.put(i, (short) ind);
            }
        }
        sib.rewind();

        setBuffer(Type.Position, 3, fpb);
        setBuffer(Type.Normal,   3, fnb);
        setBuffer(Type.TexCoord, 2, ftb);
        setBuffer(Type.Index,    3, sib);
    }

    @Override
    public void read(CWImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        p = capsule.readFloat("p", 0);
        q = capsule.readFloat("q", 0);
        radius = capsule.readFloat("radius", 0);
        width = capsule.readFloat("width", 0);
        steps = capsule.readInt("steps", 0);
        radialSamples = capsule.readInt("radialSamples", 0);
    }
    
    @Override
    public void write(CWExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(p, "p", 0);
        capsule.write(q, "q", 0);
        capsule.write(radius, "radius", 0);
        capsule.write(width, "width", 0);
        capsule.write(steps, "steps", 0);
        capsule.write(radialSamples, "radialSamples", 0);
    }

}