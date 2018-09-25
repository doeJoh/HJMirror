package com.hjess.app.base.thread;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 线程池
 * Created by HalfmanG2 on 2018/1/15.
 */
public class ExThread {

    private final ExecutorService mExecutor = Executors.newFixedThreadPool(3, new AppThreadFactory());
    private static final Handler mHandler = new Handler(Looper.getMainLooper()); // 创建 UI 线程对象

    private ExThread() {}

    private static class ThreadHolder {
        private static final ExThread et = new ExThread();
    }

    public static final ExThread getInstance() {
        return ThreadHolder.et;
    }

    public void execute(Runnable runnable) {
        mExecutor.execute(runnable);
    }

    public void submit(Callable task) {
        mExecutor.submit(task);
    }

    /**
     * 判断当前的线程是否为主线程
     * @return
     */
    public final boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    /**
     * Method_线程执行
     * @param task 要在线程中执行的任务
     */
    public void execute(final ExThreadTask task) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    task.execute();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Method_线程执行并回调
     * @param task 要在线程中执行的任务
     * @param callback 线程任务执行完的回调处理
     */
    public void execute(final ExThreadTask task, final ExThreadBack callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Object obj = task.execute();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.callback(obj);
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
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
