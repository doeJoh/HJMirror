package com.hjess.mirror.utils;

import android.util.Log;

/**
 * 日志工具
 * Created by HalfmanG2 on 2018/9/27.
 */
public class HJLog {

    private final static String TAG = "HJMirror";

    /**
     * 打印格式日志
     */
    public static void d(String format, Object...params) {
        if (params == null || params.length == 0) {
            Log.e(TAG, format);
        } else {
            Log.e(TAG, String.format(format, params));
        }
    }

    /**
     * 打印异常日志
     */
    public static void e(Exception e) {
        if (e == null) {
            return;
        }
        HJLog.d(e.toString());
        if (e.getStackTrace() != null) {
            for (StackTraceElement element : e.getStackTrace()) {
                HJLog.d("    at " + element.getClassName() + "." + element.getMethodName() + " : " + element.getLineNumber());
            }
        }
    }
}
