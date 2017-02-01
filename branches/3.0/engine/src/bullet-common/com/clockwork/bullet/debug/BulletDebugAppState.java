
package com.clockwork.bullet.debug;

import com.clockwork.app.Application;
import com.clockwork.app.state.AbstractAppState;
import com.clockwork.app.state.AppStateManager;
import com.clockwork.asset.AssetManager;
import com.clockwork.bullet.PhysicsSpace;
import com.clockwork.bullet.joints.PhysicsJoint;
import com.clockwork.bullet.objects.PhysicsCharacter;
import com.clockwork.bullet.objects.PhysicsGhostObject;
import com.clockwork.bullet.objects.PhysicsRigidBody;
import com.clockwork.bullet.objects.PhysicsVehicle;
import com.clockwork.material.Material;
import com.clockwork.math.ColorRGBA;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class BulletDebugAppState extends AbstractAppState {

    protected static final Logger logger = Logger.getLogger(BulletDebugAppState.class.getName());
    protected DebugAppStateFilter filter;
    protected Application app;
    protected AssetManager assetManager;
    protected final PhysicsSpace space;
    protected final Node physicsDebugRootNode = new Node("Physics Debug Root Node");
    protected ViewPort viewPort;
    protected RenderManager rm;
    public Material DEBUG_BLUE;
    public Material DEBUG_RED;
    public Material DEBUG_GREEN;
    public Material DEBUG_YELLOW;
    public Material DEBUG_MAGENTA;
    public Material DEBUG_PINK;
    protected HashMap<PhysicsRigidBody, Spatial> bodies = new HashMap<PhysicsRigidBody, Spatial>();
    protected HashMap<PhysicsJoint, Spatial> joints = new HashMap<PhysicsJoint, Spatial>();
    protected HashMap<PhysicsGhostObject, Spatial> ghosts = new HashMap<PhysicsGhostObject, Spatial>();
    protected HashMap<PhysicsCharacter, Spatial> characters = new HashMap<PhysicsCharacter, Spatial>();
    protected HashMap<PhysicsVehicle, Spatial> vehicles = new HashMap<PhysicsVehicle, Spatial>();

    public BulletDebugAppState(PhysicsSpace space) {
        this.space = space;
    }

    public DebugTools getNewDebugTools() {
        return new DebugTools(assetManager);
    }

    public void setFilter(DebugAppStateFilter filter) {
        this.filter = filter;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = app;
        this.rm = app.getRenderManager();
        this.assetManager = app.getAssetManager();
        setupMaterials(app);
        physicsDebugRootNode.setCullHint(Spatial.CullHint.Never);
        viewPort = rm.createMainView("Physics Debug Overlay", app.getCamera());
        viewPort.setClearFlags(false, true, false);
        viewPort.attachScene(physicsDebugRootNode);
    }

    @Override
    public void cleanup() {
        rm.removeMainView(viewPort);
        super.cleanup();
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        //update all object links
        updateRigidBodies();
        updateGhosts();
        updateCharacters();
        updateJoints();
        updateVehicles();
        //update our debug root node
        physicsDebugRootNode.updateLogicalState(tpf);
        physicsDebugRootNode.updateGeometricState();
    }

    @Override
    public void render(RenderManager rm) {
        super.render(rm);
        if (viewPort != null) {
            rm.renderScene(physicsDebugRootNode, viewPort);
        }
    }

    private void setupMaterials(Application app) {
        AssetManager manager = app.getAssetManager();
        DEBUG_BLUE = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        DEBUG_BLUE.getAdditionalRenderState().setWireframe(true);
        DEBUG_BLUE.setColor("Color", ColorRGBA.Blue);
        DEBUG_GREEN = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        DEBUG_GREEN.getAdditionalRenderState().setWireframe(true);
        DEBUG_GREEN.setColor("Color", ColorRGBA.Green);
        DEBUG_RED = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        DEBUG_RED.getAdditionalRenderState().setWireframe(true);
        DEBUG_RED.setColor("Color", ColorRGBA.Red);
        DEBUG_YELLOW = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        DEBUG_YELLOW.getAdditionalRenderState().setWireframe(true);
        DEBUG_YELLOW.setColor("Color", ColorRGBA.Yellow);
        DEBUG_MAGENTA = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        DEBUG_MAGENTA.getAdditionalRenderState().setWireframe(true);
        DEBUG_MAGENTA.setColor("Color", ColorRGBA.Magenta);
        DEBUG_PINK = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        DEBUG_PINK.getAdditionalRenderState().setWireframe(true);
        DEBUG_PINK.setColor("Color", ColorRGBA.Pink);
    }

    private void updateRigidBodies() {
        HashMap<PhysicsRigidBody, Spatial> oldObjects = bodies;
        bodies = new HashMap<PhysicsRigidBody, Spatial>();
        Collection<PhysicsRigidBody> current = space.getRigidBodyList();
        //create new map
        for (Iterator<PhysicsRigidBody> it = current.iterator(); it.hasNext();) {
            PhysicsRigidBody physicsObject = it.next();
            //copy existing spatials
            if (oldObjects.containsKey(physicsObject)) {
                Spatial spat = oldObjects.get(physicsObject);
                bodies.put(physicsObject, spat);
                oldObjects.remove(physicsObject);
            } else {
                if (filter == null || filter.displayObject(physicsObject)) {
                    logger.log(Level.FINE, "Create new debug RigidBody");
                    //create new spatial
                    Node node = new Node(physicsObject.toString());
                    node.addControl(new BulletRigidBodyDebugControl(this, physicsObject));
                    bodies.put(physicsObject, node);
                    physicsDebugRootNode.attachChild(node);
                }
            }
        }
        //remove leftover spatials
        for (Map.Entry<PhysicsRigidBody, Spatial> entry : oldObjects.entrySet()) {
            PhysicsRigidBody object = entry.getKey();
            Spatial spatial = entry.getValue();
            spatial.removeFromParent();
        }
    }

    private void updateJoints() {
        HashMap<PhysicsJoint, Spatial> oldObjects = joints;
        joints = new HashMap<PhysicsJoint, Spatial>();
        Collection<PhysicsJoint> current = space.getJointList();
        //create new map
        for (Iterator<PhysicsJoint> it = current.iterator(); it.hasNext();) {
            PhysicsJoint physicsObject = it.next();
            //copy existing spatials
            if (oldObjects.containsKey(physicsObject)) {
                Spatial spat = oldObjects.get(physicsObject);
                joints.put(physicsObject, spat);
                oldObjects.remove(physicsObject);
            } else {
                if (filter == null || filter.displayObject(physicsObject)) {
                    logger.log(Level.FINE, "Create new debug Joint");
                    //create new spatial
                    Node node = new Node(physicsObject.toString());
                    node.addControl(new BulletJointDebugControl(this, physicsObject));
                    joints.put(physicsObject, node);
                    physicsDebugRootNode.attachChild(node);
                }
            }
        }
        //remove leftover spatials
        for (Map.Entry<PhysicsJoint, Spatial> entry : oldObjects.entrySet()) {
            PhysicsJoint object = entry.getKey();
            Spatial spatial = entry.getValue();
            spatial.removeFromParent();
        }
    }

    private void updateGhosts() {
        HashMap<PhysicsGhostObject, Spatial> oldObjects = ghosts;
        ghosts = new HashMap<PhysicsGhostObject, Spatial>();
        Collection<PhysicsGhostObject> current = space.getGhostObjectList();
        //create new map
        for (Iterator<PhysicsGhostObject> it = current.iterator(); it.hasNext();) {
            PhysicsGhostObject physicsObject = it.next();
            //copy existing spatials
            if (oldObjects.containsKey(physicsObject)) {
                Spatial spat = oldObjects.get(physicsObject);
                ghosts.put(physicsObject, spat);
                oldObjects.remove(physicsObject);
            } else {
                if (filter == null || filter.displayObject(physicsObject)) {
                    logger.log(Level.FINE, "Create new debug GhostObject");
                    //create new spatial
                    Node node = new Node(physicsObject.toString());
                    node.addControl(new BulletGhostObjectDebugControl(this, physicsObject));
                    ghosts.put(physicsObject, node);
                    physicsDebugRootNode.attachChild(node);
                }
            }
        }
        //remove leftover spatials
        for (Map.Entry<PhysicsGhostObject, Spatial> entry : oldObjects.entrySet()) {
            PhysicsGhostObject object = entry.getKey();
            Spatial spatial = entry.getValue();
            spatial.removeFromParent();
        }
    }

    private void updateCharacters() {
        HashMap<PhysicsCharacter, Spatial> oldObjects = characters;
        characters = new HashMap<PhysicsCharacter, Spatial>();
        Collection<PhysicsCharacter> current = space.getCharacterList();
        //create new map
        for (Iterator<PhysicsCharacter> it = current.iterator(); it.hasNext();) {
            PhysicsCharacter physicsObject = it.next();
            //copy existing spatials
            if (oldObjects.containsKey(physicsObject)) {
                Spatial spat = oldObjects.get(physicsObject);
                characters.put(physicsObject, spat);
                oldObjects.remove(physicsObject);
            } else {
                if (filter == null || filter.displayObject(physicsObject)) {
                    logger.log(Level.FINE, "Create new debug Character");
                    //create new spatial
                    Node node = new Node(physicsObject.toString());
                    node.addControl(new BulletCharacterDebugControl(this, physicsObject));
                    characters.put(physicsObject, node);
                    physicsDebugRootNode.attachChild(node);
                }
            }
        }
        //remove leftover spatials
        for (Map.Entry<PhysicsCharacter, Spatial> entry : oldObjects.entrySet()) {
            PhysicsCharacter object = entry.getKey();
            Spatial spatial = entry.getValue();
            spatial.removeFromParent();
        }
    }

    private void updateVehicles() {
        HashMap<PhysicsVehicle, Spatial> oldObjects = vehicles;
        vehicles = new HashMap<PhysicsVehicle, Spatial>();
        Collection<PhysicsVehicle> current = space.getVehicleList();
        //create new map
        for (Iterator<PhysicsVehicle> it = current.iterator(); it.hasNext();) {
            PhysicsVehicle physicsObject = it.next();
            //copy existing spatials
            if (oldObjects.containsKey(physicsObject)) {
                Spatial spat = oldObjects.get(physicsObject);
                vehicles.put(physicsObject, spat);
                oldObjects.remove(physicsObject);
            } else {
                if (filter == null || filter.displayObject(physicsObject)) {
                    logger.log(Level.FINE, "Create new debug Vehicle");
                    //create new spatial
                    Node node = new Node(physicsObject.toString());
                    node.addControl(new BulletVehicleDebugControl(this, physicsObject));
                    vehicles.put(physicsObject, node);
                    physicsDebugRootNode.attachChild(node);
                }
            }
        }
        //remove leftover spatials
        for (Map.Entry<PhysicsVehicle, Spatial> entry : oldObjects.entrySet()) {
            PhysicsVehicle object = entry.getKey();
            Spatial spatial = entry.getValue();
            spatial.removeFromParent();
        }
    }

    /**
     * Interface that allows filtering out objects from the debug display
     */
    public static interface DebugAppStateFilter {

        /**
         * Queries an object to be displayed
         *
         * @param obj The object to be displayed
         * @return return true if the object should be displayed, false if not
         */
        public boolean displayObject(Object obj);
    }
}
