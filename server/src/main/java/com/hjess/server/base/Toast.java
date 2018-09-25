package com.hjess.server.base;

/**
 * 提示组件
 * Created by HalfmanG2 on 2018/1/4.
 */
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JWindow;

/**
 * 吐司提示框组件
 *
 *
 * @author ccw
 * @since:2014-2-28
 */
public class Toast extends JWindow {

    private static final long serialVersionUID = 1L;
    private String message = "";
    private final Insets insets = new Insets(12, 24, 12, 24);
    private int period = 1500;
    private Font font;
    private JLabel label;

    public Toast(Window parent, String message, int period) {
        super(parent);
        this.message = message;
        this.period = period;
        font = new Font("宋体", Font.PLAIN, 14);
        Dimension dimension = getStringSize(font, true, message);
        setSize(dimension);
        // 相对JFrame的位置
        setLocationRelativeTo(parent);
        label = new JLabel();
        label.setBounds(insets.left, insets.top, dimension.width, dimension.height);
        label.setFont(font);
        label.setText(message);
        add(label);
    }

    /**
     * 启动提示
     */

    public void start() {
        this.setVisible(true);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                setVisible(false);
            }
        }, period);
    }

    /**
     * 修改消息
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
        label.setText(message);
        Dimension size = getStringSize(font, true, message);
        label.setBounds(insets.left, insets.top, size.width, size.height);
        setSize(size);
        revalidate();
        repaint();
        if (!isVisible()) {
            start();
        }
    }

    /**
     * 得到字符串的宽-高
     *
     * @param font
     *            字体
     * @param isAntiAliased
     *            反锯齿
     * @param text
     *            文本
     * @return
     */
    private Dimension getStringSize(Font font, boolean isAntiAliased,
                                    String text) {
        FontRenderContext renderContext = new FontRenderContext(null,
                isAntiAliased, false);
        Rectangle2D bounds = font.getStringBounds(text, renderContext);
        int width = (int) bounds.getWidth() + 2 * insets.left;
        int height = (int) bounds.getHeight() + insets.top * 2;
        return new Dimension(width, height);
    }

}
