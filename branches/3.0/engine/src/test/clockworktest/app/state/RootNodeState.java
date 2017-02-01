

package clockworktest.app.state;

import com.clockwork.app.state.AbstractAppState;
import com.clockwork.scene.Node;

public class RootNodeState extends AbstractAppState {

    private Node rootNode = new Node("Root Node");

    public Node getRootNode(){
        return rootNode;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
    }

}
