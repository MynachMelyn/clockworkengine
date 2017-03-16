
package com.clockwork.app;

import com.clockwork.system.AppSettings;
import com.clockwork.system.CWCanvasContext;
import com.clockwork.system.CWSystem;
import java.applet.Applet;
import java.awt.Canvas;
import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 */
public class AppletHarness extends Applet {

    public static final HashMap<Application, Applet> appToApplet
                         = new HashMap<Application, Applet>();

    protected CWCanvasContext context;
    protected Canvas canvas;
    protected Application app;

    protected String appClass;
    protected URL appCfg = null;
    protected URL assetCfg = null;

    public static Applet getApplet(Application app){
        return appToApplet.get(app);
    }

    private void createCanvas(){
        AppSettings settings = new AppSettings(true);

        // load app cfg
        if (appCfg != null){
            InputStream in = null;
            try {
                in = appCfg.openStream();
                settings.load(in);
                in.close();
            } catch (IOException ex){
                // Called before application has been created ....
                // Display error message through AWT
                JOptionPane.showMessageDialog(this, "An error has occured while "
                                                  + "loading applet configuration"
                                                  + ex.getMessage(),
                                              "CW Applet",
                                              JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                if (in != null)
                    try {
                    in.close();
                } catch (IOException ex) {
                }
            }
        }

        if (assetCfg != null){
            settings.putString("AssetConfigURL", assetCfg.toString());
        }

        settings.setWidth(getWidth());
        settings.setHeight(getHeight());

        CWSystem.setLowPermissions(true);

        try{
            Class<? extends Application> clazz = (Class<? extends Application>) Class.forName(appClass);
            app = clazz.newInstance();
        }catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }catch (InstantiationException ex){
            ex.printStackTrace();
        }catch (IllegalAccessException ex){
            ex.printStackTrace();
        }

        appToApplet.put(app, this);
        app.setSettings(settings);
        app.createCanvas();

        context = (CWCanvasContext) app.getContext();
        canvas = context.getCanvas();
        canvas.setSize(getWidth(), getHeight());

        add(canvas);
        app.startCanvas();
    }

    @Override
    public final void update(Graphics g) {
        canvas.setSize(getWidth(), getHeight());
    }

    @Override
    public void init(){
        appClass = getParameter("AppClass");
        if (appClass == null)
            throw new RuntimeException("The required parameter AppClass isn't specified!");

        try {
            appCfg = new URL(getParameter("AppSettingsURL"));
        } catch (MalformedURLException ex) {
            System.out.println(ex.getMessage());
            appCfg = null;
        }

        try {
            assetCfg = new URL(getParameter("AssetConfigURL"));
        } catch (MalformedURLException ex){
            System.out.println(ex.getMessage());
            assetCfg = getClass().getResource("/com/clockwork/asset/Desktop.cfg");
        }

        createCanvas();
        System.out.println("applet:init");
    }

    @Override
    public void start(){
        context.setAutoFlushFrames(true);
        System.out.println("applet:start");
    }

    @Override
    public void stop(){
        context.setAutoFlushFrames(false);
        System.out.println("applet:stop");
    }

    @Override
    public void destroy(){
        System.out.println("applet:destroyStart");
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                removeAll();
                System.out.println("applet:destroyRemoved");
            }
        });
        app.stop(true);
        System.out.println("applet:destroyDone");

        appToApplet.remove(app);
    }

}
