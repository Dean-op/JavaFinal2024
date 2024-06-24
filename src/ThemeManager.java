//ThemeManager类

import javax.swing.*;
import java.awt.*;

public class ThemeManager {

    public void applyDarkTheme(JTextArea textArea, JEditorPane previewPane) {
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        previewPane.setBackground(Color.BLACK);
        previewPane.setForeground(Color.WHITE);
    }

    public void applyLightTheme(JTextArea textArea, JEditorPane previewPane) {
        textArea.setBackground(Color.WHITE);
        textArea.setForeground(Color.BLACK);
        previewPane.setBackground(Color.WHITE);
        previewPane.setForeground(Color.BLACK);
    }
}
