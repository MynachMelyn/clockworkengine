
package com.clockwork.system;

import com.clockwork.app.SettingsDialog;
import com.clockwork.app.SettingsDialog.SelectionListener;
import com.clockwork.asset.AssetManager;
import com.clockwork.asset.AssetNotFoundException;
import com.clockwork.asset.DesktopAssetManager;
import com.clockwork.audio.AudioRenderer;
import com.clockwork.system.CWContext.Type;
import com.clockwork.texture.Image;
import com.clockwork.texture.image.DefaultImageRaster;
import com.clockwork.texture.image.ImageRaster;
import com.clockwork.util.Screenshots;
import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 */
public class CWDesktopSystem extends CWSystemDelegate {

    @Override
    public AssetManager newAssetManager(URL configFile) {
        return new DesktopAssetManager(configFile);
    }
    
    @Override
    public void writeImageFile(OutputStream outStream, String format, ByteBuffer imageData, int width, int height) throws IOException {
        BufferedImage awtImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Screenshots.convertScreenShot(imageData, awtImage);
        ImageIO.write(awtImage, format, outStream);
    }

    @Override
    public ImageRaster createImageRaster(Image image, int slice) {
        assert image.getEfficentData() == null;
        return new DefaultImageRaster(image, slice);
    }

    @Override
    public AssetManager newAssetManager() {
        return new DesktopAssetManager(null);
    }

    @Override
    public void showErrorDialog(String message) {
        final String msg = message;
        final String title = "Error in application";
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    @Override
    public boolean showSettingsDialog(AppSettings sourceSettings, final boolean loadFromRegistry) {
        if (SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Cannot run from EDT");
        }

        final AppSettings settings = new AppSettings(false);
        settings.copyFrom(sourceSettings);
        String iconPath = sourceSettings.getSettingsDialogImage();        
        if(iconPath == null){
            iconPath = "";
        }
        final URL iconUrl = CWSystem.class.getResource(iconPath.startsWith("/") ? iconPath : "/" + iconPath);
        if (iconUrl == null) {
            throw new AssetNotFoundException(sourceSettings.getSettingsDialogImage());
        }

        final AtomicBoolean done = new AtomicBoolean();
        final AtomicInteger result = new AtomicInteger();
        final Object lock = new Object();

        final SelectionListener selectionListener = new SelectionListener() {

            public void onSelection(int selection) {
                synchronized (lock) {
                    done.set(true);
                    result.set(selection);
                    lock.notifyAll();
                }
            }
        };
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                synchronized (lock) {
                    SettingsDialog dialog = new SettingsDialog(settings, iconUrl, loadFromRegistry);
                    dialog.setSelectionListener(selectionListener);
                    dialog.showDialog();
                }
            }
        });

        synchronized (lock) {
            while (!done.get()) {
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                }
            }
        }

        sourceSettings.copyFrom(settings);

        return result.get() == SettingsDialog.APPROVE_SELECTION;
    }

    private CWContext newContextLwjgl(AppSettings settings, CWContext.Type type) {
        try {
            Class<? extends CWContext> ctxClazz = null;
            switch (type) {
                case Canvas:
                    ctxClazz = (Class<? extends CWContext>) Class.forName("com.clockwork.system.lwjgl.LwjglCanvas");
                    break;
                case Display:
                    ctxClazz = (Class<? extends CWContext>) Class.forName("com.clockwork.system.lwjgl.LwjglDisplay");
                    break;
                case OffscreenSurface:
                    ctxClazz = (Class<? extends CWContext>) Class.forName("com.clockwork.system.lwjgl.LwjglOffscreenBuffer");
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported context type " + type);
            }

            return ctxClazz.newInstance();
        } catch (InstantiationException ex) {
            logger.log(Level.SEVERE, "Failed to create context", ex);
        } catch (IllegalAccessException ex) {
            logger.log(Level.SEVERE, "Failed to create context", ex);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "CRITICAL ERROR: Context class is missing!\n"
                    + "Make sure CW_lwjgl-ogl is on the classpath.", ex);
        }

        return null;
    }

    private CWContext newContextJogl(AppSettings settings, CWContext.Type type) {
        try {
            Class<? extends CWContext> ctxClazz = null;
            switch (type) {
                case Display:
                    ctxClazz = (Class<? extends CWContext>) Class.forName("com.clockwork.system.jogl.JoglNewtDisplay");
                    break;
                case Canvas:
                    ctxClazz = (Class<? extends CWContext>) Class.forName("com.clockwork.system.jogl.JoglNewtCanvas");
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported context type " + type);
            }

            return ctxClazz.newInstance();
        } catch (InstantiationException ex) {
            logger.log(Level.SEVERE, "Failed to create context", ex);
        } catch (IllegalAccessException ex) {
            logger.log(Level.SEVERE, "Failed to create context", ex);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "CRITICAL ERROR: Context class is missing!\n"
                    + "Make sure CW_jogl is on the classpath.", ex);
        }

        return null;
    }

    private CWContext newContextCustom(AppSettings settings, CWContext.Type type) {
        try {
            String className = settings.getRenderer().substring("CUSTOM".length());

            Class<? extends CWContext> ctxClazz = null;
            ctxClazz = (Class<? extends CWContext>) Class.forName(className);
            return ctxClazz.newInstance();
        } catch (InstantiationException ex) {
            logger.log(Level.SEVERE, "Failed to create context", ex);
        } catch (IllegalAccessException ex) {
            logger.log(Level.SEVERE, "Failed to create context", ex);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "CRITICAL ERROR: Context class is missing!", ex);
        }

        return null;
    }

    @Override
    public CWContext newContext(AppSettings settings, Type contextType) {
        initialize(settings);
        CWContext ctx;
        if (settings.getRenderer() == null
                || settings.getRenderer().equals("NULL")
                || contextType == CWContext.Type.Headless) {
            ctx = new NullContext();
            ctx.setSettings(settings);
        } else if (settings.getRenderer().startsWith("LWJGL")) {
            ctx = newContextLwjgl(settings, contextType);
            ctx.setSettings(settings);
        } else if (settings.getRenderer().startsWith("JOGL")) {
            ctx = newContextJogl(settings, contextType);
            ctx.setSettings(settings);
        } else if (settings.getRenderer().startsWith("CUSTOM")) {
            ctx = newContextCustom(settings, contextType);
            ctx.setSettings(settings);
        } else {
            throw new UnsupportedOperationException(
                    "Unrecognizable renderer specified: "
                    + settings.getRenderer());
        }
        return ctx;
    }

    @Override
    public AudioRenderer newAudioRenderer(AppSettings settings) {
        initialize(settings);
        Class<? extends AudioRenderer> clazz = null;
        try {
            if (settings.getAudioRenderer().startsWith("LWJGL")) {
                clazz = (Class<? extends AudioRenderer>) Class.forName("com.clockwork.audio.lwjgl.LwjglAudioRenderer");
            } else if (settings.getAudioRenderer().startsWith("JOAL")) {
                clazz = (Class<? extends AudioRenderer>) Class.forName("com.clockwork.audio.joal.JoalAudioRenderer");
            } else {
                throw new UnsupportedOperationException(
                        "Unrecognizable audio renderer specified: "
                        + settings.getAudioRenderer());
            }

            AudioRenderer ar = clazz.newInstance();
            return ar;
        } catch (InstantiationException ex) {
            logger.log(Level.SEVERE, "Failed to create context", ex);
        } catch (IllegalAccessException ex) {
            logger.log(Level.SEVERE, "Failed to create context", ex);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "CRITICAL ERROR: Audio implementation class is missing!\n"
                    + "Make sure CW_lwjgl-oal or jm3_joal is on the classpath.", ex);
        }
        return null;
    }

    @Override
    public void initialize(AppSettings settings) {
        if (initialized) {
            return;
        }

        initialized = true;
        try {
            if (!lowPermissions) {
                // can only modify logging settings
                // if permissions are available
//                CWFormatter formatter = new CWFormatter();
//                Handler fileHandler = new FileHandler("CW.log");
//                fileHandler.setFormatter(formatter);
//                Logger.getLogger("").addHandler(fileHandler);
//                Handler consoleHandler = new ConsoleHandler();
//                consoleHandler.setFormatter(formatter);
//                Logger.getLogger("").removeHandler(Logger.getLogger("").getHandlers()[0]);
//                Logger.getLogger("").addHandler(consoleHandler);
            }
//        } catch (IOException ex){
//            logger.log(Level.SEVERE, "I/O Error while creating log file", ex);
        } catch (SecurityException ex) {
            logger.log(Level.SEVERE, "Security error in creating log file", ex);
        }
        logger.log(Level.INFO, "Running on {0}", getFullName());

        if (!lowPermissions) {
            try {
                Natives.extractNativeLibs(getPlatform(), settings);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error while copying native libraries", ex);
            }
        }
    }
}
