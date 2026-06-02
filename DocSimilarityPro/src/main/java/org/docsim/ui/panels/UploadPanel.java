package org.docsim.ui.panels;

import org.docsim.AppTheme;
import org.docsim.extractor.FileExtractor;
import org.docsim.model.Document;
import org.docsim.model.SimilarityResult;
import org.docsim.preprocessing.TextPreprocessor;
import org.docsim.ui.MainWindow;
import org.docsim.ui.components.UIComponents;
import org.docsim.utils.SimilarityEngine;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Upload & Analyse Panel.
 *
 * Allows the user to add 2+ documents via file chooser or drag-and-drop,
 * previews metadata, then triggers analysis on a background thread.
 */
public class UploadPanel extends JPanel {

    private final MainWindow app;

    // Drop-zone cards (always show exactly 2 for pairwise comparison)
    private final DropZoneCard zone1 = new DropZoneCard("Document A");
    private final DropZoneCard zone2 = new DropZoneCard("Document B");

    private final JLabel statusLabel = new JLabel(" ");
    private final JProgressBar progressBar = new JProgressBar();
    private       JButton analyseBtn;

    public UploadPanel(MainWindow app) {
        this.app = app;
        setBackground(AppTheme.BG);
        setLayout(new BorderLayout());
        build();
    }

    private void build() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(32, 40, 32, 40));

        // Title
        JLabel title = UIComponents.sectionTitle("Upload Documents");
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = UIComponents.subLabel("Supports  TXT · PDF · DOCX   ·   Drag & Drop or click to browse");
        sub.setAlignmentX(LEFT_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(4));
        content.add(sub);
        content.add(Box.createVerticalStrut(28));

        // Drop zones row
        JPanel zones = new JPanel(new GridLayout(1, 2, 20, 0));
        zones.setOpaque(false);
        zones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        zones.setAlignmentX(LEFT_ALIGNMENT);
        zones.add(zone1);
        zones.add(zone2);
        content.add(zones);
        content.add(Box.createVerticalStrut(28));

        // Progress bar
        progressBar.setMaximum(100);
        progressBar.setStringPainted(true);
        progressBar.setString("Ready");
        progressBar.setForeground(AppTheme.ACCENT);
        progressBar.setBackground(AppTheme.SURFACE);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(0, 22));
        progressBar.setAlignmentX(LEFT_ALIGNMENT);
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        content.add(progressBar);
        content.add(Box.createVerticalStrut(10));

        // Status
        statusLabel.setFont(AppTheme.FONT_SMALL);
        statusLabel.setForeground(AppTheme.TEXT_DIM);
        statusLabel.setAlignmentX(LEFT_ALIGNMENT);
        content.add(statusLabel);
        content.add(Box.createVerticalStrut(20));

        // Analyse button
        analyseBtn = UIComponents.gradientButton(
            "  ⚙   Run Similarity Analysis  ",
            new Color(0x1F6FEB), AppTheme.ACCENT);
        analyseBtn.setPreferredSize(new Dimension(240, 44));
        analyseBtn.setMaximumSize(new Dimension(260, 44));
        analyseBtn.setAlignmentX(LEFT_ALIGNMENT);
        analyseBtn.setEnabled(false);
        analyseBtn.addActionListener(e -> runAnalysis());
        content.add(analyseBtn);

        JScrollPane scroll = UIComponents.darkScroll(content);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    /** Checks whether both zones have a file and enables/disables the button. */
    private void updateButton() {
        analyseBtn.setEnabled(zone1.getFile() != null && zone2.getFile() != null);
    }

    /** Run the heavy work on a SwingWorker thread. */
    private void runAnalysis() {
        File f1 = zone1.getFile();
        File f2 = zone2.getFile();
        if (f1 == null || f2 == null) return;

        analyseBtn.setEnabled(false);
        progressBar.setValue(0);
        progressBar.setString("Starting…");

        SwingWorker<SimilarityResult, String> worker = new SwingWorker<>() {
            @Override
            protected SimilarityResult doInBackground() throws Exception {
                publish("Extracting text from documents…"); setProgress(10);

                Document doc1 = FileExtractor.load(f1);
                Document doc2 = FileExtractor.load(f2);

                publish("Preprocessing — cleaning & tokenising…"); setProgress(30);
                TextPreprocessor.process(doc1);
                TextPreprocessor.process(doc2);

                publish("Running 6 similarity algorithms…"); setProgress(55);
                SimilarityResult result = SimilarityEngine.analyse(doc1, doc2);

                publish("Finalising results…"); setProgress(90);
                return result;
            }

            @Override
            protected void process(List<String> chunks) {
                String msg = chunks.get(chunks.size() - 1);
                statusLabel.setText(msg);
                progressBar.setString(msg);
            }

            @Override
            protected void done() {
                try {
                    SimilarityResult result = get();
                    progressBar.setValue(100);
                    progressBar.setString("Analysis complete!");
                    statusLabel.setText("Done in " + result.getTotalTimeMs() + " ms");
                    app.onAnalysisComplete(result);
                } catch (Exception ex) {
                    progressBar.setString("Error");
                    statusLabel.setText("Error: " + ex.getCause().getMessage());
                    JOptionPane.showMessageDialog(UploadPanel.this,
                        "Analysis failed:\n" + ex.getCause().getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    analyseBtn.setEnabled(true);
                }
            }
        };

        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                progressBar.setValue((Integer) evt.getNewValue());
            }
        });

        worker.execute();
    }

    // ── Inner class: DropZoneCard ─────────────────────────────────────────────
    private class DropZoneCard extends JPanel {

        private final String title;
        private File file = null;
        private boolean dragOver = false;

        // Info labels inside the card (shown after file is loaded)
        private final JLabel nameLabel  = styled(null);
        private final JLabel sizeLabel  = styled(null);
        private final JLabel typeLabel  = styled(null);
        private final JLabel tokenLabel = styled(null);
        private final JLabel promptLabel;

        DropZoneCard(String title) {
            this.title = title;
            promptLabel = styled("Click or drop a file here");

            setOpaque(false);
            setLayout(new BorderLayout(0, 0));
            setPreferredSize(new Dimension(380, 260));

            // Drag-and-drop
            new DropTarget(this, new DropTargetAdapter() {
                public void dragEnter(DropTargetDragEvent e) { dragOver = true; repaint(); }
                public void dragExit (DropTargetEvent   e) { dragOver = false; repaint(); }
                public void drop     (DropTargetDropEvent e) {
                    dragOver = false;
                    e.acceptDrop(DnDConstants.ACTION_COPY);
                    try {
                        @SuppressWarnings("unchecked")
                        List<File> files = (List<File>) e.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);
                        if (!files.isEmpty()) setFile(files.get(0));
                    } catch (Exception ex) { ex.printStackTrace(); }
                    repaint();
                }
            });

            // Click to open file chooser
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { chooseFile(); }
                public void mouseEntered(MouseEvent e) { setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
            });

            buildUI();
        }

        private JLabel styled(String text) {
            JLabel l = text == null ? new JLabel() : new JLabel(text);
            l.setFont(AppTheme.FONT_SMALL);
            l.setForeground(AppTheme.TEXT_DIM);
            return l;
        }

        private void buildUI() {
            JPanel inner = new JPanel();
            inner.setOpaque(false);
            inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
            inner.setBorder(new EmptyBorder(24, 20, 24, 20));

            JLabel titleLbl = new JLabel(title);
            titleLbl.setFont(AppTheme.FONT_H3);
            titleLbl.setForeground(AppTheme.ACCENT);
            titleLbl.setAlignmentX(CENTER_ALIGNMENT);

            promptLabel.setAlignmentX(CENTER_ALIGNMENT);
            nameLabel.setAlignmentX(CENTER_ALIGNMENT);
            sizeLabel.setAlignmentX(CENTER_ALIGNMENT);
            typeLabel.setAlignmentX(CENTER_ALIGNMENT);
            tokenLabel.setAlignmentX(CENTER_ALIGNMENT);

            nameLabel.setFont(AppTheme.FONT_H3);
            nameLabel.setForeground(AppTheme.TEXT);

            inner.add(titleLbl);
            inner.add(Box.createVerticalStrut(20));
            inner.add(promptLabel);
            inner.add(Box.createVerticalStrut(12));
            inner.add(nameLabel);
            inner.add(Box.createVerticalStrut(4));
            inner.add(sizeLabel);
            inner.add(Box.createVerticalStrut(2));
            inner.add(typeLabel);
            inner.add(Box.createVerticalStrut(2));
            inner.add(tokenLabel);

            add(inner, BorderLayout.CENTER);
        }

        void setFile(File f) {
            if (f == null || !FileExtractor.isSupported(f)) {
                JOptionPane.showMessageDialog(this,
                    "Unsupported file type.\nPlease upload a TXT, PDF, or DOCX file.",
                    "Invalid File", JOptionPane.WARNING_MESSAGE);
                return;
            }
            this.file = f;
            promptLabel.setText("✔  File loaded");
            promptLabel.setForeground(AppTheme.GREEN);
            nameLabel.setText(f.getName());

            long kb = f.length() / 1024;
            sizeLabel.setText("Size: " + (kb < 1 ? "< 1" : kb) + " KB");
            typeLabel.setText("Type: " + FileExtractor.extension(f.getName()).toUpperCase());
            tokenLabel.setText(" ");

            repaint();
            updateButton();
        }

        void chooseFile() {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Documents (TXT, PDF, DOCX)", "txt","pdf","docx"));
            fc.setDialogTitle("Select " + title);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                setFile(fc.getSelectedFile());
            }
        }

        public File getFile() { return file; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Card background
            g2.setColor(dragOver ? new Color(31, 111, 235, 30) : AppTheme.SURFACE);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));

            // Border (dashed when empty, solid accent when has file)
            if (file != null) {
                g2.setColor(AppTheme.GREEN);
                g2.setStroke(new BasicStroke(1.5f));
            } else {
                float[] dash = {6f, 4f};
                g2.setColor(dragOver ? AppTheme.ACCENT : AppTheme.BORDER);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 10f, dash, 0f));
            }
            g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, 12, 12));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
