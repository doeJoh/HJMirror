package com.hjess.app.utils;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * Created by HalfmanG2 on 2018/1/15.
 */

public final class Utils {

    /**
     * 获取实例
     * @return 实例
     */
    public static Utils get() {
        return Holder.ecu;
    }

    public static final boolean isEmpty(String input) {
        return TextUtils.isEmpty(input);
    }

    public static final boolean isEmpty(List<?> list) {
        return list == null || list.size() == 0;
    }

    public static final boolean isEmpty(Map<?, ?> map) {
        return map == null || map.size() == 0;
    }

    public static final boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static final boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    public static final boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    public static final boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    public static final boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static final boolean isEmpty(Object obj) {
        return obj == null;
    }


    /**
     * Method_Dip 转换 Px
     *
     * @param context 上下文
     * @param dpValue dp 值
     * @return px 值
     */
    public final int dipToPx(Context context, float dpValue) {

        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Method_Px 转换 Dip
     *
     * @param context 上下文
     * @param pxValue px 值
     * @return dp 值
     */
    public final int pxToDip(Context context, float pxValue) {

        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * Method_Px 转换 Sp
     *
     * @param context 上下文
     * @param pxValue px 值
     * @return sp 值
     */
    public final int pxToSp(Context context, float pxValue) {

        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;

        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * Method_Sp 转换 Px
     *
     * @param context 上下文
     * @param spValue sp 值
     * @return px 值
     */
    public final int spToPx(Context context, float spValue) {

        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;

        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * gps获取ip
     */
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * wifi获取ip
     */
    public String getIp(Application context) {
        try {
            //获取wifi服务
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null) {
                return null;
            }
            //判断wifi是否开启
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String ip = intToIp(ipAddress);
            return ip;
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 格式化ip地址（192.168.11.1）
     */
    private String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    /**
     * 3G/4g网络IP
     */
    private String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            // DO NOTHING!
        }
        return null;
    }

    /**
     * 获取本机的ip地址（3中方法都包括）
     */
    public String getIpAdress(Application context) {
        String ip = null;
        try {
            ip = getIp(context);
            if (ip == null) {
                ip = getIpAddress();
                if (ip == null) {
                    ip = getLocalIpAddress();
                }
            }
        } catch (Exception e) {}
        return ip;
    }

    private Utils() {}
    private static class Holder {
        private static final Utils ecu = new Utils();
    }
}
