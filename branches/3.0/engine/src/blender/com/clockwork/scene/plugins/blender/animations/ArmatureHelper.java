
package com.clockwork.scene.plugins.blender.animations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clockwork.animation.Bone;
import com.clockwork.animation.BoneTrack;
import com.clockwork.animation.Skeleton;
import com.clockwork.math.Matrix4f;
import com.clockwork.scene.plugins.blender.AbstractBlenderHelper;
import com.clockwork.scene.plugins.blender.BlenderContext;
import com.clockwork.scene.plugins.blender.curves.BezierCurve;
import com.clockwork.scene.plugins.blender.exceptions.BlenderFileException;
import com.clockwork.scene.plugins.blender.file.Pointer;
import com.clockwork.scene.plugins.blender.file.Structure;

/**
 * This class defines the methods to calculate certain aspects of animation and
 * armature functionalities.
 * 
 * @author Marcin Roguski (Kaelthas)
 */
public class ArmatureHelper extends AbstractBlenderHelper {
    private static final Logger LOGGER               = Logger.getLogger(ArmatureHelper.class.getName());

    public static final String  ARMATURE_NODE_MARKER = "armature-node";

    /**
     * This constructor parses the given blender version and stores the result.
     * Some functionalities may differ in different blender versions.
     * 
     * @param blenderVersion
     *            the version read from the blend file
     * @param blenderContext
     *            the blender context
     */
    public ArmatureHelper(String blenderVersion, BlenderContext blenderContext) {
        super(blenderVersion, blenderContext);
    }

    /**
     * This method builds the object's bones structure.
     * 
     * @param boneStructure
     *            the structure containing the bones' data
     * @param parent
     *            the parent bone
     * @param result
     *            the list where the newly created bone will be added
     * @param blenderContext
     *            the blender context
     * @throws BlenderFileException
     *             an exception is thrown when there is problem with the blender
     *             file
     */
    public void buildBones(Long armatureObjectOMA, Structure boneStructure, Bone parent, List<Bone> result, Matrix4f objectToArmatureTransformation, BlenderContext blenderContext) throws BlenderFileException {
        BoneContext bc = new BoneContext(armatureObjectOMA, boneStructure, blenderContext);
        bc.buildBone(result, objectToArmatureTransformation, blenderContext);
    }

    /**
     * This method returns a map where the key is the object's group index that
     * is used by a bone and the key is the bone index in the armature.
     * 
     * @param defBaseStructure
     *            a bPose structure of the object
     * @return bone group-to-index map
     * @throws BlenderFileException
     *             this exception is thrown when the blender file is somehow
     *             corrupted
     */
    public Map<Integer, Integer> getGroupToBoneIndexMap(Structure defBaseStructure, Skeleton skeleton, BlenderContext blenderContext) throws BlenderFileException {
        Map<Integer, Integer> result = null;
        if (skeleton.getBoneCount() != 0) {
            result = new HashMap<Integer, Integer>();
            List<Structure> deformGroups = defBaseStructure.evaluateListBase(blenderContext);// bDeformGroup
            int groupIndex = 0;
            for (Structure deformGroup : deformGroups) {
                String deformGroupName = deformGroup.getFieldValue("name").toString();
                int boneIndex = this.getBoneIndex(skeleton, deformGroupName);
                if (boneIndex >= 0) {
                    result.put(groupIndex, boneIndex);
                }
                ++groupIndex;
            }
        }
        return result;
    }

    @Override
    public boolean shouldBeLoaded(Structure structure, BlenderContext blenderContext) {
        return true;
    }

    /**
     * This method retuns the bone tracks for animation.
     * 
     * @param actionStructure
     *            the structure containing the tracks
     * @param blenderContext
     *            the blender context
     * @return a list of tracks for the specified animation
     * @throws BlenderFileException
     *             an exception is thrown when there are problems with the blend
     *             file
     */
    public BoneTrack[] getTracks(Structure actionStructure, Skeleton skeleton, BlenderContext blenderContext) throws BlenderFileException {
        if (blenderVersion < 250) {
            return this.getTracks249(actionStructure, skeleton, blenderContext);
        } else {
            return this.getTracks250(actionStructure, skeleton, blenderContext);
        }
    }

    /**
     * This method retuns the bone tracks for animation for blender version 2.50
     * and higher.
     * 
     * @param actionStructure
     *            the structure containing the tracks
     * @param blenderContext
     *            the blender context
     * @return a list of tracks for the specified animation
     * @throws BlenderFileException
     *             an exception is thrown when there are problems with the blend
     *             file
     */
    private BoneTrack[] getTracks250(Structure actionStructure, Skeleton skeleton, BlenderContext blenderContext) throws BlenderFileException {
        LOGGER.log(Level.FINE, "Getting tracks!");
        IpoHelper ipoHelper = blenderContext.getHelper(IpoHelper.class);
        int fps = blenderContext.getBlenderKey().getFps();
        Structure groups = (Structure) actionStructure.getFieldValue("groups");
        List<Structure> actionGroups = groups.evaluateListBase(blenderContext);// bActionGroup
        List<BoneTrack> tracks = new ArrayList<BoneTrack>();
        for (Structure actionGroup : actionGroups) {
            String name = actionGroup.getFieldValue("name").toString();
            int boneIndex = this.getBoneIndex(skeleton, name);
            if (boneIndex >= 0) {
                List<Structure> channels = ((Structure) actionGroup.getFieldValue("channels")).evaluateListBase(blenderContext);
                BezierCurve[] bezierCurves = new BezierCurve[channels.size()];
                int channelCounter = 0;
                for (Structure c : channels) {
                    int type = ipoHelper.getCurveType(c, blenderContext);
                    Pointer pBezTriple = (Pointer) c.getFieldValue("bezt");
                    List<Structure> bezTriples = pBezTriple.fetchData(blenderContext.getInputStream());
                    bezierCurves[channelCounter++] = new BezierCurve(type, bezTriples, 2);
                }

                Bone bone = skeleton.getBone(boneIndex);
                Ipo ipo = new Ipo(bezierCurves, fixUpAxis, blenderContext.getBlenderVersion());
                tracks.add((BoneTrack) ipo.calculateTrack(boneIndex, bone.getLocalRotation(), 0, ipo.getLastFrame(), fps, false));
            }
        }
        return tracks.toArray(new BoneTrack[tracks.size()]);
    }

    /**
     * This method retuns the bone tracks for animation for blender version 2.49
     * (and probably several lower versions too).
     * 
     * @param actionStructure
     *            the structure containing the tracks
     * @param blenderContext
     *            the blender context
     * @return a list of tracks for the specified animation
     * @throws BlenderFileException
     *             an exception is thrown when there are problems with the blend
     *             file
     */
    private BoneTrack[] getTracks249(Structure actionStructure, Skeleton skeleton, BlenderContext blenderContext) throws BlenderFileException {
        LOGGER.log(Level.FINE, "Getting tracks!");
        IpoHelper ipoHelper = blenderContext.getHelper(IpoHelper.class);
        int fps = blenderContext.getBlenderKey().getFps();
        Structure chanbase = (Structure) actionStructure.getFieldValue("chanbase");
        List<Structure> actionChannels = chanbase.evaluateListBase(blenderContext);// bActionChannel
        List<BoneTrack> tracks = new ArrayList<BoneTrack>();
        for (Structure bActionChannel : actionChannels) {
            String name = bActionChannel.getFieldValue("name").toString();
            int boneIndex = this.getBoneIndex(skeleton, name);
            if (boneIndex >= 0) {
                Pointer p = (Pointer) bActionChannel.getFieldValue("ipo");
                if (!p.isNull()) {
                    Structure ipoStructure = p.fetchData(blenderContext.getInputStream()).get(0);

                    Bone bone = skeleton.getBone(boneIndex);
                    Ipo ipo = ipoHelper.fromIpoStructure(ipoStructure, blenderContext);
                    if (ipo != null) {
                        tracks.add((BoneTrack) ipo.calculateTrack(boneIndex, bone.getLocalRotation(), 0, ipo.getLastFrame(), fps, false));
                    }
                }
            }
        }
        return tracks.toArray(new BoneTrack[tracks.size()]);
    }

    /**
     * This method returns the index of the bone in the given skeleton.
     * 
     * @param skeleton
     *            the skeleton
     * @param boneName
     *            the name of the bone
     * @return the index of the bone
     */
    private int getBoneIndex(Skeleton skeleton, String boneName) {
        int result = -1;
        for (int i = 0; i < skeleton.getBoneCount() && result == -1; ++i) {
            if (boneName.equals(skeleton.getBone(i).getName())) {
                result = i;
            }
        }
        return result;
    }
}
