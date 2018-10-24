package com.hjess.server.util;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.DdmPreferences;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.InstallException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * ADB Utils
 * Created by HalfmanG2 on 2018/10/22.
 */
public class HJAdb {
    private final static String ADB_ZIP = "./adb.zip";
    private static String ADB_EXEC;
    private static String ADB_URL;

    public static HJAdb get() {
        return AdbHolder.INSTANCE;
    }

    /**
     * Get Adb execute program.
     */
    private String getAdbExec() {
        return ADB_EXEC;
    }

    /**
     * Check and install the ADB tools.
     */
    public void checkAndInstall(Response response) {
        // Get OS type.
        String osType = HJEnv.getOS();
        if (HJVal.isEmpty(osType)) {
            osType = "";
        }
        // Init os value.
        switch (osType) {
            case HJEnv.TYPE_WIN:
                ADB_EXEC = "./platform-tools/adb.exe";
                ADB_URL = "https://dl.google.com/android/repository/platform-tools-latest-windows.zip";
                break;
            case HJEnv.TYPE_LINUX:
                ADB_EXEC = "./platform-tools/adb";
                ADB_URL = "https://dl.google.com/android/repository/platform-tools-latest-linux.zip";
                break;
            case HJEnv.TYPE_MAC:
                ADB_EXEC = "./platform-tools/adb";
                ADB_URL = "https://dl.google.com/android/repository/platform-tools-latest-darwin.zip";
                break;
            default:
                // ERROR System unknow
                response.onMessage("This program only support Windows, Mac and Linux!!");
                response.onFailed();
                return;
        }
        // Start checking.
        if (!check()) {
            response.onMessage("ADB not installed, downloading...");
            if (download()) {
                response.onMessage("Download complete, unziping...");
                // Try unzip.
                if (unzip()) {
                    response.onMessage("Unzip complete, checking again!");
                    // delete zip file.
                    new File(ADB_ZIP).delete();
                    // check again!
                    if (check()) {
                        // Check passed.
                        response.onMessage("Check passed, install success.");
                        response.onSuccess();
                        return;
                    } else {
                        // ERROR Check failed!
                        response.onMessage("Check failed, that's impossible!!!");
                        response.onFailed();
                        return;
                    }
                }
                // ERROR Unzip failed!
                response.onMessage("Unzip Failed, you can unzip adb.zip by manual.");
                response.onFailed();
                return;
            }
            // ERROR Download failed!
            response.onMessage("Download Failed, please check your network");
            response.onFailed();
            return;
        }
        // Check passed.
        response.onMessage("ADB has installed!");
        response.onSuccess();
    }

    // adb api
    private static AndroidDebugBridge adb = null;
    /**
     * Start and find Devices.
     */
    public IDevice[] startAndFind() throws Exception {
        if (adb == null) {
            AndroidDebugBridge.init(false);
            DdmPreferences.setTimeOut(20000);
            adb = AndroidDebugBridge.createBridge(getAdbExec(), true);
            if (adb == null) {
                throw new Exception("Adb initilized failed!");
            }
        }
        if (adb.hasInitialDeviceList()) {
            IDevice[] devices = adb.getDevices();
            if (devices != null) {
                return devices;
            } else {
                return new IDevice[]{};
            }
        }
        return new IDevice[]{};
    }

    /**
     * Install and run apk.
     */
    public void installAndRunApk(IDevice device, String apkPath, String applicationId,
                                 String activityPath, String data, Response response) {
        HJExc.get().execute(() -> {
            HJExc.get().executeByUI(() -> response.onMessage("Checking Plugin..."));
            // check applicationId has installed or not
            try {
                checkInstall(device, applicationId, objects -> {
                    if (objects == null || objects.length != 1 || !(objects[0] instanceof Boolean)
                            || !(boolean) objects[0]) {
                        // not install, install it.
                        HJExc.get().executeByUI(() -> response.onMessage("Installing Plugin..."));
                        try {
                            device.installPackage(apkPath, false);
                        } catch (InstallException e) {
                            HJLog.e(e);
                            HJExc.get().executeByUI(response::onFailed);
                        }
                    }
                    // start it.
                    HJExc.get().executeByUI(() -> response.onMessage("Launching Plugin..."));
                    try {
                        sendCmd(device, "am start -n " + applicationId + "/" + activityPath + " -d " + data, null);
                        HJExc.get().executeByUI(response::onSuccess);
                    } catch (Exception e) {
                        HJLog.e(e);
                        HJExc.get().executeByUI(response::onFailed);
                    }
                });
            } catch (Exception e) {
                HJLog.e(e);
                HJExc.get().executeByUI(response::onFailed);
            }
        });
    }

    /**
     * Launch a java class by adb shell.
     */
    public void runMainClassOnDroid(IDevice device, String apkPathOnDroid, String mainClassPath, String...args)
            throws Exception {
        StringBuilder sb = new StringBuilder();
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
        sendCmd(device, sb.toString(), null);
    }

    /**
     * Check packageId has installed on device or not.
     */
    private void checkInstall(IDevice device, String packageId, Callback callback) throws Exception {
        sendCmd(device, "pm list packages "+packageId, objects -> {
            if (objects != null && objects.length > 0 && objects[0] != null
                    && (""+objects[0]).length() > 0) {
                if (callback != null) {
                    callback.onCallback(true);
                }
            } else {
                if (callback != null) {
                    callback.onCallback(false);
                }
            }
        });
    }

    /**
     * Send an ADB Shell Command to Device
     */
    private void sendCmd(IDevice device, String cmd, Callback callback) throws Exception {
        // 启动命令
        HJLog.i("[RUN]"+cmd);
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
            }
            @Override
            public boolean isCancelled() {
                return false;
            }
        }, 1, TimeUnit.DAYS);
    }

    // Check adb file exist and executable.
    private boolean check() {
        File adbFile = new File(ADB_EXEC);
        return adbFile.exists() && adbFile.canExecute();
    }

    // Download ADB from Google.
    private boolean download() {
        try {
            URL url = new URL(ADB_URL);
            File target = new File(ADB_ZIP);
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(target);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, length);
                fs.flush();
            }
            fs.close();
            inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Unzip adb.zip
    private boolean unzip() {
        try {
            // Open zip InputSteam.
            ZipInputStream zip = new ZipInputStream(new FileInputStream(ADB_ZIP));
            // Create Target Folder.
            File outputDirectory = new File("./");
            if (!outputDirectory.exists()) {
                outputDirectory.mkdir();
            }
            int len;
            byte[] buffer = new byte[4096];
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    File file = new File(outputDirectory + File.separator+ entry.getName());
                    File folder = file.getParentFile();
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    FileOutputStream fileWriter = new FileOutputStream(file);
                    while ((len = zip.read(buffer)) > 0) {
                        fileWriter.write(buffer, 0, len);
                    }
                    fileWriter.flush();
                    fileWriter.close();
                    // Make adb file executable.
                    if (file.getName().equals("adb")) {
                        file.setExecutable(true);
                    }
                }
            }
            zip.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private HJAdb() {}
    private static class AdbHolder {
        private static final HJAdb INSTANCE = new HJAdb();
    }

    public interface Response {
        void onMessage(String msg);
        void onSuccess();
        void onFailed();
    }

    public interface Callback {
        void onCallback(Object... objects);
    }
}
