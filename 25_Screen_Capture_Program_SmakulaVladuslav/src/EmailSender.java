/* File EmailSender.java
Realization of EmailSender class and internal functions readCredentials(String filePath)
saveCredentials(String filePath, String login, String password) sendEmail(String receiver, String subject, String body, File attachment)
Done by Smakula Vladuslav (group: computer methematics 1)
Date 10.12.2024
*/
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.io.BufferedWriter;
import java.io.FileWriter;
import javax.crypto.SecretKey;

/**
 * A utility class to handle email sending and credential management.
 */
public class EmailSender {

    private String login;
    private String password;

    private static final String MAC_ADDRESS = AESHelper.getMACAddress();

    /**
     * Constructs an EmailSender instance using credentials from a file.
     *
     * @param credentialFilePath The file path of the credential file.
     * @throws Exception If the credentials cannot be read or decrypted.
     */
    public EmailSender(String credentialFilePath) throws Exception {
        if (MAC_ADDRESS == null) {
            throw new IOException("Failed to obtain MAC address.");
        }
        readCredentials(credentialFilePath);
    }

    /**
     * Reads and decrypts the credentials from the specified file.
     *
     * @param filePath The file path of the credential file.
     * @throws Exception If the file cannot be read or the credentials are invalid.
     */
    private void readCredentials(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            throw new IOException("Credential file not found.");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String encryptedLogin = br.readLine();
            String encryptedPassword = br.readLine();

            SecretKey key = AESHelper.deriveKeyFromMACAddress(MAC_ADDRESS);

            // Decrypt the credentials
            this.login = AESHelper.decrypt(encryptedLogin, key);
            this.password = AESHelper.decrypt(encryptedPassword, key);

            if (login.isEmpty() || password.isEmpty()) {
                throw new IOException("Credential file is invalid.");
            }
        }
    }

    /**
     * Encrypts and saves credentials to a file.
     *
     * @param filePath The file path where credentials will be saved.
     * @param login    The email login.
     * @param password The email password.
     * @throws Exception If the credentials cannot be encrypted or saved.
     */
    public static void saveCredentials(String filePath, String login, String password) throws Exception {
        SecretKey key = AESHelper.deriveKeyFromMACAddress(MAC_ADDRESS);

        // Encrypt the credentials
        String encryptedLogin = AESHelper.encrypt(login, key);
        String encryptedPassword = AESHelper.encrypt(password, key);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(encryptedLogin);
            writer.newLine();
            writer.write(encryptedPassword);
        }
    }

    /**
     * Sends an email with the specified details and attachment.
     *
     * @param receiver   The recipient's email address.
     * @param subject    The subject of the email.
     * @param body       The body content of the email.
     * @param attachment The file to attach to the email.
     * @throws MessagingException If the email cannot be sent.
     */
    public void sendEmail(String receiver, String subject, String body, File attachment) throws MessagingException {
        // Mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Authenticate
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(login, password);
            }
        });

        // Create a message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(login));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
        message.setSubject(subject);

        // Create a multipart message for the email body and attachment
        Multipart multipart = new MimeMultipart();
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body);
        multipart.addBodyPart(textPart);
        if (attachment != null && attachment.exists()) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(attachment);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(attachment.getName());
            multipart.addBodyPart(attachmentPart);
        }
        message.setContent(multipart);

        Transport.send(message);
    }
}
