package com.hjess.server.base;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * 图片视图
 * Created by HalfmanG2 on 2018/1/2.
 */
public class HJImageView extends JComponent {

    private BufferedImage image = null;

    public HJImageView() {
        super();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        System.out.println("绘制中"+getWidth()+" "+getHeight());
        // 绘制背景
        g.setColor(Color.RED);
        g.fillRect(0, 0, getWidth(), getHeight());
        // 绘制图片
        if (image != null) {
            g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        }
    }

    /**
     * 刷新图片
     * @param image 图片
     */
    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

//    public static BufferedImage zoomInImage(BufferedImage originalImage, int width, int height) {
//        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        Graphics g = newImage.getGraphics();
//        g.drawImage(originalImage, 0, 0, width, height, null);
//        g.dispose();
//        return newImage;
//    }
}
