
package com.clockwork.scene.plugins.ogre;

import com.clockwork.animation.Animation;
import com.clockwork.animation.Bone;
import com.clockwork.animation.BoneTrack;
import com.clockwork.animation.Skeleton;
import com.clockwork.asset.AssetInfo;
import com.clockwork.asset.AssetLoader;
import com.clockwork.asset.AssetManager;
import com.clockwork.math.Quaternion;
import com.clockwork.math.Vector3f;
import com.clockwork.util.xml.SAXUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SkeletonLoader extends DefaultHandler implements AssetLoader {

    private static final Logger logger = Logger.getLogger(SceneLoader.class.getName());
    private AssetManager assetManager;
    private Stack<String> elementStack = new Stack<String>();
    private HashMap<Integer, Bone> indexToBone = new HashMap<Integer, Bone>();
    private HashMap<String, Bone> nameToBone = new HashMap<String, Bone>();
    private BoneTrack track;
    private ArrayList<BoneTrack> tracks = new ArrayList<BoneTrack>();
    private Animation animation;
    private ArrayList<Animation> animations;
    private Bone bone;
    private Skeleton skeleton;
    private ArrayList<Float> times = new ArrayList<Float>();
    private ArrayList<Vector3f> translations = new ArrayList<Vector3f>();
    private ArrayList<Quaternion> rotations = new ArrayList<Quaternion>();
    private ArrayList<Vector3f> scales = new ArrayList<Vector3f>();
    private float time = -1;
    private Vector3f position;
    private Quaternion rotation;
    private Vector3f scale;
    private float angle;
    private Vector3f axis;

    public void startElement(String uri, String localName, String qName, Attributes attribs) throws SAXException {
        if (qName.equals("position") || qName.equals("translate")) {
            position = SAXUtil.parseVector3(attribs);
        } else if (qName.equals("rotation") || qName.equals("rotate")) {
            angle = SAXUtil.parseFloat(attribs.getValue("angle"));
        } else if (qName.equals("axis")) {
            assert elementStack.peek().equals("rotation")
                    || elementStack.peek().equals("rotate");
            axis = SAXUtil.parseVector3(attribs);
        } else if (qName.equals("scale")) {
            scale = SAXUtil.parseVector3(attribs);
        } else if (qName.equals("keyframe")) {
            assert elementStack.peek().equals("keyframes");
            time = SAXUtil.parseFloat(attribs.getValue("time"));
        } else if (qName.equals("keyframes")) {
            assert elementStack.peek().equals("track");
        } else if (qName.equals("track")) {
            assert elementStack.peek().equals("tracks");
            String boneName = SAXUtil.parseString(attribs.getValue("bone"));
            Bone bone = nameToBone.get(boneName);
            int index = skeleton.getBoneIndex(bone);
            track = new BoneTrack(index);
        } else if (qName.equals("boneparent")) {
            assert elementStack.peek().equals("bonehierarchy");
            String boneName = attribs.getValue("bone");
            String parentName = attribs.getValue("parent");
            Bone bone = nameToBone.get(boneName);
            Bone parent = nameToBone.get(parentName);
            parent.addChild(bone);
        } else if (qName.equals("bone")) {
            assert elementStack.peek().equals("bones");

            // insert bone into indexed map
            bone = new Bone(attribs.getValue("name"));
            int id = SAXUtil.parseInt(attribs.getValue("id"));
            indexToBone.put(id, bone);
            nameToBone.put(bone.getName(), bone);
        } else if (qName.equals("tracks")) {
            assert elementStack.peek().equals("animation");
            tracks.clear();
        } else if (qName.equals("animation")) {
            assert elementStack.peek().equals("animations");
            String name = SAXUtil.parseString(attribs.getValue("name"));
            float length = SAXUtil.parseFloat(attribs.getValue("length"));
            animation = new Animation(name, length);
        } else if (qName.equals("bonehierarchy")) {
            assert elementStack.peek().equals("skeleton");
        } else if (qName.equals("animations")) {
            assert elementStack.peek().equals("skeleton");
            animations = new ArrayList<Animation>();
        } else if (qName.equals("bones")) {
            assert elementStack.peek().equals("skeleton");
        } else if (qName.equals("skeleton")) {
            assert elementStack.size() == 0;
        }
        elementStack.add(qName);
    }

    public void endElement(String uri, String name, String qName) {
        if (qName.equals("translate") || qName.equals("position") || qName.equals("scale")) {
        } else if (qName.equals("axis")) {
        } else if (qName.equals("rotate") || qName.equals("rotation")) {
            rotation = new Quaternion();
            axis.normalizeLocal();
            rotation.fromAngleNormalAxis(angle, axis);
            angle = 0;
            axis = null;
        } else if (qName.equals("bone")) {
            bone.setBindTransforms(position, rotation, scale);
            bone = null;
            position = null;
            rotation = null;
            scale = null;
        } else if (qName.equals("bonehierarchy")) {
            Bone[] bones = new Bone[indexToBone.size()];
            // find bones without a parent and attach them to the skeleton
            // also assign the bones to the bonelist
            for (Map.Entry<Integer, Bone> entry : indexToBone.entrySet()) {
                Bone bone = entry.getValue();
                bones[entry.getKey()] = bone;
            }
            indexToBone.clear();
            skeleton = new Skeleton(bones);
        } else if (qName.equals("animation")) {
            animations.add(animation);
            animation = null;
        } else if (qName.equals("track")) {
            if (track != null) { // if track has keyframes
                tracks.add(track);
                track = null;
            }
        } else if (qName.equals("tracks")) {
            BoneTrack[] trackList = tracks.toArray(new BoneTrack[tracks.size()]);
            animation.setTracks(trackList);
            tracks.clear();
        } else if (qName.equals("keyframe")) {
            assert time >= 0;
            assert position != null;
            assert rotation != null;

            times.add(time);
            translations.add(position);
            rotations.add(rotation);
            if (scale != null) {
                scales.add(scale);
            }else{
                scales.add(new Vector3f(1,1,1));
            }

            time = -1;
            position = null;
            rotation = null;
            scale = null;
        } else if (qName.equals("keyframes")) {
            if (times.size() > 0) {
                float[] timesArray = new float[times.size()];
                for (int i = 0; i < timesArray.length; i++) {
                    timesArray[i] = times.get(i);
                }

                Vector3f[] transArray = translations.toArray(new Vector3f[translations.size()]);
                Quaternion[] rotArray = rotations.toArray(new Quaternion[rotations.size()]);
                Vector3f[] scalesArray = scales.toArray(new Vector3f[scales.size()]);
                
                track.setKeyframes(timesArray, transArray, rotArray, scalesArray);
                //track.setKeyframes(timesArray, transArray, rotArray);
            } else {
                track = null;
            }

            times.clear();
            translations.clear();
            rotations.clear();
            scales.clear();
        } else if (qName.equals("skeleton")) {
            nameToBone.clear();
        }
        assert elementStack.peek().equals(qName);
        elementStack.pop();
    }

    /**
     * Reset the SkeletonLoader in case an error occured while parsing XML.
     * This allows future use of the loader even after an error.
     */
    private void fullReset() {
        elementStack.clear();
        indexToBone.clear();
        nameToBone.clear();
        track = null;
        tracks.clear();
        animation = null;
        if (animations != null) {
            animations.clear();
        }

        bone = null;
        skeleton = null;
        times.clear();
        rotations.clear();
        translations.clear();
        time = -1;
        position = null;
        rotation = null;
        scale = null;
        angle = 0;
        axis = null;
    }

    public Object load(InputStream in) throws IOException {
        try {
            
            // Added by larynx 25.06.2011
            // Android needs the namespace aware flag set to true 
            // Kirill 30.06.2011
            // Now, hack is applied for both desktop and android to avoid
            // checking with JmeSystem.
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XMLReader xr = factory.newSAXParser().getXMLReader();  
                         
            xr.setContentHandler(this);
            xr.setErrorHandler(this);
            InputStreamReader r = new InputStreamReader(in);
            xr.parse(new InputSource(r));
            if (animations == null) {
                animations = new ArrayList<Animation>();
            }
            AnimData data = new AnimData(skeleton, animations);
            skeleton = null;
            animations = null;
            return data;
        } catch (SAXException ex) {
            IOException ioEx = new IOException("Error while parsing Ogre3D dotScene");
            ioEx.initCause(ex);
            fullReset();
            throw ioEx;
        } catch (ParserConfigurationException ex) {
            IOException ioEx = new IOException("Error while parsing Ogre3D dotScene");
            ioEx.initCause(ex);
            fullReset();
            throw ioEx;
        }
        
    }

    public Object load(AssetInfo info) throws IOException {
        assetManager = info.getManager();
        InputStream in = null;
        try {
            in = info.openStream();
            return load(in);
        } finally {
            if (in != null){
                in.close();
            }
        }
    }
}
