/* File LoginPopup.java
Realization of LoginPopup class and internal functions promptForCredentials(JFrame emailFrame)
Done by Smakula Vladuslav (group: computer methematics 1)
Date 10.12.2024
*/
import javax.swing.*;
import java.awt.*;

/**
 * A class to prompt the user for email credentials using a popup dialog.
 */
public class LoginPopup {

    /**
     * Displays a popup dialog for the user to enter email credentials.
     *
     * @param emailFrame The parent frame for the dialog.
     * @return An array containing the entered email login and password, or null if canceled.
     */
    static public String[] promptForCredentials(JFrame emailFrame) {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel loginLabel = new JLabel("Email Login:");
        JTextField loginField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        panel.add(loginLabel);
        panel.add(loginField);
        panel.add(passwordLabel);
        panel.add(passwordField);

        int option = JOptionPane.showConfirmDialog(
                emailFrame,
                panel,
                "Enter email credentials",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            String login = loginField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (login.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                        emailFrame,
                        "Both fields are required!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return null;
            }

            return new String[]{login, password};
        }

        return null; // User canceled
    }
}
