package com.hjess.server.view;

import com.android.ddmlib.IDevice;
import com.hjess.server.base.HJView;
import com.hjess.server.base.HJTable;
import com.hjess.server.util.HJAdb;
import com.hjess.server.util.HJEnv;
import com.hjess.server.util.HJExc;
import com.hjess.server.util.HJRes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

/**
 * InitView
 * Created by HalfmanG2 on 2018/10/22.
 */
public class InitView extends HJView implements HJTable.OnItemClickListener {

    public InitView() {
        super(null, 0x0);
    }

    private JPanel panel;
    private JLabel label;
    private JScrollPane scrollPane;
    private HJTable table;
    private JLabel author;

    private DefaultTableModel tableModel;

    private String textDoubleClick;

    @Override
    protected void onStart() {
        // Titlebar
        JFrame.setDefaultLookAndFeelDecorated(true);
        setTitle(HJRes.get().getValue("InitView_Title"));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        // Size and location
        Dimension size = HJEnv.getScreenSize();
        int width = size.width / 2;
        int height = size.height / 2;
        setLocation(size.width / 4, size.height / 4);
        setPreferredSize(new Dimension(width, height));
        // Layout
        GridLayout gird=new GridLayout(1,1);
        setLayout(gird);
        // Panel
        panel = new JPanel();
        panel.setLayout(null);
        // Info Label
        label = new JLabel();
        label.setBounds(5, 5, width - 10, 30);
        panel.add(label);
        // Device Table
        Vector<String> columnNames = new Vector<>();
        columnNames.add(HJRes.get().getValue("InitView_Device_Name"));
        columnNames.add(HJRes.get().getValue("InitView_Device_Status"));
        columnNames.add(HJRes.get().getValue("InitView_Operation"));
        tableModel = new DefaultTableModel(new Vector<Vector<String>>(), columnNames);
        table = new HJTable();
        table.setModel(tableModel);
        table.setEnabled(false);
        table.getColumnModel().getColumn(2).setMaxWidth(100);
        table.setEditable(false);
        // ScrollPane
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(5, 40, width - 10, height - 90);
        panel.add(scrollPane);
        // Author Label
        author = new JLabel();
        author.setHorizontalAlignment(SwingConstants.RIGHT);
        author.setForeground(new Color(0x666666));
        author.setBounds(5, height - 50, width - 10, 30);
        author.setText("Created by HalfmanG2 2018");
        panel.add(author);
        add(panel);
    }

    @Override
    protected void onDisplay() {
        textDoubleClick = HJRes.get().getValue("InitView_Double_click");
        String infoHead = HJRes.get().getValue("InitView_Checking");
        HJExc.get().execute(() -> {
            try {
                HJExc.get().executeByUI(() -> label.setText(infoHead+"Starting..."));
                Thread.sleep(2000);
                HJAdb.get().checkAndInstall(new HJAdb.Response() {
                    @Override
                    public void onMessage(String msg) {
                        HJExc.get().executeByUI(() -> label.setText(infoHead+msg));
                    }
                    @Override
                    public void onSuccess() {
                        HJExc.get().executeByUI(() -> {
                            loadDevice();
                        });
                    }
                    @Override
                    public void onFailed() {

                    }
                });
            } catch (InterruptedException ignored) {}
        });
    }

    private IDevice[] devices;
    private void refreshDevices(IDevice[] devices) {
        this.devices = devices;
        if (devices.length == 0) {
            table.setEnabled(false);
            table.setOnItemClickListener(null);
            tableModel.setRowCount(0);
        } else {
            table.setEnabled(true);
            table.setOnItemClickListener(this);
            tableModel.setRowCount(0);
            for (IDevice device : devices) {
                Vector<String> row = new Vector<>();
                row.add(device.getName());
                row.add(device.getState().name());
                row.add(textDoubleClick);
                tableModel.addRow(row);
            }
        }
    }

    @Override
    public void onClick(MouseEvent e, int row, int col) {
        if (e.getClickCount() >= 2) {
            if (row < devices.length) {
                setKeepRunning(false);
                label.setText(HJRes.get().getValue("InitView_Connect_Success"));
                ConnectView connectView = new ConnectView(this, devices[row]);
                connectView.start();
            }
        }
    }

    @Override
    public void onViewReturn(Object requestCode, Object... objects) {
        // Restart device sync.
        table.setEnabled(false);
        table.setOnItemClickListener(null);
        tableModel.setRowCount(0);
        loadDevice();
    }

    private void loadDevice() {
        label.setText(HJRes.get().getValue("InitView_Waiting_Connect"));
        setKeepRunning(true);
        HJExc.get().execute(() -> {
            while (isKeepRunning()) {
                try {
                    IDevice[] devices = HJAdb.get().startAndFind();
                    HJExc.get().executeByUI(() -> refreshDevices(devices));
                    Thread.sleep(5000);
                } catch (Exception e) {
                    setKeepRunning(false);
                    HJExc.get().executeByUI(() -> label.setText(HJRes.get().getValue("InitView_Adb_failed")));
                }
            }
        });
    }

    private volatile boolean keepRunning = true;
    private synchronized boolean isKeepRunning() {
        return keepRunning;
    }
    private synchronized void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        setKeepRunning(false);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        // resize
        Dimension size = getSize();
        label.setBounds(5, 5, size.width - 10, 30);
        scrollPane.setBounds(5, 40, size.width - 10, size.height - 90);
        author.setBounds(5, size.height - 50, size.width - 10, 30);
    }
}
