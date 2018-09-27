package com.hjess.app.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 线程执行工具
 *
 * Created by HalfmanG2 on 2018/1/15.
 */
public class HJThread {

    private final ExecutorService mExecutor = Executors.newFixedThreadPool(3, new AppThreadFactory());
    private static final Handler mHandler = new Handler(Looper.getMainLooper()); // 创建 UI 线程对象

    private HJThread() {}

    private static class ThreadHolder {
        private static final HJThread et = new HJThread();
    }

    public static final HJThread getInstance() {
        return ThreadHolder.et;
    }

    public void execute(Runnable runnable) {
        mExecutor.execute(runnable);
    }

    private class AppThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("app_thread"+System.currentTimeMillis());
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        }
    }

    /**
     * Method_在主线程中执行
     *
     * @param runnable 线程
     */
    public void executeByUI(Runnable runnable) {

        mHandler.post(runnable);
    }

    /**
     * Method_在主线程中执行通过时间
     *
     * @param runnable    线程
     * @param delayMillis 时间
     */
    public void executeByUIDelay(Runnable runnable, long delayMillis) {

        mHandler.postDelayed(runnable, delayMillis);
    }

    /**
     * Method_取消从主线程运行的线程
     *
     * @param runnable 线程
     */
    public void cancelByUI(Runnable runnable) {

        mHandler.removeCallbacks(runnable);
    }

}
