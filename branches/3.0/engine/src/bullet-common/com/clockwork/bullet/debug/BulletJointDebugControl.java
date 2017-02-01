
package com.clockwork.bullet.debug;

import com.clockwork.bullet.joints.PhysicsJoint;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Transform;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.debug.Arrow;

/**
 *
 */
public class BulletJointDebugControl extends AbstractPhysicsDebugControl {

    protected final PhysicsJoint body;
    protected final Geometry geomA;
    protected final Arrow arrowA;
    protected final Geometry geomB;
    protected final Arrow arrowB;
    protected final Transform a = new Transform(new Vector3f(), new Quaternion());
    protected final Transform b = new Transform(new Vector3f(), new Quaternion());
    protected final Vector3f offA = new Vector3f();
    protected final Vector3f offB = new Vector3f();

    public BulletJointDebugControl(BulletDebugAppState debugAppState, PhysicsJoint body) {
        super(debugAppState);
        this.body = body;
        this.geomA = new Geometry(body.toString());
        arrowA = new Arrow(Vector3f.ZERO);
        geomA.setMesh(arrowA);
        geomA.setMaterial(debugAppState.DEBUG_GREEN);
        this.geomB = new Geometry(body.toString());
        arrowB = new Arrow(Vector3f.ZERO);
        geomB.setMesh(arrowB);
        geomB.setMaterial(debugAppState.DEBUG_GREEN);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null && spatial instanceof Node) {
            Node node = (Node) spatial;
            node.attachChild(geomA);
            node.attachChild(geomB);
        } else if (spatial == null && this.spatial != null) {
            Node node = (Node) this.spatial;
            node.detachChild(geomA);
            node.detachChild(geomB);
        }
        super.setSpatial(spatial);
    }

    @Override
    protected void controlUpdate(float tpf) {
        body.getBodyA().getPhysicsLocation(a.getTranslation());
        body.getBodyA().getPhysicsRotation(a.getRotation());

        body.getBodyB().getPhysicsLocation(b.getTranslation());
        body.getBodyB().getPhysicsRotation(b.getRotation());

        geomA.setLocalTransform(a);
        geomB.setLocalTransform(b);

        arrowA.setArrowExtent(body.getPivotA());
        arrowB.setArrowExtent(body.getPivotB());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
