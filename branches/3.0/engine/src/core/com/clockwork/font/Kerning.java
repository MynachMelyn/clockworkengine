
package com.clockwork.font;

import com.clockwork.export.*;
import java.io.IOException;


/**
 * Represents kerning information for a character.
 */
public class Kerning implements Savable {

    private int second;
    private int amount;

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(second, "second", 0);
        oc.write(amount, "amount", 0);
    }

    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        second = ic.readInt("second", 0);
        amount = ic.readInt("amount", 0);
    }
}