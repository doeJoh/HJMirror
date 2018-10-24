package com.hjess.server.view;

import com.android.ddmlib.IDevice;
import com.hjess.server.base.HJView;
import com.hjess.server.util.HJAdb;
import com.hjess.server.util.HJEnv;
import com.hjess.server.util.HJExc;
import com.hjess.server.util.HJRes;
import com.hjess.server.util.HJVal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * ConnectView
 * Created by HalfmanG2 on 2018/10/23.
 */
public class ConnectView extends HJView {

    private int port;

    private int adbPort;

    private int snapPort;

    private int snapAdbPort;

    private IDevice device;

    private JLabel label;

    ConnectView(HJView parent, IDevice device) {
        super(parent, device);
        this.device = device;
    }

    @Override
    protected void onStart() {
        // Set no titlebar.
        setUndecorated(true);
        // Don't Resize.
        setResizable(false);
        // Set background.
        setBackground(Color.lightGray);
        // Calculator the location and size of window
        Dimension size = HJEnv.getScreenSize();
        int width = size.width / 8;
        int height = size.width / 16;
        Point parentLocation = parent.getLocation();
        Dimension parentSize = parent.getSize();
        int x = parentLocation.x + (parentSize.width - width) / 2;
        int y = parentLocation.y + (parentSize.height - height) / 2;
        setLocation(x, y);
        setPreferredSize(new Dimension(width, height));
        // Set Layout.
        GridLayout gird=new GridLayout(1,1);
        setLayout(gird);
        JPanel panel = new JPanel();
        panel.setLayout(null);
        label = new JLabel();
        label.setBounds(5, 5, width - 10, height - 10);
        panel.add(label);
        add(panel);
    }

    @Override
    protected void onDisplay() {
        label.setText(HJRes.get().getValue("ConnectView_Start_Checking"));
        loadPortSetting();
        HJExc.get().execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HJExc.get().executeByUI(this::startPlugin);
        });
    }

    private void loadPortSetting() {
        String portStr = HJRes.get().getSetting("port");
        if (HJVal.isEmpty(portStr) || !HJVal.isInteger(portStr)) {
            port = 58357;
        } else {
            port = Integer.valueOf(portStr);
        }
        String adbPortStr = HJRes.get().getSetting("adbPort");
        if (HJVal.isEmpty(adbPortStr) || !HJVal.isInteger(adbPortStr)) {
            adbPort = 58358;
        } else {
            adbPort = Integer.valueOf(adbPortStr);
        }
        String snapPortStr = HJRes.get().getSetting("snapPort");
        if (HJVal.isEmpty(snapPortStr) || !HJVal.isInteger(snapPortStr)) {
            snapPort = 58359;
        } else {
            snapPort = Integer.valueOf(snapPortStr);
        }
        String snapAdbPortStr = HJRes.get().getSetting("snapAdbPort");
        if (HJVal.isEmpty(snapAdbPortStr) || !HJVal.isInteger(snapAdbPortStr)) {
            snapAdbPort = 58360;
        } else {
            snapAdbPort = Integer.valueOf(snapAdbPortStr);
        }
    }

    // Check, install and launch the plugin apk.
    private void startPlugin() {
        // Check and copy plugin APK.
        File apkFile = new File(HJRes.get().getJarDir() + File.separator + "HJMirror.apk");
        if (!apkFile.exists()) {
            try {
                HJRes.get().copyFileFromJar("HJMirror.apk", "HJMirror.apk");
            } catch (IOException ignore) {}
        }
        // Install and launch Plugin on device.
        if (apkFile.exists()) {
            HJAdb.get().installAndRunApk(device, apkFile.getPath(), "com.hjess.mirror",
                    "com.hjess.mirror.InitActivity", ""+port, new HJAdb.Response() {
                @Override
                public void onMessage(String msg) {
                    label.setText(msg);
                }
                @Override
                public void onSuccess() {
                    label.setText(HJRes.get().getValue("ConnectView_Connecting"));
                    tryReadInfo();
                }
                @Override
                public void onFailed() {
                    label.setText(HJRes.get().getValue("ConnectView_Plugin_Failed"));
                }
            });
        } else {
            label.setText(HJRes.get().getValue("ConnectView_Plugin_Missing"));
        }
    }

    // Try read device info from the device which installed the plugin.
    private void tryReadInfo() {
        HJExc.get().execute(() -> {
            Socket socket = null;
            DataInputStream is = null;
            DataOutputStream os = null;
            try {
                device.createForward(adbPort, port);
                socket = new Socket("127.0.0.1", adbPort);
                is = new DataInputStream(socket.getInputStream());
                os = new DataOutputStream(socket.getOutputStream());
                os.writeInt(0x111);
                os.flush();
                int width = is.readInt();
                int height = is.readInt();
                int density = is.readInt();
                String apkPath = is.readUTF();
                os.writeInt(0x222);
                os.close();
                is.close();
                socket.close();
                // Success
                HJExc.get().executeByUI(() -> startScreenView(width, height, density / 100.0f, apkPath));
            } catch (Exception e) {
                // release
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ignore) {}
                }
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException ignore) {}
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ignore) {}
                }
                // Delay 3 Secs and Retry
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignore) {}
                HJExc.get().executeByUI(this::tryReadInfo);
            }
        });
    }

    // Launch ScreenView
    private void startScreenView(int width, int height, float density, String apkPath) {
        ScreenView screenView = new ScreenView(parent, device, 58359, width, height, density, apkPath);
        screenView.start();
        dispose();
    }
}
