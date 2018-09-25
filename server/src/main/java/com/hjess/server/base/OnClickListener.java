package com.hjess.server.base;

import java.awt.event.MouseEvent;


/**
 * 简单鼠标监听
 * Created by HalfmanG2 on 2018/1/4.
 */
public abstract class OnClickListener implements java.awt.event.MouseListener {
    public abstract void onClick();

    private boolean isPressIn = false;
    @Override
    public void mouseClicked(MouseEvent e) {
        onClick();
    }
    @Override
    public void mousePressed(MouseEvent e) {
        isPressIn = true;
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        if (isPressIn) {
            mouseClicked(e);
            isPressIn = false;
        }
    }
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {
        isPressIn = false;
    }
}
