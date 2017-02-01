

package clockworktools.converters.model.strip;

class StripStartInfo {


    FaceInfo    m_startFace;
    EdgeInfo    m_startEdge;
    boolean          m_toV1;   
      

    public StripStartInfo(FaceInfo startFace, EdgeInfo startEdge, boolean toV1){
        m_startFace    = startFace;
        m_startEdge    = startEdge;
        m_toV1         = toV1;
    }

}
