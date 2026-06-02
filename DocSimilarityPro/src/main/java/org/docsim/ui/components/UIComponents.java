package org.docsim.ui.components;

import org.docsim.AppTheme;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

// ─────────────────────────────────────────────────────────────────────────────
// RoundedPanel — panel with rounded corners and a dark-surface background
// ─────────────────────────────────────────────────────────────────────────────
class RoundedPanel extends JPanel {

    private final int arc;
    private Color bg;

    public RoundedPanel(int arc, Color bg) {
        this.arc = arc;
        this.bg  = bg;
        setOpaque(false);
    }
    public RoundedPanel(int arc) { this(arc, AppTheme.SURFACE); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arc, arc));
        g2.dispose();
        super.paintComponent(g);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GradientButton — a smooth gradient-filled button
// ─────────────────────────────────────────────────────────────────────────────
class GradientButton extends JButton {

    private Color c1, c2;
    private boolean hovered;

    public GradientButton(String text, Color c1, Color c2) {
        super(text);
        this.c1 = c1;
        this.c2 = c2;
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setForeground(Color.WHITE);
        setFont(AppTheme.FONT_H3);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color a = hovered ? c1.brighter() : c1;
        Color b = hovered ? c2.brighter() : c2;
        g2.setPaint(new GradientPaint(0, 0, a, getWidth(), 0, b));
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
        g2.dispose();
        super.paintComponent(g);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MetricCard — small info card showing a label + big value
// ─────────────────────────────────────────────────────────────────────────────
class MetricCard extends RoundedPanel {

    public MetricCard(String label, String value, Color accentColor) {
        super(AppTheme.RADIUS, AppTheme.SURFACE2);
        setLayout(new BorderLayout(0, 4));
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_SMALL);
        lbl.setForeground(AppTheme.TEXT_DIM);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 20));
        val.setForeground(accentColor);

        add(lbl, BorderLayout.NORTH);
        add(val, BorderLayout.CENTER);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ScoreBar — horizontal progress bar with a label
// ─────────────────────────────────────────────────────────────────────────────
class ScoreBar extends JPanel {

    private double value; // 0.0 – 1.0
    private final Color barColor;

    public ScoreBar(double value, Color barColor) {
        this.value    = value;
        this.barColor = barColor;
        setOpaque(false);
        setPreferredSize(new Dimension(200, 8));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        // Track
        g2.setColor(AppTheme.BORDER);
        g2.fill(new RoundRectangle2D.Double(0, 0, w, h, h, h));
        // Fill
        int fill = (int)(w * Math.min(value, 1.0));
        if (fill > 0) {
            g2.setColor(barColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, fill, h, h, h));
        }
        g2.dispose();
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Public factory — exports these components for use in panels
// ─────────────────────────────────────────────────────────────────────────────
public class UIComponents {

    public static JPanel roundedPanel(int arc, Color bg) {
        return new RoundedPanel(arc, bg);
    }
    public static JPanel roundedPanel(int arc) {
        return new RoundedPanel(arc);
    }
    public static JButton gradientButton(String text, Color c1, Color c2) {
        return new GradientButton(text, c1, c2);
    }
    public static JPanel metricCard(String label, String value, Color accent) {
        return new MetricCard(label, value, accent);
    }
    public static JPanel scoreBar(double value, Color color) {
        return new ScoreBar(value, color);
    }

    /** Styled section header label. */
    public static JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_H2);
        l.setForeground(AppTheme.TEXT);
        return l;
    }

    /** Muted sub-label. */
    public static JLabel subLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_SMALL);
        l.setForeground(AppTheme.TEXT_DIM);
        return l;
    }

    /** Dark scroll pane. */
    public static JScrollPane darkScroll(JComponent inner) {
        JScrollPane sp = new JScrollPane(inner);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.getHorizontalScrollBar().setUnitIncrement(16);
        return sp;
    }

    /** Horizontal divider. */
    public static JSeparator hDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(AppTheme.BORDER);
        return sep;
    }
}
