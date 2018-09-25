package com.hjess.server.view;

import com.android.ddmlib.IDevice;
import com.hjess.server.base.BaseView;
import com.hjess.server.base.HJTable;
import com.hjess.server.base.OnClickListener;
import com.hjess.server.base.Toast;
import com.hjess.server.util.HJDroid;
import com.hjess.server.util.HJScreen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


/**
 * 设置页面
 * Created by HalfmanG2 on 2018/1/3.
 */
public class InitView extends BaseView {
    /** 宽度 */
    private int width;
    /** 高度 */
    private int height;
    /** 当前启动屏幕表 */
    private Hashtable<IDevice, ScreenView> currentViews;

    private Toast toast;

    public InitView() {
        super(null, 0x0);
    }


    @Override
    public void onStart() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        // 设置标题
        setTitle("HJMirror初始化设置");
        // 设置关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置背景色
        setBackground(Color.lightGray);
        // 获取屏幕尺寸
        Dimension size = HJScreen.getScreenSize();
        width = size.width / 2;
        height = size.height / 2;
        // 设置View尺寸为屏幕尺寸1/4
        setPreferredSize(new Dimension(width, height));

        GridLayout gird=new GridLayout(1,1);
        setLayout(gird);

        JPanel topPane=new JPanel();//初始化面板
        topPane.setLayout(null);//设置布局NULL

        // ADB 路径
        JLabel adbNotify = new JLabel();
        adbNotify.setBounds(10, 5, width - 20, 30);
        adbNotify.setText("1、请在下方输入ADB程序绝对路径:");
        topPane.add(adbNotify);

        // ADB 路径文本框
        JTextField adbText = new JTextField();
        adbText.setText(HJDroid.ADB_PATH);
        adbText.setBounds(10, 40, width - 100, 40);
        topPane.add(adbText);

        // 浏览按钮
        JButton findBtn = new JButton();
        findBtn.setText("浏览");
        findBtn.setBounds(width - 80, 40, 70, 40);
        findBtn.addMouseListener(new OnClickListener() {
            @Override
            public void onClick() {
                JFileChooser jfc=new JFileChooser();
                jfc.setDialogTitle("选择ADB文件路径");
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jfc.showDialog(new JLabel(), "选择");
                File file=jfc.getSelectedFile();
                adbText.setText(file.getAbsolutePath());
            }
        });
        topPane.add(findBtn);

        JLabel connNotify = new JLabel();
        connNotify.setBounds(10, 85, width - 20, 30);
        connNotify.setText("2、使用数据线连上手机，然后点击连接按钮，如果无数据，请检查手机驱动！");
        topPane.add(connNotify);

        // 连接按钮
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBounds(0, 120, width, height - 120);
        JButton connectBtn = new JButton();
        Rectangle rect = connectBtn.getBounds();
        connectBtn.setBounds(rect);
        connectBtn.setText("开始连接");
        connectBtn.addMouseListener(new OnClickListener() {
            @Override
            public void onClick() {
                // 尝试连接设备
                HJDroid.get().findDevices(adbText.getText(), objects -> {
                    Boolean isSuccess = (Boolean) objects[0];
                    String message = (String) objects[1];
                    if (isSuccess) {
                        IDevice[] devices = HJDroid.get().getDevices();
                        if (devices != null && devices.length != 0) {
                            // 删除所有视图
                            bottomPanel.removeAll();
                            // 开始渲染设备列表
                            onRenderList(bottomPanel, devices);
                            // 刷新页面
                            bottomPanel.repaint();
                        } else {
                            // 弹框
                            toast.setMessage("未发现设备，请确认已连接手机并安装了手机USB驱动！");
                            toast.start();
                        }
                    } else {
                        // 提示错误
                        toast.setMessage(message);
                        toast.start();
                    }
                });
            }
        });
        // 按钮容器
        bottomPanel.add(connectBtn);
        topPane.add(bottomPanel);
        // 插入 Panel
        add(topPane);
    }

    private void onRenderList(JPanel panel, IDevice[] devices) {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("设备名称");
        columnNames.add("设备状态");
        columnNames.add("操作说明");
        // 数据列表
        Vector<Vector<String>> data = new Vector<>();
        // 遍历添加
        for (IDevice device : devices) {
            Vector<String> row = new Vector<>();
            // 插入名称
            row.add(device.getName());
            // 插入状态
            row.add(device.getState().name());
            // 插入操作
            row.add("双击启动");
            // 插入到列表
            data.add(row);
        }
        // 构造列表
        HJTable jTable = new HJTable(data, columnNames);
        jTable.getColumnModel().getColumn(2).setMaxWidth(80);
        jTable.setEditable(false);
        jTable.setOnItemClickListener((e, row, col) -> {
            if (e.getClickCount() >= 2) {
                // 判定为双击
                if (row < devices.length) {
                    // 启动ScreenView
                    showScreenView(devices[row]);
                }
            }
        });
        // 插入到布局
        JScrollPane scrollPane = new JScrollPane(jTable);
        scrollPane.setBounds(5, 0, panel.getWidth() - 10, panel.getHeight() - 40);
        panel.add(scrollPane);
    }

    private void showScreenView(IDevice device) {
        if (currentViews == null) {
            currentViews = new Hashtable<>();
        }
        if (!currentViews.containsKey(device)) {
            // 启动下一个视图
            ScreenView screenView = new ScreenView(this, device);
            currentViews.put(device, screenView);
            screenView.start();
        }
    }

    @Override
    public void onViewReturn(Object requestCode, Object... objects) {
        super.onViewReturn(requestCode, objects);
        if (requestCode != null && requestCode instanceof IDevice) {
            // ScreenView关闭触发
            IDevice device = (IDevice) requestCode;
            currentViews.remove(device);
            toast.setMessage(device.getName()+"已经关闭!");
            toast.start();
        }
    }

    @Override
    protected void onDisplay() {
        toast = new Toast(this, "", 1500);
    }
}
