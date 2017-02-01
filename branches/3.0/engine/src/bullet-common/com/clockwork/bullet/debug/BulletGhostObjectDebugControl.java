
package com.clockwork.bullet.debug;

import com.clockwork.bullet.collision.shapes.CollisionShape;
import com.clockwork.bullet.objects.PhysicsGhostObject;
import com.clockwork.bullet.util.DebugShapeFactory;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;

/**
 *
 */
public class BulletGhostObjectDebugControl extends AbstractPhysicsDebugControl {

    protected final PhysicsGhostObject body;
    protected final Vector3f location = new Vector3f();
    protected final Quaternion rotation = new Quaternion();
    protected CollisionShape myShape;
    protected Spatial geom;

    public BulletGhostObjectDebugControl(BulletDebugAppState debugAppState, PhysicsGhostObject body) {
        super(debugAppState);
        this.body = body;
        myShape = body.getCollisionShape();
        this.geom = DebugShapeFactory.getDebugShape(body.getCollisionShape());
        this.geom.setName(body.toString());
        this.geom.setName(body.toString());
        geom.setMaterial(debugAppState.DEBUG_YELLOW);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null && spatial instanceof Node) {
            Node node = (Node) spatial;
            node.attachChild(geom);
        } else if (spatial == null && this.spatial != null) {
            Node node = (Node) this.spatial;
            node.detachChild(geom);
        }
        super.setSpatial(spatial);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (myShape != body.getCollisionShape()) {
            Node node = (Node) this.spatial;
            node.detachChild(geom);
            geom = DebugShapeFactory.getDebugShape(body.getCollisionShape());
            geom.setMaterial(debugAppState.DEBUG_YELLOW);
            node.attachChild(geom);
        }
        applyPhysicsTransform(body.getPhysicsLocation(location), body.getPhysicsRotation(rotation));
        geom.setLocalScale(body.getCollisionShape().getScale());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
