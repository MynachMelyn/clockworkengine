

package com.clockwork.export.xml;

import com.clockwork.export.CWExporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.export.Savable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilderFactory;

public class XMLExporter implements CWExporter {
    
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
            // Initialize document when saving so state of previous exports isn't retained
            this.domOut = new DOMOutputCapsule(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument(), this);
            domOut.write(object, object.getClass().getName(), null);
            DOMSerialiser serialiser = new DOMSerialiser();
            serialiser.serialise(domOut.getDoc(), f);
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
