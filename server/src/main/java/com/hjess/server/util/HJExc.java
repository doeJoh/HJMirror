package com.hjess.server.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

/**
 * Thread Utils
 *
 * Created by HalfmanG2 on 17/5/27.
 */
public class HJExc {
    private static Executor mExecutor = Executors.newFixedThreadPool(6);
    private static class ThreadHolder {
        private static final HJExc mgr = new HJExc();
    }

    public static HJExc get() {
        return ThreadHolder.mgr;
    }

    /**
     * execute a thread
     * @param runnable thread task
     */
    public void execute(Runnable runnable) {
        mExecutor.execute(runnable);
    }

    /**
     * execute a task on UI thread.
     * @param runnable UI thread task
     */
    public void executeByUI(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

}
