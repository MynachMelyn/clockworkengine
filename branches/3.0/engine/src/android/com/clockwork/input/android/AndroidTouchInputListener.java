package com.clockwork.input.android;

import android.view.KeyEvent;
import android.view.MotionEvent;
import com.clockwork.input.RawInputListener;
import com.clockwork.input.event.TouchEvent;

/**
 * AndroidTouchInputListener is an inputlistener interface which defines
 * callbacks/events for android touch screens For use with class AndroidInput
 *
 *
 */
public interface AndroidTouchInputListener extends RawInputListener {

    public void onTouchEvent(TouchEvent evt);

    public void onMotionEvent(MotionEvent evt);

    public void onAndroidKeyEvent(KeyEvent evt);
}
