

package clockworktest.niftygui;

import com.clockwork.app.SimpleApplication;
import com.clockwork.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;

public class TestNiftyExamples extends SimpleApplication {

    private Nifty nifty;

    public static void main(String[] args){
        TestNiftyExamples app = new TestNiftyExamples();
        app.setPauseOnLostFocus(false);
        app.start();
    }

    public void simpleInitApp() {
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
                                                          inputManager,
                                                          audioRenderer,
                                                          guiViewPort);
        nifty = niftyDisplay.getNifty();

        nifty.fromXml("all/intro.xml", "start");

        // attach the nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);

        // disable the fly cam
        flyCam.setEnabled(false);
    }

}
