

package clockworktools.converters.model.strip;


class FaceInfo {

    int   m_v0, m_v1, m_v2;
    int   m_stripId;      // real strip Id
    int   m_testStripId;  // strip Id in an experiment
    int   m_experimentId; // in what experiment was it given an experiment Id?
    
    public FaceInfo(int v0, int v1, int v2){
        m_v0 = v0; m_v1 = v1; m_v2 = v2;
        m_stripId      = -1;
        m_testStripId  = -1;
        m_experimentId = -1;
    }
    
    public void set(FaceInfo o) {
        m_v0 = o.m_v0;
        m_v1 = o.m_v1;
        m_v2 = o.m_v2;
        
        m_stripId = o.m_stripId;
        m_testStripId = o.m_testStripId;
        m_experimentId = o.m_experimentId;
    }
}
