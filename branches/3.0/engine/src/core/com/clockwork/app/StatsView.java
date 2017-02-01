
package com.clockwork.app;

import com.clockwork.asset.AssetManager;
import com.clockwork.font.BitmapFont;
import com.clockwork.font.BitmapText;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.Statistics;
import com.clockwork.renderer.ViewPort;
import com.clockwork.renderer.queue.RenderQueue.Bucket;
import com.clockwork.scene.Node;
import com.clockwork.scene.Spatial;
import com.clockwork.scene.control.Control;

/**
 * The <code>StatsView</code> provides a heads-up display (HUD) of various
 * statistics of rendering. The data is retrieved every frame from a
 * {@link com.clockwork.renderer.Statistics} and then displayed on screen.<br/>
 * <br/>
 * Usage:<br/>
 * To use the stats view, you need to retrieve the
 * {@link com.clockwork.renderer.Statistics} from the
 * {@link com.clockwork.renderer.Renderer} used by the application. Then, attach
 * the <code>StatsView</code> to the scene graph.<br/>
 * <code><br/>
 * Statistics stats = renderer.getStatistics();<br/>
 * StatsView statsView = new StatsView("MyStats", assetManager, stats);<br/>
 * rootNode.attachChild(statsView);<br/>
 * </code>
 */
public class StatsView extends Node implements Control {

    private BitmapText[] labels;
    private Statistics statistics;

    private String[] statLabels;
    private int[] statData;

    private boolean enabled = true;
    
    private final StringBuilder stringBuilder = new StringBuilder();

    public StatsView(String name, AssetManager manager, Statistics stats){
        super(name);

        setQueueBucket(Bucket.Gui);
        setCullHint(CullHint.Never);

        statistics = stats;
        statistics.setEnabled(enabled);

        statLabels = statistics.getLabels();
        statData = new int[statLabels.length];
        labels = new BitmapText[statLabels.length];

        BitmapFont font = manager.loadFont("Interface/Fonts/Console.fnt");
        for (int i = 0; i < labels.length; i++){
            labels[i] = new BitmapText(font);
            labels[i].setLocalTranslation(0, labels[i].getLineHeight() * (i+1), 0);
            attachChild(labels[i]);
        }

        addControl(this);
    }

    public float getHeight() {
        return labels[0].getLineHeight() * statLabels.length;
    }
    
    public void update(float tpf) {
    
        if (!isEnabled()) 
            return;
            
        statistics.getData(statData);
        for (int i = 0; i < labels.length; i++) {
            stringBuilder.setLength(0);
            stringBuilder.append(statLabels[i]).append(" = ").append(statData[i]);
            labels[i].setText(stringBuilder);
        }
        
        // Moved to ResetStatsState to make sure it is
        // done even if there is no StatsView or the StatsView
        // is disable.
        //statistics.clearFrame();
    }

    public Control cloneForSpatial(Spatial spatial) {
        return (Control) spatial;
    }

    public void setSpatial(Spatial spatial) {
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        statistics.setEnabled(enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void render(RenderManager rm, ViewPort vp) {
    }

}
