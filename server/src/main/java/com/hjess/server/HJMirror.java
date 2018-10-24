package com.hjess.server;

import com.hjess.server.view.InitView;

/**
 * HJMirror,
 *
 * Created by HalfmanG2 on 2018/10/22.
 */
public class HJMirror {

    /**
     * Return HJMirror instance.
     */
    public static HJMirror get() {
        return AppHolder.INSTANCE;
    }

    private void onCreate(String[] args) {
        InitView initView = new InitView();
        initView.start();
    }

    private static class AppHolder {
        private static final HJMirror INSTANCE = new HJMirror();
    }
    private HJMirror() {}
    public static void main(String[] args) {
        HJMirror.get().onCreate(args);
    }
}
