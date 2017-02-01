
package com.clockwork.terrain.geomipmap;

import com.clockwork.scene.VertexBuffer.Type;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Stores a terrain patch's details so the LOD background thread can update
 * the actual terrain patch back on the ogl thread.
 *
 * @author Brent Owens
 *
 */
public class UpdatedTerrainPatch {

    private TerrainPatch updatedPatch;
    private int newLod;
    private int previousLod;
    private int rightLod,topLod,leftLod,bottomLod;
    private Buffer newIndexBuffer;
    //private boolean reIndexNeeded = false;
    private boolean fixEdges = false;

    public UpdatedTerrainPatch(TerrainPatch updatedPatch) {
        this.updatedPatch = updatedPatch;
    }

    public UpdatedTerrainPatch(TerrainPatch updatedPatch, int newLod) {
        this.updatedPatch = updatedPatch;
        this.newLod = newLod;
    }

    public String getName() {
        return updatedPatch.getName();
    }

    protected boolean lodChanged() {
        if ( previousLod != newLod)
            return true;
        else
            return false;
    }

    protected TerrainPatch getUpdatedPatch() {
        return updatedPatch;
    }

    protected void setUpdatedPatch(TerrainPatch updatedPatch) {
        this.updatedPatch = updatedPatch;
    }

    protected int getNewLod() {
        return newLod;
    }
    
    public void setNewLod(int newLod) {
        this.newLod = newLod;
        if (this.newLod < 0)
            throw new IllegalArgumentException("newLod cannot be less than zero, was: "+newLod);
    }

    /*protected IntBuffer getNewIndexBuffer() {
        return newIndexBuffer;
    }*/

    protected void setNewIndexBuffer(Buffer newIndexBuffer) {
        this.newIndexBuffer = newIndexBuffer;
    }


    protected int getRightLod() {
        return rightLod;
    }


    protected void setRightLod(int rightLod) {
        this.rightLod = rightLod;
    }


    protected int getTopLod() {
        return topLod;
    }


    protected void setTopLod(int topLod) {
        this.topLod = topLod;
    }


    protected int getLeftLod() {
        return leftLod;
    }


    protected void setLeftLod(int leftLod) {
        this.leftLod = leftLod;
    }


    protected int getBottomLod() {
        return bottomLod;
    }


    protected void setBottomLod(int bottomLod) {
        this.bottomLod = bottomLod;
    }

    public boolean isReIndexNeeded() {
        if (lodChanged() || isFixEdges())
            return true;
        //if (leftLod != newLod || rightLod != newLod || bottomLod != newLod || topLod != newLod)
        //    return true;
        return false;
    }

    /*public void setReIndexNeeded(boolean reIndexNeeded) {
        this.reIndexNeeded = reIndexNeeded;
    }*/

    public boolean isFixEdges() {
        return fixEdges;
    }

    public void setFixEdges(boolean fixEdges) {
        this.fixEdges = fixEdges;
    }

    /*public int getPreviousLod() {
        return previousLod;
    }*/

    public void setPreviousLod(int previousLod) {
        this.previousLod = previousLod;
    }

    public void updateAll() {
        updatedPatch.setLod(newLod);
        updatedPatch.setLodRight(rightLod);
        updatedPatch.setLodTop(topLod);
        updatedPatch.setLodLeft(leftLod);
        updatedPatch.setLodBottom(bottomLod);
        if (newIndexBuffer != null && isReIndexNeeded()) {
            updatedPatch.setPreviousLod(previousLod);
            updatedPatch.getMesh().clearBuffer(Type.Index);
            if (newIndexBuffer instanceof IntBuffer)
                updatedPatch.getMesh().setBuffer(Type.Index, 3, (IntBuffer)newIndexBuffer);
            else if (newIndexBuffer instanceof ShortBuffer)
                updatedPatch.getMesh().setBuffer(Type.Index, 3, (ShortBuffer)newIndexBuffer);
        }
    }
    
}
