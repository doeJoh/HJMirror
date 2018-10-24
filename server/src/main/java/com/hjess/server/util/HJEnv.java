package com.hjess.server.util;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Utils for Platform check.
 * Created by HalfmanG2 on 2018/10/22.
 */
public class HJEnv {

    public static final String TYPE_WIN = "win";
    public static final String TYPE_LINUX = "linux";
    public static final String TYPE_MAC = "mac";

    /**
     * Get OS type, only support windows/Linux/MacOS, null for others.
     */
    public static String getOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            return TYPE_WIN;
        } else if (osName.contains("linux")) {
            return TYPE_LINUX;
        } else if (osName.contains("mac")) {
            return TYPE_MAC;
        }
        return null;
    }

    /**
     * Is x64 JVM.
     */
    public static boolean is64() {
        String arch = System.getProperty("sun.arch.data.model");
        if (arch.contains("64")) {
            return true;
        }
        return false;
    }

    /**
     * Get screen size.
     */
    public static Dimension getScreenSize() {
        if (screenSize == null) {
            screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        }
        return screenSize;
    }
    private static Dimension screenSize = null;
}
