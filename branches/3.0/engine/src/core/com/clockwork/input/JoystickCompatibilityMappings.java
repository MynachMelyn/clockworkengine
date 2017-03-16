
package com.clockwork.input;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *  Provides compatibility mapping to different joysticks
 *  that both report their name in a unique way and require
 *  remapping to achieve a proper default layout.
 *
 *  All mappings MUST be defined before the joystick support
 *  has been initialized in the InputManager.
 *
 */
public class JoystickCompatibilityMappings {

    private static final Logger logger = Logger.getLogger(JoystickCompatibilityMappings.class.getName());

    // List of resource paths to check for the joystick-mapping.properties
    // files.
    private static String[] searchPaths = { "joystick-mapping.properties" };  

    private static Map<String,Map<String,String>> joystickMappings = new HashMap<String,Map<String,String>>();

    static {
        loadDefaultMappings();
    }

    protected static Map<String,String> getMappings( String joystickName, boolean create ) {
        Map<String,String> result = joystickMappings.get(joystickName);
        if( result == null && create ) {
            result = new HashMap<String,String>();
            joystickMappings.put(joystickName,result);
        }
        return result;          
    }
 
    /**
     *  Returns the remapped version of the axis/button name if there
     *  is a mapping for it otherwise it returns the original name.
     */
    public static String remapComponent( String joystickName, String componentId ) {
        Map<String,String> map = getMappings(joystickName.trim(), false);   
        if( map == null )
            return componentId;
        if( !map.containsKey(componentId) )
            return componentId;
        return map.get(componentId); 
    }       
 
    /**
     *  Returns a set of Joystick axis/button name remappings if they exist otherwise
     *  it returns an empty map.
     */
    public static Map<String,String> getJoystickMappings( String joystickName ) {
        Map<String,String> result = getMappings(joystickName, false);
        if( result == null )
            return Collections.emptyMap();
        return Collections.unmodifiableMap(result);
    }
    
    /**
     *  Adds a single Joystick axis or button remapping based on the 
     *  joystick's name and axis/button name.  The "remap" value will be
     *  used instead.
     */
    public static void addMapping( String stickName, String sourceComponentId, String remapId ) {
        logger.log(Level.FINE, "addMapping(" + stickName + ", " + sourceComponentId + ", " + remapId + ")" );        
        getMappings(stickName, true).put( sourceComponentId, remapId );
    } 
 
    /**
     *  Adds a preconfigured set of mappings in Properties object
     *  form where the names are dot notation "joystick"."axis/button"
     *  and the values are the remapped component name.  This calls
     *  addMapping(stickName, sourceComponent, remap) for every property
     *  that it is able to parse.
     */
    public static void addMappings( Properties p ) {
        for( Map.Entry<Object,Object> e : p.entrySet() ) {
            String key = String.valueOf(e.getKey()).trim();
            
            int split = key.indexOf( '.' );
            if( split < 0 ) {
                logger.log(Level.WARNING, "Skipping mapping:{0}", e);
                continue;
            }
            
            String stick = key.substring(0, split).trim();
            String component = key.substring(split+1).trim();            
            String value = String.valueOf(e.getValue()).trim();
            addMapping(stick, component, value);           
        }
    }
 
    /**
     *  Loads a set of compatibility mappings from the property file
     *  specified by the given URL.
     */   
    public static void loadMappingProperties( URL u ) throws IOException {
        logger.log(Level.FINE, "Loading mapping properties:{0}", u);
        InputStream in = u.openStream();
        try {        
            Properties p = new Properties();
            p.load(in);
            addMappings(p);            
        } finally {
            in.close();
        } 
    }

    protected static void loadMappings( ClassLoader cl, String path ) throws IOException { 
        logger.log(Level.FINE, "Searching for mappings for path:{0}", path);
        for( Enumeration<URL> en = cl.getResources(path); en.hasMoreElements(); ) {            
            URL u = en.nextElement();
            try { 
                loadMappingProperties(u);
            } catch( IOException e ) {
                logger.log(Level.SEVERE, "Error loading:" + u, e);   
            }                        
        } 
           
    }

    /**
     *  Loads the default compatibility mappings by looking for
     *  joystick-mapping.properties files on the classpath.
     */
    protected static void loadDefaultMappings() {
        for( String s : searchPaths ) {
            try {            
                loadMappings(JoystickCompatibilityMappings.class.getClassLoader(), s);
            } catch( IOException e ) {
                logger.log(Level.SEVERE, "Error searching resource path:{0}", s);
            }
        }
    }     
}
