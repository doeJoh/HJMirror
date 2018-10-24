package com.hjess.server.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Log utils.
 * Created by HalfmanG2 on 17/6/7.
 */
public final class HJLog {

    private static DateFormat logFormat = new SimpleDateFormat("[HH:mm:ss SSS]", Locale.ENGLISH);

    /**
     * Log info.
     */
    public static void i(String msg) {
        System.out.println(logFormat.format(System.currentTimeMillis()) + " " + msg);
    }

    /**
     * Log error.
     */
    public static void e(Exception e) {
        if (e == null) {
            return;
        }
        HJLog.i(e.toString());
        if (e.getStackTrace() != null) {
            for (StackTraceElement element : e.getStackTrace()) {
                HJLog.i("    at " + element.getClassName() + "." + element.getMethodName()
                        + " : (line " + element.getLineNumber()+")");
            }
        }
    }
}
