

package clockworktools.converters.model.strip;

/**
 *  
 */
class EdgeInfo {

    FaceInfo m_face0, m_face1;
    int m_v0, m_v1;
    EdgeInfo m_nextV0, m_nextV1;

    public EdgeInfo(int v0, int v1) {
        m_v0 = v0;
        m_v1 = v1;
        m_face0 = null;
        m_face1 = null;
        m_nextV0 = null;
        m_nextV1 = null;

    }
}
