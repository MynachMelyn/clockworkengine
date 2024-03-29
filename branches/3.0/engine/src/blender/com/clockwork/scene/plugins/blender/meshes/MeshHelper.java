
package com.clockwork.scene.plugins.blender.meshes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.clockwork.asset.BlenderKey.FeaturesToLoad;
import com.clockwork.math.Vector2f;
import com.clockwork.math.Vector3f;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.VertexBuffer;
import com.clockwork.scene.VertexBuffer.Format;
import com.clockwork.scene.VertexBuffer.Type;
import com.clockwork.scene.VertexBuffer.Usage;
import com.clockwork.scene.plugins.blender.AbstractBlenderHelper;
import com.clockwork.scene.plugins.blender.BlenderContext;
import com.clockwork.scene.plugins.blender.BlenderContext.LoadedFeatureDataType;
import com.clockwork.scene.plugins.blender.exceptions.BlenderFileException;
import com.clockwork.scene.plugins.blender.file.DynamicArray;
import com.clockwork.scene.plugins.blender.file.Pointer;
import com.clockwork.scene.plugins.blender.file.Structure;
import com.clockwork.scene.plugins.blender.materials.MaterialContext;
import com.clockwork.scene.plugins.blender.materials.MaterialHelper;
import com.clockwork.scene.plugins.blender.objects.Properties;
import com.clockwork.scene.plugins.blender.textures.TextureHelper;
import com.clockwork.util.BufferUtils;

/**
 * A class that is used in mesh calculations.
 * 
 * 
 */
public class MeshHelper extends AbstractBlenderHelper {
    private static final Logger LOGGER                   = Logger.getLogger(MeshHelper.class.getName());

    /** A type of UV data layer in traditional faced mesh (triangles or quads). */
    private static final int    UV_DATA_LAYER_TYPE_FMESH = 5;
    /** A type of UV data layer in bmesh type. */
    private static final int    UV_DATA_LAYER_TYPE_BMESH = 16;

    /**
     * This constructor parses the given blender version and stores the result. Some functionalities may differ in different blender
     * versions.
     * 
     * @param blenderVersion
     *            the version read from the blend file
     * @param blenderContext
     *            the blender context
     */
    public MeshHelper(String blenderVersion, BlenderContext blenderContext) {
        super(blenderVersion, blenderContext);
    }

    /**
     * This method reads converts the given structure into mesh. The given structure needs to be filled with the appropriate data.
     * 
     * @param structure
     *            the structure we read the mesh from
     * @return the mesh feature
     * @throws BlenderFileException
     */
    @SuppressWarnings("unchecked")
    public List<Geometry> toMesh(Structure structure, BlenderContext blenderContext) throws BlenderFileException {
        List<Geometry> geometries = (List<Geometry>) blenderContext.getLoadedFeature(structure.getOldMemoryAddress(), LoadedFeatureDataType.LOADED_FEATURE);
        if (geometries != null) {
            List<Geometry> copiedGeometries = new ArrayList<Geometry>(geometries.size());
            for (Geometry geometry : geometries) {
                copiedGeometries.add(geometry.clone());
            }
            return copiedGeometries;
        }

        // reading mesh data
        String name = structure.getName();
        MeshContext meshContext = new MeshContext();

        // reading materials
        MaterialHelper materialHelper = blenderContext.getHelper(MaterialHelper.class);
        MaterialContext[] materials = null;
        if ((blenderContext.getBlenderKey().getFeaturesToLoad() & FeaturesToLoad.MATERIALS) != 0) {
            materials = materialHelper.getMaterials(structure, blenderContext);
        }

        // reading vertices and their colors
        Vector3f[][] verticesAndNormals = this.getVerticesAndNormals(structure, blenderContext);
        List<byte[]> verticesColors = this.getVerticesColors(structure, blenderContext);

        MeshBuilder meshBuilder = new MeshBuilder(verticesAndNormals, verticesColors, this.areGeneratedTexturesPresent(materials));

        if (this.isBMeshCompatible(structure)) {
            this.readBMesh(meshBuilder, structure, blenderContext);
        } else {
            this.readTraditionalFaces(meshBuilder, structure, blenderContext);
        }

        if (meshBuilder.isEmpty()) {
            geometries = new ArrayList<Geometry>(0);
            blenderContext.addLoadedFeatures(structure.getOldMemoryAddress(), structure.getName(), structure, geometries);
            blenderContext.setMeshContext(structure.getOldMemoryAddress(), meshContext);
            return geometries;
        }

        meshContext.setVertexReferenceMap(meshBuilder.getVertexReferenceMap());

        // reading vertices groups (from the parent)
        Structure parent = blenderContext.peekParent();
        Structure defbase = (Structure) parent.getFieldValue("defbase");
        List<Structure> defs = defbase.evaluateListBase(blenderContext);
        String[] verticesGroups = new String[defs.size()];
        int defIndex = 0;
        for (Structure def : defs) {
            verticesGroups[defIndex++] = def.getFieldValue("name").toString();
        }

        // creating the result meshes
        geometries = new ArrayList<Geometry>(meshBuilder.getMeshesPartAmount());

        // reading custom properties
        Properties properties = this.loadProperties(structure, blenderContext);

        // generating meshes
        for (Entry<Integer, List<Integer>> meshEntry : meshBuilder.getMeshesMap().entrySet()) {
            int materialIndex = meshEntry.getKey();
            // key is the material index (or -1 if the material has no texture)
            // value is a list of vertex indices
            Mesh mesh = new Mesh();

            // creating vertices indices for this mesh
            List<Integer> indexList = meshEntry.getValue();
            if (meshBuilder.getVerticesAmount(materialIndex) <= Short.MAX_VALUE) {
                short[] indices = new short[indexList.size()];
                for (int i = 0; i < indexList.size(); ++i) {
                    indices[i] = indexList.get(i).shortValue();
                }
                mesh.setBuffer(Type.Index, 1, indices);
            } else {
                int[] indices = new int[indexList.size()];
                for (int i = 0; i < indexList.size(); ++i) {
                    indices[i] = indexList.get(i).intValue();
                }
                mesh.setBuffer(Type.Index, 1, indices);
            }

            VertexBuffer verticesBuffer = new VertexBuffer(Type.Position);
            verticesBuffer.setupData(Usage.Static, 3, Format.Float, BufferUtils.createFloatBuffer(meshBuilder.getVertices(materialIndex)));

            // initial vertex position (used with animation)
            VertexBuffer verticesBind = new VertexBuffer(Type.BindPosePosition);
            verticesBind.setupData(Usage.CpuOnly, 3, Format.Float, BufferUtils.createFloatBuffer(meshBuilder.getVertices(materialIndex)));

            VertexBuffer normalsBuffer = new VertexBuffer(Type.Normal);
            normalsBuffer.setupData(Usage.Static, 3, Format.Float, BufferUtils.createFloatBuffer(meshBuilder.getNormals(materialIndex)));

            // initial normals position (used with animation)
            VertexBuffer normalsBind = new VertexBuffer(Type.BindPoseNormal);
            normalsBind.setupData(Usage.CpuOnly, 3, Format.Float, BufferUtils.createFloatBuffer(meshBuilder.getNormals(materialIndex)));

            mesh.setBuffer(verticesBuffer);
            meshContext.setBindPoseBuffer(materialIndex, verticesBind);// this is stored in the context and applied when needed (when animation is applied to the mesh)

            // setting vertices colors
            if (verticesColors != null) {
                mesh.setBuffer(Type.Color, 4, meshBuilder.getVertexColorsBuffer(materialIndex));
                mesh.getBuffer(Type.Color).setNormalized(true);
            }

            // setting faces' normals
            mesh.setBuffer(normalsBuffer);
            meshContext.setBindNormalBuffer(materialIndex, normalsBind);// this is stored in the context and applied when needed (when animation is applied to the mesh)

            // creating the result
            Geometry geometry = new Geometry(name + (geometries.size() + 1), mesh);
            if (properties != null && properties.getValue() != null) {
                this.applyProperties(geometry, properties);
            }
            geometries.add(geometry);
            meshContext.putGeometry(materialIndex, geometry);
        }

        // store the data in blender context before applying the material
        blenderContext.addLoadedFeatures(structure.getOldMemoryAddress(), structure.getName(), structure, geometries);
        blenderContext.setMeshContext(structure.getOldMemoryAddress(), meshContext);

        // apply materials only when all geometries are in place
        if (materials != null) {
            for (Geometry geometry : geometries) {
                int materialNumber = meshContext.getMaterialIndex(geometry);
                if (materials[materialNumber] != null) {
                    LinkedHashMap<String, List<Vector2f>> uvCoordinates = meshBuilder.getUVCoordinates(materialNumber);
                    MaterialContext materialContext = materials[materialNumber];
                    materialContext.applyMaterial(geometry, structure.getOldMemoryAddress(), uvCoordinates, blenderContext);
                } else {
                    geometry.setMaterial(blenderContext.getDefaultMaterial());
                    LOGGER.warning("The importer came accross mesh that points to a null material. Default material is used to prevent loader from crashing, " + "but the model might look not the way it should. Sometimes blender does not assign materials properly. " + "Enter the edit mode and assign materials once more to your faces.");
                }
            }
        } else {
            // add UV coordinates if they are defined even if the material is not applied to the model
            List<VertexBuffer> uvCoordsBuffer = null;
            if (meshBuilder.hasUVCoordinates()) {
                Map<String, List<Vector2f>> uvs = meshBuilder.getUVCoordinates(0);
                if (uvs != null && uvs.size() > 0) {
                    uvCoordsBuffer = new ArrayList<VertexBuffer>(uvs.size());
                    int uvIndex = 0;
                    for (Entry<String, List<Vector2f>> entry : uvs.entrySet()) {
                        VertexBuffer buffer = new VertexBuffer(TextureHelper.TEXCOORD_TYPES[uvIndex++]);
                        buffer.setupData(Usage.Static, 2, Format.Float, BufferUtils.createFloatBuffer(entry.getValue().toArray(new Vector2f[uvs.size()])));
                        uvCoordsBuffer.add(buffer);
                    }
                }
            }

            for (Geometry geometry : geometries) {
                geometry.setMaterial(blenderContext.getDefaultMaterial());
                if (uvCoordsBuffer != null) {
                    for (VertexBuffer buffer : uvCoordsBuffer) {
                        geometry.getMesh().setBuffer(buffer);
                    }
                }
            }
        }

        return geometries;
    }

    /**
     * Tells if the given mesh structure supports BMesh.
     * 
     * @param meshStructure
     *            the mesh structure
     * @return true if BMesh is supported and false otherwise
     */
    private boolean isBMeshCompatible(Structure meshStructure) {
        Pointer pMLoop = (Pointer) meshStructure.getFieldValue("mloop");
        Pointer pMPoly = (Pointer) meshStructure.getFieldValue("mpoly");
        return pMLoop != null && pMPoly != null && pMLoop.isNotNull() && pMPoly.isNotNull();
    }

    /**
     * The method loads the UV coordinates. The result is a map where the key is the user's UV set name and the values are UV coordinates.
     * But depending on the mesh type (triangle/quads or bmesh) the lists in the map have different meaning.
     * For bmesh they are enlisted just like they are stored in the blend file (in loops).
     * For traditional faces every 4 UV's should be assigned for a single face.
     * @param meshStructure
     *            the mesh structure
     * @param useBMesh
     *            tells if we should load the coordinates from loops of from faces
     * @param blenderContext
     *            the blender context
     * @return a map that sorts UV coordinates between different UV sets
     * @throws BlenderFileException
     *             an exception is thrown when problems with blend file occur
     */
    @SuppressWarnings("unchecked")
    private Map<String, List<Vector2f>> loadUVCoordinates(Structure meshStructure, boolean useBMesh, BlenderContext blenderContext) throws BlenderFileException {
        Map<String, List<Vector2f>> result = new HashMap<String, List<Vector2f>>();
        if (useBMesh) {
            // in this case the UV's are assigned to vertices (an array is the same length as the vertex array)
            Structure loopData = (Structure) meshStructure.getFieldValue("ldata");
            Pointer pLoopDataLayers = (Pointer) loopData.getFieldValue("layers");
            List<Structure> loopDataLayers = pLoopDataLayers.fetchData(blenderContext.getInputStream());
            for (Structure structure : loopDataLayers) {
                Pointer p = (Pointer) structure.getFieldValue("data");
                if (p.isNotNull() && ((Number) structure.getFieldValue("type")).intValue() == UV_DATA_LAYER_TYPE_BMESH) {
                    String uvSetName = structure.getFieldValue("name").toString();
                    List<Structure> uvsStructures = p.fetchData(blenderContext.getInputStream());
                    List<Vector2f> uvs = new ArrayList<Vector2f>(uvsStructures.size());
                    for (Structure uvStructure : uvsStructures) {
                        DynamicArray<Number> loopUVS = (DynamicArray<Number>) uvStructure.getFieldValue("uv");
                        uvs.add(new Vector2f(loopUVS.get(0).floatValue(), loopUVS.get(1).floatValue()));
                    }
                    result.put(uvSetName, uvs);
                }
            }
        } else {
            // in this case UV's are assigned to faces (the array has the same legnth as the faces count)
            Structure facesData = (Structure) meshStructure.getFieldValue("fdata");
            Pointer pFacesDataLayers = (Pointer) facesData.getFieldValue("layers");
            List<Structure> facesDataLayers = pFacesDataLayers.fetchData(blenderContext.getInputStream());
            for (Structure structure : facesDataLayers) {
                Pointer p = (Pointer) structure.getFieldValue("data");
                if (p.isNotNull() && ((Number) structure.getFieldValue("type")).intValue() == UV_DATA_LAYER_TYPE_FMESH) {
                    String uvSetName = structure.getFieldValue("name").toString();
                    List<Structure> uvsStructures = p.fetchData(blenderContext.getInputStream());
                    List<Vector2f> uvs = new ArrayList<Vector2f>(uvsStructures.size());
                    for (Structure uvStructure : uvsStructures) {
                        DynamicArray<Number> mFaceUVs = (DynamicArray<Number>) uvStructure.getFieldValue("uv");
                        uvs.add(new Vector2f(mFaceUVs.get(0).floatValue(), mFaceUVs.get(1).floatValue()));
                        uvs.add(new Vector2f(mFaceUVs.get(2).floatValue(), mFaceUVs.get(3).floatValue()));
                        uvs.add(new Vector2f(mFaceUVs.get(4).floatValue(), mFaceUVs.get(5).floatValue()));
                        uvs.add(new Vector2f(mFaceUVs.get(6).floatValue(), mFaceUVs.get(7).floatValue()));
                    }
                    result.put(uvSetName, uvs);
                }
            }
        }
        return result;
    }

    /**
     * This method reads the mesh from the new BMesh system.
     * 
     * @param meshBuilder
     *            the mesh builder
     * @param meshStructure
     *            the mesh structure
     * @param blenderContext
     *            the blender context
     * @throws BlenderFileException
     *             an exception is thrown when there are problems with the
     *             blender file
     */
    private void readBMesh(MeshBuilder meshBuilder, Structure meshStructure, BlenderContext blenderContext) throws BlenderFileException {
        Pointer pMLoop = (Pointer) meshStructure.getFieldValue("mloop");
        Pointer pMPoly = (Pointer) meshStructure.getFieldValue("mpoly");
        Pointer pMEdge = (Pointer) meshStructure.getFieldValue("medge");
        Map<String, Vector2f[]> uvCoordinatesForFace = new HashMap<String, Vector2f[]>();

        if (pMPoly.isNotNull() && pMLoop.isNotNull() && pMEdge.isNotNull()) {
            Map<String, List<Vector2f>> uvs = this.loadUVCoordinates(meshStructure, true, blenderContext);
            int faceIndex = 0;
            List<Structure> polys = pMPoly.fetchData(blenderContext.getInputStream());
            List<Structure> loops = pMLoop.fetchData(blenderContext.getInputStream());
            for (Structure poly : polys) {
                int materialNumber = ((Number) poly.getFieldValue("mat_nr")).intValue();
                int loopStart = ((Number) poly.getFieldValue("loopstart")).intValue();
                int totLoop = ((Number) poly.getFieldValue("totloop")).intValue();
                boolean smooth = (((Number) poly.getFieldValue("flag")).byteValue() & 0x01) != 0x00;
                int[] vertexIndexes = new int[totLoop];

                for (int i = loopStart; i < loopStart + totLoop; ++i) {
                    vertexIndexes[i - loopStart] = ((Number) loops.get(i).getFieldValue("v")).intValue();
                }

                int i = 0;
                while (i < totLoop - 2) {
                    int v1 = vertexIndexes[0];
                    int v2 = vertexIndexes[i + 1];
                    int v3 = vertexIndexes[i + 2];

                    if (uvs != null) {
                        // uvs always must be added wheater we have texture or not
                        for (Entry<String, List<Vector2f>> entry : uvs.entrySet()) {
                            Vector2f[] uvCoordsForASingleFace = new Vector2f[3];
                            uvCoordsForASingleFace[0] = entry.getValue().get(loopStart);
                            uvCoordsForASingleFace[1] = entry.getValue().get(loopStart + i + 1);
                            uvCoordsForASingleFace[2] = entry.getValue().get(loopStart + i + 2);
                            uvCoordinatesForFace.put(entry.getKey(), uvCoordsForASingleFace);
                        }
                    }

                    meshBuilder.appendFace(v1, v2, v3, smooth, materialNumber, uvs == null ? null : uvCoordinatesForFace, false, faceIndex);
                    uvCoordinatesForFace.clear();
                    ++i;
                }
                ++faceIndex;
            }
        }
    }

    /**
     * This method reads the mesh from traditional triangle/quad storing
     * structures.
     * 
     * @param meshBuilder
     *            the mesh builder
     * @param meshStructure
     *            the mesh structure
     * @param blenderContext
     *            the blender context
     * @throws BlenderFileException
     *             an exception is thrown when there are problems with the
     *             blender file
     */
    private void readTraditionalFaces(MeshBuilder meshBuilder, Structure meshStructure, BlenderContext blenderContext) throws BlenderFileException {
        Pointer pMFace = (Pointer) meshStructure.getFieldValue("mface");
        List<Structure> mFaces = pMFace.isNotNull() ? pMFace.fetchData(blenderContext.getInputStream()) : null;
        if (mFaces != null && mFaces.size() > 0) {
            // indicates if the material with the specified number should have a texture attached
            Map<String, List<Vector2f>> uvs = this.loadUVCoordinates(meshStructure, false, blenderContext);
            Map<String, Vector2f[]> uvCoordinatesForFace = new HashMap<String, Vector2f[]>();
            for (int i = 0; i < mFaces.size(); ++i) {
                Structure mFace = mFaces.get(i);
                int materialNumber = ((Number) mFace.getFieldValue("mat_nr")).intValue();
                boolean smooth = (((Number) mFace.getFieldValue("flag")).byteValue() & 0x01) != 0x00;
                if (uvs != null) {
                    // uvs always must be added wheater we have texture or not
                    for (Entry<String, List<Vector2f>> entry : uvs.entrySet()) {
                        Vector2f[] uvCoordsForASingleFace = new Vector2f[3];
                        uvCoordsForASingleFace[0] = entry.getValue().get(i * 4);
                        uvCoordsForASingleFace[1] = entry.getValue().get(i * 4 + 1);
                        uvCoordsForASingleFace[2] = entry.getValue().get(i * 4 + 2);
                        uvCoordinatesForFace.put(entry.getKey(), uvCoordsForASingleFace);
                    }
                }

                int v1 = ((Number) mFace.getFieldValue("v1")).intValue();
                int v2 = ((Number) mFace.getFieldValue("v2")).intValue();
                int v3 = ((Number) mFace.getFieldValue("v3")).intValue();
                int v4 = ((Number) mFace.getFieldValue("v4")).intValue();

                meshBuilder.appendFace(v1, v2, v3, smooth, materialNumber, uvs == null ? null : uvCoordinatesForFace, false, i);
                uvCoordinatesForFace.clear();
                if (v4 > 0) {
                    if (uvs != null) {
                        // uvs always must be added wheater we have texture or not
                        for (Entry<String, List<Vector2f>> entry : uvs.entrySet()) {
                            Vector2f[] uvCoordsForASingleFace = new Vector2f[3];
                            uvCoordsForASingleFace[0] = entry.getValue().get(i * 4);
                            uvCoordsForASingleFace[1] = entry.getValue().get(i * 4 + 2);
                            uvCoordsForASingleFace[2] = entry.getValue().get(i * 4 + 3);
                            uvCoordinatesForFace.put(entry.getKey(), uvCoordsForASingleFace);
                        }
                    }
                    meshBuilder.appendFace(v1, v3, v4, smooth, materialNumber, uvs == null ? null : uvCoordinatesForFace, true, i);
                    uvCoordinatesForFace.clear();
                }
            }
        } else {
            Pointer pMEdge = (Pointer) meshStructure.getFieldValue("medge");
            List<Structure> mEdges = pMEdge.isNotNull() ? pMEdge.fetchData(blenderContext.getInputStream()) : null;
            if (mEdges != null && mEdges.size() > 0) {
                for (int i = 0; i < mEdges.size(); ++i) {
                    Structure mEdge = mEdges.get(i);
                    boolean smooth = (((Number) mEdge.getFieldValue("flag")).byteValue() & 0x01) != 0x00;

                    int v1 = ((Number) mEdge.getFieldValue("v1")).intValue();
                    int v2 = ((Number) mEdge.getFieldValue("v2")).intValue();

                    meshBuilder.appendEdge(v1, v2, smooth);
                }
            }
        }
    }

    /**
     * @return true if the material has at least one generated component and false otherwise
     */
    private boolean areGeneratedTexturesPresent(MaterialContext[] materials) {
        if (materials != null) {
            for (MaterialContext material : materials) {
                if (material != null && material.hasGeneratedTextures()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method returns the vertices colors. Each vertex is stored in byte[4] array.
     * 
     * @param meshStructure
     *            the structure containing the mesh data
     * @param blenderContext
     *            the blender context
     * @return a list of vertices colors, each color belongs to a single vertex
     * @throws BlenderFileException
     *             this exception is thrown when the blend file structure is somehow invalid or corrupted
     */
    public List<byte[]> getVerticesColors(Structure meshStructure, BlenderContext blenderContext) throws BlenderFileException {
        Pointer pMCol = (Pointer) meshStructure.getFieldValue("mcol");
        List<byte[]> verticesColors = null;
        List<Structure> mCol = null;
        if (pMCol.isNotNull()) {
            verticesColors = new ArrayList<byte[]>();
            mCol = pMCol.fetchData(blenderContext.getInputStream());
            for (Structure color : mCol) {
                byte r = ((Number) color.getFieldValue("r")).byteValue();
                byte g = ((Number) color.getFieldValue("g")).byteValue();
                byte b = ((Number) color.getFieldValue("b")).byteValue();
                byte a = ((Number) color.getFieldValue("a")).byteValue();
                verticesColors.add(new byte[] { b, g, r, a });
            }
        }
        return verticesColors;
    }

    /**
     * This method returns the vertices.
     * 
     * @param meshStructure
     *            the structure containing the mesh data
     * @param blenderContext
     *            the blender context
     * @return a list of two - element arrays, the first element is the vertex and the second - its normal
     * @throws BlenderFileException
     *             this exception is thrown when the blend file structure is somehow invalid or corrupted
     */
    @SuppressWarnings("unchecked")
    private Vector3f[][] getVerticesAndNormals(Structure meshStructure, BlenderContext blenderContext) throws BlenderFileException {
        int count = ((Number) meshStructure.getFieldValue("totvert")).intValue();
        Vector3f[][] result = new Vector3f[count][2];
        if (count == 0) {
            return result;
        }

        Pointer pMVert = (Pointer) meshStructure.getFieldValue("mvert");
        List<Structure> mVerts = pMVert.fetchData(blenderContext.getInputStream());
        if (this.fixUpAxis) {
            for (int i = 0; i < count; ++i) {
                DynamicArray<Number> coordinates = (DynamicArray<Number>) mVerts.get(i).getFieldValue("co");
                result[i][0] = new Vector3f(coordinates.get(0).floatValue(), coordinates.get(2).floatValue(), -coordinates.get(1).floatValue());

                DynamicArray<Number> normals = (DynamicArray<Number>) mVerts.get(i).getFieldValue("no");
                result[i][1] = new Vector3f(normals.get(0).shortValue() / 32767.0f, normals.get(2).shortValue() / 32767.0f, -normals.get(1).shortValue() / 32767.0f);
            }
        } else {
            for (int i = 0; i < count; ++i) {
                DynamicArray<Number> coordinates = (DynamicArray<Number>) mVerts.get(i).getFieldValue("co");
                result[i][0] = new Vector3f(coordinates.get(0).floatValue(), coordinates.get(1).floatValue(), coordinates.get(2).floatValue());

                DynamicArray<Number> normals = (DynamicArray<Number>) mVerts.get(i).getFieldValue("no");
                result[i][1] = new Vector3f(normals.get(0).shortValue() / 32767.0f, normals.get(1).shortValue() / 32767.0f, normals.get(2).shortValue() / 32767.0f);
            }
        }
        return result;
    }

    @Override
    public boolean shouldBeLoaded(Structure structure, BlenderContext blenderContext) {
        return true;
    }
}
