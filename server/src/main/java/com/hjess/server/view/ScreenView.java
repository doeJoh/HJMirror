package com.hjess.server.view;

import com.android.ddmlib.IDevice;
import com.hjess.server.MainConf;
import com.hjess.server.base.BaseView;
import com.hjess.server.util.HJDroid;
import com.hjess.server.util.HJLog;
import com.hjess.server.util.HJScreen;
import com.hjess.server.util.HJThread;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * 屏幕视图
 * Created by HalfmanG2 on 2018/2/12.
 */
public class ScreenView extends BaseView {

    /** 设备 */
    private IDevice device;
    /** 宽度 */
    private int width;
    /** 高度 */
    private int height;
    /** 保持连接 */
    private volatile boolean keepRunning;
    /** 端口号 */
    private int port = 58357;
    /** 图片 */
    private volatile BufferedImage image;

    public ScreenView(BaseView parent, IDevice device) {
        super(parent, device);
        this.device = device;
    }

    public synchronized BufferedImage getImage() {
        return image;
    }

    public synchronized void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    public synchronized boolean isKeepRunning() {
        return keepRunning;
    }

    public synchronized void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

    @Override
    protected void onStart() {
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {
                // 关闭运行
                setKeepRunning(false);
                // 通知关闭
                parent.onViewReturn(device);
            }
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        JFrame.setDefaultLookAndFeelDecorated(true);
        // 设置标题
        setTitle(device.getName());
        // 设置背景色
        setBackground(Color.lightGray);
        // 获取屏幕尺寸
        Dimension size = HJScreen.getScreenSize();
        width = size.width / 8;
        height = size.height / 4;
        // 设置View尺寸为屏幕尺寸1/4
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    public void update(Graphics graphics) {
        super.update(graphics);
    }

    @Override
    public void paint(Graphics g) {
        // 绘制图片
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        }
    }

    /**
     * 渲染屏幕尺寸
     * @param width 屏幕宽度
     * @param height 屏幕高度
     * @param density 屏幕密度
     * @param apkPath APK路径
     */
    private void renderScreen(int width, int height, float density, String apkPath) {
        // 获取 IP 地址
        int port = 58359;
        // 调整窗体尺寸
        int realWidth = (int) (width / density);
        int realHeight = (int) (height / density);
        setPreferredSize(new Dimension(realWidth, realHeight));
        pack();
        repaint();
        // 启动插件
        HJThread.get().execute(() -> {
            startPlugin(apkPath, port);
        });
        // 开启图片接受服务
        startRenderServer(port, realWidth, realHeight);
    }

    /**
     * 启动服务
     * @param port 服务端口号
     */
    private void startRenderServer(int port, int width, int height) {
        HJThread.get().execute(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Socket client = null;
            DataInputStream dis = null;
            DataOutputStream dos = null;
            try {
                // 开始连接
                setKeepRunning(true);
                device.createForward(port+1, port);
                while (isKeepRunning()) {
                    client = new Socket("127.0.0.1", port+1);
                    dis = new DataInputStream(client.getInputStream());
                    dos = new DataOutputStream(client.getOutputStream());
                    dos.writeInt(width);
                    dos.writeInt(height);
                    while (isKeepRunning()) {
                        // 写读图片状态字
                        dos.writeInt(0x11);
                        dos.flush();
                        int length = dis.readInt();
                        if (length > 0) {
                            byte[] da = new byte[length];
                            dis.readFully(da);
                            ByteArrayInputStream in = new ByteArrayInputStream(da);
                            // 读文件
                            BufferedImage image = ImageIO.read(in);
                            // 关闭输入流
                            in.close();
                            // 渲染到 UI
                            HJThread.get().executeByUI(() -> setImage(image));
                        }
                    }
                    // 关闭连接
                    dis.close();
                    dos.close();
                    client.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
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
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        // DoNothing
                    }
                }
                HJLog.d("图片服务关闭");
            }
        });
    }

    private void startPlugin(String apkPath, int port) {
        // 启动安卓端插件
        try {
            HJDroid.get().runMainClassOnDroid(device, apkPath,
                    MainConf.APK_PROCESS, ""+port);
        } catch (Exception e) {
            HJLog.d("Error:"+e.toString());
        }
    }


    @Override
    protected void onDisplay() {
        HJThread.get().execute(() -> {
            // 安装并启动插件
            HJDroid.get().installAndRunApk(device, MainConf.APK_PATH, MainConf.APK_ID,
                    MainConf.ACT_PATH, ""+port);
            // 启动线程读取信息
            tryReadInfo();
        });
    }

    /**
     * 尝试读取设备信息
     */
    private void tryReadInfo() {
        HJThread.get().execute(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HJDroid.get().readDeviceInfo(device, port, port+1, objects -> {
                Boolean ret = (Boolean) objects[0];
                if (ret) {
                    int width = (int) objects[1];
                    int height = (int) objects[2];
                    float density = (int) objects[3];
                    String apkPath = (String) objects[4];
                    // 通知获取成功
                    HJThread.get().executeByUI(() -> renderScreen(width, height, density / 100.0f, apkPath));
                } else {
                    // 重试
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    tryReadInfo();
                }
            });
        });
    }
}
