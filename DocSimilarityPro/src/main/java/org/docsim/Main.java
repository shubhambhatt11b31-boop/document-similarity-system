package org.docsim;

import com.formdev.flatlaf.FlatDarkLaf;
import org.docsim.ui.MainWindow;

import javax.swing.*;


public class Main {

    public static void main(String[] args) {


        FlatDarkLaf.setup();

        
        UIManager.put("Panel.background",           AppTheme.BG);
        UIManager.put("OptionPane.background",      AppTheme.SURFACE);
        UIManager.put("ScrollPane.background",      AppTheme.BG);
        UIManager.put("Viewport.background",        AppTheme.BG);
        UIManager.put("TextArea.background",        AppTheme.SURFACE2);
        UIManager.put("TextArea.foreground",        AppTheme.TEXT);
        UIManager.put("TextArea.caretForeground",   AppTheme.ACCENT);
        UIManager.put("TextField.background",       AppTheme.SURFACE2);
        UIManager.put("TextField.foreground",       AppTheme.TEXT);
        UIManager.put("ComboBox.background",        AppTheme.SURFACE2);
        UIManager.put("Button.arc",                 8);
        UIManager.put("Component.focusWidth",       1);
        UIManager.put("TabbedPane.tabSeparatorsFullHeight", true);

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
