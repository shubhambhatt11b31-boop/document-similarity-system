package org.docsim.ui.panels;

import org.docsim.AppTheme;
import org.docsim.ui.MainWindow;
import org.docsim.ui.components.UIComponents;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HomePanel extends JPanel {

    public HomePanel(MainWindow app) {

        setBackground(AppTheme.BG);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(100, 60, 60, 60));

        JLabel title = new JLabel("Document Similarity Detection System");

        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(AppTheme.TEXT);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel desc = new JLabel(
                "<html><center>" +
                        "Upload two documents and compare their similarity instantly.<br><br>" +
                        "Supports TXT, PDF and DOCX files with fast analysis and visual results." +
                        "</center></html>"
        );

        desc.setFont(AppTheme.FONT_BODY);
        desc.setForeground(AppTheme.TEXT_DIM);
        desc.setAlignmentX(CENTER_ALIGNMENT);

        JButton uploadBtn = UIComponents.gradientButton(
                "Upload Documents",
                AppTheme.ACCENT,
                AppTheme.ACCENT
        );

        uploadBtn.setAlignmentX(CENTER_ALIGNMENT);
        uploadBtn.setPreferredSize(new Dimension(220, 46));
        uploadBtn.setMaximumSize(new Dimension(220, 46));

        uploadBtn.addActionListener(e -> {

            Window window =
                    SwingUtilities.getWindowAncestor(this);

            if (window instanceof MainWindow mw) {
                mw.navigate("upload");
            }
        });

        content.add(Box.createVerticalGlue());

        content.add(title);

        content.add(Box.createVerticalStrut(20));

        content.add(desc);

        content.add(Box.createVerticalStrut(40));

        content.add(uploadBtn);

        content.add(Box.createVerticalGlue());

        add(content, BorderLayout.CENTER);
    }
}