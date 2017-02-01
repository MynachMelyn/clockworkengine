
package clockworktest.post;

import com.clockwork.input.InputManager;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.AnalogListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.post.filters.BloomFilter;

/**
 *
 */
public class BloomUI {

    public BloomUI(InputManager inputManager, final BloomFilter filter) {

        System.out.println("----------------- Bloom UI Debugger --------------------");
        System.out.println("-- blur Scale : press Y to increase, H to decrease");
        System.out.println("-- exposure Power : press U to increase, J to decrease");
        System.out.println("-- exposure CutOff : press I to increase, K to decrease");
        System.out.println("-- bloom Intensity : press O to increase, P to decrease");
        System.out.println("-------------------------------------------------------");

        inputManager.addMapping("blurScaleUp", new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addMapping("blurScaleDown", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("exposurePowerUp", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("exposurePowerDown", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("exposureCutOffUp", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("exposureCutOffDown", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("bloomIntensityUp", new KeyTrigger(KeyInput.KEY_O));
        inputManager.addMapping("bloomIntensityDown", new KeyTrigger(KeyInput.KEY_L));


        AnalogListener anl = new AnalogListener() {

            public void onAnalog(String name, float value, float tpf) {
                if (name.equals("blurScaleUp")) {
                    filter.setBlurScale(filter.getBlurScale() + 0.01f);
                    System.out.println("blurScale : " + filter.getBlurScale());
                }
                if (name.equals("blurScaleDown")) {
                    filter.setBlurScale(filter.getBlurScale() - 0.01f);
                    System.out.println("blurScale : " + filter.getBlurScale());
                }
                if (name.equals("exposurePowerUp")) {
                    filter.setExposurePower(filter.getExposurePower() + 0.01f);
                    System.out.println("exposurePower : " + filter.getExposurePower());
                }
                if (name.equals("exposurePowerDown")) {
                    filter.setExposurePower(filter.getExposurePower() - 0.01f);
                    System.out.println("exposurePower : " + filter.getExposurePower());
                }
                if (name.equals("exposureCutOffUp")) {
                    filter.setExposureCutOff(Math.min(1.0f, Math.max(0.0f, filter.getExposureCutOff() + 0.001f)));
                    System.out.println("exposure CutOff : " + filter.getExposureCutOff());
                }
                if (name.equals("exposureCutOffDown")) {
                    filter.setExposureCutOff(Math.min(1.0f, Math.max(0.0f, filter.getExposureCutOff() - 0.001f)));
                    System.out.println("exposure CutOff : " + filter.getExposureCutOff());
                }
                if (name.equals("bloomIntensityUp")) {
                    filter.setBloomIntensity(filter.getBloomIntensity() + 0.01f);
                    System.out.println("bloom Intensity : " + filter.getBloomIntensity());
                }
                if (name.equals("bloomIntensityDown")) {
                    filter.setBloomIntensity(filter.getBloomIntensity() - 0.01f);
                    System.out.println("bloom Intensity : " + filter.getBloomIntensity());
                }


            }
        };

        inputManager.addListener(anl, "blurScaleUp", "blurScaleDown", "exposurePowerUp", "exposurePowerDown",
                "exposureCutOffUp", "exposureCutOffDown", "bloomIntensityUp", "bloomIntensityDown");

    }
}
