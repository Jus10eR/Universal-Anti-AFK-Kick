// Import necessary packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Random;

// Class representing the Universal Anti-AFK application
public class UniversalAntiAFK {
    private JFrame frame;
    private JButton startButton;
    private JButton stopButton;
    private DefaultListModel<String> logListModel;
    private JList<String> logList;
    private JScrollPane logScrollPane;
    private Thread afkThread;

    // Constructor for the UniversalAntiAFK class
    public UniversalAntiAFK() {
        // Initialize the frame and set its properties
        frame = new JFrame("Universal Anti-AFK");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the look and feel for the UI
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and customize startButton and stopButton
        startButton = createStyledButton("Start");
        stopButton = createStyledButton("Stop");
        stopButton.setEnabled(false);
        stopButton.setBackground(new Color(150, 150, 150));

        // Create and customize the log list
        logListModel = new DefaultListModel<>();
        logList = new JList<>(logListModel);
        logList.setFont(new Font("Arial", Font.PLAIN, 12));
        logList.setBackground(Color.DARK_GRAY); 
        logList.setForeground(Color.WHITE); 
        logScrollPane = new JScrollPane(logList);
        logScrollPane.setPreferredSize(new Dimension(380, 180));
        logScrollPane.setBackground(Color.DARK_GRAY); 

        // Add ActionListener for the startButton
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Disable startButton, enable stopButton, and start the AntiAFK thread
                startButton.setEnabled(false);
                startButton.setBackground(new Color(150, 150, 150));
                stopButton.setEnabled(true);
                stopButton.setBackground(new Color(50, 50, 50));
                if (afkThread == null || !afkThread.isAlive()) {
                    afkThread = new Thread(new AntiAFKRunnable());
                    afkThread.start();
                }
            }
        });

        // Add ActionListener for the stopButton
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Disable stopButton, enable startButton, and interrupt the AntiAFK thread
                stopButton.setEnabled(false);
                stopButton.setBackground(new Color(150, 150, 150));
                startButton.setEnabled(true);
                startButton.setBackground(new Color(50, 50, 50));
                if (afkThread != null && afkThread.isAlive()) {
                    afkThread.interrupt();
                }
            }
        });

        // Create panels for buttons and log display
        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        panel.add(startButton);
        panel.add(stopButton);

        JPanel logPanel = new JPanel();
        logPanel.setBackground(Color.DARK_GRAY);
        logPanel.add(logScrollPane);

        // Set layout and add panels to the frame
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.NORTH);
        frame.add(logPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Method to create and customize styled buttons
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(50, 50, 50));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        return button;
    }

    // Main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UniversalAntiAFK();
            }
        });
    }

    // Inner class representing the AntiAFKRunnable
    private class AntiAFKRunnable implements Runnable {
        private Random random = new Random();
        private Robot robot;

        // Constructor for AntiAFKRunnable
        public AntiAFKRunnable() {
            try {
                robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }

        // Run method to simulate anti-AFK actions
        public void run() {
            try {
                logMessage("Anti-AFK started.");
                while (!Thread.interrupted()) {
                    // Generate random interval for key press
                    int randomInterval = random.nextInt(9000) + 1000;
                    Thread.sleep(randomInterval);

                    // Define array of keys and select a random key code
                    int[] keys = {KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_CONTROL};
                    int randomKeyCode = keys[random.nextInt(keys.length)];

                    // Generate random duration for key press
                    int pressDuration = random.nextInt(150) + 50;

                    // Simulate key press and log the action
                    robot.keyPress(randomKeyCode);
                    logMessage("Pressed random key: " + KeyEvent.getKeyText(randomKeyCode));

                    // Pause for the specified duration and release the key
                    Thread.sleep(pressDuration);
                    robot.keyRelease(randomKeyCode);
                }
            } catch (InterruptedException e) {
                // Log message when Anti-AFK is stopped
                logMessage("Anti-AFK stopped.");
            }
        }

        // Method to log messages in the UI
        private void logMessage(String message) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // Add the message to the logListModel
                    logListModel.addElement(message);

                    // Ensure the last index is visible in the logList
                    int lastIndex = logListModel.getSize() - 1;
                    if (lastIndex >= 0) {
                        logList.ensureIndexIsVisible(lastIndex);
                    }
                }
            });
        }
    }
}
