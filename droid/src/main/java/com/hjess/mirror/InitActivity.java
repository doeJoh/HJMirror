package com.hjess.mirror;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.hjess.mirror.utils.Utils;
import com.hjess.mirror.utils.HJThread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * InitActivity
 * Created by HalfmanG2 on 2018/1/15.
 */
public class InitActivity extends Activity {
    private TextView tv;
    private int width;
    private int height;
    private int density;
    private String apkPath;
    private int port = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (getIntent() != null) {
            Uri uri = getIntent().getData();
            if (uri != null) {
                port = Integer.parseInt(uri.toString());
            }
        }
        tv = (TextView) findViewById(R.id.textView);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            Uri uri = intent.getData();
            if (uri != null) {
                port = Integer.parseInt(uri.toString());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        String ip = Utils.get().getIp(getApplication());
        width = metric.widthPixels;
        height = metric.heightPixels + getNavigationBarHeight();
        density = (int)(metric.density * 100.0f);
        apkPath = getApplicationContext().getPackageResourcePath();
        String msg = ip +" "+apkPath+" "+width+" "+height+" "+density+"\n";
        tv.setText(msg);
        if (port != -1) {
            startServer(port);
        } else {
            tv.append(Constants.INIT_NOT_FOR_LAUNCH);
        }
    }

    private void startServer(final int port) {
        HJThread.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                ServerSocket server = null;
                Socket socket = null;
                DataInputStream is = null;
                DataOutputStream os = null;
                try {
                    server = new ServerSocket(port);
                    // 更新 UI
                    HJThread.getInstance().executeByUI(new Runnable() {
                        @Override
                        public void run() {
                            tv.append(Constants.INIT_SERVER_STARTED);
                        }
                    });
                    socket = server.accept();
                    // 更新 UI
                    HJThread.getInstance().executeByUI(new Runnable() {
                        @Override
                        public void run() {
                            tv.append(Constants.INIT_SERVER_CONNECT_IN);
                        }
                    });
                    is = new DataInputStream(socket.getInputStream());
                    os = new DataOutputStream(socket.getOutputStream());
                    boolean keepRunning = true;
                    while(keepRunning) {
                        int opt = is.readInt();
                        switch (opt) {
                            case 0x111: // Request Device Info
                                // Screen Width
                                os.writeInt(width);
                                os.flush();
                                // Screen Height
                                os.writeInt(height);
                                os.flush();
                                // Screen Density * 100
                                os.writeInt(density);
                                os.flush();
                                // APK file Path
                                os.writeUTF(apkPath);
                                os.flush();
                                break;
                            case 0x222: // Request disconnect
                                keepRunning = false;
                                break;
                            default:
                                break;
                        }
                    }
                    // Wait 3secs and finish.
                    HJThread.getInstance().executeByUIDelay(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 3000);
                } catch (final IOException e) {
                    HJThread.getInstance().cancelByUI(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(e.toString());
                        }
                    });
                } finally {
                    // release
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (os != null) os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (socket != null) socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (server != null) server.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private int getNavigationBarHeight() {
        if (!isNavigationBarShow()) {
            return 0;
        }
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    private boolean isNavigationBarShow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(this).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !(menu || back);
        }
    }

}
