package org.docsim.ui.panels;

import org.docsim.AppTheme;
import org.docsim.model.AlgorithmResult;
import org.docsim.model.SimilarityResult;
import org.docsim.ui.MainWindow;
import org.docsim.ui.components.UIComponents;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Algorithm Comparison Panel.
 *
 * Shows a rich comparison table of all 6 algorithms with their:
 * score, execution time, time complexity, space complexity,
 * description, and a visual score bar — all in one scrollable view.
 */
public class AlgorithmComparePanel extends JPanel {

    private final JPanel body;

    public AlgorithmComparePanel(MainWindow app) {
        setBackground(AppTheme.BG);
        setLayout(new BorderLayout());

        body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(32, 40, 32, 40));

        JLabel ph = UIComponents.subLabel("Run an analysis to see the algorithm comparison.");
        ph.setAlignmentX(LEFT_ALIGNMENT);
        body.add(ph);

        JScrollPane scroll = UIComponents.darkScroll(body);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    public void refresh(SimilarityResult result) {
        body.removeAll();
        body.setBorder(new EmptyBorder(32, 40, 32, 40));

        // ── Header ────────────────────────────────────────────
        JLabel title = UIComponents.sectionTitle("Algorithm Comparison");
        title.setAlignmentX(LEFT_ALIGNMENT);
        body.add(title);
        body.add(Box.createVerticalStrut(4));
        body.add(UIComponents.subLabel(
            "All 6 algorithms run on the same documents — side-by-side breakdown"));
        body.add(Box.createVerticalStrut(28));

        // ── Summary metric cards ──────────────────────────────
        List<AlgorithmResult> ars = result.getAlgorithmResults();
        JPanel metrics = new JPanel(new GridLayout(1, 6, 12, 0));
        metrics.setOpaque(false);
        metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        metrics.setAlignmentX(LEFT_ALIGNMENT);

        for (int i = 0; i < ars.size(); i++) {
            AlgorithmResult ar = ars.get(i);
            JPanel card = UIComponents.metricCard(
                ar.getAlgorithmName(),
                ar.getScorePercent(),
                AppTheme.ALGO_COLORS[i]);
            metrics.add(card);
        }
        body.add(metrics);
        body.add(Box.createVerticalStrut(28));

        // ── Comparison table ──────────────────────────────────
        JLabel tableTitle = UIComponents.sectionTitle("Detailed Comparison Table");
        tableTitle.setAlignmentX(LEFT_ALIGNMENT);
        body.add(tableTitle);
        body.add(Box.createVerticalStrut(14));

        JTable table = buildTable(ars);
        JScrollPane tableScroll = UIComponents.darkScroll(table);
        tableScroll.setAlignmentX(LEFT_ALIGNMENT);
        tableScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        body.add(tableScroll);
        body.add(Box.createVerticalStrut(28));

        // ── Score bar comparison ──────────────────────────────
        JLabel barTitle = UIComponents.sectionTitle("Score Visual Comparison");
        barTitle.setAlignmentX(LEFT_ALIGNMENT);
        body.add(barTitle);
        body.add(Box.createVerticalStrut(14));

        JPanel bars = UIComponents.roundedPanel(AppTheme.RADIUS, AppTheme.SURFACE);
        bars.setLayout(new GridLayout(ars.size() + 1, 3, 8, 10));
        bars.setBorder(new EmptyBorder(16, 20, 16, 20));
        bars.setAlignmentX(LEFT_ALIGNMENT);
        bars.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // Column headers
        addCell(bars, "Algorithm",    AppTheme.TEXT_DIM, AppTheme.FONT_BADGE);
        addCell(bars, "Score",         AppTheme.TEXT_DIM, AppTheme.FONT_BADGE);
        addCell(bars, "Visual",        AppTheme.TEXT_DIM, AppTheme.FONT_BADGE);

        for (int i = 0; i < ars.size(); i++) {
            AlgorithmResult ar = ars.get(i);
            Color c = AppTheme.ALGO_COLORS[i];

            addCell(bars, ar.getAlgorithmName(), c, AppTheme.FONT_H3);
            addCell(bars, ar.getScorePercent(),  AppTheme.TEXT, AppTheme.FONT_BODY);

            // Score bar
            JPanel barWrap = new JPanel(new BorderLayout());
            barWrap.setOpaque(false);
            JPanel bar = UIComponents.scoreBar(ar.getScore(), c);
            bar.setPreferredSize(new Dimension(0, 10));
            barWrap.add(bar, BorderLayout.CENTER);
            bars.add(barWrap);
        }
        body.add(bars);
        body.add(Box.createVerticalStrut(28));

        // ── Time comparison ───────────────────────────────────
        JLabel timeTitle = UIComponents.sectionTitle("Execution Time Comparison");
        timeTitle.setAlignmentX(LEFT_ALIGNMENT);
        body.add(timeTitle);
        body.add(Box.createVerticalStrut(14));

        long maxTime = ars.stream().mapToLong(AlgorithmResult::getExecutionTimeMs).max().orElse(1);

        JPanel timeBars = UIComponents.roundedPanel(AppTheme.RADIUS, AppTheme.SURFACE);
        timeBars.setLayout(new GridLayout(ars.size(), 3, 8, 10));
        timeBars.setBorder(new EmptyBorder(16, 20, 16, 20));
        timeBars.setAlignmentX(LEFT_ALIGNMENT);
        timeBars.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        for (int i = 0; i < ars.size(); i++) {
            AlgorithmResult ar = ars.get(i);
            Color c = AppTheme.ALGO_COLORS[i];

            addCell(timeBars, ar.getAlgorithmName(), c, AppTheme.FONT_H3);
            addCell(timeBars, ar.getExecutionTimeMs() + " ms", AppTheme.TEXT, AppTheme.FONT_BODY);

            double ratio = maxTime == 0 ? 0 : (double) ar.getExecutionTimeMs() / maxTime;
            JPanel tbWrap = new JPanel(new BorderLayout());
            tbWrap.setOpaque(false);
            JPanel tb = UIComponents.scoreBar(ratio, AppTheme.ORANGE);
            tb.setPreferredSize(new Dimension(0, 10));
            tbWrap.add(tb, BorderLayout.CENTER);
            timeBars.add(tbWrap);
        }
        body.add(timeBars);
        body.add(Box.createVerticalStrut(28));

        // ── Complexity reference table ─────────────────────────
        JLabel compTitle = UIComponents.sectionTitle("Complexity Reference");
        compTitle.setAlignmentX(LEFT_ALIGNMENT);
        body.add(compTitle);
        body.add(Box.createVerticalStrut(14));

        JPanel compTable = buildComplexityTable(ars);
        compTable.setAlignmentX(LEFT_ALIGNMENT);
        compTable.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        body.add(compTable);

        body.revalidate();
        body.repaint();
    }

    // ── Helpers ───────────────────────────────────────────────

    private JTable buildTable(List<AlgorithmResult> ars) {
        String[] cols = {"Algorithm", "Score (%)", "Time (ms)", "Time Complexity",
                         "Space Complexity", "Description"};
        Object[][] data = new Object[ars.size()][6];
        for (int i = 0; i < ars.size(); i++) {
            AlgorithmResult ar = ars.get(i);
            data[i][0] = ar.getAlgorithmName();
            data[i][1] = ar.getScorePercent();
            data[i][2] = ar.getExecutionTimeMs() + " ms";
            data[i][3] = ar.getTimeComplexity();
            data[i][4] = ar.getSpaceComplexity();
            data[i][5] = ar.getDescription();
        }

        JTable table = new JTable(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Styling
        table.setBackground(AppTheme.SURFACE);
        table.setForeground(AppTheme.TEXT);
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(31, 111, 235, 40));
        table.setSelectionForeground(AppTheme.TEXT);
        table.getTableHeader().setBackground(AppTheme.SURFACE2);
        table.getTableHeader().setForeground(AppTheme.TEXT_DIM);
        table.getTableHeader().setFont(AppTheme.FONT_BADGE);
        table.getTableHeader().setBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER));

        // Colour-code algorithm name column
        table.getColumnModel().getColumn(0).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object v,
                        boolean sel, boolean foc, int row, int col) {
                    JLabel l = (JLabel) super.getTableCellRendererComponent(
                            t, v, sel, foc, row, col);
                    l.setForeground(AppTheme.ALGO_COLORS[row % AppTheme.ALGO_COLORS.length]);
                    l.setFont(AppTheme.FONT_H3);
                    l.setBorder(new EmptyBorder(0, 8, 0, 0));
                    l.setBackground(sel ? new Color(31,111,235,40) : AppTheme.SURFACE);
                    l.setOpaque(true);
                    return l;
                }
            });

        // Stripe renderer for other columns
        DefaultTableCellRenderer stripeRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(AppTheme.FONT_BODY);
                setForeground(AppTheme.TEXT);
                setBorder(new EmptyBorder(0, 8, 0, 0));
                setBackground(sel ? new Color(31,111,235,40) :
                    (row % 2 == 0 ? AppTheme.SURFACE : AppTheme.SURFACE2));
                setOpaque(true);
                return this;
            }
        };
        for (int c = 1; c < cols.length; c++) {
            table.getColumnModel().getColumn(c).setCellRenderer(stripeRenderer);
        }

        // Column widths
        int[] widths = {130, 80, 80, 120, 120, 250};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        return table;
    }

    private JPanel buildComplexityTable(List<AlgorithmResult> ars) {
        JPanel p = UIComponents.roundedPanel(AppTheme.RADIUS, AppTheme.SURFACE);
        p.setLayout(new GridLayout(ars.size() + 1, 4, 0, 0));
        p.setBorder(new EmptyBorder(8, 12, 8, 12));

        // Header
        for (String h : new String[]{"Algorithm", "Time Complexity", "Space Complexity", "Note"}) {
            JLabel l = new JLabel(h);
            l.setFont(AppTheme.FONT_BADGE);
            l.setForeground(AppTheme.TEXT_DIM);
            l.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER));
            l.setPreferredSize(new Dimension(0, 28));
            p.add(l);
        }

        String[][] notes = {
            {"Set operations, very fast"},
            {"Trigram hashing — O(n) in practice"},
            {"Linear prefix table + sliding window"},
            {"Vocab size dominates for large docs"},
            {"Capped at 500 tokens for performance"},
            {"Smoothed log-IDF over 2-doc corpus"},
        };

        for (int i = 0; i < ars.size(); i++) {
            AlgorithmResult ar = ars.get(i);
            Color c = AppTheme.ALGO_COLORS[i];
            String[] row = {ar.getAlgorithmName(), ar.getTimeComplexity(),
                            ar.getSpaceComplexity(), notes[i][0]};
            for (int j = 0; j < 4; j++) {
                JLabel l = new JLabel(row[j]);
                l.setFont(j == 0 ? AppTheme.FONT_H3 : AppTheme.FONT_BODY);
                l.setForeground(j == 0 ? c : AppTheme.TEXT);
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER));
                l.setPreferredSize(new Dimension(0, 30));
                p.add(l);
            }
        }
        return p;
    }

    private void addCell(JPanel p, String text, Color color, Font font) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        p.add(l);
    }
}
