

package clockworktest.input;

import com.clockwork.app.SimpleApplication;
import com.clockwork.input.KeyInput;
import com.clockwork.input.MouseInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.AnalogListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.input.controls.MouseAxisTrigger;

public class TestControls extends SimpleApplication {
    
    private ActionListener actionListener = new ActionListener(){
        public void onAction(String name, boolean pressed, float tpf){
            System.out.println(name + " = " + pressed);
        }
    };
    public AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            System.out.println(name + " = " + value);
        }
    };

    public static void main(String[] args){
        TestControls app = new TestControls();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Test multiple inputs per mapping
        inputManager.addMapping("My Action",
                new KeyTrigger(KeyInput.KEY_SPACE),
                new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));

        // Test multiple listeners per mapping
        inputManager.addListener(actionListener, "My Action");
        inputManager.addListener(analogListener, "My Action");
    }

}
