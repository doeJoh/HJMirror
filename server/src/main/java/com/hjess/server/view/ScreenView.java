package com.hjess.server.view;

import com.android.ddmlib.IDevice;
import com.hjess.server.base.HJView;
import com.hjess.server.util.HJAdb;
import com.hjess.server.util.HJExc;
import com.hjess.server.util.HJLog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * ScreenView
 * Created by HalfmanG2 on 2018/10/22.
 */
public class ScreenView extends HJView {
    private IDevice device;
    private int width;
    private int height;
    private int realWidth;
    private int realHeight;
    private float density;
    private String apkPath;
    private int port;

    ScreenView(HJView parent, IDevice device, int port, int width, int height, float density, String apkPath) {
        super(parent, device);
        this.device = device;
        this.width = width;
        this.height = height;
        this.density = density;
        this.apkPath = apkPath;
        this.port = port;
    }

    @Override
    protected void onStart() {
        realWidth = (int) (width / density);
        realHeight = (int) (height / density);
        JFrame.setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(device.getName());
        setBackground(Color.lightGray);
        setPreferredSize(new Dimension(realWidth, realHeight));
    }

    @Override
    protected void onDisplay() {
        // Start plugin.
        HJExc.get().execute(() -> {
            try {
                HJAdb.get().runMainClassOnDroid(device, apkPath,
                        "com.hjess.mirror.ProcessMain", ""+port);
            } catch (Exception ignore) {}
        });
        connectPlugin(port, realWidth, realHeight);
    }

    @Override
    public void paint(Graphics g) {
        // 绘制图片
        BufferedImage img = getImage();
        if (img != null) {
            g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
        }
    }

    private void connectPlugin(int port, int width, int height) {
        HJExc.get().execute(() -> {
            Socket client = null;
            DataInputStream dis = null;
            DataOutputStream dos = null;
            setKeepRunning(true);
            try {
                device.createForward(port + 1, port);
                HJLog.i("Connecting..." + isKeepRunning());
                client = new Socket("127.0.0.1", port + 1);
                dis = new DataInputStream(client.getInputStream());
                dos = new DataOutputStream(client.getOutputStream());
                dos.writeInt(width);
                dos.writeInt(height);
                while (isKeepRunning()) {
                    dos.writeInt(0x11);
                    dos.flush();
                    int length = dis.readInt();
                    if (length > 0) {
                        byte[] da = new byte[length];
                        dis.readFully(da);
                        ByteArrayInputStream in = new ByteArrayInputStream(da);
                        BufferedImage image = ImageIO.read(in);
                        in.close();
                        HJExc.get().executeByUI(() -> setImage(image));
                    }
                }
                dis.close();
                dos.close();
                client.close();
            } catch (Exception ignore) {
                HJLog.i("Connect failed, try again.");
            } finally {
                // release
                if (dos != null) {
                    try {
                        dos.close();
                    } catch (IOException ignore) {}
                }
                if (dis != null) {
                    try {
                        dis.close();
                    } catch (IOException ignore) {}
                }
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException ignore) {}
                }
                // wait 2secs and retry.
                try {
                    Thread.sleep(2000);
                } catch (Exception ignore) {}
                if (isKeepRunning()) {
                    HJExc.get().executeByUI(() -> connectPlugin(port, width, height));
                }
            }
        });
    }

    private volatile BufferedImage image;
    private synchronized BufferedImage getImage() {
        return image;
    }
    private synchronized void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    private volatile boolean keepRunning;
    private synchronized boolean isKeepRunning() {
        return keepRunning;
    }
    private synchronized void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

    @Override
    public void windowClosed(WindowEvent e) {
        setKeepRunning(false);
        parent.onViewReturn(device);
    }
}
