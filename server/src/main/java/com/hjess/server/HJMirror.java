package com.hjess.server;

import com.hjess.server.base.HJView;
import com.hjess.server.util.HJExc;
import com.hjess.server.view.InitView;

import java.util.LinkedList;

/**
 * HJMirror,
 *
 * Created by HalfmanG2 on 2018/10/22.
 */
public class HJMirror {

    /**
     * Return HJMirror instance.
     */
    public static HJMirror get() {
        return AppHolder.INSTANCE;
    }

    private LinkedList<HJView> viewStack = new LinkedList<>();

    public void showView(HJView view) {
        viewStack.add(view);
        view.start();
    }

    public void onDipose(HJView view) {
        viewStack.remove(view);
        if (viewStack.size() == 0) {
            // wait 3secs and kill process.
            HJExc.get().execute(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignore) {}
                if (viewStack.size() == 0) {
                    HJExc.get().executeByUI(() -> System.exit(0));
                }
            });
        }
    }

    private void onCreate(String[] args) {
        showView(new InitView());
    }

    private static class AppHolder {
        private static final HJMirror INSTANCE = new HJMirror();
    }
    private HJMirror() {}
    public static void main(String[] args) {
        HJMirror.get().onCreate(args);
    }
}
