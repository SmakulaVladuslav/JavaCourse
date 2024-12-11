/* File PlaceholderHelper.java
Realization of PlaceholderHelper class and internal functions setPlaceholder(JComponent component, String placeholderText)
configureTextFieldPlaceholder(JTextField textField, String placeholderText)
configureTextAreaPlaceholder(JTextArea textArea, String placeholderText)
Done by Smakula Vladuslav (group: computer methematics 1)
Date 10.12.2024
*/
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * A utility class to add placeholder functionality to text components.
 */
public class PlaceholderHelper {

    /**
     * Sets a placeholder text for the specified component.
     *
     * @param component The text component to which the placeholder will be added.
     * @param placeholderText The placeholder text to display.
     */
    public static void setPlaceholder(JComponent component, String placeholderText) {
        if (component instanceof JTextField) {
            configureTextFieldPlaceholder((JTextField) component, placeholderText);
        } else if (component instanceof JTextArea) {
            configureTextAreaPlaceholder((JTextArea) component, placeholderText);
        }
    }

    /**
     * Configures a placeholder for a JTextField.
     *
     * @param textField The JTextField to configure.
     * @param placeholderText The placeholder text to display.
     */
    private static void configureTextFieldPlaceholder(JTextField textField, String placeholderText) {
        textField.setForeground(Color.GRAY);
        textField.setText(placeholderText);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholderText)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholderText);
                }
            }
        });
    }

    /**
     * Configures a placeholder for a JTextArea.
     *
     * @param textArea The JTextArea to configure.
     * @param placeholderText The placeholder text to display.
     */
    private static void configureTextAreaPlaceholder(JTextArea textArea, String placeholderText) {
        textArea.setForeground(Color.GRAY);
        textArea.setText(placeholderText);

        textArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textArea.getText().equals(placeholderText)) {
                    textArea.setText("");
                    textArea.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textArea.getText().isEmpty()) {
                    textArea.setForeground(Color.GRAY);
                    textArea.setText(placeholderText);
                }
            }
        });
    }
}
