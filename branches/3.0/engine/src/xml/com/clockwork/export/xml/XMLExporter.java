

package com.clockwork.export.xml;

import com.clockwork.export.JmeExporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.export.Savable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Part of the jME XML IO system as introduced in the google code jmexml project.
 * 
 */
public class XMLExporter implements JmeExporter {
    
    public static final String ELEMENT_MAPENTRY = "MapEntry";	
    public static final String ELEMENT_KEY = "Key";	
    public static final String ELEMENT_VALUE = "Value";
    public static final String ELEMENT_FLOATBUFFER = "FloatBuffer";
    public static final String ATTRIBUTE_SIZE = "size";		

    private DOMOutputCapsule domOut;
    
    public XMLExporter() {
       
    }

    public boolean save(Savable object, OutputStream f) throws IOException {
        try {
            //Initialize Document when saving so we don't retain state of previous exports
            this.domOut = new DOMOutputCapsule(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument(), this);
            domOut.write(object, object.getClass().getName(), null);
            DOMSerializer serializer = new DOMSerializer();
            serializer.serialize(domOut.getDoc(), f);
            f.flush();
            return true;
        } catch (Exception ex) {
            IOException e = new IOException();
            e.initCause(ex);
            throw e;
        }
    }

    public boolean save(Savable object, File f) throws IOException {
        return save(object, new FileOutputStream(f));
    }

    public OutputCapsule getCapsule(Savable object) {
        return domOut;
    }

    public static XMLExporter getInstance() {
            return new XMLExporter();
    }
    
}
