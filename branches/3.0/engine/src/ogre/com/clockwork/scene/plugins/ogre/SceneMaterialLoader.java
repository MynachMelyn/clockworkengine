
package com.clockwork.scene.plugins.ogre;

import com.clockwork.asset.AssetManager;
import com.clockwork.asset.AssetNotFoundException;
import com.clockwork.material.MaterialList;
import com.clockwork.scene.plugins.ogre.matext.OgreMaterialKey;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This is a utility class to load a MaterialList} from a 
 * .scene file. It is only needed because the parsing method
 * used by the SceneLoader doesn't support reading bottom XML nodes
 * before reading the top nodes.
 * 
 */
class SceneMaterialLoader extends DefaultHandler {
    
    private static final Logger logger = Logger.getLogger(SceneMaterialLoader.class.getName());
    private Stack<String> elementStack = new Stack<String>();
    private String folderName;
    private MaterialList materialList;
    private AssetManager assetManager;
    private boolean ignoreItem = false;
    
    private void reset(){
        elementStack.clear();
        materialList = null;
        ignoreItem = false;
    }
    
    private void checkTopNode(String topNode) throws SAXException{
        if (!elementStack.peek().equals(topNode)){
            throw new SAXException("dotScene parse error: Expected parent node to be " + topNode);
        }
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attribs) throws SAXException {
        if (qName.equals("externals")) {
            checkTopNode("scene");
            
            // Has an externals block, create material list.
            materialList = new MaterialList();
        } else if (qName.equals("item")) {
            checkTopNode("externals");
            if (!attribs.getValue("type").equals("material")) {
                // This is not a material external. Ignore it.
                ignoreItem = true;
            }
        } else if (qName.equals("file")) {
            checkTopNode("item");

            if (!ignoreItem) {
                String materialPath = attribs.getValue("name");
                String materialName = new File(materialPath).getName();
                String matFile = folderName + materialName;
                try {
                    MaterialList loadedMaterialList = (MaterialList) assetManager.loadAsset(new OgreMaterialKey(matFile));
                    materialList.putAll(loadedMaterialList);
                } catch (AssetNotFoundException ex) {
                    logger.log(Level.WARNING, "Cannot locate material file: {0}", matFile);
                }
            }
        }
        elementStack.push(qName);
    }
    
    @Override
    public void endElement(String uri, String name, String qName) throws SAXException {
        if (qName.equals("item") && ignoreItem) {
            ignoreItem = false;
        }
        checkTopNode(qName);
        elementStack.pop();
    }
    
    public MaterialList load(AssetManager assetManager, String folderName, InputStream in) throws IOException {
        try {
            this.assetManager = assetManager;
            this.folderName = folderName;
            
            reset();
            
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);  
            XMLReader xr = factory.newSAXParser().getXMLReader();  

            xr.setContentHandler(this);
            xr.setErrorHandler(this);

            InputStreamReader r = null;

            try {
                r = new InputStreamReader(in);
                xr.parse(new InputSource(r));
            } finally {
                if (r != null){
                    r.close();
                }
            }
            
            return materialList;
        } catch (SAXException ex) {
            IOException ioEx = new IOException("Error while parsing Ogre3D dotScene");
            ioEx.initCause(ex);
            throw ioEx;
        } catch (ParserConfigurationException ex) {
            IOException ioEx = new IOException("Error while parsing Ogre3D dotScene");
            ioEx.initCause(ex);
            throw ioEx;
        }
    }
}
