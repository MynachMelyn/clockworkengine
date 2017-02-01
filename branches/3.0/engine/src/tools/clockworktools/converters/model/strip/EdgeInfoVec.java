

package clockworktools.converters.model.strip;

import java.util.ArrayList;

class EdgeInfoVec extends ArrayList<EdgeInfo> {

    private static final long serialVersionUID = 1L;

	public EdgeInfoVec() {
        super();
    }
    
    public EdgeInfo at(int index) {
        return get(index);
    }


}
