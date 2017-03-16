

package clockworktest.app.state;

import com.clockwork.app.Application;
import com.clockwork.niftygui.NiftyCWDisplay;
import com.clockwork.scene.Spatial;
import com.clockwork.system.AppSettings;
import com.clockwork.system.CWContext;

public class TestAppStates extends Application {

    public static void main(String[] args){
        TestAppStates app = new TestAppStates();
        app.start();
    }

    @Override
    public void start(CWContext.Type contextType){
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1024, 768);
        setSettings(settings);
        
        super.start(contextType);
    }

    @Override
    public void initialize(){
        super.initialize();

        System.out.println("Initialize");

        RootNodeState state = new RootNodeState();
        viewPort.attachScene(state.getRootNode());
        stateManager.attach(state);

        Spatial model = assetManager.loadModel("Models/Teapot/Teapot.obj");
        model.scale(3);
        model.setMaterial(assetManager.loadMaterial("Interface/Logo/Logo.j3m"));
        state.getRootNode().attachChild(model);

        NiftyCWDisplay niftyDisplay = new NiftyCWDisplay(assetManager,
                                                           inputManager,
                                                           audioRenderer,
                                                           guiViewPort);
        niftyDisplay.getNifty().fromXml("Interface/Nifty/HelloCW.xml", "start");
        guiViewPort.addProcessor(niftyDisplay);
    }

    @Override
    public void update(){
        super.update();

        // do some animation
        float tpf = timer.getTimePerFrame();

        stateManager.update(tpf);
        stateManager.render(renderManager);

        // render the viewports
        renderManager.render(tpf, context.isRenderable());
    }

    @Override
    public void destroy(){
        super.destroy();

        System.out.println("Destroy");
    }
}
