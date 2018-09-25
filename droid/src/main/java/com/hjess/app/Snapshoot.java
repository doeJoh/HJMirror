package com.hjess.app;

import android.graphics.Bitmap;
import android.os.Build;
import java.lang.reflect.Method;

/**
 * 测试程序入口
 * export CLASSPATH="com.hjess.app-2.apk" && exec app_process /system/bin "com.hjess.app.TestMain" '$@'
 * Created by HalfmanG2 on 2018/2/13.
 */
public class Snapshoot {

    private Object[] size = null;

    private int width;

    private int height;

    private Method method = null;

    public Snapshoot() {
        String surfaceClassName;
        if (Build.VERSION.SDK_INT <= 17) {
            surfaceClassName = "android.view.Surface";
        } else {
            surfaceClassName = "android.view.SurfaceControl";
        }
        try {
            method = Class.forName(surfaceClassName)
                    .getDeclaredMethod("screenshot", new Class[]{Integer.TYPE, Integer.TYPE});
        } catch (Exception e) {}
    }

    public void setSize(int width, int height) {
        if (width != 0 && height != 0 && this.width != width && this.height != height) {
            size = new Object[]{width, height};
            this.width = width;
            this.height = height;
        }
    }

    /**
     * 获取屏幕截图
     * @return 截图
     * @throws Exception 截图异常
     */
    public Bitmap getShoot() {
        if (method != null && size != null) {
            try {
                return (Bitmap) method.invoke(null, size);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
