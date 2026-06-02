package org.docsim.ui;

import org.docsim.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class Sidebar extends JPanel {


    public record NavItem(String id, String icon, String label) {}

    private final List<NavItem> items;
    private String activeId;
    private final Consumer<String> onNavigate;
    private final List<NavButton> buttons = new ArrayList<>();

    public static final NavItem[] DEFAULT_ITEMS = {
        new NavItem("home",      "⌂",  "Home Dashboard"),
        new NavItem("upload",    "⤒",  "Upload Documents"),
        new NavItem("results",   "≡",  "Similarity Results"),
        new NavItem("analytics", "∿",  "Analytics & Charts"),

    };

    public Sidebar(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        this.items      = List.of(DEFAULT_ITEMS);
        this.activeId   = "home";

        setPreferredSize(new Dimension(AppTheme.SIDEBAR_W, 0));
        setBackground(AppTheme.SURFACE);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 12, 20, 12));

        // ── Logo / brand ──────────────────────────────────────
        JLabel brand = new JLabel(" DocSim Pro");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 17));
        brand.setForeground(AppTheme.ACCENT);
        brand.setBorder(new EmptyBorder(4, 8, 20, 8));
        content.add(brand);

        // ── Nav buttons ───────────────────────────────────────
        for (NavItem item : items) {
            NavButton btn = new NavButton(item);
            buttons.add(btn);
            content.add(btn);
            content.add(Box.createVerticalStrut(4));
            btn.addActionListener(e -> navigate(item.id()));
        }

        content.add(Box.createVerticalGlue());


        add(content, BorderLayout.CENTER);

        // Right border
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppTheme.BORDER));
    }


    public void navigate(String id) {
        activeId = id;
        buttons.forEach(b -> b.setActive(b.item.id().equals(id)));
        onNavigate.accept(id);
    }


    private static class NavButton extends JButton {

        final NavItem item;
        boolean active;

        NavButton(NavItem item) {
            this.item = item;
            setText(" " + item.icon() + "  " + item.label());
            setFont(AppTheme.FONT_BODY);
            setForeground(AppTheme.TEXT_DIM);
            setHorizontalAlignment(SwingConstants.LEFT);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            setPreferredSize(new Dimension(AppTheme.SIDEBAR_W - 24, 38));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { if (!active) repaint(); }
                public void mouseExited (MouseEvent e) { if (!active) repaint(); }
            });
        }

        void setActive(boolean a) {
            active = a;
            setFont(active ? AppTheme.FONT_H3 : AppTheme.FONT_BODY);
            setForeground(active ? AppTheme.ACCENT : AppTheme.TEXT_DIM);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setColor(new Color(0x58A6FF, false));
                g2.setColor(new Color(88, 166, 255, 20));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(AppTheme.ACCENT);
                g2.fillRect(0, 6, 3, getHeight() - 12);
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
