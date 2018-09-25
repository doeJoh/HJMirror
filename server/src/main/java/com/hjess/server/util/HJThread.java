package com.hjess.server.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

/**
 * 线程工具
 * Created by HalfmanG2 on 17/5/27.
 */
public class HJThread {
    /** 线程池 */
    private static Executor mExecutor = Executors.newFixedThreadPool(6); // 获取线程池对象

    /**
     * 创建者
     */
    private static class ThreadHolder {
        private static final HJThread mgr = new HJThread();
    }

    /**
     * 获取当前实例对象
     *
     * @return
     */
    public static HJThread get() {
        return ThreadHolder.mgr;
    }

    /**
     * Method_线程执行
     *
     * @param runnable 线程
     */
    public void execute(Runnable runnable) {
        mExecutor.execute(runnable);
    }

    /**
     * Method_在主线程中执行
     *
     * @param runnable 线程
     */
    public void executeByUI(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }
}
