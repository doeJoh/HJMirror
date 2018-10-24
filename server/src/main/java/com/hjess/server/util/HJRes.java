package com.hjess.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

/**
 * Resources
 * Created by HalfmanG2 on 2018/10/22.
 */
public class HJRes {

    /**
     * Get instance.
     */
    public static HJRes get() {
        return ResourcesHolder.INSTANCE;
    }

    /**
     * Get a value by key in values.properties
     */
    public String getValue(String key) {
        return getValue(key, "utf-8");
    }

    /**
     * Get a value by key in values.properties
     */
    public String getValue(String key, String encode) {
        if (values.containsKey(key)) {
            String val = values.getProperty(key);
            try {
                return new String(val.getBytes("ISO-8859-1"), encode);
            } catch (UnsupportedEncodingException e) {
                return val;
            }
        } else {
            return key;
        }
    }

    /**
     * Get a value by key in setting.properties
     */
    public String getSetting(String key) {
        if (setting.containsKey(key)) {
            return setting.get(key);
        }
        return "";
    }

    /**
     * Open a file in jar resources.
     */
    public InputStream readJarFile(String resPath) throws IOException {
        if (!resPath.startsWith("/")) {
            resPath="/"+resPath;
        }
        URL url = HJEnv.class.getResource(resPath);
        return url.openStream();
    }

    /**
     * Open a file relatPath to jar path.
     */
    public InputStream readRelatFile(String relatPath) throws IOException {
        File relatFile = new File(getJarDir()+File.separator+relatPath);
        if (!relatFile.exists()) {
            return null;
        }
        return new FileInputStream(relatFile);
    }

    /**
     * Copy a file to relatPath from inside of jar resources.
     */
    public void copyFileFromJar(String resPath, String relatPath) throws IOException {
        if (!resPath.startsWith("/")) {
            resPath="/"+resPath;
        }
        InputStream is = readJarFile(resPath);
        if (is == null) {
            return;
        }
        File target = new File(getJarDir() + File.separator + relatPath);
        if (target.exists()) {
            return;
        }
        FileOutputStream os = new FileOutputStream(target, false);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = is.read(buffer)) != -1) {
            os.write(buffer, 0, length);
            os.flush();
        }
        os.close();
        is.close();
    }

    // Get path of Jar file's folder.
    public String getJarDir() {
        String jarPath = HJRes.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            return new File(jarPath).getParentFile().getCanonicalPath();
        } catch (IOException e) {
            return "";
        }
    }

    // values properties
    private Properties values = new Properties();
    // setting properties
    private HashMap<String, String> setting = new HashMap<>();

    private HJRes() {
        // load or copy setting.properties
        loadSetting();
        // load language prefix.
        String prefix = getSetting("language");
        if (HJVal.isEmpty(prefix)) {
            prefix = "en";
        }

        HJLog.i("========== Setting loaded ==========");
        for (String key : setting.keySet()) {
            HJLog.i(key+"="+setting.get(key));
        }

        // load values with prefix
        try {
            InputStream valueStream = readJarFile("/values_"+prefix+".properties");
            values.load(valueStream);
            valueStream.close();
        } catch (Exception e) {
            HJLog.e(e);
        }
    }

    // Load setting from both setting.properties inside and outside of jar.
    private void loadSetting() {
        final String settingFile = "setting.properties";
        setting.clear();
        // Load settings
        try {
            // Read properties from setting in jar.
            InputStream settingJar = readJarFile(settingFile);
            Properties propertiesJar = new Properties();
            propertiesJar.load(settingJar);
            settingJar.close();
            // Sync it to setting map.
            loadSettingToMap(propertiesJar);

            // Try load properties out of jar
            InputStream settingOut = readRelatFile(settingFile);
            if (settingOut == null) {
                // Not exists, copy it from inside of jar and return.
                copyFileFromJar(settingFile, settingFile);
                return;
            }
            // Exists, load it.
            Properties propertiesOut = new Properties();
            propertiesOut.load(settingOut);
            settingOut.close();
            // Sync it to setting map.
            loadSettingToMap(propertiesOut);
        } catch (IOException ignore) {}
    }

    // Load setting properties to jar
    private void loadSettingToMap(Properties properties) {
        for (Object key : properties.keySet()) {
            if (key == null) {
                continue;
            }
            String keyStr = key.toString();
            try {
                String val = new String(properties.getProperty(keyStr).getBytes("ISO-8859-1"),
                        "utf-8");
                setting.put(keyStr, val);
            } catch (UnsupportedEncodingException ignore) {}
        }
    }

    private static class ResourcesHolder {
        private static final HJRes INSTANCE = new HJRes();
    }
}
