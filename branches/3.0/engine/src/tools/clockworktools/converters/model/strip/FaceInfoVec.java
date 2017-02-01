

package clockworktools.converters.model.strip;

import java.util.ArrayList;

class FaceInfoVec extends ArrayList<FaceInfo> {


    private static final long serialVersionUID = 1L;

	public FaceInfoVec() {
        super();
    }
    
    public FaceInfo at(int index) {
        return get(index);
    }

    public void reserve(int i) {
        super.ensureCapacity(i);
    }

}
