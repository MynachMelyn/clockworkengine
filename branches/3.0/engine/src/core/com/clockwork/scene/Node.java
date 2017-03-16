
package com.clockwork.scene;

import com.clockwork.bounding.BoundingVolume;
import com.clockwork.collision.Collidable;
import com.clockwork.collision.CollisionResults;
import com.clockwork.export.CWExporter;
import com.clockwork.export.CWImporter;
import com.clockwork.export.Savable;
import com.clockwork.material.Material;
import com.clockwork.util.SafeArrayList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Node defines an internal node of a scene graph. The internal
 * node maintains a collection of children and handles merging said children
 * into a single bound to allow for very fast culling of multiple nodes. Node
 * allows for any number of children to be attached.
 * 
 */
public class Node extends Spatial implements Savable {

    private static final Logger logger = Logger.getLogger(Node.class.getName());

    /** 
     * This node's children.
     */
    protected SafeArrayList<Spatial> children = new SafeArrayList<Spatial>(Spatial.class);

    /**
     * Serialisation only. Do not use.
     */
    public Node() {
    }

    /**
     * Constructor instantiates a new Node with a default empty
     * list for containing children.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     */
    public Node(String name) {
        super(name);
    }

    /**
     * 
     * getQuantity returns the number of children this node
     * maintains.
     * 
     * @return the number of children this node maintains.
     */
    public int getQuantity() {
        return children.size();        
    }

    @Override
    protected void setTransformRefresh(){
        super.setTransformRefresh();
        for (Spatial child : children.getArray()){
            if ((child.refreshFlags & RF_TRANSFORM) != 0)
                continue;

            child.setTransformRefresh();
        }
    }

    @Override
    protected void setLightListRefresh(){
        super.setLightListRefresh();
        for (Spatial child : children.getArray()){
            if ((child.refreshFlags & RF_LIGHTLIST) != 0)
                continue;

            child.setLightListRefresh();
        }
    }

    @Override
    protected void updateWorldBound(){
        super.updateWorldBound();
        
        // for a node, the world bound is a combination of all it's children
        // bounds
        BoundingVolume resultBound = null;
        for (Spatial child : children.getArray()) {
            // child bound is assumed to be updated
            assert (child.refreshFlags & RF_BOUND) == 0;
            if (resultBound != null) {
                // merge current world bound with child world bound
                resultBound.mergeLocal(child.getWorldBound());
            } else {
                // set world bound to first non-null child world bound
                if (child.getWorldBound() != null) {
                    resultBound = child.getWorldBound().clone(this.worldBound);
                }
            }
        }
        this.worldBound = resultBound;
    }

    @Override
    public void updateLogicalState(float tpf){
        super.updateLogicalState(tpf);

        if (children.isEmpty()) {
            return;
        }
        
        for (Spatial child : children.getArray()) {
            child.updateLogicalState(tpf);
        }
    }

    @Override
    public void updateGeometricState(){
        if ((refreshFlags & RF_LIGHTLIST) != 0){
            updateWorldLightList();
        }

        if ((refreshFlags & RF_TRANSFORM) != 0){
            // combine with parent transforms- same for all spatial
            // subclasses.
            updateWorldTransforms();
        }

        if (!children.isEmpty()) {
            // the important part- make sure child geometric state is refreshed
            // first before updating own world bound. This saves
            // a round-trip later on.
            // NOTE 9/19/09
            // Although it does save a round trip,
            for (Spatial child : children.getArray()) {
                child.updateGeometricState();
            }
        }            

        if ((refreshFlags & RF_BOUND) != 0){
            updateWorldBound();
        }

        assert refreshFlags == 0;
    }

    /**
     * getTriangleCount returns the number of triangles contained
     * in all sub-branches of this node that contain geometry.
     * 
     * @return the triangle count of this branch.
     */
    @Override
    public int getTriangleCount() {
        int count = 0;
        if(children != null) {
            for(int i = 0; i < children.size(); i++) {
                count += children.get(i).getTriangleCount();
            }
        }

        return count;
    }
    
    /**
     * getVertexCount returns the number of vertices contained
     * in all sub-branches of this node that contain geometry.
     * 
     * @return the vertex count of this branch.
     */
    @Override
    public int getVertexCount() {
        int count = 0;
        if(children != null) {
            for(int i = 0; i < children.size(); i++) {
               count += children.get(i).getVertexCount();
            }
        }

        return count;
    }

    /**
     * attachChild attaches a child to this node. This node
     * becomes the child's parent. The current number of children maintained is
     * returned.
     * 
     * If the child already had a parent it is detached from that former parent.
     * 
     * @param child
     *            the child to attach to this node.
     * @return the number of children maintained by this node.
     * @throws IllegalArgumentException if child is null.
     */
    public int attachChild(Spatial child) {
        if (child == null)
            throw new IllegalArgumentException("child cannot be null");

        if (child.getParent() != this && child != this) {
            if (child.getParent() != null) {
                child.getParent().detachChild(child);
            }
            child.setParent(this);
            children.add(child);

            // XXX: Not entirely correct? Forces bound update up the
            // tree stemming from the attached child. Also forces
            // transform update down the tree-
            child.setTransformRefresh();
            child.setLightListRefresh();
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,"Child ({0}) attached to this node ({1})",
                        new Object[]{child.getName(), getName()});
            }
        }
        
        return children.size();
    }
    
    /**
     * 
     * attachChildAt attaches a child to this node at an index. This node
     * becomes the child's parent. The current number of children maintained is
     * returned.
     * 
     * If the child already had a parent it is detached from that former parent.
     * 
     * @param child
     *            the child to attach to this node.
     * @return the number of children maintained by this node.
     * @throws NullPointerException if child is null.
     */
    public int attachChildAt(Spatial child, int index) {
        if (child == null)
            throw new NullPointerException();

        if (child.getParent() != this && child != this) {
            if (child.getParent() != null) {
                child.getParent().detachChild(child);
            }
            child.setParent(this);
            children.add(index, child);
            child.setTransformRefresh();
            child.setLightListRefresh();
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,"Child ({0}) attached to this node ({1})",
                        new Object[]{child.getName(), getName()});
            }
        }
        
        return children.size();
    }

    /**
     * detachChild removes a given child from the node's list.
     * This child will no longer be maintained.
     * 
     * @param child
     *            the child to remove.
     * @return the index the child was at. -1 if the child was not in the list.
     */
    public int detachChild(Spatial child) {
        if (child == null)
            throw new NullPointerException();

        if (child.getParent() == this) {
            int index = children.indexOf(child);
            if (index != -1) {
                detachChildAt(index);
            }
            return index;
        } 
            
        return -1;        
    }

    /**
     * detachChild removes a given child from the node's list.
     * This child will no longe be maintained. Only the first child with a
     * matching name is removed.
     * 
     * @param childName
     *            the child to remove.
     * @return the index the child was at. -1 if the child was not in the list.
     */
    public int detachChildNamed(String childName) {
        if (childName == null)
            throw new NullPointerException();

        for (int x = 0, max = children.size(); x < max; x++) {
            Spatial child =  children.get(x);
            if (childName.equals(child.getName())) {
                detachChildAt( x );
                return x;
            }
        }
        return -1;
    }

    /**
     * 
     * detachChildAt removes a child at a given index. That child
     * is returned for saving purposes.
     * 
     * @param index
     *            the index of the child to be removed.
     * @return the child at the supplied index.
     */
    public Spatial detachChildAt(int index) {
        Spatial child =  children.remove(index);
        if ( child != null ) {
            child.setParent( null );
            logger.log(Level.FINE, "{0}: Child removed.", this.toString());

            // since a child with a bound was detached;
            // our own bound will probably change.
            setBoundRefresh();

            // our world transform no longer influences the child.
            // XXX: Not neccessary? Since child will have transform updated
            // when attached anyway.
            child.setTransformRefresh();
            // lights are also inherited from parent
            child.setLightListRefresh();
        }
        return child;
    }

    /**
     * 
     * detachAllChildren removes all children attached to this
     * node.
     */
    public void detachAllChildren() {
        for ( int i = children.size() - 1; i >= 0; i-- ) {
            detachChildAt(i);
        }
        logger.log(Level.FINE, "{0}: All children removed.", this.toString());
    }

    /**
     * getChildIndex returns the index of the given spatial
     * in this node's list of children.
     * @param sp
     *          The spatial to look up
     * @return 
     *          The index of the spatial in the node's children, or -1
     *          if the spatial is not attached to this node
     */
    public int getChildIndex(Spatial sp) {
        return children.indexOf(sp);
    }

    /**
     * More efficient than e.g detaching and attaching as no updates are needed.
     * 
     * @param index1 The index of the first child to swap
     * @param index2 The index of the second child to swap
     */
    public void swapChildren(int index1, int index2) {
        Spatial c2 =  children.get(index2);
        Spatial c1 =  children.remove(index1);
        children.add(index1, c2);
        children.remove(index2);
        children.add(index2, c1);
    }

    /**
     * 
     * getChild returns a child at a given index.
     * 
     * @param i
     *            the index to retrieve the child from.
     * @return the child at a specified index.
     */
    public Spatial getChild(int i) {
        return children.get(i);
    }

    /**
     * getChild returns the first child found with exactly the
     * given name (case sensitive.) This method does a depth first recursive
     * search of all descendants of this node, it will return the first spatial
     * found with a matching name.
     * 
     * @param name
     *            the name of the child to retrieve. If null, we'll return null.
     * @return the child if found, or null.
     */
    public Spatial getChild(String name) {
        if (name == null) 
            return null;

        for (Spatial child : children.getArray()) {
            if (name.equals(child.getName())) {
                return child;
            } else if(child instanceof Node) {
                Spatial out = ((Node)child).getChild(name);
                if(out != null) {
                    return out;
                }
            }
        }
        return null;
    }
    
    /**
     * determines if the provided Spatial is contained in the children list of
     * this node.
     * 
     * @param spat
     *            the child object to look for.
     * @return true if the object is contained, false otherwise.
     */
    public boolean hasChild(Spatial spat) {
        if (children.contains(spat))
            return true;

        for (Spatial child : children.getArray()) {
            if (child instanceof Node && ((Node) child).hasChild(spat))
                return true;
        }

        return false;
    }

    /**
     * Returns all children to this node. Note that modifying that given
     * list is not allowed.
     *
     * @return a list containing all children to this node
     */
    public List<Spatial> getChildren() {
        return children;
    }

    @Override
    public void setMaterial(Material mat){
        for (int i = 0; i < children.size(); i++){
            children.get(i).setMaterial(mat);
        }
    }

    @Override
    public void setLodLevel(int lod){
        super.setLodLevel(lod);
        for (Spatial child : children.getArray()) {
            child.setLodLevel(lod);
        }
    }

    public int collideWith(Collidable other, CollisionResults results){
        int total = 0;
        for (Spatial child : children.getArray()){
            total += child.collideWith(other, results);
        }
        return total;
    }


     /**
     * Returns flat list of Spatials implementing the specified class AND
     * with name matching the specified pattern.
     *  
     * Note that we are <i>matching</i> the pattern, therefore the pattern
     * must match the entire pattern (i.e. it behaves as if it is sandwiched
     * between "^" and "$").
     * You can set regex modes, like case insensitivity, by using the (?X)
     * or (?X:Y) constructs.
     *  
     * By design, it is always safe to code loops like:
     *     for (Spatial spatial : node.descendantMatches(AClass.class, "regex"))
     * 
     *  
     * "Descendants" does not include self, per the definition of the word.
     * To test for descendants AND self, you must do a
     * node.matches(aClass, aRegex) +
     * node.descendantMatches(aClass, aRegex).
     * 
     *
     * @param spatialSubclass Subclass which matching Spatials must implement.
     *                        Null causes all Spatials to qualify.
     * @param nameRegex  Regular expression to match Spatial name against.
     *                        Null causes all Names to qualify.
     * @return Non-null, but possibly 0-element, list of matching Spatials (also Instances extending Spatials).
     *
     * see java.util.regex.Pattern
     * see Spatial#matches(java.lang.Class, java.lang.String) 
     */
    @SuppressWarnings("unchecked")
    public <T extends Spatial>List<T> descendantMatches(
            Class<T> spatialSubclass, String nameRegex) {
        List<T> newList = new ArrayList<T>();
        if (getQuantity() < 1) return newList;
        for (Spatial child : getChildren()) {
            if (child.matches(spatialSubclass, nameRegex))
                newList.add((T)child);
            if (child instanceof Node)
                newList.addAll(((Node) child).descendantMatches(
                        spatialSubclass, nameRegex));
        }
        return newList;
    }

    /**
     * Convenience wrapper.
     *
     * see #descendantMatches(java.lang.Class, java.lang.String) 
     */
    public <T extends Spatial>List<T> descendantMatches(
            Class<T> spatialSubclass) {
        return descendantMatches(spatialSubclass, null);
    }

    /**
     * Convenience wrapper.
     *
     * see #descendantMatches(java.lang.Class, java.lang.String) 
     */
    public <T extends Spatial>List<T> descendantMatches(String nameRegex) {
        return descendantMatches(null, nameRegex);
    }

    @Override
    public Node clone(boolean cloneMaterials){
        Node nodeClone = (Node) super.clone(cloneMaterials);
//        nodeClone.children = new ArrayList<Spatial>();
//        for (Spatial child : children){
//            Spatial childClone = child.clone();
//            childClone.parent = nodeClone;
//            nodeClone.children.add(childClone);
//        }
        return nodeClone;
    }

    @Override
    public Spatial deepClone(){
        Node nodeClone = (Node) super.clone();
        nodeClone.children = new SafeArrayList<Spatial>(Spatial.class);
        for (Spatial child : children){
            Spatial childClone = child.deepClone();
            childClone.parent = nodeClone;
            nodeClone.children.add(childClone);
        }
        return nodeClone;
    }

    @Override
    public void write(CWExporter e) throws IOException {
        super.write(e);
        e.getCapsule(this).writeSavableArrayList(new ArrayList(children), "children", null);
    }

    @Override
    public void read(CWImporter e) throws IOException {
        // XXX: Load children before loading itself!!
        // This prevents empty children list if controls query
        // it in Control.setSpatial().
        
        children = new SafeArrayList( Spatial.class, 
                                      e.getCapsule(this).readSavableArrayList("children", null) );

        // go through children and set parent to this node
        if (children != null) {
            for (Spatial child : children.getArray()) {
                child.parent = this;
            }
        }
        
        super.read(e);
    }

    @Override
    public void setModelBound(BoundingVolume modelBound) {
        if(children != null) {
            for (Spatial child : children.getArray()) {
                child.setModelBound(modelBound != null ? modelBound.clone(null) : null);
            }
        }
    }

    @Override
    public void updateModelBound() {
        if(children != null) {
            for (Spatial child : children.getArray()) {
                child.updateModelBound();
            }
        }
    }
    
    @Override
    public void depthFirstTraversal(SceneGraphVisitor visitor) {
        for (Spatial child : children.getArray()) {
            child.depthFirstTraversal(visitor);
        }
        visitor.visit(this);
    }
    
    @Override
    protected void breadthFirstTraversal(SceneGraphVisitor visitor, Queue<Spatial> queue) {
        queue.addAll(children);
    }

}
