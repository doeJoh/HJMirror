package com.hjess.server;

import com.hjess.server.view.InitView;

/**
 * 主程序入口
 * Created by HalfmanG2 on 2018/2/12.
 */
public class MainApp {

    /**
     * 程序入口方法
     * @param args 入口参数
     */
    public static void main(String[] args) {
        InitView mainView = new InitView();
        mainView.start();
    }
}
