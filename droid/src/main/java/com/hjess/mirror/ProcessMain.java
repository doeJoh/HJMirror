package com.hjess.mirror;

import android.graphics.Bitmap;

import com.hjess.mirror.utils.HJLog;
import com.hjess.mirror.utils.HJSnap;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 进程访问
 * Created by HalfmanG2 on 2018/1/24.
 */
public class ProcessMain {

    /**
     * 程序入口
     * @param args String[]{port}
     */
    public static void main(String[] args) {
        // 读取参数
        if (args == null || args.length != 1) {
            return;
        }
        // 从参数获得要启动的端口号
        int port = Integer.parseInt(args[0]);
        // 使用端口号启动服务
        ProcessMain.get().startServer(port);
    }

    // 截屏工具类
    private HJSnap snap;
    // 截屏图片数据
    private volatile byte[] data;
    // 结束标志
    private volatile boolean end;
    // 要截屏的屏幕宽度
    private volatile int width;
    // 要截屏的屏幕高度
    private volatile int height;


    /**
     * 启动服务
     * @param port 目标服务端口
     */
    private void startServer(final int port) {
        // 开启子线程发送截图
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerSocket serverSocket = null;
                Socket socket = null;
                DataOutputStream dos = null;
                DataInputStream dis = null;
                try {
                    // 开启服务
                    HJLog.d(Constants.PROCESS_MAIN_SERVER_STARTED, port);
                    serverSocket = new ServerSocket(port);
                    // 获取链接
                    socket = serverSocket.accept();
                    // 打印日志
                    String address = ""+socket.getInetAddress();
                    HJLog.d(Constants.PROCESS_MAIN_CONNECT_IN, address);
                    // 获取输出流
                    dos = new DataOutputStream(socket.getOutputStream());
                    // 获取输入流
                    dis = new DataInputStream(socket.getInputStream());
                    // 读取尺寸
                    setWidth(dis.readInt());
                    setHeight(dis.readInt());
                    // 打印日志
                    HJLog.d(Constants.PROCESS_MAIN_GET_SIZE, getWidth(), getHeight());
                    // 循环输出截图
                    while (true) {
                        try {
                            int type = dis.readInt();
                            if (type == 0x11) {
                                // 写图片
                                byte[] da = getData();
                                if (da != null && da.length > 0) {
                                    // 写长度
                                    dos.writeInt(da.length);
                                    dos.flush();
                                    // 写图片
                                    dos.write(da, 0, da.length);
                                    dos.flush();
                                } else {
                                    dos.writeInt(-1);
                                }
                            }
                        } catch (EOFException e) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    // 打印异常信息
                    HJLog.e(e);
                } finally {
                    if (dos != null) {
                        try {
                            dos.close();
                        } catch (IOException e) {
                            // DoNothing
                        }
                    }
                    if (dis != null) {
                        try {
                            dis.close();
                        } catch (IOException e) {
                            // DoNothing
                        }
                    }
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // DoNothing
                        }
                    }
                    if (serverSocket != null) {
                        try {
                            serverSocket.close();
                        } catch (Exception e) {
                            // DoNothing
                        }
                    }
                    // 打印连接
                    HJLog.d(Constants.PROCESS_MAIN_CONNECT_END);
                    // 关闭截屏
                    setEnd(true);
                }
            }
        }).start();

        // Main线程截屏
        int width;
        int height;
        // 设置截屏尺寸
        if (snap == null) {
            snap = new HJSnap();
        }
        while (!isEnd()) {
            width = getWidth();
            height = getHeight();
            if (width > 0 && height > 0) {
                snap.setSize(width, height);
                Bitmap bitmap = snap.getShoot();
                byte[] da = imageToBytes(bitmap);
                bitmap.recycle();
                setData(da);
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                // 打印异常
                HJLog.e(e);
            }
        }
        // 打印截屏关闭
        HJLog.d(Constants.PROCESS_MAIN_SNAP_END);
    }

    // 当前图片质量
    private int quality = 100;
    // Bitmap转换的输出流
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private byte[] imageToBytes(Bitmap bImage) {
        try {
            out.reset();
            bImage.compress(Bitmap.CompressFormat.JPEG, quality, out);
            quality -= 40;
            if (quality == 20) {
                quality = 100;
            }
        } catch (Exception e) {
            // 打印异常
            HJLog.e(e);
        }
        return out.toByteArray();
    }

    private synchronized byte[] getData() {
        return data;
    }

    private synchronized void setData(byte[] data) {
        this.data = data;
    }

    private synchronized boolean isEnd() {
        return end;
    }

    private synchronized void setEnd(boolean end) {
        this.end = end;
    }

    private synchronized int getWidth() {
        return width;
    }

    private synchronized void setWidth(int width) {
        this.width = width;
    }

    private synchronized int getHeight() {
        return height;
    }

    private synchronized void setHeight(int height) {
        this.height = height;
    }

    /**
     * 获取当前实例对象
     */
    private static ProcessMain get() {
        return Holder.mgr;
    }
    /** 构造方法 */
    private ProcessMain() {}
    /**
     * 创建者
     */
    private static class Holder {
        private static final ProcessMain mgr = new ProcessMain();
    }
}
