package com.hjess.app.base;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

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
     * 判断应用是否处于后台
     */
    public final boolean isApplicationBroughtToBackground(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
            if (!tasks.isEmpty()) {
                ComponentName topActivity = tasks.get(0).topActivity;
                if (!topActivity.getPackageName().equals(context.getApplicationContext().getPackageName())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private Utils() {}
    private static class Holder {
        private static final Utils ecu = new Utils();
    }
}
