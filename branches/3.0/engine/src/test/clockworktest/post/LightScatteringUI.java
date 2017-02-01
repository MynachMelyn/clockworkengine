

package clockworktest.post;

import com.clockwork.input.InputManager;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.AnalogListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.post.filters.LightScatteringFilter;

/**
 *
 */
public class LightScatteringUI {
    private LightScatteringFilter filter;
    public LightScatteringUI(InputManager inputManager, LightScatteringFilter proc) {
        filter=proc;


        System.out.println("----------------- LightScattering UI Debugger --------------------");
        System.out.println("-- Sample number : press Y to increase, H to decrease");
        System.out.println("-- blur start : press U to increase, J to decrease");
        System.out.println("-- blur width : press I to increase, K to decrease");
        System.out.println("-- Light density : press O to increase, P to decrease");
//        System.out.println("-- Toggle AO on/off : press space bar");
//        System.out.println("-- Use only AO : press Num pad 0");
//        System.out.println("-- Output config declaration : press P");
        System.out.println("-------------------------------------------------------");
    
        inputManager.addMapping("sampleUp", new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addMapping("sampleDown", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("blurStartUp", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("blurStartDown", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("blurWidthUp", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("blurWidthDown", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("lightDensityUp", new KeyTrigger(KeyInput.KEY_O));
        inputManager.addMapping("lightDensityDown", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("outputConfig", new KeyTrigger(KeyInput.KEY_P));
//        inputManager.addMapping("toggleUseAO", new KeyTrigger(KeyInput.KEY_SPACE));
//        inputManager.addMapping("toggleUseOnlyAo", new KeyTrigger(KeyInput.KEY_NUMPAD0));
        
        ActionListener acl = new ActionListener() {

            public void onAction(String name, boolean keyPressed, float tpf) {

                if (name.equals("sampleUp")) {
                    filter.setNbSamples(filter.getNbSamples()+1);
                    System.out.println("Nb Samples : "+filter.getNbSamples());
                }
                if (name.equals("sampleDown")) {
                   filter.setNbSamples(filter.getNbSamples()-1);
                   System.out.println("Nb Samples : "+filter.getNbSamples());
                }
                if (name.equals("outputConfig") && keyPressed) {
                   System.out.println("lightScatteringFilter.setNbSamples("+filter.getNbSamples()+");");
                   System.out.println("lightScatteringFilter.setBlurStart("+filter.getBlurStart()+"f);");
                   System.out.println("lightScatteringFilter.setBlurWidth("+filter.getBlurWidth()+"f);");
                   System.out.println("lightScatteringFilter.setLightDensity("+filter.getLightDensity()+"f);");
                }
               

            }
        };

         AnalogListener anl = new AnalogListener() {

            public void onAnalog(String name, float value, float tpf) {
               
                if (name.equals("blurStartUp")) {
                    filter.setBlurStart(filter.getBlurStart()+0.001f);
                    System.out.println("Blur start : "+filter.getBlurStart());
                }
                if (name.equals("blurStartDown")) {
                    filter.setBlurStart(filter.getBlurStart()-0.001f);
                    System.out.println("Blur start : "+filter.getBlurStart());
                }
                 if (name.equals("blurWidthUp")) {
                    filter.setBlurWidth(filter.getBlurWidth()+0.001f);
                    System.out.println("Blur Width : "+filter.getBlurWidth());
                }
                if (name.equals("blurWidthDown")) {
                    filter.setBlurWidth(filter.getBlurWidth()-0.001f);
                    System.out.println("Blur Width : "+filter.getBlurWidth());
                }
                if (name.equals("lightDensityUp")) {
                    filter.setLightDensity(filter.getLightDensity()+0.001f);
                    System.out.println("light Density : "+filter.getLightDensity());
                }
                if (name.equals("lightDensityDown")) {
                     filter.setLightDensity(filter.getLightDensity()-0.001f);
                    System.out.println("light Density : "+filter.getLightDensity());
                }

            }
        };
        inputManager.addListener(acl,"sampleUp","sampleDown","outputConfig");

        inputManager.addListener(anl, "blurStartUp","blurStartDown","blurWidthUp", "blurWidthDown","lightDensityUp", "lightDensityDown");
     
    }
    
    

}
