package com.hjess.app;

/**
 * 常量池
 *
 * Created by HalfmanG2 on 2018/9/27.
 */
class Constants {

    // ProcessMain

    final static String PROCESS_MAIN_SERVER_STARTED = "服务启动, 端口号：%d";
    final static String PROCESS_MAIN_CONNECT_IN = "发现连接, 地址：%s";
    final static String PROCESS_MAIN_GET_SIZE = "设备宽高: %d x %d";
    final static String PROCESS_MAIN_CONNECT_END = "连接关闭";
    final static String PROCESS_MAIN_SNAP_END = "截屏关闭";

    // InitActivity
    final static String INIT_NOT_FOR_LAUNCH = "本APP并非用于本地启动，请关闭本APP并杀死进程!";
    final static String INIT_SERVER_STARTED = "服务已经启动，正在等待连接，请不要关闭！\n";
    final static String INIT_SERVER_CONNECT_IN = "发现连接，正在传输，请不要关闭！\n";
}
