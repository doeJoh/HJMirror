package com.hjess.server.util;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * 屏幕设备
 * Created by HalfmanG2 on 2017/12/8.
 */
public final class HJScreen {

    /** 屏幕尺寸 */
    private static Dimension screenSize = null;
    /**
     * 获取屏幕尺寸
     * @return 屏幕尺寸
     */
    public static Dimension getScreenSize() {
        if (screenSize == null) {
            screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        }
        return screenSize;
    }
}
