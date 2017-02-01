

package clockworktools.converters.model.strip;

import java.util.ArrayList;


class StripInfoVec extends ArrayList<StripInfo> {


    private static final long serialVersionUID = 1L;

	public StripInfoVec() {
        super();
    }
    
    public StripInfo at(int index) {
        return get(index);
    }
    
}
