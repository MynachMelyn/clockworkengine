
package com.clockwork.bullet;

import com.clockwork.app.AppTask;
import com.clockwork.asset.AssetManager;
import com.clockwork.bullet.collision.*;
import com.clockwork.bullet.collision.shapes.CollisionShape;
import com.clockwork.bullet.control.PhysicsControl;
import com.clockwork.bullet.control.RigidBodyControl;
import com.clockwork.bullet.joints.PhysicsJoint;
import com.clockwork.bullet.objects.PhysicsCharacter;
import com.clockwork.bullet.objects.PhysicsGhostObject;
import com.clockwork.bullet.objects.PhysicsRigidBody;
import com.clockwork.bullet.objects.PhysicsVehicle;
import com.clockwork.math.Transform;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PhysicsSpace - The central jbullet-CW physics space
 *
 */
public class PhysicsSpace {

    private static final Logger logger = Logger.getLogger(PhysicsSpace.class.getName());
    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;
    public static final int AXIS_Z = 2;
    private long physicsSpaceId = 0;
    private static ThreadLocal<ConcurrentLinkedQueue<AppTask<?>>> pQueueTL =
            new ThreadLocal<ConcurrentLinkedQueue<AppTask<?>>>() {
                @Override
                protected ConcurrentLinkedQueue<AppTask<?>> initialValue() {
                    return new ConcurrentLinkedQueue<AppTask<?>>();
                }
            };
    private ConcurrentLinkedQueue<AppTask<?>> pQueue = new ConcurrentLinkedQueue<AppTask<?>>();
    private static ThreadLocal<PhysicsSpace> physicsSpaceTL = new ThreadLocal<PhysicsSpace>();
    private BroadphaseType broadphaseType = BroadphaseType.DBVT;
//    private DiscreteDynamicsWorld dynamicsWorld = null;
//    private BroadphaseInterface broadphase;
//    private CollisionDispatcher dispatcher;
//    private ConstraintSolver solver;
//    private DefaultCollisionConfiguration collisionConfiguration;
//    private Map<GhostObject, PhysicsGhostObject> physicsGhostNodes = new ConcurrentHashMap<GhostObject, PhysicsGhostObject>();
    private Map<Long, PhysicsGhostObject> physicsGhostObjects = new ConcurrentHashMap<Long, PhysicsGhostObject>();
    private Map<Long, PhysicsCharacter> physicsCharacters = new ConcurrentHashMap<Long, PhysicsCharacter>();
    private Map<Long, PhysicsRigidBody> physicsBodies = new ConcurrentHashMap<Long, PhysicsRigidBody>();
    private Map<Long, PhysicsJoint> physicsJoints = new ConcurrentHashMap<Long, PhysicsJoint>();
    private Map<Long, PhysicsVehicle> physicsVehicles = new ConcurrentHashMap<Long, PhysicsVehicle>();
    private List<PhysicsCollisionListener> collisionListeners = new LinkedList<PhysicsCollisionListener>();
    private List<PhysicsCollisionEvent> collisionEvents = new LinkedList<PhysicsCollisionEvent>();
    private Map<Integer, PhysicsCollisionGroupListener> collisionGroupListeners = new ConcurrentHashMap<Integer, PhysicsCollisionGroupListener>();
    private ConcurrentLinkedQueue<PhysicsTickListener> tickListeners = new ConcurrentLinkedQueue<PhysicsTickListener>();
    private PhysicsCollisionEventFactory eventFactory = new PhysicsCollisionEventFactory();
    private Vector3f worldMin = new Vector3f(-10000f, -10000f, -10000f);
    private Vector3f worldMax = new Vector3f(10000f, 10000f, 10000f);
    private float accuracy = 1f / 60f;
    private int maxSubSteps = 4;
    private AssetManager debugManager;

    static {
//        System.loadLibrary("bulletCW");
//        initNativePhysics();
    }

    /**
     * Get the current PhysicsSpace running on this thread For
     * parallel physics, this can also be called from the OpenGL thread to
     * receive the PhysicsSpace
     *
     * @return the PhysicsSpace running on this thread
     */
    public static PhysicsSpace getPhysicsSpace() {
        return physicsSpaceTL.get();
    }

    /**
     * Used internally
     *
     * @param space
     */
    public static void setLocalThreadPhysicsSpace(PhysicsSpace space) {
        physicsSpaceTL.set(space);
    }

    public PhysicsSpace() {
        this(new Vector3f(-10000f, -10000f, -10000f), new Vector3f(10000f, 10000f, 10000f), BroadphaseType.DBVT);
    }

    public PhysicsSpace(BroadphaseType broadphaseType) {
        this(new Vector3f(-10000f, -10000f, -10000f), new Vector3f(10000f, 10000f, 10000f), broadphaseType);
    }

    public PhysicsSpace(Vector3f worldMin, Vector3f worldMax) {
        this(worldMin, worldMax, BroadphaseType.AXIS_SWEEP_3);
    }

    public PhysicsSpace(Vector3f worldMin, Vector3f worldMax, BroadphaseType broadphaseType) {
        this.worldMin.set(worldMin);
        this.worldMax.set(worldMax);
        this.broadphaseType = broadphaseType;
        create();
    }

    /**
     * Has to be called from the (designated) physics thread
     */
    public void create() {
        physicsSpaceId = createPhysicsSpace(worldMin.x, worldMin.y, worldMin.z, worldMax.x, worldMax.y, worldMax.z, broadphaseType.ordinal(), false);
        pQueueTL.set(pQueue);
        physicsSpaceTL.set(this);

//        collisionConfiguration = new DefaultCollisionConfiguration();
//        dispatcher = new CollisionDispatcher(collisionConfiguration);
//        switch (broadphaseType) {
//            case SIMPLE:
//                broadphase = new SimpleBroadphase();
//                break;
//            case AXIS_SWEEP_3:
//                broadphase = new AxisSweep3(Converter.convert(worldMin), Converter.convert(worldMax));
//                break;
//            case AXIS_SWEEP_3_32:
//                broadphase = new AxisSweep3_32(Converter.convert(worldMin), Converter.convert(worldMax));
//                break;
//            case DBVT:
//                broadphase = new DbvtBroadphase();
//                break;
//        }
//
//        solver = new SequentialImpulseConstraintSolver();
//
//        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
//        dynamicsWorld.setGravity(new javax.vecmath.Vector3f(0, -9.81f, 0));
//
//        broadphase.getOverlappingPairCache().setInternalGhostPairCallback(new GhostPairCallback());
//        GImpactCollisionAlgorithm.registerAlgorithm(dispatcher);
//
//        //register filter callback for tick / collision
//        setTickCallback();
//        setContactCallbacks();
//        //register filter callback for collision groups
//        setOverlapFilterCallback();
    }

    private native long createPhysicsSpace(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int broadphaseType, boolean threading);

    private void preTick_native(float f) {
        AppTask task = pQueue.poll();
        task = pQueue.poll();
        while (task != null) {
            while (task.isCancelled()) {
                task = pQueue.poll();
            }
            try {
                task.invoke();
            } catch (Exception ex) {
                logger.log(Level.SEVERE, null, ex);
            }
            task = pQueue.poll();
        }
        for (Iterator<PhysicsTickListener> it = tickListeners.iterator(); it.hasNext();) {
            PhysicsTickListener physicsTickCallback = it.next();
            physicsTickCallback.prePhysicsTick(this, f);
        }
    }

    private void postTick_native(float f) {
        for (Iterator<PhysicsTickListener> it = tickListeners.iterator(); it.hasNext();) {
            PhysicsTickListener physicsTickCallback = it.next();
            physicsTickCallback.physicsTick(this, f);
        }
    }

    private void addCollision_native() {
    }

    private boolean needCollision_native(PhysicsCollisionObject objectA, PhysicsCollisionObject objectB) {
        return false;
    }

    private void addCollisionEvent_native(PhysicsCollisionObject node, PhysicsCollisionObject node1, long manifoldPointObjectId) {
        collisionEvents.add(eventFactory.getEvent(PhysicsCollisionEvent.TYPE_PROCESSED, node, node1, manifoldPointObjectId));
    }

    /**
     * updates the physics space
     *
     * @param time the current time value
     */
    public void update(float time) {
        update(time, maxSubSteps);
    }

    /**
     * updates the physics space, uses maxSteps
     *
     * @param time the current time value
     * @param maxSteps
     */
    public void update(float time, int maxSteps) {
//        if (getDynamicsWorld() == null) {
//            return;
//        }
        //step simulation
        stepSimulation(physicsSpaceId, time, maxSteps, accuracy);
    }

    private native void stepSimulation(long space, float time, int maxSteps, float accuracy);

    public void distributeEvents() {
        //add collision callbacks
//        synchronized (collisionEvents) {
        for (Iterator<PhysicsCollisionEvent> it = collisionEvents.iterator(); it.hasNext();) {
            PhysicsCollisionEvent physicsCollisionEvent = it.next();
            for (PhysicsCollisionListener listener : collisionListeners) {
                listener.collision(physicsCollisionEvent);
            }
            //recycle events
            eventFactory.recycle(physicsCollisionEvent);
            it.remove();
        }
//        }
    }

    public static <V> Future<V> enqueueOnThisThread(Callable<V> callable) {
        AppTask<V> task = new AppTask<V>(callable);
        System.out.println("created apptask");
        pQueueTL.get().add(task);
        return task;
    }

    /**
     * calls the callable on the next physics tick (ensuring e.g. force
     * applying)
     *
     * @param <V>
     * @param callable
     * @return Future object
     */
    public <V> Future<V> enqueue(Callable<V> callable) {
        AppTask<V> task = new AppTask<V>(callable);
        pQueue.add(task);
        return task;
    }

    /**
     * adds an object to the physics space
     *
     * @param obj the PhysicsControl or Spatial with PhysicsControl to add
     */
    public void add(Object obj) {
        if (obj instanceof PhysicsControl) {
            ((PhysicsControl) obj).setPhysicsSpace(this);
        } else if (obj instanceof Spatial) {
            Spatial node = (Spatial) obj;
            PhysicsControl control = node.getControl(PhysicsControl.class);
            control.setPhysicsSpace(this);
        } else if (obj instanceof PhysicsCollisionObject) {
            addCollisionObject((PhysicsCollisionObject) obj);
        } else if (obj instanceof PhysicsJoint) {
            addJoint((PhysicsJoint) obj);
        } else {
            throw (new UnsupportedOperationException("Cannot add this kind of object to the physics space."));
        }
    }

    public void addCollisionObject(PhysicsCollisionObject obj) {
        if (obj instanceof PhysicsGhostObject) {
            addGhostObject((PhysicsGhostObject) obj);
        } else if (obj instanceof PhysicsRigidBody) {
            addRigidBody((PhysicsRigidBody) obj);
        } else if (obj instanceof PhysicsVehicle) {
            addRigidBody((PhysicsVehicle) obj);
        } else if (obj instanceof PhysicsCharacter) {
            addCharacter((PhysicsCharacter) obj);
        }
    }

    /**
     * removes an object from the physics space
     *
     * @param obj the PhysicsControl or Spatial with PhysicsControl to remove
     */
    public void remove(Object obj) {
        if (obj instanceof PhysicsControl) {
            ((PhysicsControl) obj).setPhysicsSpace(null);
        } else if (obj instanceof Spatial) {
            Spatial node = (Spatial) obj;
            PhysicsControl control = node.getControl(PhysicsControl.class);
            control.setPhysicsSpace(null);
        } else if (obj instanceof PhysicsCollisionObject) {
            removeCollisionObject((PhysicsCollisionObject) obj);
        } else if (obj instanceof PhysicsJoint) {
            removeJoint((PhysicsJoint) obj);
        } else {
            throw (new UnsupportedOperationException("Cannot remove this kind of object from the physics space."));
        }
    }

    public void removeCollisionObject(PhysicsCollisionObject obj) {
        if (obj instanceof PhysicsGhostObject) {
            removeGhostObject((PhysicsGhostObject) obj);
        } else if (obj instanceof PhysicsRigidBody) {
            removeRigidBody((PhysicsRigidBody) obj);
        } else if (obj instanceof PhysicsCharacter) {
            removeCharacter((PhysicsCharacter) obj);
        }
    }

    /**
     * adds all physics controls and joints in the given spatial node to the
     * physics space (e.g. after loading from disk) - recursive if node
     *
     * @param spatial the rootnode containing the physics objects
     */
    public void addAll(Spatial spatial) {
        if (spatial.getControl(RigidBodyControl.class) != null) {
            RigidBodyControl physicsNode = spatial.getControl(RigidBodyControl.class);
            physicsNode.setPhysicsSpace(this);
            //add joints
            List<PhysicsJoint> joints = physicsNode.getJoints();
            for (Iterator<PhysicsJoint> it1 = joints.iterator(); it1.hasNext();) {
                PhysicsJoint physicsJoint = it1.next();
                //add connected physicsnodes if they are not already added
                if (physicsJoint.getBodyA() instanceof PhysicsControl) {
                    add(physicsJoint.getBodyA());
                } else {
                    addRigidBody(physicsJoint.getBodyA());
                }
                if (physicsJoint.getBodyA() instanceof PhysicsControl) {
                    add(physicsJoint.getBodyB());
                } else {
                    addRigidBody(physicsJoint.getBodyB());
                }
                addJoint(physicsJoint);
            }
        } else if (spatial.getControl(PhysicsControl.class) != null) {
            spatial.getControl(PhysicsControl.class).setPhysicsSpace(this);
        }
        //recursion
        if (spatial instanceof Node) {
            List<Spatial> children = ((Node) spatial).getChildren();
            for (Iterator<Spatial> it = children.iterator(); it.hasNext();) {
                Spatial spat = it.next();
                addAll(spat);
            }
        }
    }

    /**
     * Removes all physics controls and joints in the given spatial from the
     * physics space (e.g. before saving to disk) - recursive if node
     *
     * @param spatial the rootnode containing the physics objects
     */
    public void removeAll(Spatial spatial) {
        if (spatial.getControl(RigidBodyControl.class) != null) {
            RigidBodyControl physicsNode = spatial.getControl(RigidBodyControl.class);
            physicsNode.setPhysicsSpace(null);
            //remove joints
            List<PhysicsJoint> joints = physicsNode.getJoints();
            for (Iterator<PhysicsJoint> it1 = joints.iterator(); it1.hasNext();) {
                PhysicsJoint physicsJoint = it1.next();
                //add connected physicsnodes if they are not already added
                if (physicsJoint.getBodyA() instanceof PhysicsControl) {
                    remove(physicsJoint.getBodyA());
                } else {
                    removeRigidBody(physicsJoint.getBodyA());
                }
                if (physicsJoint.getBodyA() instanceof PhysicsControl) {
                    remove(physicsJoint.getBodyB());
                } else {
                    removeRigidBody(physicsJoint.getBodyB());
                }
                removeJoint(physicsJoint);
            }
        } else if (spatial.getControl(PhysicsControl.class) != null) {
            spatial.getControl(PhysicsControl.class).setPhysicsSpace(null);
        }
        //recursion
        if (spatial instanceof Node) {
            List<Spatial> children = ((Node) spatial).getChildren();
            for (Iterator<Spatial> it = children.iterator(); it.hasNext();) {
                Spatial spat = it.next();
                removeAll(spat);
            }
        }
    }

    private native void addCollisionObject(long space, long id);

    private native void removeCollisionObject(long space, long id);

    private native void addRigidBody(long space, long id);

    private native void removeRigidBody(long space, long id);

    private native void addCharacterObject(long space, long id);

    private native void removeCharacterObject(long space, long id);

    private native void addAction(long space, long id);

    private native void removeAction(long space, long id);

    private native void addVehicle(long space, long id);

    private native void removeVehicle(long space, long id);

    private native void addConstraint(long space, long id);

    private native void addConstraintC(long space, long id, boolean collision);

    private native void removeConstraint(long space, long id);

    private void addGhostObject(PhysicsGhostObject node) {
        if (physicsGhostObjects.containsKey(node.getObjectId())) {
            logger.log(Level.WARNING, "GhostObject {0} already exists in PhysicsSpace, cannot add.", node);
            return;
        }
        physicsGhostObjects.put(node.getObjectId(), node);
        logger.log(Level.FINE, "Adding ghost object {0} to physics space.", Long.toHexString(node.getObjectId()));
        addCollisionObject(physicsSpaceId, node.getObjectId());
    }

    private void removeGhostObject(PhysicsGhostObject node) {
        if (!physicsGhostObjects.containsKey(node.getObjectId())) {
            logger.log(Level.WARNING, "GhostObject {0} does not exist in PhysicsSpace, cannot remove.", node);
            return;
        }
        physicsGhostObjects.remove(node.getObjectId());
        logger.log(Level.FINE, "Removing ghost object {0} from physics space.", Long.toHexString(node.getObjectId()));
        removeCollisionObject(physicsSpaceId, node.getObjectId());
    }

    private void addCharacter(PhysicsCharacter node) {
        if (physicsCharacters.containsKey(node.getObjectId())) {
            logger.log(Level.WARNING, "Character {0} already exists in PhysicsSpace, cannot add.", node);
            return;
        }
        physicsCharacters.put(node.getObjectId(), node);
        logger.log(Level.FINE, "Adding character {0} to physics space.", Long.toHexString(node.getObjectId()));
        addCharacterObject(physicsSpaceId, node.getObjectId());
        addAction(physicsSpaceId, node.getControllerId());
//        dynamicsWorld.addCollisionObject(node.getObjectId(), CollisionFilterGroups.CHARACTER_FILTER, (short) (CollisionFilterGroups.STATIC_FILTER | CollisionFilterGroups.DEFAULT_FILTER));
//        dynamicsWorld.addAction(node.getControllerId());
    }

    private void removeCharacter(PhysicsCharacter node) {
        if (!physicsCharacters.containsKey(node.getObjectId())) {
            logger.log(Level.WARNING, "Character {0} does not exist in PhysicsSpace, cannot remove.", node);
            return;
        }
        physicsCharacters.remove(node.getObjectId());
        logger.log(Level.FINE, "Removing character {0} from physics space.", Long.toHexString(node.getObjectId()));
        removeAction(physicsSpaceId, node.getControllerId());
        removeCharacterObject(physicsSpaceId, node.getObjectId());
//        dynamicsWorld.removeAction(node.getControllerId());
//        dynamicsWorld.removeCollisionObject(node.getObjectId());
    }

    private void addRigidBody(PhysicsRigidBody node) {
        if (physicsBodies.containsKey(node.getObjectId())) {
            logger.log(Level.WARNING, "RigidBody {0} already exists in PhysicsSpace, cannot add.", node);
            return;
        }
        physicsBodies.put(node.getObjectId(), node);

        //Workaround
        //It seems that adding a Kinematic RigidBody to the dynamicWorld prevent it from being non kinematic again afterward.
        //so we add it non kinematic, then set it kinematic again.
        boolean kinematic = false;
        if (node.isKinematic()) {
            kinematic = true;
            node.setKinematic(false);
        }
        addRigidBody(physicsSpaceId, node.getObjectId());
        if (kinematic) {
            node.setKinematic(true);
        }

        logger.log(Level.FINE, "Adding RigidBody {0} to physics space.", node.getObjectId());
        if (node instanceof PhysicsVehicle) {
            logger.log(Level.FINE, "Adding vehicle constraint {0} to physics space.", Long.toHexString(((PhysicsVehicle) node).getVehicleId()));
            physicsVehicles.put(((PhysicsVehicle) node).getVehicleId(), (PhysicsVehicle) node);
            addVehicle(physicsSpaceId, ((PhysicsVehicle) node).getVehicleId());
        }
    }

    private void removeRigidBody(PhysicsRigidBody node) {
        if (!physicsBodies.containsKey(node.getObjectId())) {
            logger.log(Level.WARNING, "RigidBody {0} does not exist in PhysicsSpace, cannot remove.", node);
            return;
        }
        if (node instanceof PhysicsVehicle) {
            logger.log(Level.FINE, "Removing vehicle constraint {0} from physics space.", Long.toHexString(((PhysicsVehicle) node).getVehicleId()));
            physicsVehicles.remove(((PhysicsVehicle) node).getVehicleId());
            removeVehicle(physicsSpaceId, ((PhysicsVehicle) node).getVehicleId());
        }
        logger.log(Level.FINE, "Removing RigidBody {0} from physics space.", Long.toHexString(node.getObjectId()));
        physicsBodies.remove(node.getObjectId());
        removeRigidBody(physicsSpaceId, node.getObjectId());
    }

    private void addJoint(PhysicsJoint joint) {
        if (physicsJoints.containsKey(joint.getObjectId())) {
            logger.log(Level.WARNING, "Joint {0} already exists in PhysicsSpace, cannot add.", joint);
            return;
        }
        logger.log(Level.FINE, "Adding Joint {0} to physics space.", Long.toHexString(joint.getObjectId()));
        physicsJoints.put(joint.getObjectId(), joint);
        addConstraintC(physicsSpaceId, joint.getObjectId(), !joint.isCollisionBetweenLinkedBodys());
//        dynamicsWorld.addConstraint(joint.getObjectId(), !joint.isCollisionBetweenLinkedBodys());
    }

    private void removeJoint(PhysicsJoint joint) {
        if (!physicsJoints.containsKey(joint.getObjectId())) {
            logger.log(Level.WARNING, "Joint {0} does not exist in PhysicsSpace, cannot remove.", joint);
            return;
        }
        logger.log(Level.FINE, "Removing Joint {0} from physics space.", Long.toHexString(joint.getObjectId()));
        physicsJoints.remove(joint.getObjectId());
        removeConstraint(physicsSpaceId, joint.getObjectId());
//        dynamicsWorld.removeConstraint(joint.getObjectId());
    }

    public Collection<PhysicsRigidBody> getRigidBodyList() {
        return new LinkedList<PhysicsRigidBody>(physicsBodies.values());
    }

    public Collection<PhysicsGhostObject> getGhostObjectList() {
        return new LinkedList<PhysicsGhostObject>(physicsGhostObjects.values());
    }

    public Collection<PhysicsCharacter> getCharacterList() {
        return new LinkedList<PhysicsCharacter>(physicsCharacters.values());
    }

    public Collection<PhysicsJoint> getJointList() {
        return new LinkedList<PhysicsJoint>(physicsJoints.values());
    }

    public Collection<PhysicsVehicle> getVehicleList() {
        return new LinkedList<PhysicsVehicle>(physicsVehicles.values());
    }

    /**
     * Sets the gravity of the PhysicsSpace, set before adding physics objects!
     *
     * @param gravity
     */
    public void setGravity(Vector3f gravity) {
        gravity.set(gravity);
        setGravity(physicsSpaceId, gravity);
    }

    private native void setGravity(long spaceId, Vector3f gravity);

    //TODO: getGravity
    private final Vector3f gravity = new Vector3f(0,-9.81f,0);
    public Vector3f getGravity(Vector3f gravity) {
        return gravity.set(this.gravity);
    }
    
//    /**
//     * applies gravity value to all objects
//     */
//    public void applyGravity() {
////        dynamicsWorld.applyGravity();
//    }
//
//    /**
//     * clears forces of all objects
//     */
//    public void clearForces() {
////        dynamicsWorld.clearForces();
//    }
//
    /**
     * Adds the specified listener to the physics tick listeners. The listeners
     * are called on each physics step, which is not necessarily each frame but
     * is determined by the accuracy of the physics space.
     *
     * @param listener
     */
    public void addTickListener(PhysicsTickListener listener) {
        tickListeners.add(listener);
    }

    public void removeTickListener(PhysicsTickListener listener) {
        tickListeners.remove(listener);
    }

    /**
     * Adds a CollisionListener that will be informed about collision events
     *
     * @param listener the CollisionListener to add
     */
    public void addCollisionListener(PhysicsCollisionListener listener) {
        collisionListeners.add(listener);
    }

    /**
     * Removes a CollisionListener from the list
     *
     * @param listener the CollisionListener to remove
     */
    public void removeCollisionListener(PhysicsCollisionListener listener) {
        collisionListeners.remove(listener);
    }

    /**
     * Adds a listener for a specific collision group, such a listener can
     * disable collisions when they happen. There can be only one listener
     * per collision group.
     *
     * @param listener
     * @param collisionGroup
     */
    public void addCollisionGroupListener(PhysicsCollisionGroupListener listener, int collisionGroup) {
        collisionGroupListeners.put(collisionGroup, listener);
    }

    public void removeCollisionGroupListener(int collisionGroup) {
        collisionGroupListeners.remove(collisionGroup);
    }

    /**
     * Performs a ray collision test and returns the results as a list of
     * PhysicsRayTestResults
     */
    public List rayTest(Vector3f from, Vector3f to) {
        List results = new LinkedList();
        rayTest(from, to, results);
        return (List<PhysicsRayTestResult>) results;
    }

    /**
     * Performs a ray collision test and returns the results as a list of
     * PhysicsRayTestResults
     */
    public List<PhysicsRayTestResult> rayTest(Vector3f from, Vector3f to, List<PhysicsRayTestResult> results) {
        results.clear();
        rayTest_native(from, to, physicsSpaceId, results);
        return results;
    }

    public native void rayTest_native(Vector3f from, Vector3f to, long physicsSpaceId, List<PhysicsRayTestResult> results);

//    private class InternalRayListener extends CollisionWorld.RayResultCallback {
//
//        private List<PhysicsRayTestResult> results;
//
//        public InternalRayListener(List<PhysicsRayTestResult> results) {
//            this.results = results;
//        }
//
//        @Override
//        public float addSingleResult(LocalRayResult lrr, boolean bln) {
//            PhysicsCollisionObject obj = (PhysicsCollisionObject) lrr.collisionObject.getUserPointer();
//            results.add(new PhysicsRayTestResult(obj, Converter.convert(lrr.hitNormalLocal), lrr.hitFraction, bln));
//            return lrr.hitFraction;
//        }
//    }
    /**
     * Performs a sweep collision test and returns the results as a list of
     * PhysicsSweepTestResults You have to use different Transforms for
     * start and end (at least distance > 0.4f). SweepTest will not see a
     * collision if it starts INSIDE an object and is moving AWAY from its
     * center.
     */
    public List<PhysicsSweepTestResult> sweepTest(CollisionShape shape, Transform start, Transform end) {
        List<PhysicsSweepTestResult> results = new LinkedList<PhysicsSweepTestResult>();
//        if (!(shape.getCShape() instanceof ConvexShape)) {
//            logger.log(Level.WARNING, "Trying to sweep test with incompatible mesh shape!");
//            return results;
//        }
//        dynamicsWorld.convexSweepTest((ConvexShape) shape.getCShape(), Converter.convert(start, sweepTrans1), Converter.convert(end, sweepTrans2), new InternalSweepListener(results));
        return results;

    }

    /**
     * Performs a sweep collision test and returns the results as a list of
     * PhysicsSweepTestResults You have to use different Transforms for
     * start and end (at least distance > 0.4f). SweepTest will not see a
     * collision if it starts INSIDE an object and is moving AWAY from its
     * center.
     */
    public List<PhysicsSweepTestResult> sweepTest(CollisionShape shape, Transform start, Transform end, List<PhysicsSweepTestResult> results) {
        results.clear();
//        if (!(shape.getCShape() instanceof ConvexShape)) {
//            logger.log(Level.WARNING, "Trying to sweep test with incompatible mesh shape!");
//            return results;
//        }
//        dynamicsWorld.convexSweepTest((ConvexShape) shape.getCShape(), Converter.convert(start, sweepTrans1), Converter.convert(end, sweepTrans2), new InternalSweepListener(results));
        return results;
    }

//    private class InternalSweepListener extends CollisionWorld.ConvexResultCallback {
//
//        private List<PhysicsSweepTestResult> results;
//
//        public InternalSweepListener(List<PhysicsSweepTestResult> results) {
//            this.results = results;
//        }
//
//        @Override
//        public float addSingleResult(LocalConvexResult lcr, boolean bln) {
//            PhysicsCollisionObject obj = (PhysicsCollisionObject) lcr.hitCollisionObject.getUserPointer();
//            results.add(new PhysicsSweepTestResult(obj, Converter.convert(lcr.hitNormalLocal), lcr.hitFraction, bln));
//            return lcr.hitFraction;
//        }
//    }
    /**
     * destroys the current PhysicsSpace so that a new one can be created
     */
    public void destroy() {
        physicsBodies.clear();
        physicsJoints.clear();

//        dynamicsWorld.destroy();
//        dynamicsWorld = null;
    }

    /**
     * // * used internally //
     *
     * @return the dynamicsWorld //
     */
    public long getSpaceId() {
        return physicsSpaceId;
    }

    public BroadphaseType getBroadphaseType() {
        return broadphaseType;
    }

    public void setBroadphaseType(BroadphaseType broadphaseType) {
        this.broadphaseType = broadphaseType;
    }

    /**
     * Sets the maximum amount of extra steps that will be used to step the
     * physics when the fps is below the physics fps. Doing this maintains
     * determinism in physics. For example a maximum number of 2 can compensate
     * for framerates as low as 30fps when the physics has the default accuracy
     * of 60 fps. Note that setting this value too high can make the physics
     * drive down its own fps in case its overloaded.
     *
     * @param steps The maximum number of extra steps, default is 4.
     */
    public void setMaxSubSteps(int steps) {
        maxSubSteps = steps;
    }

    /**
     * get the current accuracy of the physics computation
     *
     * @return the current accuracy
     */
    public float getAccuracy() {
        return accuracy;
    }

    /**
     * sets the accuracy of the physics computation, default=1/60s
     *
     * @param accuracy
     */
    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public Vector3f getWorldMin() {
        return worldMin;
    }

    /**
     * only applies for AXIS_SWEEP broadphase
     *
     * @param worldMin
     */
    public void setWorldMin(Vector3f worldMin) {
        this.worldMin.set(worldMin);
    }

    public Vector3f getWorldMax() {
        return worldMax;
    }

    /**
     * only applies for AXIS_SWEEP broadphase
     *
     * @param worldMax
     */
    public void setWorldMax(Vector3f worldMax) {
        this.worldMax.set(worldMax);
    }

    /**
     * Enable debug display for physics.
     *
     * @deprecated in favor of BulletDebugAppState, use
     * BulletAppState.setDebugEnabled(boolean) to add automatically
     * @param manager AssetManager to use to create debug materials
     */
    @Deprecated
    public void enableDebug(AssetManager manager) {
        debugManager = manager;
    }

    /**
     * Disable debug display
     */
    public void disableDebug() {
        debugManager = null;
    }

    public AssetManager getDebugManager() {
        return debugManager;
    }

    public static native void initNativePhysics();

    /**
     * interface with Broadphase types
     */
    public enum BroadphaseType {

        /**
         * basic Broadphase
         */
        SIMPLE,
        /**
         * better Broadphase, needs worldBounds , max Object number = 16384
         */
        AXIS_SWEEP_3,
        /**
         * better Broadphase, needs worldBounds , max Object number = 65536
         */
        AXIS_SWEEP_3_32,
        /**
         * Broadphase allowing quicker adding/removing of physics objects
         */
        DBVT;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Finalizing PhysicsSpace {0}", Long.toHexString(physicsSpaceId));
        finalizeNative(physicsSpaceId);
    }

    private native void finalizeNative(long objectId);
}
