

package clockworktest.gui;

import com.clockwork.app.SimpleApplication;
import com.clockwork.font.BitmapFont;
import com.clockwork.font.BitmapText;
import com.clockwork.font.LineWrapMode;
import com.clockwork.font.Rectangle;
import com.clockwork.input.KeyInput;
import com.clockwork.input.RawInputListener;
import com.clockwork.input.controls.ActionListener;
import com.clockwork.input.controls.KeyTrigger;
import com.clockwork.input.event.*;

public class TestBitmapFont extends SimpleApplication {

    private String txtB =
    "ABCDEFGHIKLMNOPQRSTUVWXYZ1234567 890`~!@#$%^&*()-=_+[]\\;',./{}|:<>?";

    private BitmapText txt;
    private BitmapText txt2;
    private BitmapText txt3;

    public static void main(String[] args){
        TestBitmapFont app = new TestBitmapFont();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        inputManager.addMapping("WordWrap", new KeyTrigger(KeyInput.KEY_TAB));
        inputManager.addListener(keyListener, "WordWrap");
        inputManager.addRawInputListener(textListener);

        BitmapFont fnt = assetManager.loadFont("Interface/Fonts/Default.fnt");
        txt = new BitmapText(fnt, false);
        txt.setBox(new Rectangle(0, 0, settings.getWidth(), settings.getHeight()));
        txt.setSize(fnt.getPreferredSize() * 2f);
        txt.setText(txtB);
        txt.setLocalTranslation(0, txt.getHeight(), 0);
        guiNode.attachChild(txt);

        txt2 = new BitmapText(fnt, false);
        txt2.setSize(fnt.getPreferredSize() * 1.2f);
        txt2.setText("Text without restriction. \nText without restriction. Text without restriction. Text without restriction");
        txt2.setLocalTranslation(0, txt2.getHeight(), 0);
        guiNode.attachChild(txt2);

        txt3 = new BitmapText(fnt, false);
        txt3.setBox(new Rectangle(0, 0, settings.getWidth(), 0));
        txt3.setText("Press Tab to toggle word-wrap. type text and enter to input text");
        txt3.setLocalTranslation(0, settings.getHeight()/2, 0);
        guiNode.attachChild(txt3);
    }

    private ActionListener keyListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("WordWrap") && !isPressed) {
                txt.setLineWrapMode( txt.getLineWrapMode() == LineWrapMode.Word ?
                                        LineWrapMode.NoWrap : LineWrapMode.Word );
            }
        }
    };

    private RawInputListener textListener = new RawInputListener() {
        private StringBuilder str = new StringBuilder();

        @Override
        public void onMouseMotionEvent(MouseMotionEvent evt) { }

        @Override
        public void onMouseButtonEvent(MouseButtonEvent evt) { }

        @Override
        public void onKeyEvent(KeyInputEvent evt) {
            if (evt.isReleased())
                return;
            if (evt.getKeyChar() == '\n' || evt.getKeyChar() == '\r') {
                txt3.setText(str.toString());
                str.setLength(0);
            } else {
                str.append(evt.getKeyChar());
            }
        }

        @Override
        public void onJoyButtonEvent(JoyButtonEvent evt) { }

        @Override
        public void onJoyAxisEvent(JoyAxisEvent evt) { }

        @Override
        public void endInput() { }

        @Override
        public void beginInput() { }

        @Override
        public void onTouchEvent(TouchEvent evt) { }

    };

}
