package com.hjess.server.util;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.DdmPreferences;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.InstallException;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * ADB接口
 * Created by HalfmanG2 on 2018/1/2.
 */
public final class HJDroid {
    /** ADB 路径 */
    public static String ADB_PATH = "/Users/HalfmanG2/Develop/SDK/platform-tools/adb";
    /** ADB桥接接口 */
    private static AndroidDebugBridge adb = null;
    /** 是否已经初始化 */
    private boolean inited = false;

    /**
     * 获取当前实例对象
     */
    public static HJDroid get() {
        return Holder.mgr;
    }

    /**
     * 初始化方法
     * @param adbPath ADB 程序的 PATH
     */
    private boolean init(String adbPath) {
        if (!inited) {
            try {
                ADB_PATH = adbPath;
                // 初始化 ADB
                AndroidDebugBridge.init(false);
                // 设置超时
                DdmPreferences.setTimeOut(20000);
                // 初始化 ADB 接口
                adb = AndroidDebugBridge.createBridge(ADB_PATH, true);
                // 是否成功初始化
                inited = adb != null;
            } catch (Exception e) {
                inited = false;
                return inited;
            }
            // 返回ADB 初始化是否成功
            return inited;
        }
        return false;
    }

    public boolean isInited() {
        return inited;
    }

    public void findDevices(String adbPath, Callback callback) {
        ADB_PATH = adbPath;
        if (!isInited()) {
            boolean result = init(ADB_PATH);
            if (!result) {
                callback.onCallback(Boolean.FALSE, "ADB初始化失败！");
            }
        }
        // 执行线程
        HJThread.get().execute(() -> {
            int count = 0;
            while(!adb.hasInitialDeviceList()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
                count++;
                if (count > 100) {
                    break;
                }
            }
            if (adb.hasInitialDeviceList()) {
                HJThread.get().executeByUI(() -> {
                    // 成功返回
                    callback.onCallback(Boolean.TRUE, "发现设备！");
                });

            } else {
                HJThread.get().executeByUI(() -> {
                    // 失败返回
                    callback.onCallback(Boolean.FALSE, "未找到设备！");
                });
            }
        });
    }

    /**
     * 获取设备列表
     * @return 设备列表
     */
    public IDevice[] getDevices() {
        if (adb != null && adb.hasInitialDeviceList()) {
            IDevice[] devices = adb.getDevices();
            if (devices != null) {
                return devices;
            } else {
                return new IDevice[]{};
            }
        } else {
            return new IDevice[]{};
        }
    }

    /**
     * 执行安卓设备上已安装的APK中的Java Main类
     * @param device 安卓设备
     * @param apkPathOnDroid 安卓上已安装的APK的位置，通常为/data/app/xxxx-x.apk
     * @param mainClassPath APK中Java Main类的包名
     * @param args 携带的参数
     */
    public void runMainClassOnDroid(IDevice device, String apkPathOnDroid, String mainClassPath,
                                    String...args)
            throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("export CLASSPATH=");
        sb.append(apkPathOnDroid);
        sb.append(" && exec app_process /system/bin ");
        sb.append(mainClassPath);
        if (args != null && args.length > 0) {
            for (String arg : args) {
                sb.append(" '");
                sb.append(arg);
                sb.append("'");
            }
        }
        System.out.println("RUN:"+mainClassPath+"  "+sb.toString());
        sendCmd(device, sb.toString(), null);
    }

    /**
     * 检查ID是否已经安装
     * @param device 要检查的设备
     * @param packageId 要检查的APPID
     */
    public void checkInstall(IDevice device, String packageId, Callback callback) {
        try {
            sendCmd(device, "pm list packages "+packageId, new Callback() {
                @Override
                public void onCallback(Object... objects) {
                    if (objects != null && objects.length > 0 && objects[0] != null
                            && (""+objects[0]).length() > 0) {
                        HJLog.d("检测插件安装："+objects[0]);
                        callback.onCallback(true);
                    } else {
                        HJLog.d("检测插件安装："+"未安装!");
                        callback.onCallback(false);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 安装并启动APK
     * @param device 目标设备
     * @param apkPath 要安装的APK路径
     * @param applicationId APK的应用 ID
     * @param activityPath 要启动的页面
     */
    public void installAndRunApk(IDevice device, String apkPath,
                                 String applicationId, String activityPath, String data) {
        HJThread.get().execute(() -> {
            // 检查是否已经安装了APP
            checkInstall(device, applicationId, objects -> {
                if (objects == null || objects.length != 1 || !(objects[0] instanceof Boolean)
                        || !(boolean)objects[0]) {
                    // 未检测到安装, 安装应用
                    HJLog.d("准备安装："+apkPath);
                    try {
                        device.installPackage(apkPath, false);
                    } catch (InstallException e) {
                        HJLog.d("安装异常："+e);
                        e.printStackTrace();
                    }
                    // 输入日志
                    HJLog.d("正在安装中...");
                }
                // 启动应用
                try {
                    HJLog.d("正在启动...");
                    sendCmd(device, "am start -n "+applicationId+"/"+activityPath+" -d "+data, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    /**
     * 读取设备信息
     * @param device 设备
     * @param port 设备开启的端口号
     * @param localPort 本地监听映射端口号
     * @param callback 回调
     */
    public void readDeviceInfo(IDevice device, int port, int localPort, Callback callback) {
        HJThread.get().execute(() -> {
            try {
                device.createForward(localPort, port);
                Socket socket = new Socket("127.0.0.1", localPort);
                DataInputStream is = new DataInputStream(socket.getInputStream());
                DataOutputStream os = new DataOutputStream(socket.getOutputStream());
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
                callback.onCallback(Boolean.TRUE, width, height, density, apkPath);
            } catch (Exception e) {
                callback.onCallback(Boolean.FALSE, e.toString());
            }
        });
    }

    /**
     * 向ADB SHELL发送命令
     * @param device 目标设备
     * @param cmd 命令
     * @param callback 命令回调
     * @throws Exception 发送异常
     */
    public void sendCmd(IDevice device, String cmd, Callback callback) throws Exception {
        HJLog.d("执行命令："+cmd);
        // 启动命令
        device.executeShellCommand(cmd, new IShellOutputReceiver() {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            @Override
            public void addOutput(byte[] data, int offset, int length) {
                bos.write(data, offset, length);
            }
            @Override
            public void flush() {
                try {
                    bos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String msg = new String(bos.toByteArray());
                if (callback != null) {
                    callback.onCallback(msg);
                }
                HJLog.d(msg);
            }
            @Override
            public boolean isCancelled() {
                return false;
            }
        }, 1, TimeUnit.DAYS);
    }

    public interface Callback {
        void onCallback(Object... objects);
    }

    /**
     * 创建者
     */
    private static class Holder {
        private static final HJDroid mgr = new HJDroid();
    }
    private HJDroid() {}
}
