package com.hjess.server.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.util.HashMap;

/**
 * JNIUtil
 *
 * A Util to load the JNI lib in Jar.
 *
 * Created by shengdong.huang on 2017/10/17.
 */
public class JNIUtil {

    /**
     * load lib by name, use this method in static block, like:
     * static {
     *     JNIUtil.load("test");
     * }
     *
     * @param libs The file names of libs you want to load
     */
    public static void load(String ... libs) {
        if (libs != null) {
            for (String lib : libs) {
                if (lib != null && lib.length() > 0) {
                    load(lib);
                }
            }
        }
    }

    private static boolean load(String lib) {
        String SEP = "/";
        String osName = getOS();
        if (osName != null) {
            // get possible url
            String is64Str = (is64() ? "x64" : "x86");
            String DLLRES = SEP+osName + SEP + is64Str + SEP;
            HashMap<String, URL> possibleURL = new HashMap<>();
            // <lib>.dll
            String dllName = lib+".dll";
            possibleURL.put(dllName, JNIUtil.class.getResource(DLLRES + dllName));
            // <lib>.lib
            String libName = lib+".lib";
            possibleURL.put(libName, JNIUtil.class.getResource(DLLRES+libName));
            // <lib>.so
            String soName = "lib" + lib+".so";
            possibleURL.put(soName, JNIUtil.class.getResource(DLLRES + soName));
            // <lib>.a
            String aName = "lib" + lib+".a";
            possibleURL.put(aName, JNIUtil.class.getResource(DLLRES + aName));
            // <lib>.dylib
            String dylibName = "lib" + lib+".dylib";
            possibleURL.put(dylibName, JNIUtil.class.getResource(DLLRES + dylibName));
            // <lib>.jnilib
            String jnilibName = "lib" + lib+".jnilib";
            possibleURL.put(jnilibName, JNIUtil.class.getResource(DLLRES + jnilibName));
            // Try
            String targetName = null;
            URL targetURL = null;
            for (String name : possibleURL.keySet()) {
                if (name != null && possibleURL.get(name) != null) {
                    targetName = name;
                    targetURL = possibleURL.get(targetName);
                    break;
                }
            }
            // failed
            if (targetURL == null) {
                System.out.println("JNIUtil: lib:"+lib+" is not found!");
                return false;
            }
            // success
            BufferedOutputStream os = null;
            BufferedInputStream is = null;
            File f = new File(JARUtil.getJarDir()+File.separator+targetName);
            try {
                if (f.exists()) {
                    f.delete();
                }
                os = new BufferedOutputStream(new FileOutputStream(f));
                is = new BufferedInputStream(targetURL.openStream());
                byte[] buf = new byte[4096];
                int i;
                while ((i = is.read(buf)) != -1) {
                    os.write(buf, 0, i);
                    os.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("JNIUtil: lib: TempFile of "+lib+" cannot be created!");
            } finally {
                try {
                    if (os != null) os.close();
                    if (is != null) is.close();
                } catch (Exception e) {
                    // Do Nothing!
                }
            }
            // load file.
            System.loadLibrary(lib);
            // Log the message.
            System.out.println("JNIUtil: \""+f+"\" is loaded!");
            return true;
        } else {
            System.out.println("JNIUtil: This OS is not supported!");
            return false;
        }
    }

    /**
     * Get OS type, only support windows/Linux/MacOS, null for others.
     */
    private static String getOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            return "win";
        } else if (osName.contains("linux")) {
            return "linux";
        } else if (osName.contains("mac")) {
            return "mac";
        }
        return null;
    }

    /**
     * Is x64 JVM.
     */
    private static boolean is64() {
        String arch = System.getProperty("sun.arch.data.model");
        if (arch.contains("64")) {
            return true;
        }
        return false;
    }

    /** Avoid to instance */
    private JNIUtil() {}
}
