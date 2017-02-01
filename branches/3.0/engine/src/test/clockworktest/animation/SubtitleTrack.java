
package clockworktest.animation;

import com.clockwork.cinematic.events.GuiTrack;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.render.TextRenderer;

/**
 *
 */
public class SubtitleTrack extends GuiTrack{
    private String text="";

    public SubtitleTrack(Nifty nifty, String screen,float initialDuration, String text) {
        super(nifty, screen, initialDuration);
        this.text=text;
    }

    @Override
    public void onPlay() {
        super.onPlay();
        nifty.getScreen(screen).findElementByName("text").getRenderer(TextRenderer.class).setText(text);
    }








}
