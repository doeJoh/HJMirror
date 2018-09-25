package com.hjess.server.base;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * 表格类
 * Created by HalfmanG2 on 2018/2/12.
 */
public class HJTable extends JTable {

    private boolean editable = true;

    public HJTable() {
    }

    public HJTable(TableModel dm) {
        super(dm);
    }

    public HJTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    public HJTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }

    public HJTable(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    public HJTable(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
    }

    public HJTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
        removeMouseListener(mouseListener);
        addMouseListener(mouseListener);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (editable) {
            return super.isCellEditable(row, column);
        }
        return false;
    }

    private MouseListener mouseListener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int row =((JTable)e.getSource()).rowAtPoint(e.getPoint());
            int  col=((JTable)e.getSource()).columnAtPoint(e.getPoint());
            if (onItemClickListener != null) {
                onItemClickListener.onClick(e, row, col);
            }
        }
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    };

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        public void onClick(MouseEvent e, int row, int col);
    }
}
