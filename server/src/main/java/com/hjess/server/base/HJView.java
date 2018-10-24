package com.hjess.server.base;

import com.hjess.server.util.HJExc;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

/**
 * 基础视图
 * Created by HalfmanG2 on 2017/12/8.
 */
public abstract class HJView extends JFrame implements WindowListener, ComponentListener {
    /** 父页面 */
    protected HJView parent;
    /** 请求码 */
    protected Object requestCode;

    public HJView(HJView parent, Object requestCode) {
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
    protected HJView getParentView() {
        return parent;
    }

    /**
     * 启动页面
     */
    public void start() {
        HJExc.get().executeByUI(()->{
            // ComponentListener
            addComponentListener(this);
            // WindowListener
            addWindowListener(this);
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

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}

    @Override
    public void componentResized(ComponentEvent e) {}

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentShown(ComponentEvent e) {}

    @Override
    public void componentHidden(ComponentEvent e) {}
}
