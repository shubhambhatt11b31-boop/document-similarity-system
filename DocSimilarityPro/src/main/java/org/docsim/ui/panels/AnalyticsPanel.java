    package org.docsim.ui.panels;

    import org.docsim.AppTheme;
    import org.docsim.model.AlgorithmResult;
    import org.docsim.model.SimilarityResult;
    import org.docsim.ui.MainWindow;
    import org.docsim.ui.components.UIComponents;
    import org.jfree.chart.ChartFactory;
    import org.jfree.chart.ChartPanel;
    import org.jfree.chart.JFreeChart;
    import org.jfree.chart.axis.CategoryAxis;
    import org.jfree.chart.axis.NumberAxis;
    import org.jfree.chart.plot.*;
    import org.jfree.chart.renderer.category.BarRenderer;
    import org.jfree.chart.renderer.category.LineAndShapeRenderer;
    import org.jfree.data.category.DefaultCategoryDataset;
    import org.jfree.data.general.DefaultPieDataset;

    import javax.swing.*;
    import javax.swing.border.EmptyBorder;
    import java.awt.*;
    import java.util.List;
    import java.util.Map;

    /**
     * Analytics & Charts Panel.
     * Renders four charts using JFreeChart:
     *   1. Pie   — algorithm contribution to final score
     *   2. Bar   — raw scores per algorithm
     *   3. Bar   — execution time per algorithm
     *   4. Line  — top-10 word frequency comparison
     */
    public class AnalyticsPanel extends JPanel {

        private final MainWindow app;
        private final JPanel body;

        public AnalyticsPanel(MainWindow app) {
            this.app = app;
            setBackground(AppTheme.BG);
            setLayout(new BorderLayout());

            body = new JPanel();
            body.setOpaque(false);
            body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
            body.setBorder(new EmptyBorder(32, 40, 32, 40));

            JLabel ph = UIComponents.subLabel("Run an analysis to see charts.");
            ph.setAlignmentX(LEFT_ALIGNMENT);
            body.add(ph);

            JScrollPane scroll = UIComponents.darkScroll(body);
            scroll.setBorder(null);
            add(scroll, BorderLayout.CENTER);
        }

        public void refresh(SimilarityResult result) {
            body.removeAll();
            body.setBorder(new EmptyBorder(32, 40, 32, 40));

            JLabel title = UIComponents.sectionTitle("Analytics & Charts");
            title.setAlignmentX(LEFT_ALIGNMENT);
            body.add(title);
            body.add(Box.createVerticalStrut(4));
            body.add(UIComponents.subLabel("JFreeChart visualisations of the similarity analysis"));
            body.add(Box.createVerticalStrut(28));

            List<AlgorithmResult> ars = result.getAlgorithmResults();

            // ── Row 1: Pie + Score Bar ────────────────────────────
            JPanel row1 = new JPanel(new GridLayout(1, 2, 20, 0));
            row1.setOpaque(false);
            row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));
            row1.setAlignmentX(LEFT_ALIGNMENT);
            row1.add(wrapChart(buildPieChart(ars), "Algorithm Score Distribution (Pie)"));
            row1.add(wrapChart(buildScoreBarChart(ars), "Algorithm Similarity Scores (Bar)"));
            body.add(row1);
            body.add(Box.createVerticalStrut(20));



            // ── Row 3: Final score Meter ──────────────────────────
            body.add(wrapChart(buildFinalScoreBarChart(result), "Final Weighted Score vs Individual Scores"));

            body.revalidate();
            body.repaint();
        }

        // ── Chart builders ────────────────────────────────────────

        private JFreeChart buildPieChart(List<AlgorithmResult> ars) {
            DefaultPieDataset<String> ds = new DefaultPieDataset<>();
            double[] weights = {0.20, 0.15, 0.15, 0.20, 0.15, 0.15};
            for (int i = 0; i < ars.size(); i++) {
                ds.setValue(ars.get(i).getAlgorithmName(),
                    Math.max(0.001, ars.get(i).getScore() * weights[i]));
            }
            JFreeChart chart = ChartFactory.createPieChart(null, ds, true, true, false);
            PiePlot<?> plot = (PiePlot<?>) chart.getPlot();
            styleChart(chart);
            plot.setBackgroundPaint(AppTheme.SURFACE);
            plot.setOutlineVisible(false);
            plot.setShadowPaint(null);
            for (int i = 0; i < ars.size(); i++) {
                plot.setSectionPaint(ars.get(i).getAlgorithmName(), AppTheme.ALGO_COLORS[i]);
            }
            plot.setLabelFont(AppTheme.FONT_SMALL);
            plot.setLabelBackgroundPaint(AppTheme.SURFACE2);
            plot.setLabelPaint(AppTheme.TEXT);
            return chart;
        }

        private JFreeChart buildScoreBarChart(List<AlgorithmResult> ars) {
            DefaultCategoryDataset ds = new DefaultCategoryDataset();
            for (int i = 0; i < ars.size(); i++) {
                ds.addValue(ars.get(i).getScore() * 100, "Score %", ars.get(i).getAlgorithmName());
            }
            JFreeChart chart = ChartFactory.createBarChart(
                null, "Algorithm", "Similarity %", ds,
                PlotOrientation.VERTICAL, false, true, false);
            styleBarChart(chart, ds, false);
            return chart;
        }

        private JFreeChart buildTimeBarChart(List<AlgorithmResult> ars) {
            DefaultCategoryDataset ds = new DefaultCategoryDataset();
            for (AlgorithmResult ar : ars) {
                ds.addValue(ar.getExecutionTimeMs(), "Time (ms)", ar.getAlgorithmName());
            }
            JFreeChart chart = ChartFactory.createBarChart(
                null, "Algorithm", "Time (ms)", ds,
                PlotOrientation.VERTICAL, false, true, false);
            styleBarChart(chart, ds, true);
            return chart;
        }

        private JFreeChart buildWordFreqChart(SimilarityResult result) {
            DefaultCategoryDataset ds = new DefaultCategoryDataset();
            int n = 0;
            if (result.getTopKeywordsDoc1() != null) {
                for (Map.Entry<String,Integer> e : result.getTopKeywordsDoc1()) {
                    if (n++ >= 10) break;
                    ds.addValue(e.getValue(), result.getDoc1().getFileName(), e.getKey());
                }
            }
            n = 0;
            if (result.getTopKeywordsDoc2() != null) {
                for (Map.Entry<String,Integer> e : result.getTopKeywordsDoc2()) {
                    if (n++ >= 10) break;
                    ds.addValue(e.getValue(), result.getDoc2().getFileName(), e.getKey());
                }
            }
            JFreeChart chart = ChartFactory.createLineChart(
                null, "Keyword", "Frequency", ds,
                PlotOrientation.VERTICAL, true, true, false);
            styleChart(chart);
            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(AppTheme.SURFACE);
            plot.setDomainGridlinePaint(AppTheme.BORDER);
            plot.setRangeGridlinePaint(AppTheme.BORDER);
            LineAndShapeRenderer r = (LineAndShapeRenderer) plot.getRenderer();
            r.setDefaultPaint(AppTheme.ACCENT);
            r.setSeriesPaint(0, AppTheme.ACCENT);
            r.setSeriesPaint(1, AppTheme.VIOLET);
            r.setDefaultShapesVisible(true);
            styleAxes(plot);
            return chart;
        }

        private JFreeChart buildFinalScoreBarChart(SimilarityResult result) {
            DefaultCategoryDataset ds = new DefaultCategoryDataset();
            List<AlgorithmResult> ars = result.getAlgorithmResults();
            for (AlgorithmResult ar : ars) {
                ds.addValue(ar.getScore() * 100, "Algorithms", ar.getAlgorithmName());
            }
            ds.addValue(result.getFinalScore() * 100, "Algorithms", "★ Final");

            JFreeChart chart = ChartFactory.createBarChart(
                null, null, "Score %", ds,
                PlotOrientation.HORIZONTAL, false, true, false);
            styleBarChart(chart, ds, false);
            // Highlight final bar in amber
            CategoryPlot plot = chart.getCategoryPlot();
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            int finalIdx = ds.getColumnCount() - 1;
            renderer.setSeriesPaint(0, AppTheme.ACCENT);
            // JFreeChart 1.5 uses item paint per column via custom renderer
            return chart;
        }

        // ── Style helpers ─────────────────────────────────────────

        private void styleChart(JFreeChart chart) {
            chart.setBackgroundPaint(AppTheme.SURFACE);
            chart.setBorderVisible(false);
            if (chart.getLegend() != null) {
                chart.getLegend().setBackgroundPaint(AppTheme.SURFACE);
                chart.getLegend().setItemPaint(AppTheme.TEXT);
                chart.getLegend().setItemFont(AppTheme.FONT_SMALL);
            }
        }

        private void styleBarChart(JFreeChart chart, DefaultCategoryDataset ds, boolean timeMode) {
            styleChart(chart);
            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(AppTheme.SURFACE);
            plot.setDomainGridlinePaint(AppTheme.BORDER);
            plot.setRangeGridlinePaint(AppTheme.BORDER);
            plot.setOutlineVisible(false);

            BarRenderer r = (BarRenderer) plot.getRenderer();
            r.setShadowVisible(false);
            r.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
            r.setDrawBarOutline(false);

            // Assign colours per algorithm
            for (int i = 0; i < Math.min(ds.getColumnCount(), AppTheme.ALGO_COLORS.length); i++) {
                r.setSeriesPaint(0, AppTheme.ACCENT);  // single series → column colours
            }
            // Use gradient per column
            for (int i = 0; i < ds.getColumnCount(); i++) {
                int ci = i % AppTheme.ALGO_COLORS.length;
                r.setSeriesPaint(0, AppTheme.ALGO_COLORS[0]);
            }
            r.setSeriesPaint(0, AppTheme.ACCENT);

            styleAxes(plot);
        }

        private void styleAxes(CategoryPlot plot) {
            CategoryAxis da = plot.getDomainAxis();
            da.setTickLabelFont(AppTheme.FONT_SMALL);
            da.setTickLabelPaint(AppTheme.TEXT_DIM);
            da.setAxisLinePaint(AppTheme.BORDER);
            da.setLabelPaint(AppTheme.TEXT_DIM);
            da.setLabelFont(AppTheme.FONT_SMALL);

            NumberAxis ra = (NumberAxis) plot.getRangeAxis();
            ra.setTickLabelFont(AppTheme.FONT_SMALL);
            ra.setTickLabelPaint(AppTheme.TEXT_DIM);
            ra.setAxisLinePaint(AppTheme.BORDER);
            ra.setLabelPaint(AppTheme.TEXT_DIM);
            ra.setLabelFont(AppTheme.FONT_SMALL);
        }

        /** Wrap a chart in a titled rounded card. */
        private JPanel wrapChart(JFreeChart chart, String label) {
            JPanel card = UIComponents.roundedPanel(AppTheme.RADIUS, AppTheme.SURFACE);
            card.setLayout(new BorderLayout(0, 8));
            card.setBorder(new EmptyBorder(14, 14, 14, 14));

            JLabel lbl = new JLabel(label);
            lbl.setFont(AppTheme.FONT_H3);
            lbl.setForeground(AppTheme.TEXT_DIM);

            ChartPanel cp = new ChartPanel(chart);
            cp.setOpaque(false);
            cp.setBackground(AppTheme.SURFACE);
            cp.setPreferredSize(new Dimension(400, 260));

            card.add(lbl, BorderLayout.NORTH);
            card.add(cp, BorderLayout.CENTER);
            return card;
        }
    }
