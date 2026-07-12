package com.glasscore.util;

import java.awt.Color;
import java.awt.Cursor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public final class UITheme {

    public static final Color BG = Color.decode("#a6a6a6");
    public static final Color PANEL = Color.decode("#e8e8e8");
    public static final Color PRIMARY = Color.decode("#273877");
    public static final Color ACCENT = Color.decode("#3a4f9a");
    public static final Color SIDEBAR = Color.decode("#1c2759");
    public static final Color DANGER = new Color(0x8B, 0x1E, 0x2D);
    public static final Color SUCCESS = new Color(0x1B, 0x5E, 0x3A);
    public static final Color BORDER = Color.decode("#8f8f8f");
    public static final Color TEXT = Color.decode("#1a2248");
    public static final Color MUTED = Color.decode("#3d3d3d");
    public static final Color SELECTION = Color.decode("#c5cce6");
    public static final Color HEADER_EDGE = Color.decode("#1a2456");

    private UITheme() {
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new java.awt.Dimension(0, 0));
        table.setSelectionBackground(SELECTION);
        table.setSelectionForeground(TEXT);
        table.setFont(FontUtil.data(13));
        table.setForeground(TEXT);
        table.setBackground(PANEL);

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.CENTER);
                setOpaque(true);
            }

            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable tbl, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                setFont(FontUtil.dataBold(13));
                setBackground(PRIMARY);
                setForeground(Color.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 0, 1, HEADER_EDGE),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                return this;
            }
        });
        header.setPreferredSize(new java.awt.Dimension(header.getWidth(), 34));
    }

    public static JButton primaryButton(String text) {
        return coloredButton(text, PRIMARY);
    }

    public static JButton accentButton(String text) {
        return coloredButton(text, ACCENT);
    }

    public static JButton dangerButton(String text) {
        return coloredButton(text, DANGER);
    }

    public static JButton navButton(String text) {
        JButton b = coloredButton(text, PRIMARY);
        b.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        return b;
    }

    private static JButton coloredButton(String text, Color background) {
        JButton b = new JButton(text);
        b.setUI(new BasicButtonUI());
        b.setBackground(background);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setFont(FontUtil.uiBold(14));
        return b;
    }

    public static JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FontUtil.dataBold(22));
        l.setForeground(PRIMARY);
        return l;
    }

    public static void styleField(JTextField field) {
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        field.setFont(FontUtil.body(14));
    }

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        return p;
    }
}
