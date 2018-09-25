package com.hjess.server.base;

import com.hjess.server.util.HJThread;

import javax.swing.JFrame;

/**
 * 基础视图
 * Created by HalfmanG2 on 2017/12/8.
 */
public abstract class BaseView extends JFrame {
    /** 父页面 */
    protected BaseView parent;
    /** 请求码 */
    protected Object requestCode;

    public BaseView(BaseView parent, Object requestCode) {
        super();
        this.parent = parent;
        this.requestCode = requestCode;
    }

    /**
     * 获取请求码
     * @return 请求码
     */
    public Object getRequestCode() {
        return requestCode;
    }

    /**
     * 获取父视图
     * @return 父视图
     */
    protected BaseView getParentView() {
        return parent;
    }

    /**
     * 启动页面
     */
    public void start() {
        HJThread.get().executeByUI(()->{
            // prepare
            onStart();
            // Display the window.
            display();
        });
    }

    /**
     * 视图返回
     * @param requestCode 请求码
     * @param objects 返回参数
     */
    public void onViewReturn(Object requestCode, Object ... objects) {
        // Do Nothing.
    }

    protected void display() {
        pack();
        setVisible(true);
        onDisplay();
    }

    protected abstract void onDisplay();

    protected abstract void onStart();
}
