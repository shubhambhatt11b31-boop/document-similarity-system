package org.docsim.ui.panels;

import org.docsim.AppTheme;
import org.docsim.model.AlgorithmResult;
import org.docsim.model.SimilarityResult;
import org.docsim.ui.MainWindow;
import org.docsim.ui.components.UIComponents;
import org.docsim.utils.ReportExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Results Panel — shows the final score ring, per-algorithm score cards,
 * a document metadata comparison, common keywords, and an export button.
 */
public class ResultsPanel extends JPanel {

    private final MainWindow app;
    private final JPanel     body;

    public ResultsPanel(MainWindow app) {
        this.app = app;
        setBackground(AppTheme.BG);
        setLayout(new BorderLayout());

        body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(32, 40, 32, 40));

        JLabel placeholder = UIComponents.subLabel(
            "No analysis yet — upload documents and run analysis first.");
        placeholder.setAlignmentX(LEFT_ALIGNMENT);
        body.add(placeholder);

        JScrollPane scroll = UIComponents.darkScroll(body);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    /** Called by MainWindow after a successful analysis. */
    public void refresh(SimilarityResult result) {
        body.removeAll();
        body.setBorder(new EmptyBorder(32, 40, 32, 40));

        // ── Header ────────────────────────────────────────────
        JLabel title = UIComponents.sectionTitle("Similarity Results");
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = UIComponents.subLabel("Analysed on " + result.getTimestamp()
            + "  ·  Total time: " + result.getTotalTimeMs() + " ms");
        sub.setAlignmentX(LEFT_ALIGNMENT);
        body.add(title);
        body.add(Box.createVerticalStrut(4));
        body.add(sub);
        body.add(Box.createVerticalStrut(28));

        // ── Score ring + doc info row ─────────────────────────
        JPanel topRow = new JPanel(new BorderLayout(24, 0));
        topRow.setOpaque(false);
        topRow.setAlignmentX(LEFT_ALIGNMENT);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        topRow.add(buildScoreRingCard(result), BorderLayout.WEST);
        topRow.add(buildDocInfoCard(result),   BorderLayout.CENTER);
        topRow.add(buildVerdictCard(result),   BorderLayout.EAST);
        body.add(topRow);
        body.add(Box.createVerticalStrut(24));

        // ── Algorithm score cards ─────────────────────────────
        JLabel algoTitle = UIComponents.sectionTitle("Algorithm Breakdown");
        algoTitle.setAlignmentX(LEFT_ALIGNMENT);
        body.add(algoTitle);
        body.add(Box.createVerticalStrut(12));

        JPanel algoGrid = new JPanel(new GridLayout(2, 3, 16, 16));
        algoGrid.setOpaque(false);
        algoGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        algoGrid.setAlignmentX(LEFT_ALIGNMENT);

        List<AlgorithmResult> results = result.getAlgorithmResults();


        for (AlgorithmResult ar : results) {

            String name = ar.getAlgorithmName();

            if (
                    name.contains("Jaccard") ||
                            name.contains("Rabin") ||
                            name.contains("KMP")
            ) {

                algoGrid.add(buildAlgoCard(ar, AppTheme.ACCENT));
            }
        }
        body.add(algoGrid);
        body.add(Box.createVerticalStrut(24));

        // ── Common keywords ───────────────────────────────────
        if (result.getCommonKeywords() != null && !result.getCommonKeywords().isEmpty()) {
            JLabel kwTitle = UIComponents.sectionTitle("Common Keywords");
            kwTitle.setAlignmentX(LEFT_ALIGNMENT);
            body.add(kwTitle);
            body.add(Box.createVerticalStrut(12));
            body.add(buildKeywordCloud(result.getCommonKeywords()));
            body.add(Box.createVerticalStrut(24));
        }

        // ── Top keywords per doc ──────────────────────────────
        JLabel topKWTitle = UIComponents.sectionTitle("Top Keywords per Document");
        topKWTitle.setAlignmentX(LEFT_ALIGNMENT);
        body.add(topKWTitle);
        body.add(Box.createVerticalStrut(12));

        JPanel kwRow = new JPanel(new GridLayout(1, 2, 16, 0));
        kwRow.setOpaque(false);
        kwRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        kwRow.setAlignmentX(LEFT_ALIGNMENT);
        kwRow.add(buildTopKeywordList(
            result.getDoc1().getFileName(), result.getTopKeywordsDoc1(), AppTheme.ACCENT));
        kwRow.add(buildTopKeywordList(
            result.getDoc2().getFileName(), result.getTopKeywordsDoc2(), AppTheme.VIOLET));
        body.add(kwRow);
        body.add(Box.createVerticalStrut(28));

        // ── Export button ─────────────────────────────────────
        JButton exportBtn = UIComponents.gradientButton(
            " ⤓  Export TXT Report", AppTheme.GREEN, new Color(0x2EA043));
        exportBtn.setPreferredSize(new Dimension(200, 40));
        exportBtn.setMaximumSize(new Dimension(220, 40));
        exportBtn.setAlignmentX(LEFT_ALIGNMENT);
        exportBtn.addActionListener(e -> exportReport(result));
        body.add(exportBtn);

        body.revalidate();
        body.repaint();
    }

    // ── Builder helpers ───────────────────────────────────────

    /** Big donut-ring showing the final score. */
    private JPanel buildScoreRingCard(SimilarityResult r) {
        JPanel card = UIComponents.roundedPanel(AppTheme.RADIUS);
        card.setPreferredSize(new Dimension(190, 190));
        card.setMinimumSize(new Dimension(190, 190));
        card.setLayout(null);

        // Custom paint the ring
        JPanel ring = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth()/2, cy = getHeight()/2, rad = 62;
                float score = (float) Math.min(r.getFinalScore(), 1.0);
                // Track
                g2.setStroke(new BasicStroke(14f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(AppTheme.BORDER);
                g2.draw(new Arc2D.Double(cx-rad, cy-rad, rad*2, rad*2, 90, -360, Arc2D.OPEN));
                // Fill
                Color arcColor = score >= 0.7f ? AppTheme.RED : score >= 0.4f ? AppTheme.ORANGE : AppTheme.GREEN;
                g2.setColor(arcColor);
                g2.draw(new Arc2D.Double(cx-rad, cy-rad, rad*2, rad*2, 90, -(360*score), Arc2D.OPEN));
                // Percent text
                g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
                g2.setColor(AppTheme.TEXT);
                String pct = String.format("%.1f%%", r.getFinalScore()*100);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(pct, cx - fm.stringWidth(pct)/2, cy + fm.getAscent()/2 - 2);
                g2.dispose();
            }
        };
        ring.setOpaque(false);
        ring.setBounds(0, 0, 190, 190);

        JLabel lbl = new JLabel("Final Score", SwingConstants.CENTER);
        lbl.setFont(AppTheme.FONT_SMALL);
        lbl.setForeground(AppTheme.TEXT_DIM);
        lbl.setBounds(0, 160, 190, 20);

        card.add(ring);
        card.add(lbl);
        return card;
    }

    private JPanel buildDocInfoCard(SimilarityResult r) {
        JPanel card = UIComponents.roundedPanel(AppTheme.RADIUS);
        card.setLayout(new GridLayout(5, 2, 8, 6));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[][] rows = {
            {"",              "Document A",             "Document B"},
            {"File",          r.getDoc1().getFileName(), r.getDoc2().getFileName()},
            {"Type",          r.getDoc1().getFileType(), r.getDoc2().getFileType()},
            {"Tokens",        ""+r.getDoc1().getWordCount(), ""+r.getDoc2().getWordCount()},
            {"Unique tokens", ""+r.getDoc1().getUniqueWordCount(), ""+r.getDoc2().getUniqueWordCount()},
        };

        for (int i = 1; i < rows.length; i++) {
            String[] row = rows[i];
            JLabel key = new JLabel(row[0]);
            key.setFont(AppTheme.FONT_SMALL);
            key.setForeground(AppTheme.TEXT_DIM);

            JPanel vals = new JPanel(new GridLayout(1, 2, 4, 0));
            vals.setOpaque(false);

            for (int j = 1; j <= 2; j++) {
                JLabel v = new JLabel(row[j]);
                v.setFont(j == 1 ? AppTheme.FONT_BODY : AppTheme.FONT_BODY);
                v.setForeground(j == 1 ? AppTheme.ACCENT : AppTheme.VIOLET);
                v.setToolTipText(row[j]);
                vals.add(v);
            }
            card.add(key);
            card.add(vals);
        }
        return card;
    }

    private JPanel buildVerdictCard(SimilarityResult r) {
        JPanel card = UIComponents.roundedPanel(AppTheme.RADIUS);
        card.setPreferredSize(new Dimension(160, 190));
        card.setMinimumSize(new Dimension(140, 190));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(24, 16, 24, 16));

        double s = r.getFinalScore();
        Color verdColor = s >= 0.7 ? AppTheme.RED : s >= 0.4 ? AppTheme.ORANGE : AppTheme.GREEN;

        JLabel icon = new JLabel(s >= 0.7 ? "⚠" : s >= 0.4 ? "≈" : "✓");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        icon.setForeground(verdColor);
        icon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel verdict = new JLabel("<html><center>" + r.getVerdict().replace(" ", "<br>") + "</center></html>");
        verdict.setFont(AppTheme.FONT_H3);
        verdict.setForeground(verdColor);
        verdict.setAlignmentX(CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(icon);
        card.add(Box.createVerticalStrut(10));
        card.add(verdict);
        card.add(Box.createVerticalGlue());
        return card;
    }

    private JPanel buildAlgoCard(AlgorithmResult ar, Color accent) {
        JPanel card = UIComponents.roundedPanel(AppTheme.RADIUS, AppTheme.SURFACE2);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Name + score
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel name = new JLabel(ar.getAlgorithmName());
        name.setFont(AppTheme.FONT_H3);
        name.setForeground(accent);

        JLabel score = new JLabel(ar.getScorePercent());
        score.setFont(new Font("Segoe UI", Font.BOLD, 18));
        score.setForeground(AppTheme.TEXT);
        score.setHorizontalAlignment(SwingConstants.RIGHT);

        top.add(name, BorderLayout.WEST);
        top.add(score, BorderLayout.EAST);

        // Progress bar
        JPanel bar = UIComponents.scoreBar(ar.getScore(), accent);
        bar.setPreferredSize(new Dimension(0, 8));

        // Metadata
        // Metadata
        JPanel meta = new JPanel(new GridLayout(1, 1));
        meta.setOpaque(false);




        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(bar);
        center.add(Box.createVerticalStrut(8));
        center.add(meta);



        card.add(top,    BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildKeywordCloud(List<String> words) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        for (String w : words) {
            JLabel chip = new JLabel(w) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(31, 111, 235, 30));
                    g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),8,8));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            chip.setBorder(new EmptyBorder(4, 10, 4, 10));
            chip.setFont(AppTheme.FONT_SMALL);
            chip.setForeground(AppTheme.ACCENT);
            chip.setOpaque(false);
            p.add(chip);
        }
        return p;
    }

    private JPanel buildTopKeywordList(String docName,
                                        List<Map.Entry<String,Integer>> kws, Color accent) {
        JPanel card = UIComponents.roundedPanel(AppTheme.RADIUS, AppTheme.SURFACE2);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel title = new JLabel(docName);
        title.setFont(AppTheme.FONT_H3);
        title.setForeground(accent);
        card.add(title, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        if (kws != null) {
            for (Map.Entry<String,Integer> e : kws) {
                JPanel row = new JPanel(new BorderLayout());
                row.setOpaque(false);
                JLabel wrd = new JLabel(e.getKey());
                wrd.setFont(AppTheme.FONT_BODY);
                wrd.setForeground(AppTheme.TEXT);
                JLabel cnt = new JLabel(e.getValue() + "×");
                cnt.setFont(AppTheme.FONT_SMALL);
                cnt.setForeground(AppTheme.TEXT_DIM);
                row.add(wrd, BorderLayout.WEST);
                row.add(cnt, BorderLayout.EAST);
                list.add(row);
            }
        }
        card.add(UIComponents.darkScroll(list), BorderLayout.CENTER);
        return card;
    }

    private JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_SMALL);
        l.setForeground(AppTheme.TEXT_DIM);
        return l;
    }

    private void exportReport(SimilarityResult result) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Report As…");
        fc.setSelectedFile(new File("similarity_report.txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ReportExporter.exportTXT(result, fc.getSelectedFile());
                JOptionPane.showMessageDialog(this,
                    "Report saved to:\n" + fc.getSelectedFile().getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Export failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
