

package clockworktest.gui;

import com.clockwork.app.SimpleApplication;
import com.clockwork.input.RawInputListener;
import com.clockwork.input.event.*;
import com.clockwork.math.FastMath;
import com.clockwork.system.AppSettings;
import com.clockwork.texture.Texture;
import com.clockwork.texture.Texture2D;
import com.clockwork.ui.Picture;

public class TestSoftwareMouse extends SimpleApplication {

    private Picture cursor;

    private RawInputListener inputListener = new RawInputListener() {

        private float x = 0, y = 0;

        public void beginInput() {
        }
        public void endInput() {
        }
        public void onJoyAxisEvent(JoyAxisEvent evt) {
        }
        public void onJoyButtonEvent(JoyButtonEvent evt) {
        }
        public void onMouseMotionEvent(MouseMotionEvent evt) {
            x += evt.getDX();
            y += evt.getDY();

            // Prevent mouse from leaving screen
            AppSettings settings = TestSoftwareMouse.this.settings;
            x = FastMath.clamp(x, 0, settings.getWidth());
            y = FastMath.clamp(y, 0, settings.getHeight());

            // adjust for hotspot
            cursor.setPosition(x, y - 64);
        }
        public void onMouseButtonEvent(MouseButtonEvent evt) {
        }
        public void onKeyEvent(KeyInputEvent evt) {
        }
        public void onTouchEvent(TouchEvent evt) {
        }
    };

    public static void main(String[] args){
        TestSoftwareMouse app = new TestSoftwareMouse();

//        AppSettings settings = new AppSettings(true);
//        settings.setFrameRate(60);
//        app.setSettings(settings);

        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
//        inputManager.setCursorVisible(false);

        Texture tex = assetManager.loadTexture("Interface/Logo/Cursor.png");

        cursor = new Picture("cursor");
        cursor.setTexture(assetManager, (Texture2D) tex, true);
        cursor.setWidth(64);
        cursor.setHeight(64);
        guiNode.attachChild(cursor);

        inputManager.addRawInputListener(inputListener);

//        Image img = tex.getImage();
//        ByteBuffer data = img.getData(0);
//        IntBuffer image = BufferUtils.createIntBuffer(64 * 64);
//        for (int y = 0; y < 64; y++){
//            for (int x = 0; x < 64; x++){
//                int rgba = data.getInt();
//                image.put(rgba);
//            }
//        }
//        image.clear();
//
//        try {
//            Cursor cur = new Cursor(64, 64, 2, 62, 1, image, null);
//            Mouse.setNativeCursor(cur);
//        } catch (LWJGLException ex) {
//            Logger.getLogger(TestSoftwareMouse.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
