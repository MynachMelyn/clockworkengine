

package clockworktools.optimize;

import com.clockwork.bounding.BoundingBox;
import com.clockwork.bounding.BoundingVolume;
import com.clockwork.collision.CollisionResults;
import com.clockwork.material.Material;
import com.clockwork.math.Matrix4f;
import com.clockwork.math.Ray;
import com.clockwork.math.Triangle;
import com.clockwork.renderer.Camera;
import com.clockwork.renderer.queue.RenderQueue;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.debug.WireBox;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Octree {

    private final ArrayList<OCTTriangle> allTris = new ArrayList<OCTTriangle>();
    private final Geometry[] geoms;
    private final BoundingBox bbox;
    private Octnode root;

    private CollisionResults boundResults = new CollisionResults();

    private static List<Geometry> getGeometries(Spatial scene){
        if (scene instanceof Geometry){
            List<Geometry> geomList = new ArrayList<Geometry>(1);
            geomList.add((Geometry) scene);
            return geomList;
        }else if (scene instanceof Node){
            Node n = (Node) scene;
            List<Geometry> geoms = new ArrayList<Geometry>();
            for (Spatial child : n.getChildren()){
                geoms.addAll(getGeometries(child));
            }
            return geoms;
        }else{
            throw new UnsupportedOperationException("Unsupported scene element class");
        }
    }

    public Octree(Spatial scene){
        scene.updateGeometricState();

        List<Geometry> geomsList = getGeometries(scene);
        geoms = new Geometry[geomsList.size()];
        geomsList.toArray(geoms);
        // generate bound box for all geom
        bbox = new BoundingBox();
        for (Geometry geom : geoms){
            BoundingVolume bv = geom.getWorldBound();
            bbox.mergeLocal(bv);
        }

        // set largest extent
        float extent = Math.max(bbox.getXExtent(), Math.max(bbox.getYExtent(), bbox.getZExtent()));
        bbox.setXExtent(extent);
        bbox.setYExtent(extent);
        bbox.setZExtent(extent);

        Triangle t = new Triangle();
        for (int g = 0; g < geoms.length; g++){
            Mesh m = geoms[g].getMesh();
            for (int i = 0; i < m.getTriangleCount(); i++){
                m.getTriangle(i, t);
                OCTTriangle ot = new OCTTriangle(t.get1(), t.get2(), t.get3(), i, g);
                allTris.add(ot);
                // convert triangle to world space
//                geom.getWorldTransform().transformVector(t.get1(), t.get1());
//                geom.getWorldTransform().transformVector(t.get2(), t.get2());
//                geom.getWorldTransform().transformVector(t.get3(), t.get3());
            }
        }
    }

    public void construct(int maxDepth, float maxVolume, int minTrisPerNode){
        root = new Octnode(bbox, allTris);
        root.subdivide(maxDepth, maxVolume, minTrisPerNode);
        root.collectTriangles(geoms);
    }

    public void createFastOctnodes(List<Geometry> globalGeomList){
        root.createFastOctnode(globalGeomList);
    }

    public BoundingBox getBound(){
        return bbox;
    }

    public FastOctnode getFastRoot(){
        return root.fastNode;
    }

    public void generateFastOctnodeLinks(){
        root.generateFastOctnodeLinks(null, null, 0);
    }
    
    public void generateRenderSet(Set<Geometry> renderSet, Camera cam){
        root.generateRenderSet(renderSet, cam);
    }

    public void renderBounds(RenderQueue rq, Matrix4f transform, WireBox box, Material mat){
        root.renderBounds(rq, transform, box, mat);
    }

    public void intersect(Ray r, float farPlane, Geometry[] geoms, CollisionResults results){
        boundResults.clear();
        bbox.collideWith(r, boundResults);
        if (boundResults.size() > 0){
            float tMin = boundResults.getClosestCollision().getDistance();
            float tMax = boundResults.getFarthestCollision().getDistance();

            tMin = Math.max(tMin, 0);
            tMax = Math.min(tMax, farPlane);

            root.intersectWhere(r, geoms, tMin, tMax, results);
        }
    }
}
