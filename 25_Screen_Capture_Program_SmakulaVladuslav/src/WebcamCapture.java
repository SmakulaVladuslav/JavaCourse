/* File WebcamCapture.java
Realization of main class WebcamCapture and internal functions createDevicesList(), setupListeners(), captureFrame(ActionEvent e)
startCamera(String cameraName)
Done by Smakula Vladuslav (group: computer methematics 1)
Date 10.12.2024
*/

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * A webcam capture application using Swing for GUI and Sarxos's Webcam Capture API for webcam interaction.
 */
public class WebcamCapture {

    private List<Webcam> availableDevices;
    private String currentRunningCameraName;
    private Webcam currentWebcam;
    private JFrame frame;
    private JLabel videoLabel;
    private JComboBox<String> cameraChoiceComboBox;
    private JButton captureButton;

    /**
     * Constructor that initializes the webcam capture application UI and sets up listeners.
     */
    public WebcamCapture() {
        frame = new JFrame("Webcam Capture");
        videoLabel = new JLabel();
        cameraChoiceComboBox = new JComboBox<>();
        captureButton = new JButton("Capture Frame");

        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Select Camera: "));
        topPanel.add(cameraChoiceComboBox);
        topPanel.add(captureButton);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(videoLabel, BorderLayout.CENTER);
        frame.setVisible(true);

        createDevicesList();
        setupListeners();
    }

    /**
     * Detects available webcam devices and populates the camera selection combo box.
     */
    private void createDevicesList() {
        availableDevices = Webcam.getWebcams();
        if (availableDevices.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No webcams detected!", "Error", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
            return;
        }

        for (Webcam device : availableDevices) {
            cameraChoiceComboBox.addItem(device.getName());
        }
    }

    /**
     * Sets up event listeners for camera selection and frame capture actions.
     */
    private void setupListeners() {
        cameraChoiceComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = cameraChoiceComboBox.getSelectedItem().toString();
                int selectedIndex = cameraChoiceComboBox.getSelectedIndex();
                if (selectedIndex != -1 && !selectedItem.equals(currentRunningCameraName)) {
                    currentRunningCameraName = selectedItem;
                    startCamera(currentRunningCameraName);
                }
            }
        });
        captureButton.addActionListener(this::captureFrame);
    }

    /**
     * Captures a single frame from the currently active webcam and opens a new window to display it.
     *
     * @param e The action event triggered by pressing the capture button.
     */
    private void captureFrame(ActionEvent e) {
        if (currentWebcam != null && currentWebcam.isOpen()) {
            BufferedImage image = currentWebcam.getImage();
            if (image != null) {
                SwingUtilities.invokeLater(() -> new EmailCreation(image));
            } else {
                JOptionPane.showMessageDialog(frame, "No frame captured!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "No webcam active!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Starts the webcam stream for the specified camera.
     *
     * @param cameraName The name of the webcam to activate.
     */
    private void startCamera(String cameraName) {
        if (currentWebcam != null && currentWebcam.isOpen()) {
            currentWebcam.close();
        }

        for (Webcam webcam : availableDevices) {
            if (webcam.getName().equals(cameraName)) {
                currentWebcam = webcam;
                break;
            }
        }

        if (currentWebcam != null) {
            currentWebcam.setViewSize(WebcamResolution.VGA.getSize());
            currentWebcam.open();

            new Thread(() -> {
                while (currentWebcam.isOpen() && cameraName.equals(currentRunningCameraName)) {
                    BufferedImage image = currentWebcam.getImage();
                    if (image != null) {
                        ImageIcon icon = new ImageIcon(image);
                        videoLabel.setIcon(icon);
                    }

                    try {
                        Thread.sleep(30); // Limit frame rate to ~30 FPS
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * Entry point for the webcam capture application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(WebcamCapture::new);
    }
}
