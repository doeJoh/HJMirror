package com.hjess.app;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
     * @param args String[]{ip, port}
     */
    public static void main(String[] args) {
        // 读取参数
        if (args == null || args.length != 1) {
            return;
        }
        int port = Integer.parseInt(args[0]);
        // 启动服务
        ProcessMain.get().startServer(port);
    }

    /** 截屏工具类 */
    private Snapshoot snapshoot;

    private volatile byte[] data;

    public synchronized byte[] getData() {
        return data;
    }

    public synchronized void setData(byte[] data) {
        this.data = data;
    }

    private volatile boolean end;
    private volatile int width;
    private volatile int height;
    public synchronized boolean isEnd() {
        return end;
    }

    public synchronized void setEnd(boolean end) {
        this.end = end;
    }

    public synchronized int getWidth() {
        return width;
    }

    public synchronized void setWidth(int width) {
        this.width = width;
    }

    public synchronized int getHeight() {
        return height;
    }

    public synchronized void setHeight(int height) {
        this.height = height;
    }

    /**
     * 启动服务
     * @param port 目标服务端口
     */
    private void startServer(final int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerSocket serverSocket = null;
                Socket socket = null;
                DataOutputStream dos = null;
                DataInputStream dis = null;
                try {
                    // 开启服务
                    Log.d("halfman", "服务启动~~");
                    serverSocket = new ServerSocket(port);
                    // 获取链接
                    socket = serverSocket.accept();
                    dos = new DataOutputStream(socket.getOutputStream());
                    dis = new DataInputStream(socket.getInputStream());
                    Log.d("halfman", "发现连接");
                    // 读取尺寸
                    setWidth(dis.readInt());
                    setHeight(dis.readInt());
                    Log.d("halfman", "得到尺寸："+getWidth()+"x"+getHeight());
                    // 循环输出截图
                    while (true) {
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
                    }
                } catch (Exception e) {
                    // DoNothing
                    Log.d("halfman", e.toString());
                    for (StackTraceElement element : e.getStackTrace()) {
                        Log.d("halfman", element.getMethodName()+" "+element.getLineNumber());
                    }
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
                    Log.d("halfman", "连接关闭");
                }
            }
        }).start();

        int width = 0;
        int height = 0;
        // 设置截屏尺寸
        if (snapshoot == null) {
            snapshoot = new Snapshoot();
        }
        while (!isEnd()) {
            width = getWidth();
            height = getHeight();
            snapshoot.setSize(width, height);
            if (width != 0 && height != 0) {
                Bitmap bitmap = snapshoot.getShoot();
                byte[] da = imageToBytes(bitmap);
                bitmap.recycle();
                setData(da);
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int quality = 100;
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
            e.printStackTrace();
        }
        return out.toByteArray();
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
