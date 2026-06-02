package org.docsim;

import java.awt.*;


public final class AppTheme {


    public static final Color BG       = new Color(0xF5F7FA);
    public static final Color SURFACE  = Color.WHITE;
    public static final Color SURFACE2 = new Color(0xEEF2F7);
    public static final Color BORDER   = new Color(0xD0D7E2);


    public static final Color ACCENT   = new Color(0x2563EB);
    public static final Color GREEN    = new Color(0x16A34A);
    public static final Color ORANGE   = new Color(0xEA580C);
    public static final Color RED      = new Color(0xDC2626);
    public static final Color VIOLET   = new Color(0x7C3AED);
    public static final Color CYAN     = new Color(0x0891B2);


    public static final Color[] ALGO_COLORS = {
            new Color(0x2563EB),
            new Color(0x16A34A),
            new Color(0xEA580C)
    };


    public static final Color TEXT       = new Color(0x111827);
    public static final Color TEXT_DIM   = new Color(0x6B7280);
    public static final Color TEXT_FAINT = new Color(0x9CA3AF);

    // ── Fonts ────────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_H2     = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_H3     = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO   = new Font("Consolas", Font.PLAIN, 12);
    public static final Font FONT_BADGE  = new Font("Segoe UI", Font.BOLD, 11);

    // ── Dimensions ───────────────────────────────────────────────
    public static final int SIDEBAR_W = 210;
    public static final int RADIUS    = 10;
    public static final int PAD       = 16;
    public static final int PAD_SM    = 8;

    // ── Algorithm Names ──────────────────────────────────────────
    public static final String[] ALGO_NAMES = {
            "Jaccard",
            "Rabin-Karp",
            "KMP"
    };

    private AppTheme() {}
}