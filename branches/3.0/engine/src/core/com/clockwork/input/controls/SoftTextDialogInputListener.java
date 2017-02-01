
package com.clockwork.input.controls;

/**
 *
 */
public interface SoftTextDialogInputListener {

    public static int COMPLETE = 0;
    public static int CANCEL = 1;

    public void onSoftText(int action, String text);
}
