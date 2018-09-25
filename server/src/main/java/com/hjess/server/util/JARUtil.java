package com.hjess.server.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * JNIUtil
 * Created by shengdong.huang on 2017/10/17.
 */
public class JARUtil {

    public static String getJarPath() {
        File file = getFile();
        if(file == null) {
            return null;
        } else {
            try {
                return file.getCanonicalPath();
            } catch (IOException var2) {
                return null;
            }
        }
    }

    public static String getJarDir() {
        File file = getFile();
        if(file == null) {
            return null;
        } else {
            try {
                return getFile().getParentFile().getCanonicalPath();
            } catch (IOException var2) {
                return null;
            }
        }
    }

    public static String getJarName() {
        File file = getFile();
        return file == null?null:getFile().getName();
    }

    private static File getFile() {
        String path = JARUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            return null;
        }

        return new File(path);
    }

    private JARUtil() {}
}
