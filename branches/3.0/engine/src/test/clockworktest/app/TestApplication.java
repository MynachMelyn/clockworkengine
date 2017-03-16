

package clockworktest.app;

import com.clockwork.app.Application;
import com.clockwork.system.AppSettings;
import com.clockwork.system.CWContext.Type;

/**
 * Test Application functionality, such as create, restart, destroy, etc.
 */
public class TestApplication {

    public static void main(String[] args) throws InterruptedException{
        System.out.println("Creating application..");
        Application app = new Application();
        System.out.println("Starting application in LWJGL mode..");
        app.start();
        System.out.println("Waiting 5 seconds");
        Thread.sleep(5000);
        System.out.println("Closing application..");
        app.stop();

        Thread.sleep(2000);
        System.out.println("Starting in fullscreen mode");
        app = new Application();
        AppSettings settings = new AppSettings(true);
        settings.setFullscreen(true);
        settings.setResolution(-1,-1); // current width/height
        app.setSettings(settings);
        app.start();
        Thread.sleep(5000);
        app.stop();

        Thread.sleep(2000);
        System.out.println("Creating offscreen buffer application");
        app = new Application();
        app.start(Type.OffscreenSurface);
        Thread.sleep(3000);
        System.out.println("Destroying offscreen buffer");
        app.stop();
    }

}
