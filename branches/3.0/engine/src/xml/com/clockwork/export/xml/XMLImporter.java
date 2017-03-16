

package com.clockwork.export.xml;

import com.clockwork.asset.AssetInfo;
import com.clockwork.asset.AssetManager;
import com.clockwork.export.InputCapsule;
import com.clockwork.export.CWImporter;
import com.clockwork.export.Savable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class XMLImporter implements CWImporter {

    private AssetManager assetManager;
    private DOMInputCapsule domIn;
    int formatVersion = 0;
    
    public XMLImporter() {
    }

    public int getFormatVersion() {
        return formatVersion;
    }
    
    public AssetManager getAssetManager(){
        return assetManager;
    }

    public void setAssetManager(AssetManager assetManager){
        this.assetManager = assetManager;
    }

    public Object load(AssetInfo info) throws IOException{
        assetManager = info.getManager();
        InputStream in = info.openStream();
        Savable obj = load(in);
        in.close();
        return obj;
    }
    
    public Savable load(File f) throws IOException {
        FileInputStream fis = null; 
        try {
            fis = new FileInputStream(f);
            Savable sav = load(fis);
            return sav;
        } finally {
            if (fis != null) fis.close();
        }
    }

    public Savable load(InputStream f) throws IOException {
        try {
            domIn = new DOMInputCapsule(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f), this);
            return domIn.readSavable(null, null);
        } catch (SAXException e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        } catch (ParserConfigurationException e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        }
    }

    public InputCapsule getCapsule(Savable id) {
        return domIn;
    }

    public static XMLImporter getInstance() {
        return new XMLImporter();
    }

}
