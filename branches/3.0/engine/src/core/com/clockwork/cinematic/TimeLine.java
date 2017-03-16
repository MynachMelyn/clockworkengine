
package com.clockwork.cinematic;

import com.clockwork.export.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 */
public class TimeLine extends HashMap<Integer, KeyFrame> implements Savable {

    protected int keyFramesPerSeconds = 30;
    protected int lastKeyFrameIndex = 0;

    public TimeLine() {
        super();
    }

    public KeyFrame getKeyFrameAtTime(float time) {
        return get(getKeyFrameIndexFromTime(time));
    }

    public KeyFrame getKeyFrameAtIndex(int keyFrameIndex) {
        return get(keyFrameIndex);
    }

    public void addKeyFrameAtTime(float time, KeyFrame keyFrame) {
        addKeyFrameAtIndex(getKeyFrameIndexFromTime(time), keyFrame);
    }

    public void addKeyFrameAtIndex(int keyFrameIndex, KeyFrame keyFrame) {
        put(keyFrameIndex, keyFrame);
        keyFrame.setIndex(keyFrameIndex);
        if (lastKeyFrameIndex < keyFrameIndex) {
            lastKeyFrameIndex = keyFrameIndex;
        }
    }

    public void removeKeyFrame(int keyFrameIndex) {
        remove(keyFrameIndex);
        if (lastKeyFrameIndex == keyFrameIndex) {
            KeyFrame kf = null;
            for (int i = keyFrameIndex; kf == null && i >= 0; i--) {
                kf = getKeyFrameAtIndex(i);
                lastKeyFrameIndex = i;
            }
        }
    }
    
    

    public void removeKeyFrame(float time) {
        removeKeyFrame(getKeyFrameIndexFromTime(time));
    }

    public int getKeyFrameIndexFromTime(float time) {
        return Math.round(time * keyFramesPerSeconds);
    }
    
    public float getKeyFrameTime(KeyFrame keyFrame) {
        return (float)keyFrame.getIndex()/(float)keyFramesPerSeconds;
    }

    public Collection<KeyFrame> getAllKeyFrames() {
        return values();
    }

    public int getLastKeyFrameIndex() {
        return lastKeyFrameIndex;
    }

    public void write(CWExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        ArrayList list = new ArrayList();
        list.addAll(values());
        oc.writeSavableArrayList(list, "keyFrames", null);
    }

    public void read(CWImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        ArrayList list = ic.readSavableArrayList("keyFrames", null);
        for (Iterator it = list.iterator(); it.hasNext();) {
            KeyFrame keyFrame = (KeyFrame) it.next();
            addKeyFrameAtIndex(keyFrame.getIndex(), keyFrame);
        }
    }
}
