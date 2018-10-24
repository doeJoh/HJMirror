package com.hjess.server.util;

import java.util.List;
import java.util.Map;

/**
 * Value utils
 * Created by HalfmanG2 on 2018/10/22.
 */
public class HJVal {

    public static boolean isEmpty(String input) {
        return input == null || input.length() == 0;
    }

    public static boolean isEmpty(List<?> list) {
        return list == null || list.size() == 0;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.size() == 0;
    }

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Object obj) {
        return obj == null;
    }

    public static boolean isEmpty(int obj) {
        return obj == 0;
    }

    public static boolean isEmpty(float obj) {
        return obj == 0f;
    }

    public static boolean isEmpty(long obj) {
        return obj == 0L;
    }

    public static boolean isInteger(String integerStr) {
        try {
            //把字符串强制转换为数字
            Integer.valueOf(integerStr);
            //如果是数字，返回True
            return true;
        } catch (Exception e) {
            //如果抛出异常，返回False
            return false;
        }
    }
}
