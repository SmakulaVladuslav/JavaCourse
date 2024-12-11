/* File EmailCreation.java
Realization of EmailCreation class and internal functions
sendEmail(String receiver, String subject, String body, BufferedImage attachment)
resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) readUserEmailData()
Done by Smakula Vladuslav (group: computer methematics 1)
Date 10.12.2024
*/
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.mail.MessagingException;

/**
 * A class to create and send emails with an attached image captured from a webcam.
 */
public class EmailCreation {

    private JFrame emailFrame;
    private EmailSender sendingService;

    /**
     * Constructor to initialize the email composer UI with the captured frame.
     *
     * @param frame The captured frame image to attach to the email.
     */
    public EmailCreation(BufferedImage frame) {
        readUserEmailData();
        emailFrame = new JFrame("Compose Email");
        emailFrame.setSize(600, 400);
        emailFrame.setLayout(new BorderLayout());
        emailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Input panel for email fields
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField emailReceiver = new JTextField();
        JTextField emailHeaderField = new JTextField();
        JTextArea emailBodyArea = new JTextArea();
        emailBodyArea.setLineWrap(true);
        emailBodyArea.setWrapStyleWord(true);

        PlaceholderHelper.setPlaceholder(emailReceiver, "Recipient Email");
        PlaceholderHelper.setPlaceholder(emailHeaderField, "Subject");
        PlaceholderHelper.setPlaceholder(emailBodyArea, "Type your email here...");

        inputPanel.add(emailReceiver, BorderLayout.NORTH);
        JPanel emailDetailsPanel = new JPanel(new BorderLayout());
        emailDetailsPanel.add(emailHeaderField, BorderLayout.NORTH);
        emailDetailsPanel.add(new JScrollPane(emailBodyArea), BorderLayout.CENTER);
        inputPanel.add(emailDetailsPanel, BorderLayout.CENTER);

        // Bottom panel with captured image and send button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        BufferedImage resizedImage = resizeImage(frame, 200, 150);
        JLabel imageLabel = new JLabel(new ImageIcon(resizedImage));
        imageLabel.setPreferredSize(new Dimension(200, 150));
        bottomPanel.add(imageLabel, BorderLayout.WEST);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(event -> {
            String receiver = emailReceiver.getText();
            String subject = emailHeaderField.getText();
            String body = emailBodyArea.getText();
            sendEmail(receiver, subject, body, frame);
            emailFrame.dispose();
        });
        bottomPanel.add(sendButton, BorderLayout.EAST);

        emailFrame.add(inputPanel, BorderLayout.CENTER);
        emailFrame.add(bottomPanel, BorderLayout.SOUTH);
        emailFrame.setVisible(true);
    }

    /**
     * Sends an email with the specified details and the image attachment.
     *
     * @param receiver   The recipient's email address.
     * @param subject    The subject of the email.
     * @param body       The body content of the email.
     * @param attachment The image attachment.
     */
    private void sendEmail(String receiver, String subject, String body, BufferedImage attachment) {
        try {
            File tempAttachment = File.createTempFile("attachment", ".png");
            ImageIO.write(attachment, "png", tempAttachment);
            sendingService.sendEmail(receiver, subject, body, tempAttachment);
            JOptionPane.showMessageDialog(
                    emailFrame,
                    "Email sent to " + receiver + "!\nSubject: " + subject + "\nBody: " + body,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
            tempAttachment.deleteOnExit();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    emailFrame,
                    "Failed to save the attachment: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (MessagingException e) {
            JOptionPane.showMessageDialog(
                    emailFrame,
                    "Failed to send email: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Resizes an image to the specified dimensions.
     *
     * @param originalImage The original image to resize.
     * @param targetWidth   The desired width of the resized image.
     * @param targetHeight  The desired height of the resized image.
     * @return The resized image.
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return resizedImage;
    }

    /**
     * Reads user email data and initializes the email sending service.
     * Prompts for credentials if no data is found.
     */
    private void readUserEmailData() {
        try {
            sendingService = new EmailSender("email_login_data.data");
        } catch (Exception e) {
            String[] credentials = LoginPopup.promptForCredentials(emailFrame);
            if (credentials == null) {
                return;
            }

            String login = credentials[0];
            String password = credentials[1];

            try {
                EmailSender.saveCredentials("email_login_data.data", login, password);
                sendingService = new EmailSender("email_login_data.data");
            } catch (Exception ee) {
                JOptionPane.showMessageDialog(
                        emailFrame,
                        "Failed to save credentials: " + ee.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
