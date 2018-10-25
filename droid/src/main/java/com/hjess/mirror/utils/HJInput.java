package com.hjess.mirror.utils;

import android.annotation.SuppressLint;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.SystemClock;
import android.view.InputEvent;
import android.view.MotionEvent;

import java.lang.reflect.Method;

/**
 * Input Utils
 * Created by HalfmanG2 on 2018/10/25.
 */
public class HJInput {

    private static InputManager mInputManager;

    private static Method mInjectInputEventMethod;

    @SuppressLint("PrivateApi")
    public HJInput() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mInputManager = (InputManager) InputManager.class.getDeclaredMethod("getInstance").invoke(null);
                mInjectInputEventMethod = InputManager.class.getMethod("injectInputEvent", InputEvent.class, Integer.TYPE);
            }
        } catch (Exception ignore) {}
    }

    public boolean sendTouchEvent(int action, float x, float y) {
        return sendMotionEvent(4098, action, x, y);
    }

    public boolean sendMotionEvent(int inputSource, int action, float x, float y) {
        if (mInputManager == null || mInjectInputEventMethod == null) {
            return false;
        }
        try {
            long when = SystemClock.uptimeMillis();
            MotionEvent event = MotionEvent.obtain(when, when, action, x, y, 1.0f, 1.0f,
                    0, 1.0f, 1.0f, 0, 0);
            event.setSource(inputSource);
            mInjectInputEventMethod.invoke(mInputManager, event, 0);
            event.recycle();
            return true;
        } catch (Exception e) {
            HJLog.e(e);
        }
        return false;
    }
}
