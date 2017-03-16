
package com.clockwork.niftygui;

import com.clockwork.font.BitmapFont;
import com.clockwork.font.BitmapText;
import de.lessvoid.nifty.spi.render.RenderFont;

public class RenderFontCW implements RenderFont {

    private NiftyCWDisplay display;
    private BitmapFont font;
    private BitmapText text;
    private float actualSize;

    /**
     * Initialize the font.
     * @param name font filename
     */
    public RenderFontCW(String name, NiftyCWDisplay display) {
        this.display = display;
        font = display.getAssetManager().loadFont(name);
        text = new BitmapText(font);
        actualSize = font.getPreferredSize();
        text.setSize(actualSize);
    }

    public BitmapText createText() {
      return new BitmapText(font);
    }

    public BitmapFont getFont() {
        return font;
    }
    
    public BitmapText getText(){
        return text;
    }

    /**
     * get font height.
     * @return height
     */
    public int getHeight() {
        return (int) text.getLineHeight();
    }

    /**
     * get font width of the given string.
     * @param str text
     * @return width of the given text for the current font
     */
    public int getWidth(final String str) {
        if (str.length() == 0) {
            return 0;
        }
 
        // Note: BitmapFont is now fixed to return the proper line width
        //       at least for now.  The older commented out (by someone else, not me)
        //       code below is arguably 'more accurate' if BitmapFont gets
        //       buggy again.  The issue is that the BitmapText and BitmapFont
        //       use a different algorithm for calculating size and both must
        //       be modified in sync.       
        int result = (int) font.getLineWidth(str);
//        text.setText(str);
//        text.updateLogicalState(0);
//        int result = (int) text.getLineWidth();

        return result;
    }

    public int getWidth(final String str, final float size) {
      // Note: This is supposed to return the width of the String when scaled
      //       with the size factor. Since I don't know how to do that with
      //       the font rendering in CW this will only work correctly with
      //       a size value of 1.f and will return inaccurate values otherwise.
      return getWidth(str);
    }

    /**
     * Return the width of the given character including kerning information.
     * @param currentCharacter current character
     * @param nextCharacter next character
     * @param size font size
     * @return width of the character or null when no information for the character is available
     */
    public int getCharacterAdvance(final char currentCharacter, final char nextCharacter, final float size) {
        return Math.round(font.getCharacterAdvance(currentCharacter, nextCharacter, size));
    }

    public void dispose() {
    }
}
