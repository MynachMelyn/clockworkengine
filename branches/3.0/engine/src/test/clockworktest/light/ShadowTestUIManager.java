/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clockworktest.light;

import com.clockwork.asset.AssetManager;
import com.clockwork.font.BitmapFont;
import com.clockwork.font.BitmapText;
import com.clockwork.input.InputManager;
import com.clockwork.input.KeyInput;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.renderer.Camera;
import com.clockwork.renderer.ViewPort;
import com.clockwork.scene.Node;
import com.clockwork.shadow.AbstractShadowFilter;
import com.clockwork.shadow.AbstractShadowRenderer;
import com.clockwork.shadow.CompareMode;
import com.clockwork.shadow.EdgeFilteringMode;

/**
 *
 * 
 */
public class ShadowTestUIManager implements ActionListener {

    private BitmapText shadowTypeText;
    private BitmapText shadowCompareText;
    private BitmapText shadowFilterText;
    private BitmapText shadowIntensityText;
    private final static String TYPE_TEXT = "(Space) Shadow type : ";
    private final static String COMPARE_TEXT = "(enter) Shadow compare ";
    private final static String FILTERING_TEXT = "(f) Edge filtering : ";
    private final static String INTENSITY_TEXT = "(t:up, g:down) Shadow intensity : ";
    private boolean hardwareShadows = true;
    private AbstractShadowRenderer plsr;
    private AbstractShadowFilter plsf;
    private ViewPort viewPort;
    private int filteringIndex = 0;
    private int renderModeIndex = 0;
    

    public ShadowTestUIManager(AssetManager assetManager,AbstractShadowRenderer plsr, AbstractShadowFilter plsf, 
            Node guiNode, InputManager inputManager, ViewPort viewPort) {
        this.plsr = plsr;
        this.plsf = plsf;
        this.viewPort = viewPort;
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        shadowTypeText = createText(guiFont);
        shadowCompareText = createText(guiFont);
        shadowFilterText = createText(guiFont);
        shadowIntensityText = createText(guiFont);

        shadowTypeText.setText(TYPE_TEXT + "Processor");
        shadowCompareText.setText(COMPARE_TEXT + (hardwareShadows ? "Hardware" : "Software"));
        shadowFilterText.setText(FILTERING_TEXT + plsr.getEdgeFilteringMode().toString());
        shadowIntensityText.setText(INTENSITY_TEXT + plsr.getShadowIntensity());

        shadowTypeText.setLocalTranslation(10, viewPort.getCamera().getHeight() - 20, 0);
        shadowCompareText.setLocalTranslation(10, viewPort.getCamera().getHeight() - 40, 0);
        shadowFilterText.setLocalTranslation(10, viewPort.getCamera().getHeight() - 60, 0);
        shadowIntensityText.setLocalTranslation(10, viewPort.getCamera().getHeight() - 80, 0);

        guiNode.attachChild(shadowTypeText);
        guiNode.attachChild(shadowCompareText);
        guiNode.attachChild(shadowFilterText);
        guiNode.attachChild(shadowIntensityText);

        inputManager.addMapping("toggle", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("changeFiltering", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("ShadowUp", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("ShadowDown", new KeyTrigger(KeyInput.KEY_G));
        inputManager.addMapping("ThicknessUp", new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addMapping("ThicknessDown", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("toggleHW", new KeyTrigger(KeyInput.KEY_RETURN));


        inputManager.addListener(this, "toggleHW", "toggle", "ShadowUp", "ShadowDown", "ThicknessUp", "ThicknessDown", "changeFiltering");

    }


    public void onAction(String name, boolean keyPressed, float tpf) {
        if (name.equals("toggle") && keyPressed) {
            renderModeIndex += 1;
            renderModeIndex %= 3;

            switch (renderModeIndex) {
                case 0:
                    viewPort.addProcessor(plsr);
                    shadowTypeText.setText(TYPE_TEXT + "Processor");
                    break;
                case 1:
                    viewPort.removeProcessor(plsr);
                    plsf.setEnabled(true);
                    shadowTypeText.setText(TYPE_TEXT + "Filter");
                    break;
                case 2:
                    plsf.setEnabled(false);
                    shadowTypeText.setText(TYPE_TEXT + "None");
                    break;
            }



        } else if (name.equals("toggleHW") && keyPressed) {
            hardwareShadows = !hardwareShadows;
            plsr.setShadowCompareMode(hardwareShadows ? CompareMode.Hardware : CompareMode.Software);
            plsf.setShadowCompareMode(hardwareShadows ? CompareMode.Hardware : CompareMode.Software);

            shadowCompareText.setText(COMPARE_TEXT + (hardwareShadows ? "Hardware" : "Software"));
        }


        if (name.equals("changeFiltering") && keyPressed) {
            filteringIndex = plsr.getEdgeFilteringMode().ordinal();
            filteringIndex = (filteringIndex + 1) % EdgeFilteringMode.values().length;
            EdgeFilteringMode m = EdgeFilteringMode.values()[filteringIndex];
            plsr.setEdgeFilteringMode(m);
            plsf.setEdgeFilteringMode(m);
            shadowFilterText.setText(FILTERING_TEXT + m.toString());
        }

        if (name.equals("ShadowUp") && keyPressed) {
            plsr.setShadowIntensity(plsr.getShadowIntensity() + 0.1f);
            plsf.setShadowIntensity(plsf.getShadowIntensity() + 0.1f);

            shadowIntensityText.setText(INTENSITY_TEXT + plsr.getShadowIntensity());
        }
        if (name.equals("ShadowDown") && keyPressed) {
            plsr.setShadowIntensity(plsr.getShadowIntensity() - 0.1f);
            plsf.setShadowIntensity(plsf.getShadowIntensity() - 0.1f);
            shadowIntensityText.setText(INTENSITY_TEXT + plsr.getShadowIntensity());
        }
        if (name.equals("ThicknessUp") && keyPressed) {
            plsr.setEdgesThickness(plsr.getEdgesThickness() + 1);
            plsf.setEdgesThickness(plsf.getEdgesThickness() + 1);
            System.out.println("Shadow thickness : " + plsr.getEdgesThickness());
        }
        if (name.equals("ThicknessDown") && keyPressed) {
            plsr.setEdgesThickness(plsr.getEdgesThickness() - 1);
            plsf.setEdgesThickness(plsf.getEdgesThickness() - 1);
            System.out.println("Shadow thickness : " + plsr.getEdgesThickness());
        }

    }

    private BitmapText createText(BitmapFont guiFont) {
        BitmapText t = new BitmapText(guiFont, false);
        t.setSize(guiFont.getCharSet().getRenderedSize() * 0.75f);
        return t;
    }
}
