

package com.clockwork.newvideo;

import com.fluendo.jst.BusHandler;
import com.fluendo.jst.Message;
import com.fluendo.jst.Pipeline;
import com.fluendo.utils.Debug;
import com.clockwork.app.SimpleApplication;
import com.clockwork.system.AppSettings;
import com.clockwork.texture.Texture2D;
import com.clockwork.ui.Picture;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TestNewVideo extends SimpleApplication implements BusHandler {

    private Picture picture;
    private JmeVideoPipeline p;
    private int frame = 0;

    public static void main(String[] args){
        TestNewVideo app = new TestNewVideo();
        AppSettings settings = new AppSettings(true);
//        settings.setFrameRate(24);
        app.setSettings(settings);
        app.start();
    }

    private void createVideo(){
        Debug.level = Debug.INFO;
        p = new JmeVideoPipeline(this);
        p.getBus().addHandler(this);
        try {
            p.inputStream = new FileInputStream("E:\\VideoTest.ogv");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        p.setState(Pipeline.PLAY);
    }

    @Override
    public void simpleUpdate(float tpf){
//        if (p == null)
//            return;

        Texture2D tex = p.getTexture();
        if (tex == null)
            return;

        if (picture != null){
            synchronized (tex){
                try {
                    tex.wait();
                } catch (InterruptedException ex) {
                    // ignore
                }
                tex.getImage().setUpdateNeeded();
                renderer.setTexture(0, tex);
                ((VideoTexture)tex).free();
                System.out.println("PLAY  : " + (frame++));
            }
            return;
        }
        
        picture = new Picture("VideoPicture", true);
        picture.setPosition(0, 0);
        picture.setWidth(settings.getWidth());
        picture.setHeight(settings.getHeight());
        picture.setTexture(assetManager, tex, false);
        rootNode.attachChild(picture);
    }

    public void simpleInitApp() {
        // start video playback
        createVideo();
    }

    @Override
    public void destroy(){
        if (p != null){
            p.setState(Pipeline.STOP);
            p.shutDown();
        }
        super.destroy();
    }

    public void handleMessage(Message msg) {
        switch (msg.getType()){
            case Message.EOS:
                Debug.log(Debug.INFO, "EOS: playback ended");
                /*
                enqueue(new Callable<Void>(){
                    public Void call() throws Exception {
                        rootNode.detachChild(picture);
                        p.setState(Element.STOP);
                        p.shutDown();
                        p = null;
                        return null;
                    }
                });
                
                Texture2D tex = p.getTexture();
                synchronized (tex){
                    tex.notifyAll();
                }
                 */
                break;
            case Message.STREAM_STATUS:
                Debug.info(msg.toString());
                break;
        }
    }
}
