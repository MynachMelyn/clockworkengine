
package com.clockwork.system;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * AppSettings provides a store of configuration
 * to be used by the application.
 * 
 * By default only the CWContext context} uses the configuration,
 * however the user may set and retrieve the settings as well.
 * The settings can be stored either in the Java preferences
 * (using #save(java.lang.String) } or
 * a .properties file (using #save(java.io.OutputStream) }.
 *
 */
public final class AppSettings extends HashMap<String, Object> {

    private static final AppSettings defaults = new AppSettings(false);

    /**
     * Use LWJGL as the display system and force using the OpenGL1.1 renderer.
     *
     * see AppSettings#setRenderer(java.lang.String)
     */
    public static final String LWJGL_OPENGL1 = "LWJGL-OPENGL1";

    /**
     * Use LWJGL as the display system and force using the OpenGL2.0 renderer.
     * 
     * If the underlying system does not support OpenGL2.0, then the context
     * initialization will throw an exception.
     *
     * see AppSettings#setRenderer(java.lang.String)
     */
    public static final String LWJGL_OPENGL2 = "LWJGL-OpenGL2";

    /**
     * Use LWJGL as the display system and force using the core OpenGL3.3 renderer.
     * 
     * If the underlying system does not support OpenGL3.2, then the context
     * initialization will throw an exception.
     * Does not have any shaders that support OpenGL3.2; this
     * option is not really useful.
     * 
     * Note: OpenGL 3.2 is used to give 3.x support to Mac users.
     *
     * see AppSettings#setRenderer(java.lang.String)
     */
    public static final String LWJGL_OPENGL3 = "LWJGL-OpenGL3";

    /**
     * Use LWJGL as the display system and allow the context
     * to choose an appropriate renderer based on system capabilities.
     * 
     * If the GPU supports OpenGL2 or later, then the OpenGL2.0 renderer will
     * be used, otherwise, the OpenGL1.1 renderer is used.
     *
     * see AppSettings#setRenderer(java.lang.String)
     */
    public static final String LWJGL_OPENGL_ANY = "LWJGL-OpenGL-Any";

    /**
     * Use the LWJGL OpenAL based renderer for audio capabilities.
     *
     * see AppSettings#setAudioRenderer(java.lang.String)
     */
    public static final String LWJGL_OPENAL = "LWJGL";

    /**
     * Use the Android MediaPlayer / SoundPool based renderer for Android audio capabilities.
     * 
     * NOTE: Supports Android 2.2+ platforms.  This is the current default for
     * Android platforms.
     *
     * see AppSettings#setAudioRenderer(java.lang.String)
     */
    public static final String ANDROID_MEDIAPLAYER = "MediaPlayer";

    /**
     * Use the OpenAL Soft based renderer for Android audio capabilities.
     * 
     * NOTE: Only to be used on Android 2.3+ platforms due to using OpenSL.
     *
     * see AppSettings#setAudioRenderer(java.lang.String)
     */
    public static final String ANDROID_OPENAL_SOFT = "OpenAL_SOFT";

    static {
        defaults.put("Width", 640);
        defaults.put("Height", 480);
        defaults.put("BitsPerPixel", 24);
        defaults.put("Frequency", 60);
        defaults.put("DepthBits", 24);
        defaults.put("StencilBits", 0);
        defaults.put("Samples", 0);
        defaults.put("Fullscreen", false);
        defaults.put("Title", "Clockwork Engine");
        defaults.put("Renderer", LWJGL_OPENGL2);
        defaults.put("AudioRenderer", LWJGL_OPENAL);
        defaults.put("DisableJoysticks", true);
        defaults.put("UseInput", true);
        defaults.put("VSync", true);
        defaults.put("FrameRate", -1);
      //  defaults.put("SettingsDialogImage", "/com/clockwork/app/Monkey.png");
        defaults.put("MinHeight", 0);
        defaults.put("MinWidth", 0);
      //  defaults.put("Icons", null);
    }

    /**
     * Create a new instance of AppSettings.
     * 
     * If loadDefaults is true, then the default settings
     * will be set on the AppSettings.
     * Use false if you want to change some settings but you would like the
     * application to load settings from previous launches.
     *
     * @param loadDefaults If default settings are to be loaded.
     */
    public AppSettings(boolean loadDefaults) {
        if (loadDefaults) {
            putAll(defaults);
        }
    }

    /**
     * Copies all settings from other to this
     * AppSettings.
     * 
     * Any settings that are specified in other will overwrite settings
     * set on this AppSettings.
     *
     * @param other The AppSettings to copy the settings from
     */
    public void copyFrom(AppSettings other) {
        this.putAll(other);
    }

    /**
     * Same as #copyFrom(com.clockwork.system.AppSettings) }, except
     * doesn't overwrite settings that are already set.
     *
     * @param other  The AppSettings to merge the settings from
     */
    public void mergeFrom(AppSettings other) {
        for (String key : other.keySet()) {
            if (get(key) == null) {
                put(key, other.get(key));
            }
        }
    }

    /**
     * Loads the settings from the given properties input stream.
     *
     * @param in The InputStream to load from
     * @throws IOException If an IOException occurs
     *
     * see #save(java.io.OutputStream)
     */
    public void load(InputStream in) throws IOException {
        Properties props = new Properties();
        props.load(in);
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            if (val != null) {
                val = val.trim();
            }
            if (key.endsWith("(int)")) {
                key = key.substring(0, key.length() - 5);
                int iVal = Integer.parseInt(val);
                putInteger(key, iVal);
            } else if (key.endsWith("(string)")) {
                putString(key.substring(0, key.length() - 8), val);
            } else if (key.endsWith("(bool)")) {
                boolean bVal = Boolean.parseBoolean(val);
                putBoolean(key.substring(0, key.length() - 6), bVal);
            } else if (key.endsWith("(float)")) {
                float fVal = Float.parseFloat(val);
                putFloat(key.substring(0, key.length() - 7), fVal);
            } else {
                throw new IOException("Cannot parse key: " + key);
            }
        }
    }

    /**
     * Saves all settings to the given properties output stream.
     *
     * @param out The OutputStream to write to
     * @throws IOException If an IOException occurs
     *
     * see #load(java.io.InputStream)
     */
    public void save(OutputStream out) throws IOException {
        Properties props = new Properties();
        for (Map.Entry<String, Object> entry : entrySet()) {
            Object val = entry.getValue();
            String type;
            if (val instanceof Integer) {
                type = "(int)";
            } else if (val instanceof String) {
                type = "(string)";
            } else if (val instanceof Boolean) {
                type = "(bool)";
            } else if (val instanceof Float) {
                type = "(float)";
            } else {
                // See the note in the AppSettings.save(String)
                // method regarding object type settings.
                continue;
            }
            props.setProperty(entry.getKey() + type, val.toString());
        }
        props.store(out, "CW AppSettings");
    }

    /**
     * Loads settings previously saved in the Java preferences.
     *
     * @param preferencesKey The preferencesKey previously used to save the settings.
     * @throws BackingStoreException If an exception occurs with the preferences
     *
     * see #save(java.lang.String)
     */
    public void load(String preferencesKey) throws BackingStoreException {
        Preferences prefs = Preferences.userRoot().node(preferencesKey);
        String[] keys = prefs.keys();
        if (keys != null) {
            for (String key : keys) {
                if (key.charAt(1) == '_') {
                    // Try loading using new method
                    switch (key.charAt(0)) {
                        case 'I':
                            put(key.substring(2), prefs.getInt(key, (Integer) 0));
                            break;
                        case 'F':
                            put(key.substring(2), prefs.getFloat(key, (Float) 0f));
                            break;
                        case 'S':
                            put(key.substring(2), prefs.get(key, (String) null));
                            break;
                        case 'B':
                            put(key.substring(2), prefs.getBoolean(key, (Boolean) false));
                            break;
                        default:
                            throw new UnsupportedOperationException("Undefined setting type: " + key.charAt(0));
                    }
                } else {
                    // Use old method for compatibility with older preferences
                    // TODO: Remove when no longer neccessary
                    Object defaultValue = defaults.get(key);
                    if (defaultValue instanceof Integer) {
                        put(key, prefs.getInt(key, (Integer) defaultValue));
                    } else if (defaultValue instanceof String) {
                        put(key, prefs.get(key, (String) defaultValue));
                    } else if (defaultValue instanceof Boolean) {
                        put(key, prefs.getBoolean(key, (Boolean) defaultValue));
                    }
                }
            }
        }
    }

    /**
     * Saves settings into the Java preferences.
     * 
     * On the Windows operating system, the preferences are saved in the registry
     * at the following key:
     * HKEY_CURRENT_USER\Software\JavaSoft\Prefs\[preferencesKey]
     *
     * @param preferencesKey The preferences key to save at. Generally the
     * application's unique name.
     *
     * @throws BackingStoreException If an exception occurs with the preferences
     */
    public void save(String preferencesKey) throws BackingStoreException {
        Preferences prefs = Preferences.userRoot().node(preferencesKey);

        // Clear any previous settings set before saving, this will
        // purge any other parameters set in older versions of the app, so
        // that they don't leak onto the AppSettings of newer versions.
        prefs.clear();

        for (String key : keySet()) {
            Object val = get(key);
            if (val instanceof Integer) {
                prefs.putInt("I_" + key, (Integer) val);
            } else if (val instanceof Float) {
                prefs.putFloat("F_" + key, (Float) val);
            } else if (val instanceof String) {
                prefs.put("S_" + key, (String) val);
            } else if (val instanceof Boolean) {
                prefs.putBoolean("B_" + key, (Boolean) val);
            }
            // NOTE: Ignore any parameters of unsupported types instead
            // of throwing exception. This is specifically for handling
            // BufferedImage which is used in setIcons(), as you do not
            // want to export such data in the preferences.
        }

        // Ensure the data is properly written into preferences before
        // continuing.
        prefs.sync();
    }

    /**
     * Get an integer from the settings.
     * 
     * If the key is not set, then 0 is returned.
     */
    public int getInteger(String key) {
        Integer i = (Integer) get(key);
        if (i == null) {
            return 0;
        }

        return i.intValue();
    }

    /**
     * Get a boolean from the settings.
     * 
     * If the key is not set, then false is returned.
     */
    public boolean getBoolean(String key) {
        Boolean b = (Boolean) get(key);
        if (b == null) {
            return false;
        }

        return b.booleanValue();
    }

    /**
     * Get a string from the settings.
     * 
     * If the key is not set, then null is returned.
     */
    public String getString(String key) {
        String s = (String) get(key);
        if (s == null) {
            return null;
        }

        return s;
    }

    /**
     * Get a float from the settings.
     * 
     * If the key is not set, then 0.0 is returned.
     */
    public float getFloat(String key) {
        Float f = (Float) get(key);
        if (f == null) {
            return 0f;
        }

        return f.floatValue();
    }

    /**
     * Set an integer on the settings.
     */
    public void putInteger(String key, int value) {
        put(key, Integer.valueOf(value));
    }

    /**
     * Set a boolean on the settings.
     */
    public void putBoolean(String key, boolean value) {
        put(key, Boolean.valueOf(value));
    }

    /**
     * Set a string on the settings.
     */
    public void putString(String key, String value) {
        put(key, value);
    }

    /**
     * Set a float on the settings.
     */
    public void putFloat(String key, float value) {
        put(key, Float.valueOf(value));
    }

    /**
     * Enable or disable mouse emulation on touchscreen based devices.
     * This will convert taps on the touchscreen or movement of finger
     * over touchscreen (only the first) into the appropriate mouse events.
     *
     * @param emulateMouse If mouse emulation should be enabled.
     */
    public void setEmulateMouse(boolean emulateMouse) {
        putBoolean("TouchEmulateMouse", emulateMouse);
    }

    /**
     * Returns true if mouse emulation is enabled, false otherwise.
     *
     * @return Mouse emulation mode.
     */
    public boolean isEmulateMouse() {
        return getBoolean("TouchEmulateMouse");
    }

    /**
     * Specify if the X or Y (or both) axes should be flipped for emulated mouse.
     *
     * @param flipX Set to flip X axis
     * @param flipY Set to flip Y axis
     *
     * see #setEmulateMouse(boolean)
     */
    public void setEmulateMouseFlipAxis(boolean flipX, boolean flipY) {
        putBoolean("TouchEmulateMouseFlipX", flipX);
        putBoolean("TouchEmulateMouseFlipY", flipY);
    }

    public boolean isEmulateMouseFlipX() {
        return getBoolean("TouchEmulateMouseFlipX");
    }

    public boolean isEmulateMouseFlipY() {
        return getBoolean("TouchEmulateMouseFlipY");
    }

    /**
     * @param frameRate The frame-rate is the upper limit on how high
     * the application's frames-per-second can go.
     * (Default: -1 no frame rate limit imposed)
     */
    public void setFrameRate(int frameRate) {
        putInteger("FrameRate", frameRate);
    }

    /**
     * @param use If true, the application will initialize and use input.
     * Set to false for headless applications that do not require keyboard
     * or mouse input.
     * (Default: true)
     */
    public void setUseInput(boolean use) {
        putBoolean("UseInput", use);
    }

    /**
     * @param use If true, the application will initialize and use joystick
     * input. Set to false if no joystick input is desired.
     * (Default: false)
     */
    public void setUseJoysticks(boolean use) {
        putBoolean("DisableJoysticks", !use);
    }

    /**
     * Set the graphics renderer to use, one of:
     * 
     * AppSettings.LWJGL_OPENGL1 - Force OpenGL1.1 compatability
     * AppSettings.LWJGL_OPENGL2 - Force OpenGL2 compatability
     * AppSettings.LWJGL_OPENGL3 - Force OpenGL3.3 compatability
     * AppSettings.LWJGL_OPENGL_ANY - Choose an appropriate
     * OpenGL version based on system capabilities
     * null - Disable graphics rendering
     * 
     * @param renderer The renderer to set
     * (Default: AppSettings.LWJGL_OPENGL2)
     */
    public void setRenderer(String renderer) {
        putString("Renderer", renderer);
    }

    /**
     * Set a custom graphics renderer to use. The class should implement
     * the CWContext} interface.
     * @param clazz The custom context class.
     * (Default: not set)
     */
    public void setCustomRenderer(Class<? extends CWContext> clazz){
        put("Renderer", "CUSTOM" + clazz.getName());
    }

    /**
     * Set the audio renderer to use. One of:
     * 
     * AppSettings.LWJGL_OPENAL - Default for LWJGL
     * null - Disable audio
     * 
     * @param audioRenderer
     * (Default: LWJGL)
     */
    public void setAudioRenderer(String audioRenderer) {
        putString("AudioRenderer", audioRenderer);
    }

    /**
     * @param value the width for the rendering display.
     * (Default: 640)
     */
    public void setWidth(int value) {
        putInteger("Width", value);
    }

    /**
     * @param value the height for the rendering display.
     * (Default: 480)
     */
    public void setHeight(int value) {
        putInteger("Height", value);
    }

    /**
     * Set the resolution for the rendering display
     * @param width The width
     * @param height The height
     * (Default: 640x480)
     */
    public void setResolution(int width, int height) {
        setWidth(width);
        setHeight(height);
    }


    /**
     * @param value the minimum width the settings window will allow for the rendering display.
     * (Default: 0)
     */
    public void setMinWidth(int value) {
        putInteger("MinWidth", value);
    }

    /**
     * @param value the minimum height the settings window will allow for the rendering display.
     * (Default: 0)
     */
    public void setMinHeight(int value) {
        putInteger("MinHeight", value);
    }

    /**
     * Set the minimum resolution the settings window will allow for the rendering display
     * @param width The minimum width
     * @param height The minimum height
     * (Default: 0x0)
     */
    public void setMinResolution(int width, int height) {
        setMinWidth(width);
        setMinHeight(height);
    }



    /**
     * Set the frequency, also known as refresh rate, for the
     * rendering display.
     * @param value The frequency
     * (Default: 60)
     */
    public void setFrequency(int value) {
        putInteger("Frequency", value);
    }

    /**
     * Sets the number of depth bits to use.
     * 
     * The number of depth bits specifies the precision of the depth buffer.
     * To increase precision, specify 32 bits. To decrease precision, specify
     * 16 bits. On some platforms 24 bits might not be supported, in that case,
     * specify 16 bits.
     * (Default: 24)
     *
     * @param value The depth bits
     */
    public void setDepthBits(int value){
        putInteger("DepthBits", value);
    }

    /**
     * Set the number of stencil bits.
     * 
     * This value is only relevant when the stencil buffer is being used.
     * Specify 8 to indicate an 8-bit stencil buffer, specify 0 to disable
     * the stencil buffer.
     * 
     * (Default: 0)
     *
     * @param value Number of stencil bits
     */
    public void setStencilBits(int value){
        putInteger("StencilBits", value);
    }

    /**
     * Set the bits per pixel for the display. Appropriate
     * values are 16 for RGB565 color format, or 24 for RGB8 color format.
     *
     * @param value The bits per pixel to set
     * (Default: 24)
     */
    public void setBitsPerPixel(int value) {
        putInteger("BitsPerPixel", value);
    }

    /**
     * Set the number of samples per pixel. A value of 1 indicates
     * each pixel should be single-sampled, higher values indicate
     * a pixel should be multi-sampled.
     *
     * @param value The number of samples
     * (Default: 1)
     */
    public void setSamples(int value) {
        putInteger("Samples", value);
    }

    /**
     * @param title The title of the rendering display
     */
    public void setTitle(String title) {
        putString("Title", title);
    }

    /**
     * @param value true to enable full-screen rendering, false to render in a window
     * (Default: false)
     */
    public void setFullscreen(boolean value) {
        putBoolean("Fullscreen", value);
    }

    /**
     * Set to true to enable vertical-synchronization, limiting and synchronizing
     * every frame rendered to the monitor's refresh rate.
     * @param value
     * (Default: false)
     */
    public void setVSync(boolean value) {
        putBoolean("VSync", value);
    }

    /**
     * Enable 3D stereo.
     * This feature requires hardware support from the GPU driver.
     * see <a href="http://en.wikipedia.org/wiki/Quad_buffering">http://en.wikipedia.org/wiki/Quad_buffering</a><br />
     * Once enabled, filters or scene processors that handle 3D stereo rendering
     * could use this feature to render using hardware 3D stereo.
     * (Default: false)
     */
    public void setStereo3D(boolean value){
        putBoolean("Stereo3D", value);
    }

    /**
     * Sets the application icons to be used, with the most preferred first.
     * For Windows you should supply at least one 16x16 icon and one 32x32. The former is used for the title/task bar,
     * the latter for the alt-tab icon.
     * Linux (and similar platforms) expect one 32x32 icon.
     * Mac OS X should be supplied one 128x128 icon.
     * 
     * The icon is used for the settings window, and the LWJGL render window. Not currently supported for JOGL.
     * Note that a bug in Java 6 (bug ID 6445278, currently hidden but available in Google cache) currently prevents
     * the icon working for alt-tab on the settings dialog in Windows.
     *
     * @param value An array of BufferedImages to use as icons.
     * (Default: not set)
     */
    public void setIcons(Object[] value) {
        put("Icons", value);
    }

    /**
     * Sets the path of the settings dialog image to use.
     * 
     * The image will be displayed in the settings dialog when the
     * application is started.
     * 
     *
     * @param path The path to the image in the classpath.
     */
    public void setSettingsDialogImage(String path) {
        putString("SettingsDialogImage", path);
    }

    /**
     * Get the framerate.
     * see #setFrameRate(int)
     */
    public int getFrameRate() {
        return getInteger("FrameRate");
    }

    /**
     * Get the use input state.
     * see #setUseInput(boolean)
     */
    public boolean useInput() {
        return getBoolean("UseInput");
    }

    /**
     * Get the renderer
     * see #setRenderer(java.lang.String)
     */
    public String getRenderer() {
        return getString("Renderer");
    }

    /**
     * Get the width
     * see #setWidth(int)
     */
    public int getWidth() {
        return getInteger("Width");
    }

    /**
     * Get the height
     * see #setHeight(int)
     */
    public int getHeight() {
        return getInteger("Height");
    }

    /**
     * Get the width
     * see #setWidth(int)
     */
    public int getMinWidth() {
        return getInteger("MinWidth");
    }

    /**
     * Get the height
     * see #setHeight(int)
     */
    public int getMinHeight() {
        return getInteger("MinHeight");
    }

    /**
     * Get the bits per pixel
     * see #setBitsPerPixel(int)
     */
    public int getBitsPerPixel() {
        return getInteger("BitsPerPixel");
    }

    /**
     * Get the frequency
     * see #setFrequency(int)
     */
    public int getFrequency() {
        return getInteger("Frequency");
    }

    /**
     * Get the number of depth bits
     * see #setDepthBits(int)
     */
    public int getDepthBits() {
        return getInteger("DepthBits");
    }

    /**
     * Get the number of stencil bits
     * see #setStencilBits(int)
     */
    public int getStencilBits() {
        return getInteger("StencilBits");
    }

    /**
     * Get the number of samples
     * see #setSamples(int)
     */
    public int getSamples() {
        return getInteger("Samples");
    }

    /**
     * Get the application title
     * see #setTitle(java.lang.String)
     */
    public String getTitle() {
        return getString("Title");
    }

    /**
     * Get the vsync state
     * see #setVSync(boolean)
     */
    public boolean isVSync() {
        return getBoolean("VSync");
    }

    /**
     * Get the fullscreen state
     * see #setFullscreen(boolean)
     */
    public boolean isFullscreen() {
        return getBoolean("Fullscreen");
    }

    /**
     * Get the use joysticks state
     * see #setUseJoysticks(boolean)
     */
    public boolean useJoysticks() {
        return !getBoolean("DisableJoysticks");
    }

    /**
     * Get the audio renderer
     * see #setAudioRenderer(java.lang.String)
     */
    public String getAudioRenderer() {
        return getString("AudioRenderer");
    }

    /**
     * Get the stereo 3D state
     * see #setStereo3D(boolean)
     */
    public boolean useStereo3D(){
        return getBoolean("Stereo3D");
    }

    /**
     * Get the icon array
     * see #setIcons(java.lang.Object[])
     */
    public Object[] getIcons() {
        return (Object[]) get("Icons");
    }

    /**
     * Get the settings dialog image
     * see #setSettingsDialogImage(java.lang.String)
     */
    public String getSettingsDialogImage() {
        return getString("SettingsDialogImage");
    }
}
